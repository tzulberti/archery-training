package ar.com.tzulberti.archerytraining.database.consts;

/**
 * Created by tzulberti on 6/2/17.
 */

public class PlayoffSerieConsts extends BaseSerieConsts {

    public static final String TABLE_NAME = "playoff_serie";

    public static final String PLAYOFF_ID_COLUMN_NAME = "playoff_id";

    public static final String OPPONENT_TOTAL_SCORE_COLUMN_NAME_COLUMN_NAME = "opponent_total_score";
    public static final String USER_TOTAL_SCORE_COLUMN_NAME = "user_total_score";

    @Override
    public String getScoreColumnName() {
        return USER_TOTAL_SCORE_COLUMN_NAME;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}
