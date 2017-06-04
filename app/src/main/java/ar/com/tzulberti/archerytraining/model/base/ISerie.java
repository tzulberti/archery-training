package ar.com.tzulberti.archerytraining.model.base;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tzulberti on 6/3/17.
 */

public interface ISerie extends Serializable {

    List<? extends AbstractArrow> getArrows();

    int getIndex();

    ISerieContainer getContainer();

    int getTotalScore();

    void updateTotalScore(int arrowScore);
}
