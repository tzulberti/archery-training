package ar.com.tzulberti.archerytraining.model.playoff;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by tzulberti on 6/2/17.
 */

public class ComputerPlayOffConfiguration implements Serializable {

    public long id;
    public Playoff playoff;
    public int minScore;
    public int maxScore;
}
