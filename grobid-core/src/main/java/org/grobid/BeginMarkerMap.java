package org.grobid;

import java.util.ArrayList;
import java.util.List;

public class BeginMarkerMap {
	public static List<String> getDigitPatterns() {
		List<String> pattern = new ArrayList<>();
		pattern.add("[0-9]*[.]?");

		return pattern;
	}
	
	public static List<String> getLetterBracketPatterns() {
		List<String> pattern = new ArrayList<>();
		pattern.add("[0-9]+");
		pattern.add("[a-zA-Z0-9\\p{L}- \\.\\(\\)\\/]+[\\+]?[0-9]*[a-zA-Z]?");
		pattern.add("[a-zA-Z0-9\\p{L}- \\.\\(\\)\\/]+[0-9]{4}[a-zA-Z]?");
		pattern.add("[a-zA-Z0-9\\p{L}- \\.\\(\\)\\/]+[,]? [0-9]{4}[a-zA-Z]?");
		pattern.add("[a-zA-Z0-9\\p{L}- \\.\\(\\)\\/]+ and [a-zA-Z0-9\\p{L}- \\.\\(\\)\\/]+[,]? [0-9]{4}[a-zA-Z]?");
		pattern.add("[a-zA-Z0-9\\p{L}- \\.\\(\\)\\/]+ et al.[,]? [0-9]{4}[a-zA-Z]?");

		return pattern;
	}

	public static List<String> getRoundBracketPatterns() {
		List<String> pattern = new ArrayList<>();
		pattern.add("\\([a-zA-Z0-9\\p{L}- \\.\\(\\)\\/]+, [0-9]{4}[a-zA-Z]?\\)");
		pattern.add("\\([a-zA-Z0-9\\p{L}- \\.\\(\\)\\/]+ & [a-zA-Z0-9\\p{L}- \\.\\(\\)\\/]+, [0-9]{4}[a-zA-Z]?\\)");
		pattern.add("\\([a-zA-Z0-9\\p{L}- \\.\\(\\)\\/]+, [a-zA-Z0-9\\p{L}- \\.\\(\\)\\/]+, & [a-zA-Z0-9\\p{L}- \\.\\(\\)\\/]+, [0-9]{4}[a-zA-Z]?\\)");
		pattern.add("\\([a-zA-Z0-9\\p{L}- \\.\\(\\)\\/]+, [a-zA-Z0-9\\p{L}- \\.\\(\\)\\/]+, [a-zA-Z0-9\\p{L}- \\.\\(\\)\\/]+, & [a-zA-Z0-9\\p{L}- \\.\\(\\)\\/]+, [0-9]{4}[a-zA-Z]?\\)");
		pattern.add("\\([a-zA-Z0-9\\p{L}- \\.\\(\\)\\/]+[,]? [0-9]{4}[a-zA-Z]?\\)");
		pattern.add("\\([a-zA-Z0-9\\p{L}- \\.\\(\\)\\/]+ and [a-zA-Z0-9\\p{L}- \\.\\(\\)\\/]+[,]? [0-9]{4}[a-zA-Z]?\\)");
		pattern.add("\\([a-zA-Z0-9\\p{L}- \\.\\(\\)\\/]+ et al.[,]? [0-9]{4}[a-zA-Z]?\\)");
		pattern.add("\\([a-zA-Z0-9\\p{L}- \\.\\(\\)\\/]+[,]? \\([a-zA-Z0-9\\p{L}- \\.\\(\\)\\/]+[,]? and \\([a-zA-Z0-9\\p{L}- \\.\\(\\)\\/]+ [0-9]{4}[a-zA-Z]?\\)");

		return pattern;
	}

	public static List<String> getSquareBracketPatterns() {
		List<String> pattern = new ArrayList<>();
		pattern.add("\\[[0-9]+\\]");
		pattern.add("\\[[a-zA-Z0-9\\p{L}- \\.\\(\\)\\/]+[\\+]?[0-9]*[a-zA-Z]?\\]");
		pattern.add("\\[[a-zA-Z0-9\\p{L}- \\.\\(\\)\\/]+[0-9]{4}[a-zA-Z]?\\]");
		pattern.add("\\[[a-zA-Z0-9\\p{L}- \\.\\(\\)\\/]+[,]? [0-9]{4}[a-zA-Z]?\\]");
		pattern.add("\\[[a-zA-Z0-9\\p{L}- \\.\\(\\)\\/]+ and [a-zA-Z0-9\\p{L}- \\.\\(\\)\\/]+[,]? [0-9]{4}[a-zA-Z]?\\]");
		pattern.add("\\[[a-zA-Z0-9\\p{L}- \\.\\(\\)\\/]+ et al.[,]? [0-9]{4}[a-zA-Z]?\\]");

		return pattern;
	}

	/**
	 * @param firstChar not null and has to be "(" or "["
	 * @return
	 */
	public static List<String> getPatternsByFirstCharacter(char firstChar) {
		if (firstChar == '(') {
			return getRoundBracketPatterns();
		} else if (firstChar == '[') {
			return getSquareBracketPatterns();
		} else if (Character.isDigit(firstChar)) {
			return getDigitPatterns();
		} else if (Character.isLetter(firstChar)) {
			return getLetterBracketPatterns();
		} else {
			return null;
		}
	}
}
