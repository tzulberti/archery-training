package ar.com.tzulberti.archerytraining.database.consts;

/**
 * Created by tzulberti on 6/26/17.
 */

public abstract class BaseSerieConsts {

    public static final String ID_COLUMN_NAME = "id";
    public static final String SERIE_INDEX_COLUMN_NAME = "serie_index";

    public abstract String getScoreColumnName();

    public abstract String getTableName();

    /**
     * All the series belongs to a container (tournament, etc...) so this
     * returns the column that references that table
     * @return
     */
    public abstract String getContainerIdColumnName();
}
