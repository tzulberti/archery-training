package ar.com.tzulberti.archerytraining.model.base;

import java.io.Serializable;
import java.util.List;

/**
 * Interface used for the container series
 *
 * Created by tzulberti on 6/3/17.
 */
public interface ISerie extends IPrimaryKeyTable {

    String SERIE_INDEX_COLUMN_NAME = "serie_index";

    List<? extends AbstractArrow> getArrows();

    int getIndex();

    ISerieContainer getContainer();

    int getTotalScore();

    void updateTotalScore(int arrowScore);

    long getId();

    void setId(long id);



    String getScoreColumnName();

    String getTableName();

    /**
     * All the series belongs to a container (tournament, etc...) so this
     * returns the column that references that table
     */
    String getContainerIdColumnName();
}
