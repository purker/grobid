package org.grobid.core.layout;

import java.util.ArrayList;
import java.util.List;

public class LayoutTokenLine {
	private List<LayoutToken> tokens;
	private String text;
	private List<Integer> tokenCountOnIndex = new ArrayList<Integer>();

	public LayoutTokenLine(List<LayoutToken> line) {
		StringBuffer sb = new StringBuffer();
		int tokenCount = 0;

		for (LayoutToken layoutToken : line) {
			String text = layoutToken.getText();
			sb.append(text);

			for (int i = 0; i < text.length(); i++) {
				tokenCountOnIndex.add(tokenCount);
			}
			tokenCount++;
		}

		this.text = sb.toString();
		this.tokens = line;
	}

	public List<LayoutToken> getTokens() {
		return tokens;
	}

	public void setTokens(List<LayoutToken> tokens) {
		this.tokens = tokens;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<Integer> getTokenCountOnIndex() {
		return tokenCountOnIndex;
	}

	public void setTokenCountOnIndex(List<Integer> tokenCountOnIndex) {
		this.tokenCountOnIndex = tokenCountOnIndex;
	}

	@Override
	public String toString() {
		return text;
	}
}