package org.grobid.core.engines.training;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.grobid.core.document.TrainingDocument;
import org.grobid.core.engines.AbstractParser;
import org.grobid.core.engines.AffiliationAddressParser;
import org.grobid.core.factory.GrobidFactory;
import org.grobid.core.layout.LayoutToken;
import org.grobid.core.utilities.TokenLabelPair;

public class HeaderAffiliationTrainingCreation extends AbstractTrainingCreation {

	public HeaderAffiliationTrainingCreation(TrainingDocument doc) {
		super(doc);
	}

	protected File getFileFromDoc(TrainingDocument doc) {
		return doc.getDocumentSource().getTeiFileHeaderAffiliation();
	}

	@Override
	protected TrainingSteps getTrainingStep() {
		return TrainingSteps.HEADER_AFFILIATION;
	}

	@Override
	protected void writeTeiToFile(Writer writer, String buffer) throws IOException {
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		writer.write("\n<tei xmlns=\"http://www.tei-c.org/ns/1.0\"" + " xmlns:xlink=\"http://www.w3.org/1999/xlink\" " + "xmlns:mml=\"http://www.w3.org/1998/Math/MathML\">");
		writer.write("\n\t<teiHeader>\n\t\t<fileDesc>\n\t\t\t<sourceDesc>");
		writer.write("\n\t\t\t\t<biblStruct>\n\t\t\t\t\t<analytic>\n\t\t\t\t\t\t<author>\n\n");

		writer.write(buffer);

		writer.write("\n\t\t\t\t\t\t</author>\n\t\t\t\t\t</analytic>");
		writer.write("\n\t\t\t\t</biblStruct>\n\t\t\t</sourceDesc>\n\t\t</fileDesc>");
		writer.write("\n\t</teiHeader>\n</tei>\n");
	}

}
