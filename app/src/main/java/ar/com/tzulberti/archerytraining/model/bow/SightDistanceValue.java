package ar.com.tzulberti.archerytraining.model.bow;

import java.io.Serializable;

import ar.com.tzulberti.archerytraining.model.base.BaseArcheryTrainingModel;

/**
 * For given bow, has the sight position for each distance
 *
 * Created by tzulberti on 6/12/17.
 */
public class SightDistanceValue extends BaseArcheryTrainingModel implements Serializable {

    public static final String TABLE_NAME = "sight_distance_values";


    public static final String BOW_ID_COLUMN_NAME = "bow_id";
    public static final String DISTANCE_COLUMN_NAME = "distance";
    public static final String SIGHT_VALUE_COLUMN_NAME = "sight_value";

    public long id;
    public int distance;
    public float sightValue;

    public Bow bow;
}
