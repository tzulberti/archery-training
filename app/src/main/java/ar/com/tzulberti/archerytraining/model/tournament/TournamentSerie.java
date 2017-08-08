package ar.com.tzulberti.archerytraining.model.tournament;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ar.com.tzulberti.archerytraining.model.base.AbstractArrow;
import ar.com.tzulberti.archerytraining.model.base.ISerie;
import ar.com.tzulberti.archerytraining.model.base.ISerieContainer;
import ar.com.tzulberti.archerytraining.model.constrains.RoundConstraint;
import ar.com.tzulberti.archerytraining.model.constrains.TournamentConstraint;

/**
 * Has the information of one serie of the round
 * Created by tzulberti on 5/22/17.
 */
public class TournamentSerie implements ISerie, Serializable {

    public long id;
    public int index;
    public int totalScore;
    public List<TournamentSerieArrow> arrows;
    public Tournament tournament;

    public TournamentSerie() {
        this.arrows = new ArrayList<>();
    }


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
        return this.tournament;
    }

    @Override
    public int getTotalScore() {
        return totalScore;
    }

    @Override
    public void updateTotalScore(int arrowScore) {
        this.totalScore += arrowScore;
    }
}
