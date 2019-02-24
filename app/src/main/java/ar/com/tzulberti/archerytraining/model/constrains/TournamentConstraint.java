package ar.com.tzulberti.archerytraining.model.constrains;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ar.com.tzulberti.archerytraining.model.base.BaseArcheryTrainingModel;

/**
 * Has the constrains for the tournament/playoff information
 *
 * Created by tzulberti on 7/19/17.
 */
public class TournamentConstraint extends BaseArcheryTrainingModel implements Serializable {

    public static final String TABLE_NAME = "tournament_constraint";


    public static final String NAME_COLUMN_NAME = "name";
    public static final String IS_OUTDOOR_COLUMN_NAME = "is_outdoor";
    public static final String STRING_XML_KEY_COLUMN_NAME = "string_xml_key";

    public static final String ROUND_CONSTRAINT_1_ID_COLUMN_NAME = "round_constraint_1_id";
    public static final String ROUND_CONSTRAINT_2_ID_COLUMN_NAME = "round_constraint_2_id";
    public static final String ROUND_CONSTRAINT_3_ID_COLUMN_NAME = "round_constraint_3_id";
    public static final String ROUND_CONSTRAINT_4_ID_COLUMN_NAME = "round_constraint_4_id";
    public static final String ROUND_CONSTRAINT_5_ID_COLUMN_NAME = "round_constraint_5_id";
    public static final String ROUND_CONSTRAINT_6_ID_COLUMN_NAME = "round_constraint_6_id";

    public int id;
    public String name;
    public boolean isOutdoor;
    public String stringXMLKey;


    public int roundContraint1Id;
    public Integer roundContraint2Id;
    public Integer roundContraint3Id;
    public Integer roundContraint4Id;
    public Integer roundContraint5Id;
    public Integer roundContraint6Id;

    public String translatedName;
    public List<RoundConstraint> roundConstraintList;

    public TournamentConstraint(int id, String name, boolean isOutdoor, String stringXMLKey,
            int roundContraint1Id, Integer roundContraint2Id, Integer roundContraint3Id,
            Integer roundContraint4Id, Integer roundContraint5Id, Integer roundContraint6Id) {
        this.id = id;
        this.name = name;
        this.isOutdoor = isOutdoor;
        this.stringXMLKey = stringXMLKey;
        this.roundContraint1Id = roundContraint1Id;
        this.roundContraint2Id = roundContraint2Id;
        this.roundContraint3Id = roundContraint3Id;
        this.roundContraint4Id = roundContraint4Id;
        this.roundContraint5Id = roundContraint5Id;
        this.roundContraint6Id = roundContraint6Id;
        this.roundConstraintList = new ArrayList<>();
    }

    /**
     * Gets the constraint for the round index based on the information
     *
     * @param roundIndex the round index which starts in 1
     * @return the RoundConstraint for that index
     */
    public RoundConstraint getContraintForRound(int roundIndex) {
        return this.roundConstraintList.get(roundIndex -1);
    }


    /**
     * Gets the constraint for the current serie
     *
     * @param serieIndex the serie index for which get the constraint. This value starts at 1
     * @return the round constraint for that serie
     */
    public RoundConstraint getConstraintForSerie(int serieIndex) {
        int acumSeries = 0;
        for (RoundConstraint roundConstraint : this.roundConstraintList) {
            if (acumSeries < serieIndex && serieIndex <= roundConstraint.seriesPerRound + acumSeries ) {
                return roundConstraint;
            } else {
                acumSeries += roundConstraint.seriesPerRound;
            }
        }
        return null;
    }

    /**
     * Identifies the round where the serie belongs to
     *
     * @param serieIndex the serie of the container to find to which round it belongs
     * @return the round where the serie belongs to
     */
    public int getRoundIndex(int serieIndex) {
        int acumSeries = 0;
        int roundIndex = 1;
        for (RoundConstraint roundConstraint : this.roundConstraintList) {
            if (acumSeries < serieIndex && serieIndex <= roundConstraint.seriesPerRound + acumSeries ) {
                return roundIndex;
            } else {
                acumSeries += roundConstraint.seriesPerRound;
                roundIndex += 1;
            }
        }
        return roundIndex;
    }

    /**
     *
     * @return the max score possible taking into account the max value for each round
     */
    public int getMaxPossibleScore() {
        int maxScore = 0;
        for (RoundConstraint roundConstraint : this.roundConstraintList) {
            maxScore += roundConstraint.maxScore * roundConstraint.arrowsPerSeries * roundConstraint.seriesPerRound;
        }
        return maxScore;
    }

    /**
     *
     * @return the max ammount of series that the container might have
     */
    public int getMaxSeries() {
        int res = 0;
        for (RoundConstraint roundConstraint : roundConstraintList) {
            res += roundConstraint.seriesPerRound;
        }
        return res;
    }


}
