package ar.com.tzulberti.archerytraining.model.tournament;

import java.io.Serializable;
import java.util.List;

import ar.com.tzulberti.archerytraining.model.base.AbstractArrow;

/**
 * Created by tzulberti on 5/22/17.
 */

public class TournamentSerieArrow extends AbstractArrow implements Serializable{

    public TournamentSerie serie;
    public Tournament tournament;

}
