package ar.com.tzulberti.archerytraining.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tzulberti on 4/18/17.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "archery_training.db";
    public static final String TABLE_NAME = "serie_information";

    public static final String ID_COLUMN_NAME = "id";
    public static final String DATETIME_COLUMN_NAME = "datetime";
    public static final String DISTANCE_COLUMN_NAME = "distance";
    public static final String ARROWS_AMOUNT_COLUMN_NAME = "arrows_amount";

    protected static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format(
                "CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s LONG NOT NULL, %s INTEGER NOT NULL, %s INTEGER NOT NULL);",
                TABLE_NAME, ID_COLUMN_NAME, DATETIME_COLUMN_NAME, DISTANCE_COLUMN_NAME, ARROWS_AMOUNT_COLUMN_NAME
        ));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_NAME));
        this.onCreate(db);
    }


}
