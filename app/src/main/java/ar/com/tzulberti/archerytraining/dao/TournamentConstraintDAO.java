package ar.com.tzulberti.archerytraining.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.com.tzulberti.archerytraining.database.DatabaseHelper;
import ar.com.tzulberti.archerytraining.database.consts.BaseSerieConsts;
import ar.com.tzulberti.archerytraining.database.consts.SerieInformationConsts;
import ar.com.tzulberti.archerytraining.database.consts.TournamentConstraintConsts;
import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;
import ar.com.tzulberti.archerytraining.model.common.SeriesPerScore;
import ar.com.tzulberti.archerytraining.model.common.TournamentConstraint;

/**
 * Created by tzulberti on 7/19/17.
 */

public class TournamentConstraintDAO {

    private DatabaseHelper databaseHelper;

    public TournamentConstraintDAO(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void insertContraint(TournamentConstraint tournamentConstraint) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TournamentConstraintConsts.ID_COLUMN_NAME, tournamentConstraint.id);
        contentValues.put(TournamentConstraintConsts.NAME_COLUMN_NAME, tournamentConstraint.name);
        contentValues.put(TournamentConstraintConsts.ARROWS_PER_SERIES_COLUMN_NAME, tournamentConstraint.arrowsPerSeries);
        contentValues.put(TournamentConstraintConsts.DISTANCE_COLUMN_NAME, tournamentConstraint.distance);
        contentValues.put(TournamentConstraintConsts.IS_OUTDOOR_COLUMN_NAME, tournamentConstraint.isOutdoor ? 1: 0);
        contentValues.put(TournamentConstraintConsts.MIN_SCORE_COLUMN_NAME, tournamentConstraint.minScore);
        contentValues.put(TournamentConstraintConsts.SERIES_PER_ROUND_COLUMN_NAME, tournamentConstraint.seriesPerRound);
        contentValues.put(TournamentConstraintConsts.TARGET_IMAGE_COLUMN_NAME, tournamentConstraint.targetImage);

        db.insertOrThrow(TournamentConstraintConsts.TABLE_NAME, null, contentValues);
    }


    public Map<Integer, TournamentConstraint> getValues() {
        Map<Integer, TournamentConstraint> res = new HashMap<>();
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT " +
                    TournamentConstraintConsts.ID_COLUMN_NAME + ", " +
                    TournamentConstraintConsts.NAME_COLUMN_NAME + ", " +
                    TournamentConstraintConsts.DISTANCE_COLUMN_NAME + ", " +
                    TournamentConstraintConsts.SERIES_PER_ROUND_COLUMN_NAME + ", " +
                    TournamentConstraintConsts.ARROWS_PER_SERIES_COLUMN_NAME + ", " +
                    TournamentConstraintConsts.MIN_SCORE_COLUMN_NAME + ", " +
                    TournamentConstraintConsts.TARGET_IMAGE_COLUMN_NAME + ", " +
                    TournamentConstraintConsts.IS_OUTDOOR_COLUMN_NAME + " " +
                "FROM " +  TournamentConstraintConsts.TABLE_NAME,
                null
        );

        while (cursor.moveToNext()) {
            res.put(
                cursor.getInt(0),
                new TournamentConstraint(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getInt(3),
                    cursor.getInt(4),
                    cursor.getInt(5),
                    cursor.getString(6),
                    cursor.getInt(7) == 1
                )
            );
        }

        return res;
    }
}
