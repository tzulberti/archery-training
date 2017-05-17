package ar.com.tzulberti.archerytraining.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ar.com.tzulberti.archerytraining.consts.SerieInformationConsts;

/**
 * Created by tzulberti on 4/18/17.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "archery_training.db";


    protected static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format(
                "CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s LONG NOT NULL, %s INTEGER NOT NULL, %s INTEGER NOT NULL);",
                SerieInformationConsts.TABLE_NAME, SerieInformationConsts.ID_COLUMN_NAME, SerieInformationConsts.DATETIME_COLUMN_NAME,
                SerieInformationConsts.DISTANCE_COLUMN_NAME, SerieInformationConsts.ARROWS_AMOUNT_COLUMN_NAME
        ));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", SerieInformationConsts.TABLE_NAME));
        this.onCreate(db);
    }


}
