package ar.com.tzulberti.archerytraining.model.playoff;

import java.io.Serializable;
import java.util.List;

import ar.com.tzulberti.archerytraining.model.base.AbstractArrow;
import ar.com.tzulberti.archerytraining.model.base.ISerie;
import ar.com.tzulberti.archerytraining.model.base.ISerieContainer;
import ar.com.tzulberti.archerytraining.model.tournament.Tournament;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerieArrow;

/**
 * Created by tzulberti on 6/3/17.
 */

public class PlayoffSerie implements ISerie, Serializable {

    public long id;
    public int index;
    public int userTotalScore;
    public int opponentTotalScore;
    public List<PlayoffSerieArrow> arrows;
    public Playoff playoff;

    @Override
    public List<? extends AbstractArrow> getArrows() {
        return this.arrows;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public ISerieContainer getContainer() {
        return this.playoff;
    }

    @Override
    public int getTotalScore() {
        return this.userTotalScore;
    }

    @Override
    public void updateTotalScore(int arrowScore) {
        this.userTotalScore += arrowScore;
    }
}
