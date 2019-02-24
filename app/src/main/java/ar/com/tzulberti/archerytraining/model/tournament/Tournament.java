package ar.com.tzulberti.archerytraining.model.tournament;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;
import ar.com.tzulberti.archerytraining.model.base.BaseArcheryTrainingModel;
import ar.com.tzulberti.archerytraining.model.base.ISerie;
import ar.com.tzulberti.archerytraining.model.base.ISerieContainer;
import ar.com.tzulberti.archerytraining.model.constrains.TournamentConstraint;

/**
 * Has the information of all the tournament
 *
 * Created by tzulberti on 5/17/17.
 */
public class Tournament extends BaseArcheryTrainingModel implements ISerieContainer, Serializable {

    public static final String TABLE_NAME = "tournaments";

    public static final String NAME_COLUMN_NAME = "name";
    public static final String DATETIME_COLUMN_NAME = "datetime";
    public static final String IS_TOURNAMENT_DATA_COLUMN_NAME = "is_tournament_data";
    public static final String TOTAL_SCORE_COLUMN_NAME = "total_score";



    public long id;
    public String name;
    public Date datetime;
    public boolean isTournament;
    public int totalScore;
    public int tournamentConstraintId;

    public List<TournamentSerie> series;
    public TournamentConstraint tournamentConstraint;

    public Tournament() {}

    public Tournament(long id, String name, Date datetime) {
        this.id = id;
        this.name = name;
        this.datetime = datetime;
        this.series = new ArrayList<>();
    }

    @Override
    public TournamentConstraint getTournamentConstraint() {
        return this.tournamentConstraint;
    }

    @Override
    public List<? extends ISerie> getSeries() {
        return this.series;
    }

    @Override
    public long getId() { return this.id; }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getFilename() {
        return this.name + "-" + DatetimeHelper.DATE_FORMATTER.format(this.datetime);
    }
}
