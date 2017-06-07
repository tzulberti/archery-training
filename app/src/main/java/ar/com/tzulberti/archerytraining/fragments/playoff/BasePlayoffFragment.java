package ar.com.tzulberti.archerytraining.fragments.playoff;

import ar.com.tzulberti.archerytraining.MainActivity;
import ar.com.tzulberti.archerytraining.dao.PlayoffDAO;
import ar.com.tzulberti.archerytraining.dao.SerieDataDAO;
import ar.com.tzulberti.archerytraining.fragments.BaseClickableFragment;


/**
 * Created by tzulberti on 6/4/17.
 */

public abstract class BasePlayoffFragment extends BaseClickableFragment {

    protected final static String PLAYOFF_ARGUMENT_KEY = "playoff";

    protected PlayoffDAO playoffDAO;
    protected SerieDataDAO serieDataDAO;

    public void setObjects() {
        MainActivity activity = (MainActivity) getActivity();
        this.playoffDAO = activity.getPlayoffDAO();
        this.serieDataDAO = activity.getSerieDAO();
    }
}