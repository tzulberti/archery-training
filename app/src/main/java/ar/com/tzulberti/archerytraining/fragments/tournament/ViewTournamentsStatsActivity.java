package ar.com.tzulberti.archerytraining.fragments.tournament;

import ar.com.tzulberti.archerytraining.MainActivity;
import ar.com.tzulberti.archerytraining.dao.BaseArrowSeriesDAO;
import ar.com.tzulberti.archerytraining.fragments.common.AbstractArrowSeriesStatsActivity;

/**
 * Created by tzulberti on 6/26/17.
 */

public class ViewTournamentsStatsActivity extends AbstractArrowSeriesStatsActivity {

    protected BaseArrowSeriesDAO setBaseArrowSeriesDAO() {
        return this.tournamentDAO;
    }
}
