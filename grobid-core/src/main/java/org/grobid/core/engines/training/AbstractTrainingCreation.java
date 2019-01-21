package org.grobid.core.engines.training;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import org.grobid.core.document.TrainingDocument;
import org.grobid.core.layout.LayoutToken;
import org.grobid.core.main.GrobidConstants;
import org.grobid.core.utilities.TokenLabelPair;

public abstract class AbstractTrainingCreation {

	protected TrainingDocument doc;

	public AbstractTrainingCreation(TrainingDocument doc) {
		this.doc = doc;
	}

	public void createTeiFile(StringBuilder buffer, TrainingStepMethod method)
			throws FileNotFoundException, IOException {
		TrainingSteps step = getTrainingStep();
		if (buffer != null && buffer.length() > 0) {
			File file = getFileFromDoc(doc);
			if (file.exists()) {
				TrainingSteps.print(step, TrainingStepMethod.ALREADY_EXISTING);
			} else {
				TrainingSteps.print(step, method);

				try (Writer writer = new OutputStreamWriter(new FileOutputStream(file, false), GrobidConstants.CHARSET)) {
					writeTeiToFile(writer, buffer.toString());
				}
			}
		} else
			TrainingSteps.printEmpty(step, method);
	}

	protected abstract void writeTeiToFile(Writer writer, String buffer) throws IOException;

	protected abstract TrainingSteps getTrainingStep();

	protected abstract File getFileFromDoc(TrainingDocument doc);


}
