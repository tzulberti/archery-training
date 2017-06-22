package ar.com.tzulberti.archerytraining.model.series;


import java.io.Serializable;
import java.util.Date;

/**
 * Created by tzulberti on 4/18/17.
 */

public class SerieData implements Serializable {

    public int id;
    public int distance;
    public int arrowsAmount;
    public Date datetime;
}
