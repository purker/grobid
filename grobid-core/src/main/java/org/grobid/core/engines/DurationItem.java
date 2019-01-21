package org.grobid.core.engines;

public class DurationItem
{
	private long start;
	private long end;
	private long duration;

	public DurationItem(long start)
	{
		this.start = start;
	}

	public long getStart()
	{
		return start;
	}

	public void setStart(long start)
	{
		this.start = start;
	}

	public long getEnd()
	{
		return end;
	}

	public void setEnd(long end)
	{
		this.end = end;
	}

	public long getDuration()
	{
		return duration;
	}

	public void setDuration(long duration)
	{
		this.duration = duration;
	}

}
