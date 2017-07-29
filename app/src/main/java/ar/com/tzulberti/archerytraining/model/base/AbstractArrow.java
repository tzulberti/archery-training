package ar.com.tzulberti.archerytraining.model.base;

import java.io.Serializable;

/**
 * Created by tzulberti on 6/3/17.
 */

public class AbstractArrow implements Serializable{

    public long id;
    public float xPosition;
    public float yPosition;
    public int score;
    public boolean isX;
}
