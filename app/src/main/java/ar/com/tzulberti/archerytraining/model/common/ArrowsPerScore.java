package ar.com.tzulberti.archerytraining.model.common;

import java.io.Serializable;

/**
 * Used to show the number of times that the arrow score was done
 *
 * Created by tzulberti on 6/25/17.
 */
public class ArrowsPerScore implements Serializable {

    public int arrowsAmount;
    public int score;
    public boolean isX;
}
