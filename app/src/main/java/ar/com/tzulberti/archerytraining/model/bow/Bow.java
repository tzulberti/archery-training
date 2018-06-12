package ar.com.tzulberti.archerytraining.model.bow;

import java.io.Serializable;
import java.util.List;

import ar.com.tzulberti.archerytraining.model.base.BaseArcheryTrainingModel;

/**
 * Has the information one one of the bows that are shown on the database
 *
 * Created by tzulberti on 6/12/17.
 */
public class Bow extends BaseArcheryTrainingModel implements Serializable {

    public static final String TABLE_NAME = "bow";

    public static final String NAME_COLUMN_NAME = "name";

    public long id;
    public String name;

    public List<SightDistanceValue> sightDistanceValues;
}
