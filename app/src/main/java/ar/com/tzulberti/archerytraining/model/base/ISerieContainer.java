package ar.com.tzulberti.archerytraining.model.base;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tzulberti on 6/3/17.
 */

public interface ISerieContainer extends Serializable {

    List<? extends ISerie> getSeries();

    long getId();

    int getSerieMaxPossibleScore();
}
