package ar.com.tzulberti.archerytraining.database.migrations;

import android.database.sqlite.SQLiteDatabase;

import ar.com.tzulberti.archerytraining.database.consts.BaseArcheryTrainingConsts;
import ar.com.tzulberti.archerytraining.database.consts.HumanPlayoffConfigurationConsts;
import ar.com.tzulberti.archerytraining.database.consts.PlayoffConsts;

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
            "CREATE TABLE " + HumanPlayoffConfigurationConsts.TABLE_NAME + " (" +
                    HumanPlayoffConfigurationConsts.ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    HumanPlayoffConfigurationConsts.PLAYOFF_ID_COLUMN_NAME + " INTEGER NOT NULL, " +
                    HumanPlayoffConfigurationConsts.OPPONENT_NAME_COLUMN_NAME + " TEXT NOT NULL, " +
                    BaseArcheryTrainingConsts.IS_SYNCED + " INTEGER NOT NULL DEFAULT 0, " +
                    "FOREIGN KEY (" + HumanPlayoffConfigurationConsts.PLAYOFF_ID_COLUMN_NAME + ") REFERENCES " + PlayoffConsts.TABLE_NAME + " ( " +  PlayoffConsts.ID_COLUMN_NAME + " ) " +
                    ");"
        );
    }
}
