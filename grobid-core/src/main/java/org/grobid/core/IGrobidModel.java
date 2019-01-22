package org.grobid.core;

/**
 * Created by lfoppiano on 19/08/16.
 */
public interface IGrobidModel {
    String getFolderName();

    String getModelPath();

    String getModelName();

    String getTemplateName();

	boolean isCorpusSplitted();

    String toString();

}
