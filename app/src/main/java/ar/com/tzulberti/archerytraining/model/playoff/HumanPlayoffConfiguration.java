package ar.com.tzulberti.archerytraining.model.playoff;

import java.io.Serializable;

/**
 * The information of the playoff when the user selected a human opponent
 *
 * Created by tzulberti on 8/16/17.
 */
public class HumanPlayoffConfiguration implements Serializable {
    public long id;
    public Playoff playoff;
    public String opponentName;
}
