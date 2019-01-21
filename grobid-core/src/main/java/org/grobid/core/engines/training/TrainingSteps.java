package org.grobid.core.engines.training;

import org.apache.commons.lang3.StringUtils;

public enum TrainingSteps {

	SEGMENTATION("segmentation", 0),
		BODY("body", 1),
			FIGURE("figure", 2),
			TABLE("table", 2),
		HEADER("header", 1),
			HEADER_AFFILIATION("header.affiliation", 2),
			HEADER_AUTHORS("header.authors", 2),
			HEADER_DATE("header.date", 2),
			HEADER_REFERENCE("header.reference", 2),
		REFERENCESEGMENTER("referenceSegmenter", 1),
			REFERENCES("references", 2),
				REFERENCES_AUTHORS("references.authors", 3);

	private String name;
	private int indentation;

	private TrainingSteps(String name, int indentation) {
		this.name = name;
		this.indentation=indentation;
	}

	public String getName() {
		return name;
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
