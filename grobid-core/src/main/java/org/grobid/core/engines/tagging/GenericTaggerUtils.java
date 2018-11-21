package org.grobid.core.engines.tagging;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import org.apache.commons.lang3.StringUtils;
import org.grobid.core.exceptions.GrobidException;
import org.grobid.core.utilities.Pair;
import org.grobid.core.utilities.Triple;
import org.wipo.analyzers.wipokr.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
	public static List<Pair<String, String>> getTokensAndLabels(String labeledResult) {
		Function<List<String>, Pair<String, String>> fromSplits = new Function<List<String>, Pair<String, String>>() {
            @Override public Pair<String, String> apply(List<String> splits) {
				return new Pair<>(splits.get(0), splits.get(splits.size() - 1));
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

	public static List<Pair<String, String>> getTokensAndLabelsAsPair(List<String> labels) {
		List<Pair<String, String>> pairs = new ArrayList<>();
		for (String string : labels) {
			String[] split = string.split(" ");
			if (split.length != 2) {
				if (split.length == 1 && split[0].startsWith("@newline")) //can also be "@newline\n"
					continue;
				else
					throw new GrobidException("Token pair not correctly splitted");
			} else
				pairs.add(new Pair<>(split[0], StringUtils.removeEnd(split[1], "\n")));
		}
		return pairs;
	}
}