package org.grobid.core.document;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.grobid.core.layout.LayoutToken;
import org.grobid.trainer.document.TrainingDocumentSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for representing, processing and exchanging a document item.
 *
 * @author Patrice Lopez
 */

public class TrainingDocument extends Document {
	protected static final Logger LOGGER = LoggerFactory.getLogger(TrainingDocument.class);

	protected File errorPath = null;

	public TrainingDocument(TrainingDocumentSource documentSource, String errorPath) {
		super((DocumentSource) documentSource);
		this.errorPath = new File(errorPath);
	}

	@Override
	public TrainingDocumentSource getDocumentSource() {
		return (TrainingDocumentSource) super.getDocumentSource();
	}

	public File getErrorPath() {
		return errorPath;
	}


}