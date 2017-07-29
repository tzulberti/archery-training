package ar.com.tzulberti.archerytraining.fragments.playoff;


import ar.com.tzulberti.archerytraining.fragments.common.AbstractArrowSeriesStatsActivity;

/**
 * Created by tzulberti on 6/26/17.
 */

public class ViewPlayoffStatsActivity extends AbstractArrowSeriesStatsActivity {


    protected void setBaseArrowSeriesDAO() {
        this.baseArrowSeriesDAO = this.playoffDAO;
    }
}
