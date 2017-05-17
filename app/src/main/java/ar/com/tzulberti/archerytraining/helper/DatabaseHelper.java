package ar.com.tzulberti.archerytraining.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ar.com.tzulberti.archerytraining.consts.PracticeConsts;
import ar.com.tzulberti.archerytraining.consts.ScoringInformationConsts;
import ar.com.tzulberti.archerytraining.consts.SerieInformationConsts;

/**
 * Created by tzulberti on 4/18/17.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "archery_training.db";


    protected static final int DATABASE_VERSION = 3;

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
        this.createPracticeTable(db);
        this.createScoringInformationTable(db);
    }

    private void createPracticeTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + PracticeConsts.TABLE_NAME + "( " +
                        PracticeConsts.ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PracticeConsts.NAME_COLUMN_NAME + " TEXT NOT NULL, " +
                        PracticeConsts.DATETIME_COLUMN_NAME + " LONG NOT NULL, " +
                        PracticeConsts.DISTANCE_COLUMN_NAME + " INTEGER NOT NULL, " +
                        PracticeConsts.IS_TOURNAMENT_DATA_COLUMN_NAME + " INTEGER NOT NULL, " +
                        PracticeConsts.IS_OUTDOOR_COLUMN_NAME + " INTEGER NOT NULL, " +
                        PracticeConsts.TARGET_SIZE_COLUMN_NAME + " INTEGER NOT NULL " +
                        ");"
        );
    }

    private void createScoringInformationTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + ScoringInformationConsts.TABLE_NAME + " (" +
                        ScoringInformationConsts.ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ScoringInformationConsts.PRACTICE_ID_COLUMN_NAME + " INTEGER NOT NULL, " +
                        ScoringInformationConsts.SERIE_INDEX_COLUMN_NAME + " INTEGER NOT NULL, " +
                        ScoringInformationConsts.SCORE_COLUMN_NAME + " INTEGER NOT NULL, " +
                        ScoringInformationConsts.X_POSITION_COLUMN_NAME + " INTEGER NOT NULL, " +
                        ScoringInformationConsts.Y_POSITION_COLUMN_NAME + " INTEGER NOT NULL, " +
                        "FOREIGN KEY (" + ScoringInformationConsts.PRACTICE_ID_COLUMN_NAME + ") REFERENCES " + PracticeConsts.TABLE_NAME + " ( " +  PracticeConsts.ID_COLUMN_NAME + " )" +
                        ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 2) {
            this.createPracticeTable(db);
            this.createScoringInformationTable(db);
        } else {
            throw new RuntimeException("Unexpected oldVersion: " + String.valueOf(oldVersion));
        }
    }


}
