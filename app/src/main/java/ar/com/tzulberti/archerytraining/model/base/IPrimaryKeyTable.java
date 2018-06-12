package ar.com.tzulberti.archerytraining.model.base;

import java.io.Serializable;

public interface IPrimaryKeyTable extends Serializable{

    String getIdColumn();
}
