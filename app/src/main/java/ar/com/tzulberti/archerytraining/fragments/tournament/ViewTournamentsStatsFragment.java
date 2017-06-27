package ar.com.tzulberti.archerytraining.fragments.tournament;

import ar.com.tzulberti.archerytraining.MainActivity;
import ar.com.tzulberti.archerytraining.fragments.common.AbstractArrowSeriesStatsFragment;

/**
 * Created by tzulberti on 6/26/17.
 */

public class ViewTournamentsStatsFragment extends AbstractArrowSeriesStatsFragment {
    @Override
    protected void setBaseArrowSeriesDAO() {
        MainActivity activity = (MainActivity) getActivity();
        this.baseArrowSeriesDAO = activity.getTournamentDAO();
    }
}
