package ar.com.tzulberti.archerytraining.database.migrations;

import android.database.sqlite.SQLiteDatabase;

import ar.com.tzulberti.archerytraining.database.consts.TournamentSerieArrowConsts;

/**
 * Add column on arrows table to indentify if the arrow is an X
 *
 * Created by tzulberti on 6/3/17.
 */
public class DatabaseDatbasseMigration5 implements IDatbasseMigration {
    @Override
    public int getCurentVersion() {
        return 5;
    }

    @Override
    public void executeMigration(SQLiteDatabase db) {
        db.execSQL(String.format(
                "ALTER TABLE %s ADD COLUMN %s INTEGER NOT NULL DEFAULT 0",
                TournamentSerieArrowConsts.TABLE_NAME, TournamentSerieArrowConsts.IS_X_COLUMN_NAME
        ));
    }
}
