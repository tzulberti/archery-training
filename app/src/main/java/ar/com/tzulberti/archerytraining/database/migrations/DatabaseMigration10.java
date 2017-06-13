package ar.com.tzulberti.archerytraining.database.migrations;

import android.database.sqlite.SQLiteDatabase;

import ar.com.tzulberti.archerytraining.database.consts.BowConsts;
import ar.com.tzulberti.archerytraining.database.consts.SightDistanceValueConsts;

/**
 * Created by tzulberti on 6/12/17.
 */

public class DatabaseMigration10 implements IDatbasseMigration {

    @Override
    public int getCurentVersion() {
        return 10;
    }

    @Override
    public void executeMigration(SQLiteDatabase db) {
        this.createBowTable(db);
        this.createSightValueTable(db);

    }

    private void createBowTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + BowConsts.TABLE_NAME + " (" +
                        BowConsts.ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        BowConsts.NAME_COLUMN_NAME + " TEXT NOT NULL " +
                        ")"
        );
    }

    private void createSightValueTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + SightDistanceValueConsts.TABLE_NAME + " (" +
                        SightDistanceValueConsts.ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        SightDistanceValueConsts.BOW_ID_COLUMN_NAME + " INTEGER NOT NULL, " +
                        SightDistanceValueConsts.DISTANCE_COLUMN_NAME + " INTEGER NOT NULL, " +
                        SightDistanceValueConsts.SIGHT_VALUE_COLUMN_NAME + " FLOAT NOT NULL, " +
                        "FOREIGN KEY (" + SightDistanceValueConsts.BOW_ID_COLUMN_NAME + ") REFERENCES " + BowConsts.TABLE_NAME + " ( " +  BowConsts.ID_COLUMN_NAME + " ) " +
                        ")"
        );
    }
}
