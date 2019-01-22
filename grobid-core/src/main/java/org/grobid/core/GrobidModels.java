package org.grobid.core;

import org.apache.commons.lang3.StringUtils;
import org.grobid.core.utilities.GrobidProperties;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.grobid.core.engines.EngineParsers.LOGGER;

/**
 * This enum class acts as a registry for all Grobid models.
 *
 * @author Patrice Lopez
 */
public enum GrobidModels implements IGrobidModel {
    AFFIILIATON_ADDRESS("affiliation-address"),
    SEGMENTATION("segmentation"),
	CITATION("citation", false),
    REFERENCE_SEGMENTER("reference-segmenter"),
	DATE("date", false),
    DICTIONARIES_LEXICAL_ENTRIES("dictionaries-lexical-entries"),
    DICTIONARIES_SENSE("dictionaries-sense"),
    EBOOK("ebook"),
    ENTITIES_CHEMISTRY("entities/chemistry"),
    //	ENTITIES_CHEMISTRY("chemistry"),
    FULLTEXT("fulltext"),
    SHORTTEXT("shorttext"),
    FIGURE("figure"),
    TABLE("table"),
    HEADER("header"),
    NAMES_CITATION("name/citation"),
	NAMES_HEADER("name/header", false),
    PATENT_PATENT("patent/patent"),
    PATENT_NPL("patent/npl"),
    PATENT_ALL("patent/all"),
    PATENT_STRUCTURE("patent/structure"),
    PATENT_EDIT("patent/edit"),
    ENTITIES_NER("ner"),
    ENTITIES_NERFR("nerfr"),
    ENTITIES_NERSense("nersense"),
    //	ENTITIES_BIOTECH("entities/biotech"),
    ENTITIES_BIOTECH("bio"),
    ASTRO("astro");

	/**
     * Absolute path to the model.
     */
    private String modelPath;
    private String folderName;
	private boolean splittedCorpus = true; //true = contains tei and raw subdirectory, false = tei files in corpus directory

    private static final ConcurrentMap<String, IGrobidModel> models = new ConcurrentHashMap<>();

    GrobidModels(String folderName) {
        this.folderName = folderName;
        File path = GrobidProperties.getModelPath(this);
        if (!path.exists()) {
            // to be reviewed
            /*System.err.println("Warning: The file path to the "
                    + this.name() + " CRF model is invalid: "
					+ path.getAbsolutePath());*/
        }
        modelPath = path.getAbsolutePath();
    }

	GrobidModels(String folderName, boolean splittedCorpus) {
		this(folderName);
		this.splittedCorpus = splittedCorpus;
	}

    public String getFolderName() {
        return folderName;
    }

    public String getModelPath() {
        return modelPath;
    }

    public String getModelName() {
        return folderName.replaceAll("/", "-");
    }

    public String getTemplateName() {
        return StringUtils.substringBefore(folderName, "/") + ".template";
    }

    @Override
    public String toString() {
        return folderName;
    }

    public static IGrobidModel modelFor(final String name) {
        if (models.isEmpty()) {
            for (IGrobidModel model : values())
                models.putIfAbsent(model.getFolderName(), model);
        }

        models.putIfAbsent(name.toString(/* null-check */), new GrobidModel(name));
        return models.get(name);
    }

    public String getName() {
        return name();
    }

	public boolean isSplittedCorpus() {
		return splittedCorpus;
	}

	@Override
	public boolean isCorpusSplitted() {
		return splittedCorpus;
	}

}
