package org.grobid.core.utilities;

public class TokenLabelPair extends Pair<String, String> {

	public TokenLabelPair(String token, String label) {
		super(token, label);
	}

	public String getToken() {
		return a;
	}

	public String getLabel() {
		return b;
	}
}
