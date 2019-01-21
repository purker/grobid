package org.grobid.core.engines;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Duration
{
	private static Map<DurationEnum, DurationItem> startTimes = new HashMap<>();
	private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	public static void addStart(String enumString)
	{
		DurationEnum item = DurationEnum.valueOf(enumString);
		addStart(item);
	}

	public static DurationItem addEnd(String enumString)
	{
		DurationEnum elem = DurationEnum.valueOf(enumString);
		return addEnd(elem);
	}

	public static DurationItem getDurationItemByString(String enumString)
	{
		DurationEnum elem = DurationEnum.valueOf(enumString);
		DurationItem durationItem = startTimes.get(elem);

		return durationItem;
	}

	public static String getDurationFormatted(DurationItem durationItem)
	{
		return "" + Math.round(durationItem.getDuration() / 1000F) + "s";
	}

	public static void addStart(DurationEnum elem)
	{
		long start = System.currentTimeMillis();

		System.out.println(sdf.format(new Date(start)) + " | " + elem + " starting");
		startTimes.put(elem, new DurationItem(start));
	}

	public static DurationItem addEnd(DurationEnum elem)
	{
		long end = System.currentTimeMillis();
		DurationItem durationItem = startTimes.get(elem);

		durationItem.setEnd(end);
		durationItem.setDuration(durationItem.getEnd() - durationItem.getStart());

		System.out.println(sdf.format(new Date(end)) + " | " + elem + " finished (" + getDurationFormatted(durationItem) + ")");

		return durationItem;
	}

	public static String addEndGetDuration(String string)
	{
		DurationItem item = addEnd(string);
		return getDurationFormatted(item);
	}
}
