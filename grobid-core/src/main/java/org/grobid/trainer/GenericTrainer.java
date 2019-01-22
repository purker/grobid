package org.grobid.trainer;

import java.io.File;

import org.grobid.core.IGrobidModel;

/**
 * User: zholudev
 * Date: 3/20/14
 */
public interface GenericTrainer {
    void train(File template, File trainingData, File outputModel, int numThreads, IGrobidModel model);
	void trainExistingModel(File template, File trainingData, File inputModel, File outputModel, int numThreads, IGrobidModel model);
    String getName();
	public void setEpsilon(double epsilon);
	public void setWindow(int window);
	public double getEpsilon();
	public int getWindow();
	public int getNbMaxIterations();
	public void setNbMaxIterations(int iterations);
}
