package ar.com.tzulberti.archerytraining.model.common;

import java.io.Serializable;

/**
 * Used to chart the number of series that has the same score.
 *
 * Created by tzulberti on 6/25/17.
 */
public class SeriesPerScore implements IElementByScore {

    public int serieScore;
    public int seriesAmount;

    @Override
    public int getScore() {
        return this.serieScore;
    }

    @Override
    public int getAmount() {
        return this.seriesAmount;
    }
}
