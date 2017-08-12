package ar.com.tzulberti.archerytraining.database.consts;

/**
 * The schema used to represent the configuration when doing playoff against
 * the computer
 *
 * Created by tzulberti on 6/2/17.
 */
public class ComputerPlayoffConfigurationConsts extends BaseArcheryTrainingConsts {

    public static final String TABLE_NAME = "computer_playoff_configuration";

    public static final String PLAYOFF_ID_COLUMN_NAME = "playoff_id";
    public static final String MIN_SCORE_COLUMN_NAME = "min_score";
    public static final String MAX_SCORE_COLUMN_NAME = "max_score";
}
