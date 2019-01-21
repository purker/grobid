package org.grobid.core.engines.featurefile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import shadedwipo.org.apache.commons.lang3.StringUtils;

public class FeatureRow {
	private static final String separator = "\t";

	private String token;
	private Collection<String> features;
	private String label;

	public FeatureRow(String row) {
		String[] s = row.split("\t");
		this.token = s[0].trim();
		this.features = new ArrayList<String>(Arrays.asList(Arrays.copyOfRange(s, 1, s.length - 1)));
		this.label = s[s.length - 1];

	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Collection<String> getFeatures() {
		return features;
	}

	public void setFeatures(Collection<String> features) {
		this.features = features;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return token + separator + StringUtils.join(features, separator) + separator + label;
	}
}
