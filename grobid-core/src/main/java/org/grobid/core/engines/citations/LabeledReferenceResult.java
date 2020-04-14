package org.grobid.core.engines.citations;

import org.grobid.core.layout.BoundingBox;
import org.grobid.core.layout.LayoutToken;
import org.grobid.core.layout.LayoutTokenLine;
import org.grobid.core.utilities.BoundingBoxCalculator;

import java.util.List;

/**
 * User: zholudev
 * Date: 4/15/14
 */
public class LabeledReferenceResult {
    private String label = null;
    private String referenceText;
	private String features; // optionally the vector of features corresponding to the token referenceText
    private List<BoundingBox> coordinates = null;
    private List<LayoutToken> tokens = null;  

//    public LabeledReferenceResult(String label, String referenceText) {
//        this.label = label;
//        this.referenceText = referenceText;
//    }

    public LabeledReferenceResult(String referenceText) {
        this.referenceText = referenceText;
    }

    public LabeledReferenceResult(String label, String referenceText, 
            List<LayoutToken> referenceTokens, String features, 
            List<BoundingBox> coordinates) {
        this.label = label;
        this.referenceText = referenceText;
		this.tokens = referenceTokens;
        this.features = features;
        this.coordinates = coordinates;
    }

    public String getLabel() {
        return label;
    }

    public String getReferenceText() {
        return referenceText;
    }
	
    public String getFeatures() {
        return features;
    }

    public List<BoundingBox> getCoordinates() {
        return coordinates;
    }

    public List<LayoutToken> getTokens() {
        return this.tokens;
    }

    @Override
    public String toString() {
        return "** " + (label == null ? "" : label) + " ** " + referenceText;
    }

	public void addTokens(LayoutTokenLine additional) {
		this.tokens.addAll(additional.getTokens());
		this.referenceText = referenceText + additional.getText();
		this.coordinates = BoundingBoxCalculator.calculate(tokens);
	}
}
