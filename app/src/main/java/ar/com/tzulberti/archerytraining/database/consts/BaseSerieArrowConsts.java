package ar.com.tzulberti.archerytraining.database.consts;

/**
 * Common class that has the arrow information for one serie
 *
 * Created by tzulberti on 6/26/17.
 */
public abstract class BaseSerieArrowConsts extends BaseArcheryTrainingConsts {

    public static final String SCORE_COLUMN_NAME = "score";
    public static final String X_POSITION_COLUMN_NAME = "x_position";
    public static final String Y_POSITION_COLUMN_NAME = "y_position";
    public static final String IS_X_COLUMN_NAME = "is_x";

    public abstract String getTableName();

    public abstract String getSerieColumnName();
}
