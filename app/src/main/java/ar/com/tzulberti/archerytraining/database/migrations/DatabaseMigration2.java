package ar.com.tzulberti.archerytraining.database.migrations;

import android.database.sqlite.SQLiteDatabase;

import ar.com.tzulberti.archerytraining.database.InsertConstantValues;
import ar.com.tzulberti.archerytraining.model.constrains.TournamentConstraint;

/**
 * Updates the target information when using complete
 */

public class DatabaseMigration2 implements IDatabaseMigration {
    @Override
    public int getCurentVersion() {
        return 2;
    }

    @Override
    public void executeMigration(SQLiteDatabase db) {
        InsertConstantValues inserter = new InsertConstantValues();
        inserter.insertAllFixtureData(db);
    }
}
