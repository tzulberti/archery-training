package ar.com.tzulberti.archerytraining.database.migrations;

import android.database.sqlite.SQLiteDatabase;

import ar.com.tzulberti.archerytraining.database.InsertConstantValues;

public class DatabaseMigration3 implements  IDatabaseMigration {
    @Override
    public int getCurentVersion() {
        return 3;
    }

    @Override
    public void executeMigration(SQLiteDatabase db) {
        InsertConstantValues inserter = new InsertConstantValues();
        inserter.insertAllFixtureData(db);
    }
}
