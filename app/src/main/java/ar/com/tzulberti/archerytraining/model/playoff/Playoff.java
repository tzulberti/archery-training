package ar.com.tzulberti.archerytraining.model.playoff;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import ar.com.tzulberti.archerytraining.model.base.ISerie;
import ar.com.tzulberti.archerytraining.model.base.ISerieContainer;
import ar.com.tzulberti.archerytraining.model.constrains.TournamentConstraint;

/**
 * Has the information of the playoff
 *
 * That is the part of the tournament when one shoots against the other
 *
 * Created by tzulberti on 6/3/17.
 */
public class Playoff implements ISerieContainer, Serializable{

    public long id;
    public Date datetime;
    public String name;
    public int userPlayoffScore;
    public int opponentPlayoffScore;
    public int tournamentConstraintId;
    public TournamentConstraint tournamentConstraint;

    public ComputerPlayOffConfiguration computerPlayOffConfiguration;
    public List<PlayoffSerie> series;

    @Override
    public List<? extends ISerie> getSeries() {
        return this.series;
    }

    @Override
    public long getId() { return this.id; }
    

}
