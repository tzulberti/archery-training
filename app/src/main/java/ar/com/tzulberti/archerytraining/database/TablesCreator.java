package ar.com.tzulberti.archerytraining.database;

import android.database.sqlite.SQLiteDatabase;

import ar.com.tzulberti.archerytraining.model.bow.Bow;
import ar.com.tzulberti.archerytraining.model.bow.SightDistanceValue;
import ar.com.tzulberti.archerytraining.model.constrains.RoundConstraint;
import ar.com.tzulberti.archerytraining.model.constrains.TournamentConstraint;
import ar.com.tzulberti.archerytraining.model.playoff.ComputerPlayOffConfiguration;
import ar.com.tzulberti.archerytraining.model.playoff.HumanPlayoffConfiguration;
import ar.com.tzulberti.archerytraining.model.playoff.Playoff;
import ar.com.tzulberti.archerytraining.model.playoff.PlayoffSerie;
import ar.com.tzulberti.archerytraining.model.playoff.PlayoffSerieArrow;
import ar.com.tzulberti.archerytraining.model.series.SerieData;
import ar.com.tzulberti.archerytraining.model.tournament.Tournament;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerie;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerieArrow;


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
class TablesCreator {

    void createAll(SQLiteDatabase db) {
        this.createRoundConstraintTable(db);
        this.createTournamentConstraintTable(db);
        this.createSeriesTable(db);
        this.createTournamentTable(db);
        this.createTournamentSerieTable(db);
        this.createTournamentSerieArrowTable(db);
        this.createPlayoffTable(db);
        this.createPlayoffSerieTable(db);
        this.createPlayoffSerieArrowTable(db);
        this.createComputerPlayoffTable(db);
        this.createHumanPlayoffTable(db);
        this.createBowTable(db);
        this.createSightValueTable(db);
    }


