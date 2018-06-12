package ar.com.tzulberti.archerytraining.model.tournament;

import java.io.Serializable;
import java.util.List;

import ar.com.tzulberti.archerytraining.model.base.AbstractArrow;

/**
 * For each arrow shooted on the tournament it has its information
 *
 * Created by tzulberti on 5/22/17.
 */
public class TournamentSerieArrow extends AbstractArrow implements Serializable{

    public static final String TABLE_NAME = "tournament_serie_arrow";

    public static final String SERIE_INDEX_COLUMN_NAME = "tournament_serie_index";
    public static final String TOURNAMENT_ID_COLUMN_NAME = "tournament_id";

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getSerieColumnName() {return SERIE_INDEX_COLUMN_NAME;}

    public TournamentSerie serie;
    public Tournament tournament;

}
