package ar.com.tzulberti.archerytraining.database.migrations;

import android.database.sqlite.SQLiteDatabase;

import ar.com.tzulberti.archerytraining.database.consts.SerieInformationConsts;

/**
 * Set the series_information training_type as NOT NULL, and sets the default value to 0
 *
 * Created by tzulberti on 6/22/17.
 */
public class DatabaseMigration23 implements  IDatbasseMigration {

    @Override
    public int getCurentVersion() {
        return 23;
    }

    @Override
    public void executeMigration(SQLiteDatabase db) {
        db.execSQL(String.format(
                "CREATE TABLE foobar ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s LONG NOT NULL, %s INTEGER NOT NULL, %s INTEGER NOT NULL, %s INTEGER);",
                SerieInformationConsts.ID_COLUMN_NAME, SerieInformationConsts.DATETIME_COLUMN_NAME,
                SerieInformationConsts.DISTANCE_COLUMN_NAME, SerieInformationConsts.ARROWS_AMOUNT_COLUMN_NAME,
                SerieInformationConsts.TRAINING_TYPE_COLUMN_NAME
        ));

        db.execSQL(
            "INSERT INTO foobar (" +
                    SerieInformationConsts.ID_COLUMN_NAME + ", " +
                    SerieInformationConsts.DATETIME_COLUMN_NAME + ", " +
                    SerieInformationConsts.DISTANCE_COLUMN_NAME + ", " +
                    SerieInformationConsts.ARROWS_AMOUNT_COLUMN_NAME + ", " +
                    SerieInformationConsts.TRAINING_TYPE_COLUMN_NAME +
            ") " +
            "SELECT " +
                SerieInformationConsts.ID_COLUMN_NAME + ", " +
                SerieInformationConsts.DATETIME_COLUMN_NAME + ", " +
                SerieInformationConsts.DISTANCE_COLUMN_NAME + ", " +
                SerieInformationConsts.ARROWS_AMOUNT_COLUMN_NAME + ", " +
                SerieInformationConsts.TRAINING_TYPE_COLUMN_NAME + " " +
            "FROM " + SerieInformationConsts.TABLE_NAME
        );

        db.execSQL(
            "UPDATE foobar " +
            "SET " + SerieInformationConsts.TRAINING_TYPE_COLUMN_NAME + " = 0 " +
            "WHERE " +   SerieInformationConsts.TRAINING_TYPE_COLUMN_NAME + " IS NULL; "
        );
        db.execSQL(
            "DROP TABLE " +  SerieInformationConsts.TABLE_NAME
        );
        db.execSQL(
            "CREATE TABLE " + SerieInformationConsts.TABLE_NAME + "( " +
                    SerieInformationConsts.ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SerieInformationConsts.DATETIME_COLUMN_NAME + " LONG NOT NULL, " +
                    SerieInformationConsts.DISTANCE_COLUMN_NAME + " INTEGER NOT NULL, " +
                    SerieInformationConsts.ARROWS_AMOUNT_COLUMN_NAME + " INTEGER NOT NULL, " +
                    SerieInformationConsts.TRAINING_TYPE_COLUMN_NAME + " INTEGER NOT NULL " +
            ");"
        );
        db.execSQL(
            "INSERT INTO " + SerieInformationConsts.TABLE_NAME + "  (" +
                    SerieInformationConsts.ID_COLUMN_NAME + ", " +
                    SerieInformationConsts.DATETIME_COLUMN_NAME + ", " +
                    SerieInformationConsts.DISTANCE_COLUMN_NAME + ", " +
                    SerieInformationConsts.ARROWS_AMOUNT_COLUMN_NAME + ", " +
                    SerieInformationConsts.TRAINING_TYPE_COLUMN_NAME + " " +
            ")  " +
            "SELECT " +
                    SerieInformationConsts.ID_COLUMN_NAME + ", " +
                    SerieInformationConsts.DATETIME_COLUMN_NAME + ", " +
                    SerieInformationConsts.DISTANCE_COLUMN_NAME + ", " +
                    SerieInformationConsts.ARROWS_AMOUNT_COLUMN_NAME + ", " +
                    SerieInformationConsts.TRAINING_TYPE_COLUMN_NAME + " " +
            "FROM foobar"
        );
        db.execSQL("DROP TABLE foobar");
    }
}
