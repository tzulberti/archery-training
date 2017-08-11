package ar.com.tzulberti.archerytraining.database.consts;

/**
 * Has all the information of one playoff
 *
 * Created by tzulberti on 6/2/17.
 */
public class PlayoffConsts extends BaseSerieContainerConsts {

    public static final String TABLE_NAME = "playoff";


    public static final String NAME_COLUMN_NAME = "name";
    public static final String DATETIME_COLUMN_NAME = "datetime";
    public static final String USER_PLAYOFF_SCORE_COLUMN_NAME = "user_playoff_score";
    public static final String OPPONENT_PLAYOFF_SCORE_COLUMN_NAME = "opponent_playoff_score";

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}
