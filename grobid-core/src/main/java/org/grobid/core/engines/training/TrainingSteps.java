package org.grobid.core.engines.training;

import org.apache.commons.lang3.StringUtils;
import org.grobid.core.IGrobidModel;
import org.grobid.core.GrobidModels;

public enum TrainingSteps {

	SEGMENTATION("segmentation", 0, GrobidModels.SEGMENTATION),
		BODY("body", 1, GrobidModels.FULLTEXT),
			FIGURE("figure", 2, GrobidModels.FIGURE),
			TABLE("table", 2, GrobidModels.TABLE),
		HEADER("header", 1, GrobidModels.HEADER),
			HEADER_AFFILIATION("header.affiliation", 2, GrobidModels.AFFIILIATON_ADDRESS),
			HEADER_AUTHORS("header.authors", 2, GrobidModels.NAMES_HEADER),
			HEADER_DATE("header.date", 2, GrobidModels.DATE),
			HEADER_REFERENCE("header.reference", 2, GrobidModels.CITATION),
		REFERENCESEGMENTER("referenceSegmenter", 1, GrobidModels.REFERENCE_SEGMENTER),
			REFERENCES("references", 2, GrobidModels.CITATION),
				REFERENCES_AUTHORS("references.authors", 3, GrobidModels.NAMES_CITATION);

	private String name;
	private int indentation;
	private IGrobidModel model;

	private TrainingSteps(String name, int indentation, IGrobidModel model) {
		this.name = name;
		this.indentation = indentation;
		this.model = model;
	}

	public String getName() {
		return name;
	}
	
	public int getIndentation() {
		return indentation;
	}
	
	public IGrobidModel getModel() {
		return model;
	}

	public static void print(TrainingSteps step, TrainingStepMethod method) {
		System.out.println(StringUtils.repeat(" ", step.indentation * 2) + step.name + "\t" + method);
	}

	public static void printEmpty(TrainingSteps step, TrainingStepMethod method) {
		System.out.println(StringUtils.repeat(" ", step.indentation * 2) + step.name + "\t EMPTY (" + method + ")");
	}

	public static void print(TrainingSteps step, FileType type) {
		//System.out.println(StringUtils.repeat(" ", step.indentation * 2) + step.name + ": write " + type);
	}

	
}
