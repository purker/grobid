package org.grobid.core.engines.featurefile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class FeatureFile implements Iterable<FeatureRow>, Iterator<FeatureRow> {
	private List<FeatureRow> rows = new ArrayList<>();
	private StringTokenizer lineTokenizer;
	private FeatureRow currentRow;
	private int currentRowIndex;

	public FeatureFile(String s) {
		this.lineTokenizer = new StringTokenizer(s, "\n\r");
	}

	public List<FeatureRow> getRows() {
		return rows;
	}

	public void setRows(List<FeatureRow> rows) {
		this.rows = rows;
	}

	@Override
	public boolean hasNext() {
		return lineTokenizer.hasMoreTokens();
	}

	@Override
	public FeatureRow next() {
		String row = lineTokenizer.nextToken();
		currentRowIndex++;
		currentRow = new FeatureRow(row);
		rows.add(currentRow);
		
		return currentRow;
	}

	@Override
	public Iterator<FeatureRow> iterator() {
		return this;
	}

	public FeatureRow getCurrentRow() {
		return currentRow;
	}

	public void setCurrentRow(FeatureRow currentRow) {
		this.currentRow = currentRow;
	}

	public int getCurrentIndex() {
		return currentRowIndex;
	}

}
