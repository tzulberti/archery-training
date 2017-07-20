package ar.com.tzulberti.archerytraining.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import ar.com.tzulberti.archerytraining.database.DatabaseHelper;
import ar.com.tzulberti.archerytraining.database.consts.SerieInformationConsts;
import ar.com.tzulberti.archerytraining.database.consts.TournamentConstraintConsts;
import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;
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

        db.insert(TournamentConstraintConsts.TABLE_NAME, null, contentValues);
    }
}
