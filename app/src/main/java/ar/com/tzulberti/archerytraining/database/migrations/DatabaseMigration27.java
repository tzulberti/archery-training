package ar.com.tzulberti.archerytraining.database.migrations;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;


import java.util.Arrays;
import java.util.List;

import ar.com.tzulberti.archerytraining.database.InsertConstantValues;
import ar.com.tzulberti.archerytraining.database.consts.PlayoffConsts;

import ar.com.tzulberti.archerytraining.database.consts.TournamentConstraintConsts;
import ar.com.tzulberti.archerytraining.database.consts.TournamentConsts;


/**
 * Updates the schema so the tournament and playoff table reference the
 * a value on TournamentConstraints
 *
 * Created by tzulberti on 7/19/17.
 */

public class DatabaseMigration27 implements  IDatbasseMigration {

    @Override
    public int getCurentVersion() {
        return 26;
    }

    @Override
    public void executeMigration(SQLiteDatabase db) {

        InsertConstantValues insertConstantValues = new InsertConstantValues();
        insertConstantValues.insertTournamentConstrainsData(db);


        // Create the tournament_constraint_table for tournaments and playoff
        this.updateTableSchema(db, TournamentConsts.TABLE_NAME, Arrays.asList(
                TournamentConsts.ID_COLUMN_NAME,
                TournamentConsts.DATETIME_COLUMN_NAME,
                TournamentConsts.IS_TOURNAMENT_DATA_COLUMN_NAME,
                TournamentConsts.NAME_COLUMN_NAME,
                TournamentConsts.TOTAL_SCORE_COLUMN_NAME,
                TournamentConsts.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME
                )
        );

        this.updateTableSchema(db, PlayoffConsts.TABLE_NAME, Arrays.asList(
                PlayoffConsts.ID_COLUMN_NAME,
                PlayoffConsts.DATETIME_COLUMN_NAME,
                PlayoffConsts.NAME_COLUMN_NAME,
                PlayoffConsts.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME,
                PlayoffConsts.OPPONENT_PLAYOFF_SCORE_COLUMN_NAME,
                PlayoffConsts.USER_PLAYOFF_SCORE_COLUMN_NAME
                )
        );

    }

    private void updateTableSchema(SQLiteDatabase db, String tableName, List<String> columnsToCopy) {
        // the column doesn't exists so we should create it and set the values

        // in both cases the column names are the same
        db.execSQL(
                "ALTER TABLE " + tableName + " ADD COLUMN " + TournamentConsts.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME + " INTEGER;"
        );

        db.execSQL(
                "UPADTE " + tableName + " " +
                        "SET " + TournamentConsts.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME + " = ( " +
                        "SELECT " +
                        TournamentConstraintConsts.TABLE_NAME + "." + TournamentConstraintConsts.ID_COLUMN_NAME + " " +
                        "FROM " + TournamentConstraintConsts.TABLE_NAME + " " +
                        "WHERE " +
                        TournamentConstraintConsts.TABLE_NAME + "." + TournamentConstraintConsts.DISTANCE_COLUMN_NAME + " = " + tableName + ".distance" + " " +
                        "AND " + TournamentConstraintConsts.TABLE_NAME + "." + TournamentConstraintConsts.IS_OUTDOOR_COLUMN_NAME + " = 1 " +
                        ");"
        );

        // If no value was found then assume that it was a 70m tournament
        db.execSQL(
                "UPADTE " + tableName + " " +
                        "SET " + TournamentConsts.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME + " = 1 " +
                        "WHERE " +
                        tableName + "." + TournamentConsts.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME + " IS NULL "
        );


        db.execSQL("CREATE TABLE " + tableName + "_old AS SELECT * FROM " + tableName);

        // drop the old table to create a new one with the new schema
        db.execSQL("DROP TABLE " + tableName);


        // create the new table
        if (tableName.equals(TournamentConsts.TABLE_NAME)) {
            db.execSQL(
                    "CREATE TABLE " + TournamentConsts.TABLE_NAME + "( " +
                            TournamentConsts.ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            TournamentConsts.NAME_COLUMN_NAME + " TEXT NOT NULL, " +
                            TournamentConsts.DATETIME_COLUMN_NAME + " LONG NOT NULL, " +
                            TournamentConsts.IS_TOURNAMENT_DATA_COLUMN_NAME + " INTEGER NOT NULL, " +
                            TournamentConsts.TOTAL_SCORE_COLUMN_NAME + " INTEGER NOT NULL DEFAULT 0," +
                            TournamentConsts.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME + " INTEGER NOT NULL, " +
                            "FOREIGN KEY (" + TournamentConsts.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME + ") REFERENCES " + TournamentConstraintConsts.TABLE_NAME + " ( " +  TournamentConstraintConsts.ID_COLUMN_NAME + " ) " +
                            ");"
            );
        } else {
            db.execSQL(
                    "CREATE TABLE " + PlayoffConsts.TABLE_NAME + " (" +
                            PlayoffConsts.ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            PlayoffConsts.DATETIME_COLUMN_NAME + " LONG NOT NULL, " +
                            PlayoffConsts.NAME_COLUMN_NAME + " TEXT NOT NULL, " +
                            PlayoffConsts.OPPONENT_PLAYOFF_SCORE_COLUMN_NAME + " INTEGER NOT NULL DEFAULT 0, " +
                            PlayoffConsts.USER_PLAYOFF_SCORE_COLUMN_NAME + " INTEGER NOT NULL DEFAULT 0, " +
                            PlayoffConsts.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME + " INTEGER NOT NULL, " +
                            "FOREIGN KEY (" + PlayoffConsts.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME + ") REFERENCES " + TournamentConstraintConsts.TABLE_NAME + " ( " +  TournamentConstraintConsts.ID_COLUMN_NAME + " ) " +
                            ")"
            );
        }
        // copy all the existing data
        db.execSQL("INSERT INTO "  + tableName +  "(" + StringUtils.join(columnsToCopy, ", ") + ") SELECT " + StringUtils.join(columnsToCopy, ", ") + " FROM " + tableName + "_old");
        db.execSQL("DROP TABLE " + tableName + "_old");
    }
}
