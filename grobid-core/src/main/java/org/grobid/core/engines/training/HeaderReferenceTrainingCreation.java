package org.grobid.core.engines.training;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import org.grobid.core.document.TrainingDocument;

public class HeaderReferenceTrainingCreation extends AbstractTrainingCreation {

	public HeaderReferenceTrainingCreation(TrainingDocument doc) {
		super(doc);
	}

	@Override
	protected void writeTeiToFile(Writer writer, String buffer) throws IOException {
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		writer.write("<citations>\n");

		writer.write(buffer.toString());

		writer.write("</citations>\n");
	}

	@Override
	protected TrainingSteps getTrainingStep() {
		return TrainingSteps.HEADER_REFERENCE;
	}

	@Override
	protected File getFileFromDoc(TrainingDocument doc) {
		return doc.getDocumentSource().getTeiFileHeaderReference();
	}

}
