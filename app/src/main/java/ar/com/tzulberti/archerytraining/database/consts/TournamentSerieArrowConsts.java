package ar.com.tzulberti.archerytraining.database.consts;

/**
 * The schema information of the table that has the arrow information for one serie
 * for a tournament
 *
 * Created by tzulberti on 4/25/17.
 */
public class TournamentSerieArrowConsts extends BaseSerieArrowConsts {

    public static final String TABLE_NAME = "tournament_serie_arrow";

    public static final String SERIE_INDEX_COLUMN_NAME = "tournament_serie_index";
    public static final String TOURNAMENT_ID_COLUMN_NAME = "tournament_id";

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getSerieColumnName() {return SERIE_INDEX_COLUMN_NAME;}
}
