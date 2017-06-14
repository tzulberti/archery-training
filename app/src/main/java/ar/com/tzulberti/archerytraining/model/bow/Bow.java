package ar.com.tzulberti.archerytraining.model.bow;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tzulberti on 6/12/17.
 */

public class Bow implements Serializable{

    public long id;
    public String name;

    public List<SightDistanceValue> sightDistanceValues;
}
