package org.grobid.trainer.document;

import java.io.File;

import org.grobid.core.document.DocumentSource;

public class TrainingDocumentSource extends DocumentSource {
	private File teiFileSegmentation;
	private File teiFileReferenceSegmenter;

	public TrainingDocumentSource(File inputFile) {
		super(inputFile);
	}

	public File getTeiFileSegmentation() {
		return teiFileSegmentation;
	}

	public void setTeiFileSegmentation(File teiFile) {
		this.teiFileSegmentation = teiFile;
	}

	public File getTeiFileReferenceSegmenter() {
		return teiFileReferenceSegmenter;
	}

	public void setTeiFileReferenceSegmenter(File teiFileReferenceSegmenter) {
		this.teiFileReferenceSegmenter = teiFileReferenceSegmenter;
	}

}
