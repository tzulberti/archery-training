package ar.com.tzulberti.archerytraining.model.common;

import java.io.Serializable;

/**
 * Created by tzulberti on 7/19/17.
 */

public class TournamentConstraint implements Serializable {

    public int id;
    public String name;
    public int distance;
    public int seriesPerRound;
    public int arrowsPerSeries;
    public int minScore;
    public String targetImage;
    public boolean isOutdoor;

    public TournamentConstraint(int id, String name, int distance, int seriesPerRound, int arrowsPerSeries, int minScore, String targetImage, boolean isOutdoor) {
        this.id = id;
        this.name = name;
        this.distance = distance;
        this.seriesPerRound = seriesPerRound;
        this.arrowsPerSeries = arrowsPerSeries;
        this.minScore = minScore;
        this.targetImage = targetImage;
        this.isOutdoor = isOutdoor;
    }
}
