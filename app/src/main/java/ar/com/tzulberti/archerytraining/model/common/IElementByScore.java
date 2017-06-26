package ar.com.tzulberti.archerytraining.model.common;

import java.io.Serializable;

/**
 * Created by tzulberti on 6/26/17.
 */

public interface IElementByScore extends Serializable {

    int getScore();

    int getAmount();
}
