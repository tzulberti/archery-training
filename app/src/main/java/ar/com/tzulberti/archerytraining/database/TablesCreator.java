package ar.com.tzulberti.archerytraining.database;

import android.database.sqlite.SQLiteDatabase;

import ar.com.tzulberti.archerytraining.database.consts.ComputerPlayoffConfigurationConsts;
import ar.com.tzulberti.archerytraining.database.consts.PlayoffConsts;
import ar.com.tzulberti.archerytraining.database.consts.PlayoffSerieArrowConsts;
import ar.com.tzulberti.archerytraining.database.consts.PlayoffSerieConsts;
import ar.com.tzulberti.archerytraining.database.consts.SerieInformationConsts;
import ar.com.tzulberti.archerytraining.database.consts.TournamentConsts;
import ar.com.tzulberti.archerytraining.database.consts.TournamentSerieArrowConsts;
import ar.com.tzulberti.archerytraining.database.consts.TournamentSerieConsts;

/**
 * Used to create all the tables when the application is started for the first time
 *
 * In this case no migration is going to be executed so this is going to create all the
 * required tables. Because of this, this can not be used from the database migrations
 * to create the tables for the first time
 *
 *
 * Created by tzulberti on 6/3/17.
 */
public class TablesCreator {

    public void createAll(SQLiteDatabase db) {
        this.createSeriesTable(db);
        this.createTournamentTable(db);
        this.createTournamentSerieTable(db);
        this.createTournamentSerieArrowTable(db);
        this.createPlayoffTable(db);
        this.createPlayoffSerieTable(db);
        this.createPlayoffSerieArrowTable(db);
        this.createComputerPlayoffTable(db);
    }


    private void createSeriesTable(SQLiteDatabase db) {
        db.execSQL(String.format(
                "CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s LONG NOT NULL, %s INTEGER NOT NULL, %s INTEGER NOT NULL);",
                SerieInformationConsts.TABLE_NAME, SerieInformationConsts.ID_COLUMN_NAME, SerieInformationConsts.DATETIME_COLUMN_NAME,
                SerieInformationConsts.DISTANCE_COLUMN_NAME, SerieInformationConsts.ARROWS_AMOUNT_COLUMN_NAME
        ));
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
                        TournamentConsts.TARGET_SIZE_COLUMN_NAME + " INTEGER NOT NULL, " +
                        TournamentConsts.TOTAL_SCORE_COLUMN_NAME + " INTEGER NOT NULL DEFAULT 0" +
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
                        TournamentSerieArrowConsts.IS_X_COLUMN_NAME + " INTEGER NOT NULL DEFAULT 0, " +
                        "FOREIGN KEY (" + TournamentSerieArrowConsts.TOURNAMENT_ID_COLUMN_NAME + ") REFERENCES " + TournamentConsts.TABLE_NAME + " ( " +  TournamentConsts.ID_COLUMN_NAME + " ), " +
                        "FOREIGN KEY (" + TournamentSerieArrowConsts.SERIE_ID_COLUMN_NAME + ") REFERENCES " + TournamentSerieConsts.TABLE_NAME + " ( " +  TournamentSerieConsts.ID_COLUMN_NAME + " ) " +
                        ");"
        );
    }


    private void createPlayoffTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + PlayoffConsts.TABLE_NAME + " (" +
                        PlayoffConsts.ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PlayoffConsts.DATETIME_COLUMN_NAME + " LONG NOT NULL, " +
                        PlayoffConsts.DISTANCE_COLUMN_NAME + " INTEGER NOT NULL, " +
                        PlayoffConsts.NAME_COLUMN_NAME + " TEXT NOT NULL, " +
                        PlayoffConsts.OPPONENT_PLAYOFF_SCORE_COLUMN_NAME + " INTEGER NOT NULL DEFAULT 0, " +
                        PlayoffConsts.USER_PLAYOFF_SCORE_COLUMN_NAME + " INTEGER NOT NULL DEFAULT 0" +
                ")"
        );
    }

    private void createPlayoffSerieTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + PlayoffSerieConsts.TABLE_NAME + " (" +
                        PlayoffSerieConsts.ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PlayoffSerieConsts.INDEX_COLUMN_NAME + " INTEGER NOT NULL, " +
                        PlayoffSerieConsts.PLAYOFF_ID_COLUMN_NAME + " INTEGER NOT NULL, " +
                        PlayoffSerieConsts.OPPONENT_TOTAL_SCORE_COLUMN_NAME_COLUMN_NAME + " INTEGER NOT NULL, " +
                        PlayoffSerieConsts.USER_TOTAL_SCORE_COLUMN_NAME + " INTEGER NOT NULL, " +
                        "FOREIGN KEY (" + PlayoffSerieConsts.PLAYOFF_ID_COLUMN_NAME + ") REFERENCES " + PlayoffConsts.TABLE_NAME + " ( " +  PlayoffConsts.ID_COLUMN_NAME + " ) " +
                ")"
        );
    }

    private void createPlayoffSerieArrowTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + PlayoffSerieArrowConsts.TABLE_NAME + " (" +
                        PlayoffSerieArrowConsts.ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PlayoffSerieArrowConsts.PLAYOFF_ID_COLUMN_NAME + " INTEGER NOT NULL, " +
                        PlayoffSerieArrowConsts.SERIE_ID_COLUMN_NAME + " INTEGER NOT NULL, " +
                        PlayoffSerieArrowConsts.SCORE_COLUMN_NAME + " INTEGER NOT NULL, " +
                        PlayoffSerieArrowConsts.X_POSITION_COLUMN_NAME + " REAL NOT NULL, " +
                        PlayoffSerieArrowConsts.Y_POSITION_COLUMN_NAME + " REAL NOT NULL, " +
                        PlayoffSerieArrowConsts.IS_X_COLUMN_NAME + " INTEGER NOT NULL DEFAULT 0, " +
                        "FOREIGN KEY (" + PlayoffSerieArrowConsts.PLAYOFF_ID_COLUMN_NAME + ") REFERENCES " + PlayoffConsts.TABLE_NAME + " ( " +  PlayoffConsts.ID_COLUMN_NAME + " ), " +
                        "FOREIGN KEY (" + PlayoffSerieArrowConsts.SERIE_ID_COLUMN_NAME + ") REFERENCES " + PlayoffSerieConsts.TABLE_NAME + " ( " +  PlayoffSerieConsts.ID_COLUMN_NAME + " ) " +
                ");"
        );
    }


    private void createComputerPlayoffTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + ComputerPlayoffConfigurationConsts.TABLE_NAME + " (" +
                        ComputerPlayoffConfigurationConsts.ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ComputerPlayoffConfigurationConsts.PLAYOFF_ID_COLUMN_NAME + " INTEGER NOT NULL, " +
                        ComputerPlayoffConfigurationConsts.MIN_SCORE_COLUMN_NAME + " INTEGER NOT NULL, " +
                        ComputerPlayoffConfigurationConsts.MAX_SCORE_COLUMN_NAME + " INTEGER NOT NULL, " +
                        "FOREIGN KEY (" + ComputerPlayoffConfigurationConsts.PLAYOFF_ID_COLUMN_NAME + ") REFERENCES " + PlayoffConsts.TABLE_NAME + " ( " +  PlayoffConsts.ID_COLUMN_NAME + " ) " +
                ");"
        );
    }
}

