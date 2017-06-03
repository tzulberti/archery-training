package ar.com.tzulberti.archerytraining.database.migrations;

import android.database.sqlite.SQLiteDatabase;

import ar.com.tzulberti.archerytraining.database.consts.TournamentConsts;

/**
 * Add total tournaments score column
 *
 * Created by tzulberti on 6/3/17.
 */
public class DatabaseDatbasseMigration4 implements IDatbasseMigration {


    @Override
    public int getCurentVersion() {
        return 4;
    }

    @Override
    public void executeMigration(SQLiteDatabase db) {
        db.execSQL(String.format(
                "ALTER TABLE %s ADD COLUMN %s INTEGER NOT NULL DEFAULT 0",
                TournamentConsts.TABLE_NAME, TournamentConsts.TOTAL_SCORE_COLUMN_NAME
        ));
    }
}
