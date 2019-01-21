package org.grobid.core.engines.training;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.grobid.core.document.TrainingDocument;
import org.grobid.core.exceptions.GrobidException;
import org.grobid.core.factory.GrobidFactory;
import org.grobid.core.layout.LayoutToken;
import org.grobid.core.utilities.SizedStack;
import org.grobid.core.utilities.StringUtil;
import org.grobid.core.utilities.TextUtilities;
import org.grobid.core.utilities.TokenLabelPair;

public class HeaderAuthorsTrainingCreation extends AbstractTrainingCreation {

	public HeaderAuthorsTrainingCreation(TrainingDocument doc) {
		super(doc);
	}

	@Override
	protected void writeTeiToFile(Writer writer, String buffer) throws IOException {
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		writer.write("\n<tei xmlns=\"http://www.tei-c.org/ns/1.0\"" + " xmlns:xlink=\"http://www.w3.org/1999/xlink\" " + "xmlns:mml=\"http://www.w3.org/1998/Math/MathML\">");
		writer.write("\n\t<teiHeader>\n\t\t<fileDesc>\n\t\t\t<sourceDesc>");
		writer.write("\n\t\t\t\t<biblStruct>\n\t\t\t\t\t<analytic>\n\n\t\t\t\t\t\t<author>");
		writer.write("\n\t\t\t\t\t\t\t<persName>\n");

		writer.write(buffer.toString());

		writer.write("\t\t\t\t\t\t\t</persName>\n");
		writer.write("\t\t\t\t\t\t</author>\n\n\t\t\t\t\t</analytic>");
		writer.write("\n\t\t\t\t</biblStruct>\n\t\t\t</sourceDesc>\n\t\t</fileDesc>");
		writer.write("\n\t</teiHeader>\n</tei>\n");
	}

	@Override
	protected TrainingSteps getTrainingStep() {
		return TrainingSteps.HEADER_AUTHORS;
	}

	@Override
	protected File getFileFromDoc(TrainingDocument doc) {
		return doc.getDocumentSource().getTeiFileHeaderAuthors();
	}

}
