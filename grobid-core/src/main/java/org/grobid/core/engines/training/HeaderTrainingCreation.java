package org.grobid.core.engines.training;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import org.grobid.core.document.TrainingDocument;

public class HeaderTrainingCreation extends AbstractTrainingCreation {

	public HeaderTrainingCreation(TrainingDocument doc) {
		super(doc);
	}

	protected void writeTeiToFile(Writer writer, String buffer) throws IOException {
		writer.write("<?xml version=\"1.0\" ?>\n<tei>\n\t<teiHeader>\n\t\t<fileDesc xml:id=\"" + doc.getPdfFileName().replace(".pdf", "") + "\"/>\n\t</teiHeader>\n\t<text");

		if (doc.getLanguage() != null) {
			writer.write(" xml:lang=\"" + doc.getLanguage() + "\"");
		}
		writer.write(">\n\t\t<front>\n");

		writer.write(buffer.toString());
		writer.write("\n\t\t</front>\n\t</text>\n</tei>\n");
	}

	@Override
	protected TrainingSteps getTrainingStep() {
		return TrainingSteps.HEADER;
	}

	@Override
	protected File getFileFromDoc(TrainingDocument doc) {
		return doc.getDocumentSource().getTeiFileHeader();
	}


}
