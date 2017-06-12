package ar.com.tzulberti.archerytraining.model.bow;

import java.io.Serializable;

/**
 * Created by tzulberti on 6/12/17.
 */

public class SightDistanceValue implements Serializable {

    public long id;
    public int distance;
    public float sightValue;

    public Bow bow;
}
