package org.grobid.trainer;

import java.io.File;

import org.grobid.core.GrobidModel;

/**
 * @author Patrice Lopez
 */
public interface Trainer {

    int createCRFPPData(File corpusPath, File outputFile);

	int createCRFPPData(File corpusPath, File outputTrainingFile, File outputEvalFile, double splitRatio);

    void train(); //creates model from scratch

	void train(boolean trainExistingModel); //decide to train with existing model

    /**
     *
     * @return a report
     */
    String evaluate();

	String splitTrainEvaluate(Double split);

    GrobidModel getModel();

}