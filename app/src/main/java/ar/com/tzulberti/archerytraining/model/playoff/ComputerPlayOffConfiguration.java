package ar.com.tzulberti.archerytraining.model.playoff;

import java.io.Serializable;
import java.util.Date;

import ar.com.tzulberti.archerytraining.model.base.BaseArcheryTrainingModel;

/**
 * When doing a playoff against the computer it has the configuration values
 * used
 *
 * Created by tzulberti on 6/2/17.
 */
public class ComputerPlayOffConfiguration extends BaseArcheryTrainingModel implements Serializable {

    public static final String TABLE_NAME = "computer_playoff_configuration";

    public static final String PLAYOFF_ID_COLUMN_NAME = "playoff_id";
    public static final String MIN_SCORE_COLUMN_NAME = "min_score";
    public static final String MAX_SCORE_COLUMN_NAME = "max_score";

    public long id;
    public Playoff playoff;
    public int minScore;
    public int maxScore;
}
