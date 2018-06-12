package ar.com.tzulberti.archerytraining.model.playoff;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import ar.com.tzulberti.archerytraining.model.base.BaseArcheryTrainingModel;
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
public class Playoff extends BaseArcheryTrainingModel implements ISerieContainer, Serializable{

    public static final String TABLE_NAME = "playoff";


    public static final String NAME_COLUMN_NAME = "name";
    public static final String DATETIME_COLUMN_NAME = "datetime";
    public static final String USER_PLAYOFF_SCORE_COLUMN_NAME = "user_playoff_score";
    public static final String OPPONENT_PLAYOFF_SCORE_COLUMN_NAME = "opponent_playoff_score";


    public long id;
    public Date datetime;
    public String name;
    public int userPlayoffScore;
    public int opponentPlayoffScore;
    public int tournamentConstraintId;
    public TournamentConstraint tournamentConstraint;

    public ComputerPlayOffConfiguration computerPlayOffConfiguration;
    public HumanPlayoffConfiguration humanPlayoffConfiguration;
    public List<PlayoffSerie> series;

    @Override
    public List<? extends ISerie> getSeries() {
        return this.series;
    }

    @Override
    public long getId() { return this.id; }

    @Override
    public TournamentConstraint getTournamentConstraint() {
        return this.tournamentConstraint;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }




}
