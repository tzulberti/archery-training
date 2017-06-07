package ar.com.tzulberti.archerytraining.database.migrations;

import android.database.sqlite.SQLiteDatabase;

import ar.com.tzulberti.archerytraining.database.consts.ComputerPlayoffConfigurationConsts;
import ar.com.tzulberti.archerytraining.database.consts.PlayoffConsts;

/**
 * Solves a typo when creating the computer_playoff_configuration on the column min_score
 *
 * Created by tzulberti on 6/5/17.
 */
public class DatabaseMigration7 implements IDatbasseMigration {

    @Override
    public int getCurentVersion() {
        return 7;
    }

    @Override
    public void executeMigration(SQLiteDatabase db) {
        db.execSQL("DROP TABLE " + ComputerPlayoffConfigurationConsts.TABLE_NAME + ";");
        db.execSQL(
                "CREATE TABLE " + ComputerPlayoffConfigurationConsts.TABLE_NAME + " (" +
                        ComputerPlayoffConfigurationConsts.ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ComputerPlayoffConfigurationConsts.PLAYOFF_ID_COLUMN_NAME + " INTEGER NOT NULL, " +
                        "min_score INTEGER NOT NULL, " +
                        ComputerPlayoffConfigurationConsts.MAX_SCORE_COLUMN_NAME + " INTEGER NOT NULL, " +
                        "FOREIGN KEY (" + ComputerPlayoffConfigurationConsts.PLAYOFF_ID_COLUMN_NAME + ") REFERENCES " + PlayoffConsts.TABLE_NAME + " ( " +  PlayoffConsts.ID_COLUMN_NAME + " ) " +
                        ");"
        );
    }
}
