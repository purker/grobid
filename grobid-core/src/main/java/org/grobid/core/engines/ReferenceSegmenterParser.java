package org.grobid.core.engines;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections4.CollectionUtils;
import org.grobid.BeginMarkerMap;
import org.grobid.core.GrobidModels;
import org.grobid.core.document.Document;
import org.grobid.core.document.DocumentPiece;
import org.grobid.core.document.DocumentPointer;
import org.grobid.core.document.TrainingDocument;
import org.grobid.core.engines.citations.LabeledReferenceResult;
import org.grobid.core.engines.citations.ReferenceSegmenter;
import org.grobid.core.engines.label.SegmentationLabels;
import org.grobid.core.engines.tagging.GenericTaggerUtils;
import org.grobid.core.exceptions.GrobidException;
import org.grobid.core.features.FeatureFactory;
import org.grobid.core.features.FeaturesVectorReferenceSegmenter;
import org.grobid.core.layout.Block;
import org.grobid.core.layout.BoundingBox;
import org.grobid.core.layout.LayoutToken;
import org.grobid.core.layout.LayoutTokenLine;
import org.grobid.core.utilities.BoundingBoxCalculator;
import org.grobid.core.utilities.Pair;
import org.grobid.core.utilities.StringUtil;
import org.grobid.core.utilities.TextUtilities;
import org.grobid.core.utilities.TokenLabelPair;
import org.grobid.core.utilities.Triple;
import org.grobid.core.utilities.XStreamUtil;
import org.grobid.trainer.ReferenceSegmenterTrainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.google.common.collect.Sets;

/**
 * @author Slava, Patrice
 * Date: 4/14/14
 */
