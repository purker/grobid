package org.grobid.core;

import static org.grobid.core.engines.EngineParsers.LOGGER;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.grobid.core.utilities.GrobidProperties;

public class GrobidModel implements IGrobidModel {
	private final String name;
	private boolean corpusSplitted = true;

	public GrobidModel(String name) {
		this.name = name;
	}

	public GrobidModel(String name, boolean corpusSplitted) {
		this(name);
		this.corpusSplitted = corpusSplitted;
	}

	@Override
	public String getFolderName() {
		return name;
	}

	@Override
	public String getModelPath() {
		File path = GrobidProperties.getModelPath(this);
		if (!path.exists()) {
			LOGGER.warn("Warning: The file path to the " + name + " model is invalid: " + path.getAbsolutePath());
		}
		return path.getAbsolutePath();
	}

	@Override
	public String getModelName() {
		return getFolderName().replaceAll("/", "-");
	}

	@Override
	public String getTemplateName() {
		return StringUtils.substringBefore(getFolderName(), "/") + ".template";
	}

	@Override
	public boolean isCorpusSplitted() {
		return corpusSplitted;
	}
}
