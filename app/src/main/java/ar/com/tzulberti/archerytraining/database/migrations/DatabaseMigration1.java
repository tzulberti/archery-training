package ar.com.tzulberti.archerytraining.database.migrations;

import android.database.sqlite.SQLiteDatabase;

import ar.com.tzulberti.archerytraining.model.playoff.HumanPlayoffConfiguration;
import ar.com.tzulberti.archerytraining.model.playoff.Playoff;

/**
 * Modifies the database to add the human playoff configuration
 *
 * Created by tzulberti on 8/16/17.
 */
public class DatabaseMigration1 implements IDatbasseMigration {

    @Override
    public int getCurentVersion() {
        return 1;
    }

    @Override
    public void executeMigration(SQLiteDatabase db) {
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
}