public class ReferenceSegmenterParser extends AbstractParser implements ReferenceSegmenter {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReferenceSegmenterParser.class);

	// projection scale for line length
	private static final int LINESCALE = 10;
	public static final Pattern references = Pattern.compile("^\\b*(References?|REFERENCES?|Bibliography|BIBLIOGRAPHY|"
			+ "References?\\s+and\\s+Notes?|References?\\s+Cited|REFERENCE?\\s+CITED|REFERENCES?\\s+AND\\s+NOTES?|Références|Literatur|"
			+ "LITERATURA|Literatur|Referências|BIBLIOGRAFIA|Literaturverzeichnis|Referencias|LITERATURE CITED|References and Notes)", Pattern.CASE_INSENSITIVE);


	private static final int SCALE = 2;
	private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
	private static final BigDecimal RANGE_PERCENTAGE = new BigDecimal(5);

	private static final boolean PRINT_MINX_LIMIT = false;
	//private static int countLines = 0;

	protected ReferenceSegmenterParser() {
		super(GrobidModels.REFERENCE_SEGMENTER);
	}

	@Override
	public List<LabeledReferenceResult> extract(String referenceBlock) {
		Document res = Document.createFromText(referenceBlock);

		DocumentPiece piece = new DocumentPiece(
				new DocumentPointer(0, 0, 0),
				new DocumentPointer(0, res.getTokenizations().size() - 1, res.getTokenizations().size() - 1));

		return extract(res, Sets.newTreeSet(Collections.singletonList(piece)), false);
	}

	/**
	 *
	 * @param doc Document object
	 * @return <reference_label, reference_string>  Note, that label is null when no label was detected
	 *              example: <"[1]", "Hu W., Barkana, R., &amp; Gruzinov A. Phys. Rev. Lett. 85, 1158">
	 */
	public List<LabeledReferenceResult> extract(Document doc) {
		return extract(doc, false);
	}

	public List<LabeledReferenceResult> extract(Document doc, boolean training) {
		SortedSet<DocumentPiece> referencesParts = doc.getDocumentPart(SegmentationLabels.REFERENCES);
		return extract(doc, referencesParts, training);
	}

    public List<LabeledReferenceResult> extract(Document doc, SortedSet<DocumentPiece> referencesParts, boolean training) {

		Pair<String, List<LayoutToken>> featSeg = getReferencesSectionFeatured(doc, referencesParts);
		String res;
		List<LayoutToken> tokenizationsReferences;
		if (featSeg == null) {
			return null;
		}
		
		// if featSeg is null, it usually means that no reference segment is found in the
		// document segmentation
		String featureVector = featSeg.getA();
		tokenizationsReferences = featSeg.getB();

		return getExtractionResult(tokenizationsReferences);
	}

	public List<LabeledReferenceResult> extractWithGivenLabelsFromTeiFile(TrainingDocument doc)
			throws ParserConfigurationException, SAXException, IOException {
		SortedSet<DocumentPiece> referencesParts = doc.getDocumentPart(SegmentationLabels.REFERENCES);
		Pair<String, List<LayoutToken>> featSeg = getReferencesSectionFeatured(doc, referencesParts);
		List<LayoutToken> tokenizationsReferences;
		if (featSeg == null) {
			return null;
		}
		// if featSeg is null, it usually means that no reference segment is found in the
		// document segmentation
		//not needed String featureVector = featSeg.getA();
		tokenizationsReferences = featSeg.getB();

		List<TokenLabelPair> labels = ReferenceSegmenterTrainer.getLabeledTokensFromTeiFile(null, doc.getDocumentSource().getTeiFileReferenceSegmenter());

		//pairs to triples
		List<Triple<String, String, String>> labeledTriples = new ArrayList<>();
		for (TokenLabelPair pair : labels) {
			labeledTriples.add(new Triple<String, String, String>(pair.getToken(), pair.getLabel(), null));
		}

		return getExtractionResult(tokenizationsReferences);
	}

	// TODO Angela auf private ohne static ändern
	public static List<LabeledReferenceResult> getExtractionResult(List<LayoutToken> list) {
		try {
		List<LabeledReferenceResult> labeledReferenceResults = new ArrayList<>();

		if (CollectionUtils.isEmpty(list)) {
			return labeledReferenceResults;
		}

		List<List<LayoutToken>> lines = new ArrayList<>();
		List<LayoutToken> line = new ArrayList<>();
		TreeSet<BigDecimal> minXSet = new TreeSet<>();

		// split layoutTokens in lines and calculate minX
		for (LayoutToken layoutToken : list) {
			if (layoutToken.getText().equals("\n")) {
				//TODO maybe check not empty, when multiple \n
				lines.add(line);
				line = new ArrayList<>();
				continue;
			} else {
				if (references.matcher(layoutToken.getText()).matches()) {
					// ignore Reference Header
					continue;
				}
				line.add(layoutToken);
			}
		}
		
//		count lines
//		countLines+= lines.size();
//		System.out.println("countLines: "+countLines);
//		return labeledReferenceResults;
		
		for (List<LayoutToken> l : lines) {
			if (CollectionUtils.isNotEmpty(l)) {
				LayoutToken first = l.get(0);
				if (first.getX() != -1) {
					BigDecimal bigDecimalMinX = BigDecimal.valueOf(first.getX()).setScale(SCALE, ROUNDING_MODE);
					minXSet.add(bigDecimalMinX);
				}
			}
		}
		
		BigDecimal minXLimit = getMinXLimit(minXSet);

		//print lines
//		int x=0;
//		for (List<LayoutToken> l : lines) {
//			if (CollectionUtils.isNotEmpty(l)) {
//				System.out.printf("%5d %10.2f", x++, l.get(0).getX());
//				for (LayoutToken layoutToken : l) {
//					System.out.print(layoutToken.getText());
//				}
//				System.out.println();
//			} else {
//				System.out.printf("%5d ISEMPTY\n", x++);
//			}
//		}

		
		// System.out.println("####################################################################");
		LayoutToken firstToken;

		LabeledReferenceResult r = null;

		// System.out.printf("minx: %f\n", minXLimit);
		forLines: for (List<LayoutToken> l : lines) {
			//System.out.print("\nline: ");
			if (CollectionUtils.isNotEmpty(l)) {
				firstToken = l.get(0);
				LayoutTokenLine layoutTokenLine = new LayoutTokenLine(l);
				String text = layoutTokenLine.getText();

				BigDecimal minXFirstToken = BigDecimal.valueOf(firstToken.getX()).setScale(SCALE, ROUNDING_MODE);

				// System.out.printf("%b %f %s\n", minXFirstToken.compareTo(minXLimit) <= 0, firstToken.getX(), text);
				if (minXFirstToken.compareTo(minXLimit) <= 0 || minXSet.size() >= 6) {
					if (StringUtil.isEmpty(text.trim())) {
						// ignore empty line
						continue;
					}
					Integer startTextIndex = null; //index in text after label "1. Hu, B., Raidl, G." -> 3
					List<String> patterns = BeginMarkerMap.getPatternsByFirstCharacter(text.charAt(0));
					if (patterns == null) {
						// line doesn't start with label -> append to current reference
						if (r!=null) {
							r.addTokens(layoutTokenLine);							
						}
						continue forLines;
					}
					for (String string : patterns) {
						Pattern pattern = Pattern.compile(string);
						Matcher m = pattern.matcher(text);

						if (m.find()) {
							startTextIndex = m.group(0).length();
							break;
						}
					}

					if (startTextIndex == null) {
						// System.err.println("no label found");
						if (r!=null) {
							r.addTokens(layoutTokenLine);							
						}
						continue forLines;
					}
					
					//boolean not=false;
					if((startTextIndex + 1) >= layoutTokenLine.getTokenCountOnIndex().size()) {
						// in this line is only a label -> it is no label, append to current reference
						// Example: TUW-138447.pdf Citation 10. line 2 "1999."
						if (r!=null) {
							r.addTokens(layoutTokenLine);
							//System.out.println("countxy");
							//not = true;
						}
						continue forLines;
					}
						

					// System.out.println("startTextIndex: " + startTextIndex);
					int lastLabelTokenIndex = layoutTokenLine.getTokenCountOnIndex().get(startTextIndex);
					int referenceTextLayoutTokenIndex = layoutTokenLine.getTokenCountOnIndex().get(startTextIndex + 1);

					// System.out.println(lastLabelTokenIndex);
					// System.out.println(referenceTextLayoutTokenIndex);

					String label = text.substring(0, startTextIndex);
					String refText = text.substring(startTextIndex + 1, text.length());
//					String label = text.substring(0, lastLabelTokenIndex + 1); // da +1, also mit " " am schluss?
//					String refText = text.substring(referenceTextLayoutTokenIndex, text.length());

					List<LayoutToken> tokens = layoutTokenLine.getTokens().subList(referenceTextLayoutTokenIndex, layoutTokenLine.getTokens().size());
					List<BoundingBox> coordinates = BoundingBoxCalculator.calculate(tokens);
					r = new LabeledReferenceResult(label, refText, tokens, null, coordinates);
					labeledReferenceResults.add(r);

					// System.out.println(label);
					// System.out.println(refText);
					// System.out.println(tokens);
					// System.out.println(layoutTokenLine.getTokenCountOnIndex());
					// System.out.println(layoutTokenLine.getTokens());
				} else {
					if (r!=null) {
						r.addTokens(layoutTokenLine);
					} else {
						// System.out.println("\nr is null: "+l.stream().map(s -> s.getText()).collect(Collectors.toList())+"n");
					}
				}
			}
			// for (LayoutToken t : l) {
			// System.out.print(t.getText() + "|");
			// }
		}

		// File file = new File("da.xml");
		// System.out.println(file.getAbsolutePath());
		// XStreamUtil.convertToXml(labeledReferenceResults, file, null, true);
		
		//		for (LabeledReferenceResult result : labeledReferenceResults) {
		//			System.out.println(result.getLabel());
		//			System.out.println(result.getReferenceText());
		//			System.out.println("\n");
		//		}
		
		return labeledReferenceResults;
		} //remove try chatch
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param minXSet set of x coordinates from first tokens of a line
	 * @return limit (incl.) which value is to considered as reference start
	 * 
	 * Example: 
	 * 	minXSet: [79.20, 84.24, 101.28, 317.88]
	 *	difference:   5,04   2%
	 *	difference:  22,08   9%
	 *	difference: 238,68 100%
	 *
	 *	returns 84.24 since difference < RANGE_PERCENTAGE (=5)
	 */
	private static BigDecimal getMinXLimit(TreeSet<BigDecimal> minXSet) {
		if (CollectionUtils.isEmpty(minXSet)) {
			return BigDecimal.valueOf(0);
		}
		
		if (PRINT_MINX_LIMIT) {
			System.out.println("minXSet: " + minXSet);
		}
		
		Iterator<BigDecimal> iter = minXSet.iterator();
		BigDecimal first = iter.next();
		BigDecimal last = minXSet.last();
		BigDecimal range = last.subtract(first);
		BigDecimal limit = first;
		
		if ( first.compareTo(last) == 0) {
			// TODO Angela
			System.err.println ("first equal last");
		}
		else {
			// otherwise division through zero
			while(iter.hasNext()) {
				BigDecimal current = iter.next();
				BigDecimal difference = current.subtract(first);
				BigDecimal percentage = difference.divide(range, SCALE, ROUNDING_MODE).multiply(new BigDecimal(100));
				if (PRINT_MINX_LIMIT) {
					System.out.printf("difference: %6.2f %3.0f%%\n", difference, percentage);
				}
				if (percentage.compareTo(RANGE_PERCENTAGE) <= 0) {
					limit = current;
				}
			}
		}
		return limit;
	}

	public Pair<String, String> createTrainingData(TrainingDocument doc, int id) {
		SortedSet<DocumentPiece> referencesParts = doc.getDocumentPart(SegmentationLabels.REFERENCES);
		Pair<String, List<LayoutToken>> featSeg = getReferencesSectionFeatured(doc, referencesParts);

		List<LayoutToken> tokenizations;
		if (featSeg == null) {
			return null;
		}
		// if featSeg is null, it usually means that no reference segment is found in the
		// document segmentation
		String featureVector = featSeg.getA();
		tokenizations = featSeg.getB();

		List<TokenLabelPair> labeled = null;

		if (doc.getDocumentSource().getTeiFileReferenceSegmenterExisted()) {
			try {
				labeled = ReferenceSegmenterTrainer.getLabeledTokensFromTeiFile(null, doc.getDocumentSource().getTeiFileReferenceSegmenter());
			} catch (Exception e) {
				throw new GrobidException("An exception occured while running Grobid.", e);
			}
		}
		//extract from scratch, if labels not predetermined by trainingfiles
		if (CollectionUtils.isEmpty(labeled)) {
			try {
				String res = label(featureVector);

				if (res == null) {
					return null;
				}

				labeled = GenericTaggerUtils.getTokensAndLabels(res);
			} catch (Exception e) {
				throw new GrobidException("CRF labeling in ReferenceSegmenter fails.", e);
			}
		}
		StringBuilder sb = new StringBuilder();

		//noinspection StringConcatenationInsideStringBufferAppend
		sb.append("<tei>\n" +
				"    <teiHeader>\n" +
				"        <fileDesc xml:id=\"_" + id + "\"/>\n" +
				"    </teiHeader>\n" +
				"    <text xml:lang=\"en\">\n" +
				"        <listBibl>\n");

		int tokPtr = 0;
		boolean addSpace = false;
		boolean addEOL = false;
		String lastTag = null;
		boolean refOpen = false;
		for (TokenLabelPair l : labeled) {
			String tok = l.a;
			String label = l.b;

			int tokPtr2 = tokPtr;
			for (; tokPtr2 < tokenizations.size(); tokPtr2++) {
				if (tokenizations.get(tokPtr2).t().equals(" ")) {
					addSpace = true;
				}
				else if (tokenizations.get(tokPtr2).t().equals("\n") ||
					     tokenizations.get(tokPtr).t().equals("\r") ) {
					addEOL = true;
							}
                else {
          					break;
}
			}
			tokPtr = tokPtr2;

			if (tokPtr >= tokenizations.size()) {
				LOGGER.error("Implementation error: Reached the end of tokenizations, but current token is " + tok);
				// we add a space to avoid concatenated text
				addSpace = true;
            }
            else {
				String tokenizationToken = tokenizations.get(tokPtr).getText();

				if ((tokPtr != tokenizations.size()) && !tokenizationToken.equals(tok)) {
					// and we add a space by default to avoid concatenated text
					addSpace = true;
					if (!tok.startsWith(tokenizationToken)) {
						// this is a very exceptional case due to a sequence of accent/diacresis, in this case we skip
						// a shift in the tokenizations list and continue on the basis of the labeled token
						// we check one ahead
						tokPtr++;
						tokenizationToken = tokenizations.get(tokPtr).getText();
						if (!tok.equals(tokenizationToken)) {
							// we try another position forward (second hope!)
							tokPtr++;
							tokenizationToken = tokenizations.get(tokPtr).getText();
							if (!tok.equals(tokenizationToken)) {
								// we try another position forward (last hope!)
								tokPtr++;
								tokenizationToken = tokenizations.get(tokPtr).getText();
								if (!tok.equals(tokenizationToken)) {
									// we return to the initial position
									tokPtr = tokPtr - 3;
									tokenizationToken = tokenizations.get(tokPtr).getText();
									LOGGER.error("Implementation error, tokens out of sync: " +
										tokenizationToken + " != " + tok + ", at position " + tokPtr);
								}
							}
						}
					}
					// note: if the above condition is true, this is an exceptional case due to a
					// sequence of accent/diacresis and we can go on as a full string match
				}
			}

			String plainLabel = GenericTaggerUtils.getPlainLabel(label);

			boolean tagClosed = (lastTag != null) && testClosingTag(sb, label, lastTag, addSpace, addEOL);

			if (tagClosed) {
				addSpace = false;
				addEOL = false;
			}
			if (tagClosed && lastTag.equals("<reference>")) {
				refOpen = false;
			}
			String output;
			String field;
			if (refOpen) {
				field = "<label>";
			}
			else {
				field = "<bibl><label>";
			}
			output = writeField(label, lastTag, tok, "<label>", field, addSpace, addEOL, 2);
			if (output != null) {
				sb.append(output);
				refOpen = true;
			}
			else {
				if (refOpen) {
					field = "";
				}
				else {
					field = "<bibl>";
				}
				output = writeField(label, lastTag, tok, "<reference>", field, addSpace, addEOL, 2);
				if (output != null) {
					sb.append(output);
					refOpen = true;
				}
				else {
					output = writeField(label, lastTag, tok, "<other>", "", addSpace, addEOL, 2);
					if (output != null) {
						sb.append(output);
						refOpen = false;
					}
				}
			}

			lastTag = plainLabel;
			addSpace = false;
			addEOL = false;
			tokPtr++;
		}

		if (refOpen) {
			sb.append("</bibl>");
		}

        sb.append("\n        </listBibl>\n" +
                "    </text>\n" +
                "</tei>\n");

		return new Pair<String, String>(sb.toString(), featureVector);
	}

	private boolean testClosingTag(StringBuilder buffer,
			String currentTag,
			String lastTag,
			boolean addSpace,
			boolean addEOL) {
		boolean res = false;
		if (!currentTag.equals(lastTag)) {
			res = true;
			// we close the current tag
			if (lastTag.equals("<other>")) {
				if (addEOL)
					buffer.append("<lb/>");
				if (addSpace)
					buffer.append(" ");
				buffer.append("\n");
			} else if (lastTag.equals("<label>")) {
				buffer.append("</label>");
				if (addEOL)
					buffer.append("<lb/>");
				if (addSpace)
					buffer.append(" ");
			} else if (lastTag.equals("<reference>")) {
				if (addEOL)
					buffer.append("<lb/>");
				if (addSpace)
					buffer.append(" ");
				buffer.append("</bibl>\n");
			} else {
				res = false;
			}
		}
		return res;
	}

	private String writeField(String currentTag,
			String lastTag,
			String token,
			String field,
			String outField,
			boolean addSpace,
			boolean addEOL,
			int nbIndent) {
		String result = null;
		if (currentTag.endsWith(field)) {
			if (currentTag.endsWith("<other>")) {
				result = "";
				if (currentTag.equals("I-<other>")) {
					result += "\n";
					for (int i = 0; i < nbIndent; i++) {
						result += "    ";
					}
				}
				if (addEOL)
					result += "<lb/>";
				if (addSpace)
					result += " ";
				result += TextUtilities.HTMLEncode(token);
            }
			else if ((lastTag != null) && currentTag.endsWith(lastTag)) {
				result = "";
				if (addEOL)
					result += "<lb/>";
				if (addSpace)
					result += " ";
				if (currentTag.startsWith("I-"))
					result += outField;
				result += TextUtilities.HTMLEncode(token);
            }
			else {
				result = "";
				if (outField.length() > 0) {
					for (int i = 0; i < nbIndent; i++) {
						result += "    ";
					}
				}
				if (addEOL)
					result += "<lb/>";
				if (addSpace)
					result += " ";
				result += outField + TextUtilities.HTMLEncode(token);
			}
		}
		return result;
	}

	static public Pair<String, List<LayoutToken>> getReferencesSectionFeatured(Document doc,
			SortedSet<DocumentPiece> referencesParts) {
		if ((referencesParts == null) || (referencesParts.size() == 0)) {
			return null;
		}
		FeatureFactory featureFactory = FeatureFactory.getInstance();
		List<Block> blocks = doc.getBlocks();
		if ((blocks == null) || blocks.size() == 0) {
			return null;
		}

		StringBuilder citations = new StringBuilder();
		boolean newline;
		//        String currentFont = null;
		//        int currentFontSize = -1;
		int n; // overall token number

		//int currentJournalPositions = 0;
		//int currentAbbrevJournalPositions = 0;
		//int currentConferencePositions = 0;
		//int currentPublisherPositions = 0;
		//boolean isJournalToken;
		//boolean isAbbrevJournalToken;
		//boolean isConferenceToken;
		//boolean isPublisherToken;
		//boolean skipTest;

		FeaturesVectorReferenceSegmenter features;
		FeaturesVectorReferenceSegmenter previousFeatures = null;
		boolean endblock;
		boolean startblock;
		//int mm = 0; // token position in the sentence
		int nn; // token position in the line
		double lineStartX = Double.NaN;
		boolean indented = false;

		List<LayoutToken> tokenizationsReferences = new ArrayList<LayoutToken>();
		List<LayoutToken> tokenizations = doc.getTokenizations();

		int maxLineLength = 1;
		//List<Integer> lineLengths = new ArrayList<Integer>();
		int currentLineLength = 0;
		//int lineIndex = 0;

		// we calculate current max line length and intialize the body tokenization structure
		for (DocumentPiece docPiece : referencesParts) {
			DocumentPointer dp1 = docPiece.a;
			DocumentPointer dp2 = docPiece.b;

			int tokens = dp1.getTokenDocPos();
			int tokene = dp2.getTokenDocPos();
			for (int i = tokens; i <= tokene; i++) {
				tokenizationsReferences.add(tokenizations.get(i));
				currentLineLength += tokenizations.get(i).getText().length();
				if (tokenizations.get(i).t().equals("\n") || tokenizations.get(i).t().equals("\r")) {
					//lineLengths.add(currentLineLength);
					if (currentLineLength > maxLineLength)
						maxLineLength = currentLineLength;
					currentLineLength = 0;
				}
			}
		}

		for (DocumentPiece docPiece : referencesParts) {
			DocumentPointer dp1 = docPiece.a;
			DocumentPointer dp2 = docPiece.b;

			/*for(int i=dp1.getTokenDocPos(); i<dp2.getTokenDocPos(); i++) {
				System.out.print(tokenizations.get(i));
			}	
			System.out.println("");
			*/
			//currentLineLength = lineLengths.get(lineIndex);
			nn = 0;
			int tokenIndex = 0;
			int blockIndex = dp1.getBlockPtr();
			Block block = null;
			List<LayoutToken> tokens;
			boolean previousNewline = true;
			currentLineLength = 0;
			String currentLineProfile = null;
			for (n = dp1.getTokenDocPos(); n <= dp2.getTokenDocPos(); n++) {
				String text = tokenizations.get(n).getText();

				if (text == null) {
					continue;
				}

				// set corresponding block
				if ((block != null) && (n > block.getEndToken())) {
					blockIndex++;
					tokenIndex = 0;
					currentLineLength = 0;
					currentLineProfile = null;
				}

				if (blockIndex < blocks.size()) {
					block = blocks.get(blockIndex);
					if (n == block.getStartToken()) {
						startblock = true;
						endblock = false;
					}
					else if (n == block.getEndToken()) {
						startblock = false;
						endblock = true;
					}
					else {
						startblock = false;
						endblock = false;
					}
				}
				else {
					block = null;
					startblock = false;
					endblock = false;
				}
				// set corresponding token
				if (block != null)
					tokens = block.getTokens();
				else
					tokens = null;

				if (text.equals("\n") || text.equals("\r")) {
					previousNewline = true;
					nn = 0;
					currentLineLength = 0;
					currentLineProfile = null;
					//lineIndex++;
					//currentLineLength = lineLengths.get(lineIndex);
					continue;
                }
				else {
					newline = false;
					nn += text.length(); // +1 for segmentation symbol
				}

				if (text.equals(" ") || text.equals("\t")) {
					nn++;
					continue;
				}

				if (text.trim().length() == 0) {
					continue;
				}

				LayoutToken token = null;
				if (tokens != null) {
					int i = tokenIndex;
					while (i < tokens.size()) {
						token = tokens.get(i);
						if (text.equals(token.getText())) {
							tokenIndex = i;
							break;
						}
						i++;
					}
				}

				if (previousNewline) {
					newline = true;
					previousNewline = false;
					if (token != null && previousFeatures != null) {
						double previousLineStartX = lineStartX;
						lineStartX = token.getX();
						double characterWidth = token.width / token.getText().length();
						if (!Double.isNaN(previousLineStartX)) {
							// Indentation if line start is > 1 character width to the right of previous line start
							if (lineStartX - previousLineStartX > characterWidth)
								indented = true;
							// Indentation ends if line start is > 1 character width to the left of previous line start
							else if (previousLineStartX - lineStartX > characterWidth)
								indented = false;
							// Otherwise indentation is unchanged
						}
					}
				}

				if (TextUtilities.filterLine(text)) {
					continue;
				}

				features = new FeaturesVectorReferenceSegmenter();
				features.token = token;
				features.string = text;

				if (newline) {
					features.lineStatus = "LINESTART";
				}
				Matcher m0 = featureFactory.isPunct.matcher(text);
				if (m0.find()) {
					features.punctType = "PUNCT";
				}
				if (text.equals("(") || text.equals("[")) {
					features.punctType = "OPENBRACKET";

				} else if (text.equals(")") || text.equals("]")) {
					features.punctType = "ENDBRACKET";

				} else if (text.equals(".")) {
					features.punctType = "DOT";

				} else if (text.equals(",")) {
					features.punctType = "COMMA";

				} else if (text.equals("-")) {
					features.punctType = "HYPHEN";

				} else if (text.equals("\"") || text.equals("\'") || text.equals("`")) {
					features.punctType = "QUOTE";

				}

				if ((n == 0) || (previousNewline)) {
					features.lineStatus = "LINESTART";
					if (n == 0)
						features.blockStatus = "BLOCKSTART";
					nn = 0;
				}

				if (indented) {
					features.alignmentStatus = "LINEINDENT";
                }
                else {
					features.alignmentStatus = "ALIGNEDLEFT";
				}

				{
					// look ahead...
					boolean endline = true;

					int ii = 1;
					boolean endloop = false;
					String accumulated = text;
					while ((n + ii < tokenizations.size()) && (!endloop)) {
						String tok = tokenizations.get(n + ii).getText();
						//System.out.printf("n:%d ii:%d n+ii: %d tok:%s", n, ii, n + ii, tok);
						if (tok != null) {
							if (currentLineProfile == null)
								accumulated += tok;
							if (tok.equals("\n") || tok.equals("\r")) {
								endloop = true;
								if (currentLineLength == 0) {
									currentLineLength = accumulated.length();
								}
								if (currentLineProfile == null) {
									currentLineProfile = TextUtilities.punctuationProfile(accumulated);
								}
                            }
							else if (!tok.equals(" ") && !tok.equals("\t")) {
								endline = false;
							}
							else {
								if (TextUtilities.filterLine(tok)) {
									endloop = true;
									if (currentLineLength == 0) {
										currentLineLength = accumulated.length();
									}
									if (currentLineProfile == null) {
										currentLineProfile = TextUtilities.punctuationProfile(accumulated);
									}
								}
							}
						}

						if (n + ii >= tokenizations.size() - 1) {
							endblock = true;
							endline = true;
						}

						if (endline && (block != null) && (n + ii == block.getEndToken())) {
							endblock = true;
						}
						ii++;
					}

					if ((!endline) && !(newline)) {
						features.lineStatus = "LINEIN";
                    }
					else if (!newline) {
						features.lineStatus = "LINEEND";
						previousNewline = true;
					}

					if (startblock) {
						features.blockStatus = "BLOCKSTART";
					}
					if ((!endblock) && (features.blockStatus == null))
						features.blockStatus = "BLOCKIN";
					else if (features.blockStatus == null) {
						features.blockStatus = "BLOCKEND";
					}
				}

				if (text.length() == 1) {
					features.singleChar = true;
				}

				if (Character.isUpperCase(text.charAt(0))) {
					features.capitalisation = "INITCAP";
				}

				if (featureFactory.test_all_capital(text)) {
					features.capitalisation = "ALLCAP";
				}

				if (featureFactory.test_digit(text)) {
					features.digit = "CONTAINSDIGITS";
				}

				if (featureFactory.test_common(text)) {
					features.commonName = true;
				}

				if (featureFactory.test_names(text)) {
					features.properName = true;
				}

				if (featureFactory.test_month(text)) {
					features.month = true;
				}

				Matcher m = featureFactory.isDigit.matcher(text);
				if (m.find()) {
					features.digit = "ALLDIGIT";
				}

				Matcher m2 = featureFactory.year.matcher(text);
				if (m2.find()) {
					features.year = true;
				}

				Matcher m3 = featureFactory.email.matcher(text);
				if (m3.find()) {
					features.email = true;
				}

				Matcher m4 = featureFactory.http.matcher(text);
				if (m4.find()) {
					features.http = true;
				}

				if ((token != null) && (token.getBold()))
					features.bold = true;

				if ((token != null) && (token.getItalic()))
					features.italic = true;

				if (features.capitalisation == null)
					features.capitalisation = "NOCAPS";

				if (features.digit == null)
					features.digit = "NODIGIT";

				if (features.punctType == null)
					features.punctType = "NOPUNCT";
				//System.out.println(nn + "\t" + currentLineLength + "\t" + maxLineLength);
                features.lineLength = featureFactory
                        .linearScaling(currentLineLength, maxLineLength, LINESCALE);

				features.relativePosition = featureFactory
                         .linearScaling(nn, currentLineLength, LINESCALE);

				features.punctuationProfile = currentLineProfile;

				if (previousFeatures != null)
					citations.append(previousFeatures.printVector());
				//mm++;
				previousFeatures = features;
			}
		}
		if (previousFeatures != null)
			citations.append(previousFeatures.printVector());

		return new Pair<>(citations.toString(), tokenizationsReferences);
	}
}
