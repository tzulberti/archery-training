package ar.com.tzulberti.archerytraining.database.consts;

/**
 * Created by tzulberti on 7/28/17.
 */

public abstract class BaseSerieContainerConsts {

    public static final String ID_COLUMN_NAME = "id";
    public static final String TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME = "tournament_constraint_id";

    public abstract String getTableName();
}
