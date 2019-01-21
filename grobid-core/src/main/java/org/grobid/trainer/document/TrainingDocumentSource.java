package org.grobid.trainer.document;

import java.io.File;

import org.grobid.core.document.DocumentSource;

public class TrainingDocumentSource extends DocumentSource {


	private File teiFileSegmentation;
	private File teiFileHeader;
	private File teiFileHeaderAffiliation;
	private File teiFileHeaderAuthors;
	private File teiFileHeaderDate;
	private File teiFileHeaderReference;
	private File teiFileReferenceSegmenter;
	private File teiFileReferences;
	private File teiFileReferenceAuthors;
	private Boolean teiFileReferenceSegmenterExisted;
	private String teiDirectory;

	private String pathFullText;
	private int id;
	private File teiFileBody;

	public TrainingDocumentSource(File pdfFile, String teiDirectory, String pathFullText, int id) {
		super(pdfFile);
		this.teiDirectory = teiDirectory;

		String fileName = pdfFile.getName().replace(".pdf", "");
		this.teiFileSegmentation = new File(teiDirectory, fileName + ".training.segmentation.tei.xml");
		this.teiFileHeader = new File(teiDirectory, fileName + ".training.header.tei.xml");
		this.teiFileHeaderAffiliation = new File(teiDirectory, fileName + ".training.header.affiliation.tei.xml");
		this.teiFileHeaderAuthors = new File(teiDirectory, fileName + ".training.header.authors.tei.xml");
		this.teiFileHeaderDate = new File(teiDirectory, fileName + ".training.header.date.xml");
		this.teiFileHeaderReference = new File(teiDirectory, fileName + ".training.header.reference.xml");
		this.teiFileBody = new File(teiDirectory, fileName + ".training.fulltext.tei.xml");
		this.teiFileReferenceSegmenter = new File(teiDirectory, fileName + ".training.references.referenceSegmenter.tei.xml");
		this.teiFileReferences = new File(teiDirectory, fileName + ".training.references.tei.xml");
		this.teiFileReferenceAuthors = new File(teiDirectory, fileName + ".training.references.authors.tei.xml");
		this.teiFileReferenceSegmenterExisted = teiFileReferenceSegmenter.exists();
		this.pathFullText = pathFullText;
		this.id = id;
		//TODO Angela files kann man hier setzen
	}

	public File getTeiFileSegmentation() {
		return teiFileSegmentation;
	}

	public File getTeiFileHeader() {
		return teiFileHeader;
	}

	public File getTeiFileHeaderAffiliation() {
		return teiFileHeaderAffiliation;
	}

	public File getTeiFileHeaderAuthors() {
		return teiFileHeaderAuthors;
	}

	public File getTeiFileHeaderDate() {
		return teiFileHeaderDate;
	}

	public File getTeiFileHeaderReference() {
		return teiFileHeaderReference;
	}

	public File getTeiFileBody() {
		return teiFileBody;
	}

	public File getTeiFileReferenceSegmenter() {
		return teiFileReferenceSegmenter;
	}

	public File getTeiFileReferences() {
		return teiFileReferences;
	}

	public File getTeiFileReferenceAuthors() {
		return teiFileReferenceAuthors;
	}

	public Boolean getTeiFileReferenceSegmenterExisted() {
		return teiFileReferenceSegmenterExisted;
	}

	public String getTeiDirectory() {
		return teiDirectory;
	}

	public String getPathFullText() {
		return pathFullText;
	}

	public int getId() {
		return id;
	}

}
