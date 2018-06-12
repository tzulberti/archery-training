package ar.com.tzulberti.archerytraining.model.base;

/**
 *  Basic const that have columns used on all the models
 */
public class BaseArcheryTrainingModel  implements  IPrimaryKeyTable{

    public static final String IS_SYNCED = "is_synced";

    public static final String ID_COLUMN_NAME = "id";

    public String getIdColumn() {return ID_COLUMN_NAME;}
}
