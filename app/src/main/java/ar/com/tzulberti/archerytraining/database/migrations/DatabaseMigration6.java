package ar.com.tzulberti.archerytraining.database.migrations;

import android.database.sqlite.SQLiteDatabase;

import ar.com.tzulberti.archerytraining.database.consts.ComputerPlayoffConfigurationConsts;
import ar.com.tzulberti.archerytraining.database.consts.PlayoffConsts;
import ar.com.tzulberti.archerytraining.database.consts.PlayoffSerieArrowConsts;
import ar.com.tzulberti.archerytraining.database.consts.PlayoffSerieConsts;

/**
 * Creates all the migrations related to the playoff table.
 *
 * Even if this migration creates the table, it can not use TableCreator
 * because that ones has the schema updated so it must use it's own
 * logic to create the tables
 *
 * Created by tzulberti on 6/3/17.
 */

public class DatabaseMigration6 implements IDatbasseMigration {

    @Override
    public int getCurentVersion() {
        return 6;
    }

    @Override
    public void executeMigration(SQLiteDatabase db) {
        this.createPlayoffTable(db);
        this.createPlayoffSerieTable(db);
        this.createPlayoffSerieArrowTable(db);
        this.createComputerPlayoffTable(db);
    }


    private void createPlayoffTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + PlayoffConsts.TABLE_NAME + " (" +
                        PlayoffConsts.ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PlayoffConsts.DATETIME_COLUMN_NAME + " LONG NOT NULL, " +
                        "distance INTEGER NOT NULL, " +
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
                        PlayoffSerieConsts.SERIE_INDEX_COLUMN_NAME + " INTEGER NOT NULL, " +
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
                        "min_socre INTEGER NOT NULL, " +
                        ComputerPlayoffConfigurationConsts.MAX_SCORE_COLUMN_NAME + " INTEGER NOT NULL, " +
                        "FOREIGN KEY (" + ComputerPlayoffConfigurationConsts.PLAYOFF_ID_COLUMN_NAME + ") REFERENCES " + PlayoffConsts.TABLE_NAME + " ( " +  PlayoffConsts.ID_COLUMN_NAME + " ) " +
                        ");"
        );
    }
}
