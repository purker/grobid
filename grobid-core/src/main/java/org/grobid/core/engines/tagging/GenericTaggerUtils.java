package org.grobid.core.engines.tagging;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.grobid.core.document.TrainingDocument;
import org.grobid.core.engines.featurefile.FeatureFile;
import org.grobid.core.engines.featurefile.FeatureRow;
import org.grobid.core.exceptions.GrobidException;
import org.grobid.core.utilities.Pair;
import org.grobid.core.utilities.TokenLabelPair;
import org.grobid.core.utilities.Triple;
import org.wipo.analyzers.wipokr.utils.StringUtil;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

/**
 * User: zholudev
 * Date: 4/2/14
 */
public class GenericTaggerUtils {

	public static final String START_ENTITY_LABEL_PREFIX = "I-";
	public static final Pattern SEPARATOR_PATTERN = Pattern.compile("[\t ]");

	/**
	 * @param labeledResult labeled result from a tagger
	 * @return a list of pairs - first element in a pair is a token itself, the second is a label (e.g. <footnote> or I-<footnote>)
	 * Note an empty line in the result will be transformed to a 'null' pointer of a pair
	 */
	public static List<TokenLabelPair> getTokensAndLabels(String labeledResult) {
		Function<List<String>, TokenLabelPair> fromSplits = new Function<List<String>, TokenLabelPair>() {
			@Override
			public TokenLabelPair apply(List<String> splits) {
				return new TokenLabelPair(splits.get(0), splits.get(splits.size() - 1));
			}
		};

		return processLabeledResult(labeledResult, fromSplits);
	}

	/**
	 * @param labeledResult labeled result from a tagger
	 * @return a list of triples - first element in a pair is a token itself, the second is a label (e.g. <footnote> or I-<footnote>) 
	 * and the third element is a string with the features
	 * Note an empty line in the result will be transformed to a 'null' pointer of a pair
	 */
	public static List<Triple<String, String, String>> getTokensWithLabelsAndFeatures(String labeledResult,
            final boolean addFeatureString) {
Function<List<String>, Triple<String, String, String>> fromSplits = new Function<List<String>, Triple<String, String, String>>() {
@Override public Triple<String, String, String> apply(List<String> splits) {
String featureString = addFeatureString ? Joiner.on("\t").join(splits.subList(0, splits.size() - 1)) : null;
return new Triple<>(
splits.get(0),
splits.get(splits.size() - 1),
featureString);
}
};

return processLabeledResult(labeledResult, fromSplits);
}

	private static <T> List<T> processLabeledResult(String labeledResult, Function<List<String>, T> fromSplits) {
		String[] lines = labeledResult.split("\n");
		List<T> res = new ArrayList<>(lines.length);
		for (String line : lines) {
			line = line.trim();
			if (line.isEmpty()) {
				res.add(null);
				continue;
			}
			List<String> splits = Splitter.on(SEPARATOR_PATTERN).splitToList(line);
			res.add(fromSplits.apply(splits));
		}
		return res;
	}

	// I-<citation> --> <citation>
	// <citation> --> <citation>
	public static String getPlainLabel(String label) {
		return StringUtil.startsWith(label, START_ENTITY_LABEL_PREFIX) ? StringUtil.substring(label, 2) : label;
	}

	public static boolean isBeginningOfEntity(String label) {
		return StringUtil.startsWith(label, START_ENTITY_LABEL_PREFIX);
	}

	/**
	 * @param labels one element in the list with 2 whitespace splitted words (token and label)
	 * @return
	 */
	public static List<TokenLabelPair> getTokensAndLabelsAsPair(List<String> labels) {
		List<TokenLabelPair> pairs = new ArrayList<>();
		for (String string : labels) {
			String[] split = string.split(" ");
			if (split.length != 2) {
				if (split.length == 1 && split[0].startsWith("@newline")) //can also be "@newline\n"
					continue;
				else
					throw new GrobidException("Token pair not correctly splitted, one line has to have 2 words (token and label): " + Arrays.toString(split));
			} else
				pairs.add(new TokenLabelPair(split[0], StringUtils.removeEnd(split[1], "\n")));
		}
		return pairs;
	}

	public static List<TokenLabelPair> getTokensAndLabelsAsPair(String result) {
		List<TokenLabelPair> pairs = new ArrayList<>();
		for (FeatureRow row : new FeatureFile(result)) {
			pairs.add(new TokenLabelPair(row.getToken(), row.getLabel()));
		}
		return pairs;
	}

	public static String replaceLabelsOnLabledResult(TrainingDocument doc, File teiFile, String result,
			List<TokenLabelPair> teiLabels)
			throws Exception {
		StringBuffer sb = new StringBuffer();

		//TODO length überprüfen und schauen ob überhaupt gleich
		Iterator<TokenLabelPair> newLabelsIterator = teiLabels.iterator();

		FeatureFile featureFile = new FeatureFile(result);
		int teiIndex = -1;
		for (FeatureRow row : featureFile) {
			if (newLabelsIterator.hasNext()) {
				teiIndex++;
				TokenLabelPair firstPair = newLabelsIterator.next();
				while (firstPair.getToken().equals("+LINE+") && newLabelsIterator.hasNext()) {
					teiIndex++;
					firstPair = newLabelsIterator.next();
				}
				String newToken = firstPair.getToken();
				String newLabel = firstPair.getLabel();

				String originalToken = row.getToken();

				if (!originalToken.equals(newToken)) {
					String pdfString = org.grobid.core.utilities.StringUtil.someFromCollection(featureFile.getRows(), featureFile.getCurrentIndex(), 5, r -> r.toString());
					String teiString = org.grobid.core.utilities.StringUtil.someFromCollection(teiLabels, teiIndex, 5, s -> s.toString());

					File errorResultFile = new File(doc.getErrorPath(), doc.getDocumentSource().getPdfFile().getName() + "-result.txt");
					FileUtils.writeStringToFile(errorResultFile, result, StandardCharsets.UTF_8);

					throw new GrobidException(String.format("headerTokens not synchronized:\nlabels from pdf (%s):\n%s\nlabels from tei (%s):\n%s", doc.getDocumentSource().getPdfFile().getAbsolutePath(), pdfString, teiFile, teiString));
				}

				row.setLabel(newLabel);

				sb.append(row.toString() + "\n");
			}
			if(!newLabelsIterator.hasNext() && featureFile.hasNext()) {
				featureFile.next();
				featureFile.next();
				String pdfString = org.grobid.core.utilities.StringUtil.someFromCollection(featureFile.getRows(), featureFile.getCurrentIndex(), 5, r -> r.toString());
				String teiString = org.grobid.core.utilities.StringUtil.someFromCollection(teiLabels, teiIndex, 5, s -> s.toString());

				File errorResultFile = new File(doc.getErrorPath(), doc.getDocumentSource().getPdfFile().getName() + "-result.txt");
				FileUtils.writeStringToFile(errorResultFile, result, StandardCharsets.UTF_8);

				throw new GrobidException(String.format("headerTokens not synchronized:\nlabels from pdf (%s):\n%s\nlabels from tei (%s):\n%s", doc.getDocumentSource().getPdfFile().getAbsolutePath(), pdfString, teiFile, teiString));
			}
		}

		return sb.toString();
	}
}