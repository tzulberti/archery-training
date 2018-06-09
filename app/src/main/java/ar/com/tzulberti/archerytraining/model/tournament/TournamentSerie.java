package ar.com.tzulberti.archerytraining.model.tournament;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ar.com.tzulberti.archerytraining.model.base.AbstractArrow;
import ar.com.tzulberti.archerytraining.model.base.BaseArcheryTrainingModel;
import ar.com.tzulberti.archerytraining.model.base.ISerie;
import ar.com.tzulberti.archerytraining.model.base.ISerieContainer;


/**
 * Has the information of one serie of the round of a tournament
 *
 * Created by tzulberti on 5/22/17.
 */
public class TournamentSerie extends BaseArcheryTrainingModel implements ISerie, Serializable {

    public static final String TABLE_NAME = "tournament_series";

    public static final String TOURNAMENT_ID_COLUMN_NAME = "tournament_id";
    public static final String TOTAL_SCORE_COLUMN_NAME = "total_score";
    public static final String ROUND_INDEX_COLUMN_NAME = "round_index";

    public long id;
    public int index;
    public int totalScore;
    public int roundIndex;
    public List<TournamentSerieArrow> arrows;
    public Tournament tournament;

    public TournamentSerie() {
        this.arrows = new ArrayList<>();
    }

    @Override
    public String getScoreColumnName() {
        return TOTAL_SCORE_COLUMN_NAME;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getContainerIdColumnName() { return TOURNAMENT_ID_COLUMN_NAME; }

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

    @Override
    public long getId() { return this.id; }

    @Override
    public void setId(long id) { this.id = id;}
}
