package ar.com.tzulberti.archerytraining.model.playoff;

import java.io.Serializable;

import ar.com.tzulberti.archerytraining.model.base.BaseArcheryTrainingModel;

/**
 * The information of the playoff when the user selected a human opponent
 *
 * Created by tzulberti on 8/16/17.
 */
public class HumanPlayoffConfiguration extends BaseArcheryTrainingModel implements Serializable {

    public static final String TABLE_NAME = "human_playoff_configuration";

    public static final String PLAYOFF_ID_COLUMN_NAME = "playoff_id";
    public static final String OPPONENT_NAME_COLUMN_NAME = "opponent_name";

    public long id;
    public Playoff playoff;
    public String opponentName;
}
