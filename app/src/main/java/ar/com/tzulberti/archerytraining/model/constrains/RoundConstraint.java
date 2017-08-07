package ar.com.tzulberti.archerytraining.model.constrains;

/**
 * Has the information to limit the constrain of one of the tournament rounds
 *
 * Created by tzulberti on 8/5/17.
 */
public class RoundConstraint {

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
}
