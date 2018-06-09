package ar.com.tzulberti.archerytraining.model.playoff;


import java.io.Serializable;

import ar.com.tzulberti.archerytraining.model.base.AbstractArrow;

/**
 * For each arrow the archer has shoot during a playoff, it has the information
 *
 * Created by tzulberti on 6/3/17.
 */
public class PlayoffSerieArrow extends AbstractArrow implements Serializable {

    public static final String TABLE_NAME = "playoff_serie_arrow";

    public static final String SERIE_ID_COLUMN_NAME = "playoff_serie_id";
    public static final String PLAYOFF_ID_COLUMN_NAME = "playoff_id";

    public PlayoffSerie serie;
    public Playoff playoff;

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getSerieColumnName() {return SERIE_ID_COLUMN_NAME;}
}
