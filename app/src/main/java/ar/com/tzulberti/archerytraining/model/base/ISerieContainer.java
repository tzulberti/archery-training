package ar.com.tzulberti.archerytraining.model.base;

import java.util.List;

/**
 * Created by tzulberti on 6/3/17.
 */

public interface ISerieContainer {

    List<? extends ISerie> getSeries();

    long getId();
}
