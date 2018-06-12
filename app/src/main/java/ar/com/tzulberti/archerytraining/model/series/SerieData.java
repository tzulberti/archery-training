package ar.com.tzulberti.archerytraining.model.series;


import java.io.Serializable;
import java.util.Date;

import ar.com.tzulberti.archerytraining.model.base.BaseArcheryTrainingModel;

/**
 * For each serie the user has done, it has the information. This mean, each arrow
 * the user has shoot (on tournament, playoff, or loaded as series) will belong
 * to a serie in this table.
 *
 * Created by tzulberti on 4/18/17.
 */
public class SerieData extends BaseArcheryTrainingModel implements Serializable {

    /**
     * The different training types userd for the series
     */
    public enum TrainingType {
        FREE(0),

        TOURNAMENT(1),

        PLAYOFF(2),

        RETENTIONS(3);

        private final int value;

        private TrainingType(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static final TrainingType getFromValue(int value) {
            switch (value) {
                case 0:
                    return FREE;
                case 1:
                    return TOURNAMENT;
                case 2:
                    return PLAYOFF;
                case 3:
                    return RETENTIONS;
            }
            return null;
        }
    }

    public static final String TABLE_NAME = "serie_information";

    public static final String DATETIME_COLUMN_NAME = "datetime";
    public static final String DISTANCE_COLUMN_NAME = "distance";
    public static final String ARROWS_AMOUNT_COLUMN_NAME = "arrows_amount";
    public static final String TRAINING_TYPE_COLUMN_NAME = "training_type";

    public int id;
    public int distance;
    public int arrowsAmount;
    public Date datetime;
}
