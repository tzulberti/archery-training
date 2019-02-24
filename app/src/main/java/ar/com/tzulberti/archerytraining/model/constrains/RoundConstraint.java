package ar.com.tzulberti.archerytraining.model.constrains;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.model.base.BaseArcheryTrainingModel;

/**
 * Has the information to limit the constrain of one of the tournament rounds.
 * Ie, the distance used on that round, or the max score for that round.
 *
 * Created by tzulberti on 8/5/17.
 */
public class RoundConstraint extends BaseArcheryTrainingModel implements Serializable {

    public static final String TABLE_NAME = "round_constraint";

    public static final String DISTANCE_COLUMN_NAME = "distance";
    public static final String SERIES_PER_ROUND_COLUMN_NAME = "series_per_round";
    public static final String ARROWS_PER_SERIES_COLUMN_NAME = "arrows_per_series";
    public static final String MIN_SCORE_COLUMN_NAME = "min_score";
    public static final String MAX_SCORE_COLUMN_NAME = "max_score";
    public static final String TARGET_IMAGE_COLUMN_NAME = "target_image";

    public int id;
    public int distance;
    public int seriesPerRound;
    public int arrowsPerSeries;
    public int maxScore;
    public int minScore;
    public String targetImage;


    public RoundConstraint(int id, int distance, int seriesPerRound, int arrowsPerSeries, int maxScore, int minScore, String targetImage) {
        this.id = id;
        this.distance = distance;
        this.seriesPerRound = seriesPerRound;
        this.arrowsPerSeries = arrowsPerSeries;
        this.maxScore = maxScore;
        this.minScore = minScore;
        this.targetImage = targetImage;
    }

    public int getSerieMaxPossibleScore() {
        return this.arrowsPerSeries * this.maxScore;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoundConstraint that = (RoundConstraint) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public int getDrawable() {
        if (StringUtils.equals(this.targetImage, "complete_archery_target.png")) {
            return R.drawable.complete_archery_target;
        } else if (StringUtils.equals(this.targetImage, "reduced_outdoor_target.png")) {
            return R.drawable.reduced_outdoor_target;
        } else if (StringUtils.equals(this.targetImage, "triple_spot_target.png")) {
            return R.drawable.triple_spot_target;
        } else {
            throw new RuntimeException("Missing drawable for " + this.targetImage);
        }
    }
}
