package ar.com.tzulberti.archerytraining.database.consts;

/**
 * Created by tzulberti on 4/25/17.
 */

public class TournamentConsts extends BaseSerieContainerConsts {

    public static final String TABLE_NAME = "tournaments";

    public static final String NAME_COLUMN_NAME = "name";
    public static final String DATETIME_COLUMN_NAME = "datetime";
    public static final String IS_TOURNAMENT_DATA_COLUMN_NAME = "is_tournament_data";
    public static final String TOTAL_SCORE_COLUMN_NAME = "total_score";

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

}
