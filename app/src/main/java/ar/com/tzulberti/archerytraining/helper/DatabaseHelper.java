package ar.com.tzulberti.archerytraining.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ar.com.tzulberti.archerytraining.consts.TournamentConsts;
import ar.com.tzulberti.archerytraining.consts.SerieInformationConsts;
import ar.com.tzulberti.archerytraining.consts.TournamentSerieArrowConsts;
import ar.com.tzulberti.archerytraining.consts.TournamentSerieConsts;

/**
 * Created by tzulberti on 4/18/17.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "archery_training.db";


    protected static final int DATABASE_VERSION = 4;

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
        this.createTournamentTable(db);
        this.createTournamentSerieTable(db);
        this.createTournamentSerieArrowTable(db);
    }

    private void createTournamentTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TournamentConsts.TABLE_NAME + "( " +
                        TournamentConsts.ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TournamentConsts.NAME_COLUMN_NAME + " TEXT NOT NULL, " +
                        TournamentConsts.DATETIME_COLUMN_NAME + " LONG NOT NULL, " +
                        TournamentConsts.DISTANCE_COLUMN_NAME + " INTEGER NOT NULL, " +
                        TournamentConsts.IS_TOURNAMENT_DATA_COLUMN_NAME + " INTEGER NOT NULL, " +
                        TournamentConsts.IS_OUTDOOR_COLUMN_NAME + " INTEGER NOT NULL, " +
                        TournamentConsts.TARGET_SIZE_COLUMN_NAME + " INTEGER NOT NULL " +
                        ");"
        );
    }

    private void createTournamentSerieTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TournamentSerieConsts.TABLE_NAME + " (" +
                        TournamentSerieConsts.ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TournamentSerieConsts.TOURNAMENT_ID_COLUMN_NAME + " INTEGER NOT NULL, " +
                        TournamentSerieConsts.SERIE_INDEX_COLUMN_NAME + " INTEGER NOT NULL, " +
                        TournamentSerieConsts.TOTAL_SCORE_COLUMN_NAME + " INTEGER NOT NULL, " +
                        "FOREIGN KEY (" + TournamentSerieConsts.TOURNAMENT_ID_COLUMN_NAME + ") REFERENCES " + TournamentConsts.TABLE_NAME + " ( " +  TournamentConsts.ID_COLUMN_NAME + " ), " +
                        "CONSTRAINT unq_serie_index_tournament_id UNIQUE (" + TournamentSerieConsts.SERIE_INDEX_COLUMN_NAME + ", " +   TournamentSerieConsts.TOURNAMENT_ID_COLUMN_NAME + ")" +
                        ");"
        );
    }

    private void createTournamentSerieArrowTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TournamentSerieArrowConsts.TABLE_NAME + " (" +
                        TournamentSerieArrowConsts.ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TournamentSerieArrowConsts.TOURNAMENT_ID_COLUMN_NAME + " INTEGER NOT NULL, " +
                        TournamentSerieArrowConsts.SERIE_ID_COLUMN_NAME + " INTEGER NOT NULL, " +
                        TournamentSerieArrowConsts.SCORE_COLUMN_NAME + " INTEGER NOT NULL, " +
                        TournamentSerieArrowConsts.X_POSITION_COLUMN_NAME + " REAL NOT NULL, " +
                        TournamentSerieArrowConsts.Y_POSITION_COLUMN_NAME + " REAL NOT NULL, " +
                        "FOREIGN KEY (" + TournamentSerieArrowConsts.TOURNAMENT_ID_COLUMN_NAME + ") REFERENCES " + TournamentConsts.TABLE_NAME + " ( " +  TournamentConsts.ID_COLUMN_NAME + " ), " +
                        "FOREIGN KEY (" + TournamentSerieArrowConsts.SERIE_ID_COLUMN_NAME + ") REFERENCES " + TournamentConsts.TABLE_NAME + " ( " +  TournamentSerieConsts.ID_COLUMN_NAME + " ) " +
                        ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 2) {

        } else if (oldVersion == 3) {
            this.createTournamentTable(db);
            this.createTournamentSerieTable(db);
            this.createTournamentSerieArrowTable(db);
        } else {
            throw new RuntimeException("Unexpected oldVersion: " + String.valueOf(oldVersion));
        }
    }


}
