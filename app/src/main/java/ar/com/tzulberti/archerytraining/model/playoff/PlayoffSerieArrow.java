package ar.com.tzulberti.archerytraining.model.playoff;


import java.io.Serializable;

import ar.com.tzulberti.archerytraining.model.base.AbstractArrow;

/**
 * Created by tzulberti on 6/3/17.
 */

public class PlayoffSerieArrow extends AbstractArrow implements Serializable {

    public PlayoffSerie serie;
    public Playoff playoff;
}
