package org.grobid.core.utilities;

import java.util.List;
import java.util.function.Function;

public class StringUtil
{
	public static boolean isEmpty(String s)
	{
		if(s == null) return true;
		return s.isEmpty();
	}

	public static boolean isNotEmpty(String s)
	{
		return !isEmpty(s);
	}

	/**
	 * as substring but taking into account the length of the string to avoid IndexOutOfBoundsException
	 * 
	 * @param string
	 * @param length
	 * @return
	 */
	public static String substringMaxLength(String string, int length)
	{
		return string.substring(0, Integer.min(length, string.length()));
	}

	public static <T> String someFromCollection(List<T> tokensAndLabels, int index, int limit,
			Function<T, String> toString) {
		StringBuilder sb = new StringBuilder();

		int from = Math.max(0, index - limit);
		int to = Math.min(index + limit, tokensAndLabels.size());

		for (int i = from; i < to; i++) {
			String s = toString.apply(tokensAndLabels.get(i));
			String str = (i == index ? "-->" : "") + "\t" + s;
			sb.append(str).append("\n");
		}
		return sb.toString();
	}

	public static boolean isTrimmedNotEmpty(String string) {
		return (string != null) && (string.trim().length() > 0);
	}

}
