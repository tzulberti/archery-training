package ar.com.tzulberti.archerytraining.database.migrations;

import android.database.sqlite.SQLiteDatabase;

import ar.com.tzulberti.archerytraining.database.consts.SerieInformationConsts;

/**
 * Add a column to a serie to show if it is a tournament, playoff or retentions or just
 * a normal serie
 *
 * Created by tzulberti on 6/21/17.
 */
public class DatabaseMigration21 implements IDatbasseMigration {

    @Override
    public int getCurentVersion() {
        return 21;
    }

    @Override
    public void executeMigration(SQLiteDatabase db) {
        db.execSQL(
                "ALTER TABLE " + SerieInformationConsts.TABLE_NAME + " ADD COLUMN " + SerieInformationConsts.TRAINING_TYPE_COLUMN_NAME  + " INTEGER;"
        );
    }
}
