package ar.com.tzulberti.archerytraining.database.consts;

/**
 * Created by tzulberti on 6/2/17.
 */

public class PlayoffSerieArrowConsts  extends  BaseSerieArrowConsts {

    public static final String TABLE_NAME = "playoff_serie_arrow";

    public static final String SERIE_ID_COLUMN_NAME = "playoff_serie_id";
    public static final String PLAYOFF_ID_COLUMN_NAME = "playoff_id";

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getSerieColumnName() {return SERIE_ID_COLUMN_NAME;}
}
