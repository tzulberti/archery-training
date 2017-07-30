package ar.com.tzulberti.archerytraining.activities.playoff;


import ar.com.tzulberti.archerytraining.dao.BaseArrowSeriesDAO;
import ar.com.tzulberti.archerytraining.activities.common.AbstractContainersStatsActivity;

/**
 * Created by tzulberti on 6/26/17.
 */

public class ViewPlayoffStatsActivity extends AbstractContainersStatsActivity {


    protected BaseArrowSeriesDAO setBaseArrowSeriesDAO() {
        return this.playoffDAO;
    }
}
