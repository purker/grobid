package org.grobid.core.utilities;

import org.grobid.core.layout.LayoutToken;

public class LayoutTokenLabelPair extends Pair<LayoutToken, String> {

	public LayoutTokenLabelPair(LayoutToken token, String label) {
		super(token, label);
	}

	public LayoutToken getLayoutToken() {
		return a;
	}

	public String getLabel() {
		return b;
	}
}
