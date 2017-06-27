package ar.com.tzulberti.archerytraining.database.consts;

/**
 * Created by tzulberti on 5/16/17.
 */

public class TournamentSerieConsts extends  BaseSerieConsts {

    public static final String TABLE_NAME = "tournament_series";

    public static final String TOURNAMENT_ID_COLUMN_NAME = "tournament_id";
    public static final String TOTAL_SCORE_COLUMN_NAME = "total_score";

    @Override
    public String getScoreColumnName() {
        return TOTAL_SCORE_COLUMN_NAME;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}
