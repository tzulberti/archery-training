package ar.com.tzulberti.archerytraining.model.base;

import java.io.Serializable;

/**
 * Common logic for playoff and tournament arrows
 *
 * Created by tzulberti on 6/3/17.
 */
public abstract class AbstractArrow extends BaseArcheryTrainingModel implements Serializable{

    public static final String SCORE_COLUMN_NAME = "score";
    public static final String X_POSITION_COLUMN_NAME = "x_position";
    public static final String Y_POSITION_COLUMN_NAME = "y_position";
    public static final String IS_X_COLUMN_NAME = "is_x";

    public abstract String getTableName();

    public abstract String getSerieColumnName();

    public long id;
    public float xPosition;
    public float yPosition;
    public int score;
    public boolean isX;
}
