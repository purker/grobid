package org.grobid.core.engines.training;

public enum TrainingStepMethod {

	PDF, //tei file will be created with data extracted from pdf file
	TEI, //tei file will be created with date extracted from the parent tei file
	ALREADY_EXISTING; //no tei file to create, because it already exists

	
}
