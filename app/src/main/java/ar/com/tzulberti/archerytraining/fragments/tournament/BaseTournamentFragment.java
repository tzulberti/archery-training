package ar.com.tzulberti.archerytraining.fragments.tournament;

import ar.com.tzulberti.archerytraining.MainActivity;
import ar.com.tzulberti.archerytraining.dao.SerieDataDAO;
import ar.com.tzulberti.archerytraining.dao.TournamentDAO;
import ar.com.tzulberti.archerytraining.fragments.BaseClickableFragment;

/**
 * Created by tzulberti on 5/17/17.
 */

public abstract class BaseTournamentFragment extends BaseClickableFragment{

    protected TournamentDAO tournamentDAO;

    public void setObjects() {
        MainActivity activity = (MainActivity) getActivity();
        this.tournamentDAO = activity.getTournamentDAO();
    }
}
