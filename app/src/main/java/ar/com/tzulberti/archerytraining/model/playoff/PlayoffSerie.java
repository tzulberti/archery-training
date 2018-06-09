package ar.com.tzulberti.archerytraining.model.playoff;

import java.io.Serializable;
import java.util.List;

import ar.com.tzulberti.archerytraining.model.base.AbstractArrow;
import ar.com.tzulberti.archerytraining.model.base.BaseArcheryTrainingModel;
import ar.com.tzulberti.archerytraining.model.base.ISerie;
import ar.com.tzulberti.archerytraining.model.base.ISerieContainer;
import ar.com.tzulberti.archerytraining.model.tournament.Tournament;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerieArrow;

/**
 * Saves the information of one of the series of the playoff
 *
 * Created by tzulberti on 6/3/17.
 */
public class PlayoffSerie extends BaseArcheryTrainingModel implements ISerie, Serializable {

    public static final String TABLE_NAME = "playoff_serie";

    public static final String PLAYOFF_ID_COLUMN_NAME = "playoff_id";

    public static final String OPPONENT_TOTAL_SCORE_COLUMN_NAME_COLUMN_NAME = "opponent_total_score";
    public static final String USER_TOTAL_SCORE_COLUMN_NAME = "user_total_score";

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

    @Override
    public void setId(long id) { this.id = id; }

    @Override
    public long getId() { return this.id; }

    @Override
    public String getScoreColumnName() {
        return USER_TOTAL_SCORE_COLUMN_NAME;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getContainerIdColumnName() { return PLAYOFF_ID_COLUMN_NAME; }
}
