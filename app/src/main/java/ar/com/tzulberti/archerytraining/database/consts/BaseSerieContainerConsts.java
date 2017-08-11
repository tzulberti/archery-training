package ar.com.tzulberti.archerytraining.database.consts;

/**
 * Common columns used by the container of series
 *
 * Created by tzulberti on 7/28/17.
 */
public abstract class BaseSerieContainerConsts extends BaseArcheryTrainingConsts {

    public static final String TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME = "tournament_constraint_id";

    public abstract String getTableName();
}
