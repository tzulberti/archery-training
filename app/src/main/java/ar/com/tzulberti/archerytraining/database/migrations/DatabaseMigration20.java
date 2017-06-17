package ar.com.tzulberti.archerytraining.database.migrations;

import android.database.sqlite.SQLiteDatabase;

import ar.com.tzulberti.archerytraining.database.consts.PlayoffConsts;
import ar.com.tzulberti.archerytraining.database.consts.SerieInformationConsts;
import ar.com.tzulberti.archerytraining.database.consts.TournamentConsts;
import ar.com.tzulberti.archerytraining.model.playoff.Playoff;

/**
 * All the timestamps on the database were saved as milliseconds (not as
 * seconds) since 1970. Since this is causing some issues, I will reset
 * all those values to use seconds
 *
 * Created by tzulberti on 6/16/17.
 */
public class DatabaseMigration20 implements IDatbasseMigration {

    @Override
    public int getCurentVersion() {
        return 20;
    }

    @Override
    public void executeMigration(SQLiteDatabase db) {
        String[] tableNames = new String[] {
                PlayoffConsts.TABLE_NAME,
                SerieInformationConsts.TABLE_NAME,
                TournamentConsts.TABLE_NAME
        };

        for (String tableName : tableNames) {
            db.execSQL(
                "UPDATE " + tableName + " SET datetime = datetime / 1000;"
            );
        }
    }
}
