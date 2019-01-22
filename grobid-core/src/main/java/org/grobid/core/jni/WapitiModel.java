package org.grobid.core.jni;

import fr.limsi.wapiti.SWIGTYPE_p_mdl_t;
import fr.limsi.wapiti.Wapiti;
import org.grobid.core.IGrobidModel;
import org.grobid.core.GrobidModels;
import org.grobid.core.exceptions.GrobidException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * User: zholudev
 * Date: 3/17/14
 * 
 * (https://wapiti.limsi.fr/manual.html)
 * wapiti mode [options] [input] [output]
 * 
 * mode=["train", "label", or "dump"]
 * 
 * options:
 * -m | --model <file>
 *            Specify a model file to load and to train again.
 * -p | --post
              Use posterior decoding instead of the classical  Viterbi  decod-
              ing. This generally produce better results at the cost of slower
              decoding.  This  also  allow  to  output  normalized  score  for
              sequences and labels.
 */
public class WapitiModel {
	public static final Logger LOGGER = LoggerFactory.getLogger(WapitiModel.class);

	private SWIGTYPE_p_mdl_t model;
	private File modelFile;

	public WapitiModel(File modelFile) {
		this.modelFile = modelFile;
		init();
	}

	public WapitiModel(IGrobidModel grobidModel) {
		modelFile = new File(grobidModel.getModelPath());
		init();
	}

	private synchronized void init() {
		if (model != null) {
			return;
		}
		if (!modelFile.exists() || modelFile.isDirectory()) {
			throw new GrobidException("Model file does not exists or a directory: " + modelFile.getAbsolutePath());
		}
		LOGGER.info("Loading model: " + modelFile + " (size: " + modelFile.length() + ")");
		model = WapitiWrapper.getModel(modelFile);
	}

	public String label(String data) {
		if (model == null) {
			LOGGER.warn("Model has been already closed, reopening: " + modelFile.getAbsolutePath());
			init();
		}
		String label = WapitiWrapper.label(model, data).trim();
		//TODO: VZ: Grobid currently expects tabs as separators whereas wapiti uses spaces for separating features.
		// for now it is safer to replace, although it does not look nice
		label = label.replaceAll(" ", "\t");
		return label;
	}

	public synchronized void close() {
		if (model != null) {
			Wapiti.freeModel(model);
			model = null;
		}
	}

	public static void train(File template, File trainingData, File outputModel) {
		trainWithArguments(template, trainingData, outputModel, "");
	}

	public static void trainWithArguments(File template, File trainingData, File outputModel, String params) {
		String args = String.format("train " + params + " -p %s %s %s", template.getAbsolutePath(), trainingData.getAbsolutePath(), outputModel.getAbsolutePath());
		System.out.println("Training with equivalent command line: \n" + "wapiti " + args);
		Wapiti.runWapiti(args);
	}

	public static void dump(File model, File outputFile) {
		String args = String.format("dump %s %s", model.getAbsolutePath(), outputFile.getAbsolutePath());
		Wapiti.runWapiti(args);
	}
}
