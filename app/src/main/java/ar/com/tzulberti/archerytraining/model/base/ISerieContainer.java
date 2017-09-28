package ar.com.tzulberti.archerytraining.model.base;

import java.io.Serializable;
import java.util.List;

import ar.com.tzulberti.archerytraining.model.constrains.TournamentConstraint;

/**
 * Interface used to the instance that contains more than one serie
 *
 * Created by tzulberti on 6/3/17.
 */
public interface ISerieContainer extends Serializable {

    List<? extends ISerie> getSeries();

    long getId();

    TournamentConstraint getTournamentConstraint();

}
