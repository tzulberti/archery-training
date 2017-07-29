package ar.com.tzulberti.archerytraining.database.migrations;

import android.database.sqlite.SQLiteDatabase;

import ar.com.tzulberti.archerytraining.database.consts.TournamentConsts;
import ar.com.tzulberti.archerytraining.database.consts.TournamentSerieConsts;


/**
 * Rename the practice id column on series
 *
 * Created by tzulberti on 7/29/17.
 */
public class DatabaseMigration29 implements IDatbasseMigration {
    @Override
    public int getCurentVersion() {
        return 29;
    }

    @Override
    public void executeMigration(SQLiteDatabase db) {
        // in both cases the column names are the same
        db.execSQL("CREATE TABLE " + TournamentSerieConsts.TABLE_NAME + "_old AS SELECT * FROM " + TournamentSerieConsts.TABLE_NAME);

        // drop the old table to create a new one with the new schema
        db.execSQL("DROP TABLE " + TournamentSerieConsts.TABLE_NAME);

        // create the new table
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
        // copy all the existing data
        db.execSQL(
                "INSERT INTO "  + TournamentSerieConsts.TABLE_NAME +  "( " +
                        TournamentSerieConsts.ID_COLUMN_NAME + ", " +
                        TournamentSerieConsts.TOURNAMENT_ID_COLUMN_NAME + ", " +
                        TournamentSerieConsts.SERIE_INDEX_COLUMN_NAME + ", " +
                        TournamentSerieConsts.TOTAL_SCORE_COLUMN_NAME + " " +
                ")  SELECT " +
                        TournamentSerieConsts.ID_COLUMN_NAME + ", " +
                        TournamentSerieConsts.TOURNAMENT_ID_COLUMN_NAME + ", " +
                        "practice_id, " + // the old name should be harcoded
                        TournamentSerieConsts.TOTAL_SCORE_COLUMN_NAME + " " +
                "FROM " + TournamentSerieConsts.TABLE_NAME + "_old " +
                "ORDER BY " + TournamentSerieConsts.ID_COLUMN_NAME
        );
        db.execSQL("DROP TABLE " + TournamentSerieConsts.TABLE_NAME + "_old");
    }
}
