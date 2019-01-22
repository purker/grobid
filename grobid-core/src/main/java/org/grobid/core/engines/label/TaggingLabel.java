package org.grobid.core.engines.label;

import org.grobid.core.IGrobidModel;
import org.grobid.core.engines.counters.Countable;

/**
 * Created by lfoppiano on 25/11/16.
 */
public interface TaggingLabel extends Countable {

    IGrobidModel getGrobidModel();

    String getLabel();
}