    private void createRoundConstraintTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + RoundConstraint.TABLE_NAME + " (" +
                        RoundConstraint.ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        RoundConstraint.DISTANCE_COLUMN_NAME + " INTEGER NOT NULL, " +
                        RoundConstraint.SERIES_PER_ROUND_COLUMN_NAME + " INTEGER NOT NULL, " +
                        RoundConstraint.ARROWS_PER_SERIES_COLUMN_NAME + " INTEGER NOT NULL, " +
                        RoundConstraint.MIN_SCORE_COLUMN_NAME + " INTEGER NOT NULL, " +
                        RoundConstraint.MAX_SCORE_COLUMN_NAME + " INTEGER NOT NULL, " +
                        RoundConstraint.TARGET_IMAGE_COLUMN_NAME + " TEXT NOT NULL " +
                        ")"
        );
    }

    private void createTournamentConstraintTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TournamentConstraint.TABLE_NAME + " (" +
                        TournamentConstraint.ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TournamentConstraint.NAME_COLUMN_NAME + " TEXT NOT NULL, " +
                        TournamentConstraint.IS_OUTDOOR_COLUMN_NAME + " INTEGER NOT NULL, " +
                        TournamentConstraint.STRING_XML_KEY_COLUMN_NAME + " STRING NOT NULL, " +
                        TournamentConstraint.ROUND_CONSTRAINT_1_ID_COLUMN_NAME + " INTEGER NOT NULL, " +
                        TournamentConstraint.ROUND_CONSTRAINT_2_ID_COLUMN_NAME + " INTEGER, " +
                        TournamentConstraint.ROUND_CONSTRAINT_3_ID_COLUMN_NAME + " INTEGER, " +
                        TournamentConstraint.ROUND_CONSTRAINT_4_ID_COLUMN_NAME + " INTEGER, " +
                        TournamentConstraint.ROUND_CONSTRAINT_5_ID_COLUMN_NAME + " INTEGER, " +
                        TournamentConstraint.ROUND_CONSTRAINT_6_ID_COLUMN_NAME + " INTEGER, " +
                        "FOREIGN KEY (" + TournamentConstraint.ROUND_CONSTRAINT_1_ID_COLUMN_NAME + ") REFERENCES " + RoundConstraint.TABLE_NAME + " ( " +  RoundConstraint.ID_COLUMN_NAME + " ) " +
                        "FOREIGN KEY (" + TournamentConstraint.ROUND_CONSTRAINT_2_ID_COLUMN_NAME + ") REFERENCES " + RoundConstraint.TABLE_NAME + " ( " +  RoundConstraint.ID_COLUMN_NAME + " ) " +
                        "FOREIGN KEY (" + TournamentConstraint.ROUND_CONSTRAINT_3_ID_COLUMN_NAME + ") REFERENCES " + RoundConstraint.TABLE_NAME + " ( " +  RoundConstraint.ID_COLUMN_NAME + " ) " +
                        "FOREIGN KEY (" + TournamentConstraint.ROUND_CONSTRAINT_4_ID_COLUMN_NAME + ") REFERENCES " + RoundConstraint.TABLE_NAME + " ( " +  RoundConstraint.ID_COLUMN_NAME + " ) " +
                        "FOREIGN KEY (" + TournamentConstraint.ROUND_CONSTRAINT_5_ID_COLUMN_NAME + ") REFERENCES " + RoundConstraint.TABLE_NAME + " ( " +  RoundConstraint.ID_COLUMN_NAME + " ) " +
                        "FOREIGN KEY (" + TournamentConstraint.ROUND_CONSTRAINT_6_ID_COLUMN_NAME + ") REFERENCES " + RoundConstraint.TABLE_NAME + " ( " +  RoundConstraint.ID_COLUMN_NAME + " ) " +
                        ")"
        );
    }

    private void createSeriesTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + SerieData.TABLE_NAME + "( " +
                        SerieData.ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        SerieData.DATETIME_COLUMN_NAME + " LONG NOT NULL, " +
                        SerieData.DISTANCE_COLUMN_NAME + " INTEGER NOT NULL, " +
                        SerieData.ARROWS_AMOUNT_COLUMN_NAME + " INTEGER NOT NULL, " +
                        SerieData.TRAINING_TYPE_COLUMN_NAME + " INTEGER NOT NULL, " +
                        SerieData.IS_SYNCED + " INTEGER NOT NULL DEFAULT 0 " +
                        ");"
        );
    }

    private void createTournamentTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + Tournament.TABLE_NAME + "( " +
                        Tournament.ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        Tournament.NAME_COLUMN_NAME + " TEXT NOT NULL, " +
                        Tournament.DATETIME_COLUMN_NAME + " LONG NOT NULL, " +
                        Tournament.IS_TOURNAMENT_DATA_COLUMN_NAME + " INTEGER NOT NULL, " +
                        Tournament.TOTAL_SCORE_COLUMN_NAME + " INTEGER NOT NULL DEFAULT 0," +
                        Tournament.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME + " INTEGER NOT NULL, " +
                        Tournament.IS_SYNCED + " INTEGER NOT NULL DEFAULT 0, " +
                        "FOREIGN KEY (" + Tournament.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME + ") REFERENCES " + TournamentConstraint.TABLE_NAME + " ( " +  TournamentConstraint.ID_COLUMN_NAME + " ) " +
                        ");"
        );
    }

    private void createTournamentSerieTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TournamentSerie.TABLE_NAME + " (" +
                        TournamentSerie.ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TournamentSerie.TOURNAMENT_ID_COLUMN_NAME + " INTEGER NOT NULL, " +
                        TournamentSerie.SERIE_INDEX_COLUMN_NAME + " INTEGER NOT NULL, " +
                        TournamentSerie.TOTAL_SCORE_COLUMN_NAME + " INTEGER NOT NULL, " +
                        TournamentSerie.ROUND_INDEX_COLUMN_NAME + " INTEGER NOT NULL, " +
                        TournamentSerie.IS_SYNCED + " INTEGER NOT NULL DEFAULT 0, " +
                        "FOREIGN KEY (" + TournamentSerie.TOURNAMENT_ID_COLUMN_NAME + ") REFERENCES " + Tournament.TABLE_NAME + " ( " +  Tournament.ID_COLUMN_NAME + " ), " +
                        "CONSTRAINT unq_serie_index_tournament_id UNIQUE (" + TournamentSerie.SERIE_INDEX_COLUMN_NAME + ", " +   TournamentSerie.TOURNAMENT_ID_COLUMN_NAME + ")" +
                        ");"
        );
    }

    private void createTournamentSerieArrowTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TournamentSerieArrow.TABLE_NAME + " (" +
                        TournamentSerieArrow.ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TournamentSerieArrow.TOURNAMENT_ID_COLUMN_NAME + " INTEGER NOT NULL, " +
                        TournamentSerieArrow.SERIE_INDEX_COLUMN_NAME + " INTEGER NOT NULL, " +
                        TournamentSerieArrow.SCORE_COLUMN_NAME + " INTEGER NOT NULL, " +
                        TournamentSerieArrow.X_POSITION_COLUMN_NAME + " REAL NOT NULL, " +
                        TournamentSerieArrow.Y_POSITION_COLUMN_NAME + " REAL NOT NULL, " +
                        TournamentSerieArrow.IS_X_COLUMN_NAME + " INTEGER NOT NULL DEFAULT 0, " +
                        TournamentSerieArrow.IS_SYNCED + " INTEGER NOT NULL DEFAULT 0, " +
                        "FOREIGN KEY (" + TournamentSerieArrow.TOURNAMENT_ID_COLUMN_NAME + ") REFERENCES " + Tournament.TABLE_NAME + " ( " +  Tournament.ID_COLUMN_NAME + " ), " +
                        "FOREIGN KEY (" + TournamentSerieArrow.SERIE_INDEX_COLUMN_NAME + ") REFERENCES " + TournamentSerie.TABLE_NAME + " ( " +  TournamentSerie.ID_COLUMN_NAME + " ) " +
                        ");"
        );
    }


    private void createPlayoffTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + Playoff.TABLE_NAME + " (" +
                        Playoff.ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        Playoff.DATETIME_COLUMN_NAME + " LONG NOT NULL, " +
                        Playoff.NAME_COLUMN_NAME + " TEXT NOT NULL, " +
                        Playoff.OPPONENT_PLAYOFF_SCORE_COLUMN_NAME + " INTEGER NOT NULL DEFAULT 0, " +
                        Playoff.USER_PLAYOFF_SCORE_COLUMN_NAME + " INTEGER NOT NULL DEFAULT 0, " +
                        Playoff.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME + " INTEGER NOT NULL, " +
                        Playoff.IS_SYNCED + " INTEGER NOT NULL DEFAULT 0, " +
                        "FOREIGN KEY (" + Playoff.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME + ") REFERENCES " + TournamentConstraint.TABLE_NAME + " ( " +  TournamentConstraint.ID_COLUMN_NAME + " ) " +
                ")"
        );
    }

    private void createPlayoffSerieTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + PlayoffSerie.TABLE_NAME + " (" +
                        PlayoffSerie.ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PlayoffSerie.SERIE_INDEX_COLUMN_NAME + " INTEGER NOT NULL, " +
                        PlayoffSerie.PLAYOFF_ID_COLUMN_NAME + " INTEGER NOT NULL, " +
                        PlayoffSerie.OPPONENT_TOTAL_SCORE_COLUMN_NAME_COLUMN_NAME + " INTEGER NOT NULL, " +
                        PlayoffSerie.USER_TOTAL_SCORE_COLUMN_NAME + " INTEGER NOT NULL, " +
                        PlayoffSerie.IS_SYNCED + " INTEGER NOT NULL DEFAULT 0, " +
                        "FOREIGN KEY (" + PlayoffSerie.PLAYOFF_ID_COLUMN_NAME + ") REFERENCES " + Playoff.TABLE_NAME + " ( " +  Playoff.ID_COLUMN_NAME + " ) " +
                ")"
        );
    }

    private void createPlayoffSerieArrowTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + PlayoffSerieArrow.TABLE_NAME + " (" +
                        PlayoffSerieArrow.ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PlayoffSerieArrow.PLAYOFF_ID_COLUMN_NAME + " INTEGER NOT NULL, " +
                        PlayoffSerieArrow.SERIE_ID_COLUMN_NAME + " INTEGER NOT NULL, " +
                        PlayoffSerieArrow.SCORE_COLUMN_NAME + " INTEGER NOT NULL, " +
                        PlayoffSerieArrow.X_POSITION_COLUMN_NAME + " REAL NOT NULL, " +
                        PlayoffSerieArrow.Y_POSITION_COLUMN_NAME + " REAL NOT NULL, " +
                        PlayoffSerieArrow.IS_X_COLUMN_NAME + " INTEGER NOT NULL DEFAULT 0, " +
                        PlayoffSerieArrow.IS_SYNCED + " INTEGER NOT NULL DEFAULT 0, " +
                        "FOREIGN KEY (" + PlayoffSerieArrow.PLAYOFF_ID_COLUMN_NAME + ") REFERENCES " + Playoff.TABLE_NAME + " ( " +  Playoff.ID_COLUMN_NAME + " ), " +
                        "FOREIGN KEY (" + PlayoffSerieArrow.SERIE_ID_COLUMN_NAME + ") REFERENCES " + PlayoffSerie.TABLE_NAME + " ( " +  PlayoffSerie.ID_COLUMN_NAME + " ) " +
                ");"
        );
    }


    private void createComputerPlayoffTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + ComputerPlayOffConfiguration.TABLE_NAME + " (" +
                        ComputerPlayOffConfiguration.ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ComputerPlayOffConfiguration.PLAYOFF_ID_COLUMN_NAME + " INTEGER NOT NULL, " +
                        ComputerPlayOffConfiguration.MIN_SCORE_COLUMN_NAME + " INTEGER NOT NULL, " +
                        ComputerPlayOffConfiguration.MAX_SCORE_COLUMN_NAME + " INTEGER NOT NULL, " +
                        ComputerPlayOffConfiguration.IS_SYNCED + " INTEGER NOT NULL DEFAULT 0, " +
                        "FOREIGN KEY (" + ComputerPlayOffConfiguration.PLAYOFF_ID_COLUMN_NAME + ") REFERENCES " + Playoff.TABLE_NAME + " ( " +  Playoff.ID_COLUMN_NAME + " ) " +
                ");"
        );
    }

    private void createHumanPlayoffTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + HumanPlayoffConfiguration.TABLE_NAME + " (" +
                        HumanPlayoffConfiguration.ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        HumanPlayoffConfiguration.PLAYOFF_ID_COLUMN_NAME + " INTEGER NOT NULL, " +
                        HumanPlayoffConfiguration.OPPONENT_NAME_COLUMN_NAME + " TEXT NOT NULL, " +
                        HumanPlayoffConfiguration.IS_SYNCED + " INTEGER NOT NULL DEFAULT 0, " +
                        "FOREIGN KEY (" + HumanPlayoffConfiguration.PLAYOFF_ID_COLUMN_NAME + ") REFERENCES " + Playoff.TABLE_NAME + " ( " +  Playoff.ID_COLUMN_NAME + " ) " +
                        ");"
        );
    }

    private void createBowTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + Bow.TABLE_NAME + " (" +
                        Bow.ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        Bow.NAME_COLUMN_NAME + " TEXT NOT NULL, " +
                        Bow.IS_SYNCED + " INTEGER NOT NULL DEFAULT 0 " +
                        ")"
        );
    }

    private void createSightValueTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + SightDistanceValue.TABLE_NAME + " (" +
                        SightDistanceValue.ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        SightDistanceValue.BOW_ID_COLUMN_NAME + " INTEGER NOT NULL, " +
                        SightDistanceValue.DISTANCE_COLUMN_NAME + " INTEGER NOT NULL, " +
                        SightDistanceValue.SIGHT_VALUE_COLUMN_NAME + " FLOAT NOT NULL, " +
                        SightDistanceValue.IS_SYNCED + " INTEGER NOT NULL DEFAULT 0, " +
                        "FOREIGN KEY (" + SightDistanceValue.BOW_ID_COLUMN_NAME + ") REFERENCES " + Bow.TABLE_NAME + " ( " +  Bow.ID_COLUMN_NAME + " ) " +
                        ")"
        );
    }



}

