package ar.com.tzulberti.archerytraining.database.migrations;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ar.com.tzulberti.archerytraining.database.InsertConstantValues;
import ar.com.tzulberti.archerytraining.database.consts.PlayoffConsts;
import ar.com.tzulberti.archerytraining.database.consts.SerieInformationConsts;
import ar.com.tzulberti.archerytraining.database.consts.TournamentConstraintConsts;
import ar.com.tzulberti.archerytraining.database.consts.TournamentConsts;
import ar.com.tzulberti.archerytraining.model.common.TournamentConstraint;
import ar.com.tzulberti.archerytraining.model.playoff.Playoff;
import ar.com.tzulberti.archerytraining.model.tournament.Tournament;

/**
 * Created by tzulberti on 7/19/17.
 */

public class DatabaseMigration25 implements  IDatbasseMigration {

    @Override
    public int getCurentVersion() {
        return 25;
    }

    @Override
    public void executeMigration(SQLiteDatabase db) {
        InsertConstantValues insertConstantValues = new InsertConstantValues();
        insertConstantValues.insertTournamentConstrainsData(db);

        // Create the tournament_constraint_table for tournaments and playoff
        db.execSQL(
                "ALTER TABLE " + TournamentConsts.TABLE_NAME + " ADD COLUMN " + TournamentConsts.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME  + " INTEGER;"
        );


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
        // in both cases the column names are the same
        db.execSQL(
                "ALTER TABLE " + tableName + " ADD COLUMN " + TournamentConsts.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME  + " INTEGER;"
        );

        db.execSQL(
            "UPADTE " + tableName + " " +
            "SET " + TournamentConsts.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME  + " = ( " +
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
                "SET " + TournamentConsts.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME  + " = 1 " +
                "WHERE " +
                      tableName + "." + TournamentConsts.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME + " IS NULL "
        );

        // disable the FK for a minute because it have I am going to create a temprorary table
        db.setForeignKeyConstraintsEnabled(false);

        // create the temporary table without the distance/is_outdoor/target_size column
        db.execSQL("CREATE TABLE " + tableName + "_old AS SELECT * FROM " + tableName);
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
                            "FOREIGN KEY (" + TournamentConsts.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME + ") REFERENCES " + TournamentConstraintConsts.TABLE_NAME + " ( " +  TournamentConstraintConsts.ID_COLUMN_NAME + " )," +
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
                            "FOREIGN KEY (" + PlayoffConsts.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME + ") REFERENCES " + TournamentConstraintConsts.TABLE_NAME + " ( " +  TournamentConstraintConsts.ID_COLUMN_NAME + " )," +
                            ")"
            );
        }
        // copy all the existing data
        db.execSQL("INSERT INTO "  + tableName +  "(" + StringUtils.join(columnsToCopy, ",") + ") SELECT " + StringUtils.join(columnsToCopy, ",") + tableName + "_copy");
        db.execSQL("DROP TABLE " + tableName + "_copy");

        // reenable the FK information
        db.setForeignKeyConstraintsEnabled(true);
    }
}
