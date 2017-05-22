package ar.com.tzulberti.archerytraining.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.security.cert.Extension;
import java.util.ArrayList;
import java.util.List;

import ar.com.tzulberti.archerytraining.consts.SerieInformationConsts;
import ar.com.tzulberti.archerytraining.consts.TournamentConsts;
import ar.com.tzulberti.archerytraining.consts.TournamentSerieArrowConsts;
import ar.com.tzulberti.archerytraining.consts.TournamentSerieConsts;
import ar.com.tzulberti.archerytraining.helper.DatabaseHelper;
import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;
import ar.com.tzulberti.archerytraining.model.tournament.ExistingTournamentData;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerieArrowData;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerieData;

/**
 * Created by tzulberti on 5/17/17.
 */

public class TournamentDAO {

    private DatabaseHelper databaseHelper;

    public TournamentDAO(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public List<ExistingTournamentData> getExistingTournaments() {
        List<ExistingTournamentData> res = new ArrayList<>();
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                String.format(
                        "SELECT %s, %s, %s " +
                        "FROM %s " +
                        "ORDER BY %s DESC",
                        TournamentConsts.ID_COLUMN_NAME, TournamentConsts.NAME_COLUMN_NAME, TournamentConsts.DATETIME_COLUMN_NAME,
                        TournamentConsts.TABLE_NAME,
                        TournamentConsts.DATETIME_COLUMN_NAME
                ),
                null
        );

        while (cursor.moveToNext()) {
            res.add(new ExistingTournamentData(
                    cursor.getInt(0),
                    cursor.getString(1),
                    DatetimeHelper.databaseValueToDate(cursor.getLong(2))
            ));
        }
        return res;
    }


    public long createTournament(String name, int distance, int targetSize, boolean isTournament, boolean isOutdoor) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TournamentConsts.DISTANCE_COLUMN_NAME, distance);
        contentValues.put(TournamentConsts.NAME_COLUMN_NAME, name);
        contentValues.put(TournamentConsts.DATETIME_COLUMN_NAME, DatetimeHelper.getCurrentTime());
        contentValues.put(TournamentConsts.TARGET_SIZE_COLUMN_NAME, targetSize);
        contentValues.put(TournamentConsts.IS_OUTDOOR_COLUMN_NAME, isOutdoor);
        contentValues.put(TournamentConsts.IS_TOURNAMENT_DATA_COLUMN_NAME, isTournament);
        long id = db.insert(TournamentConsts.TABLE_NAME, null, contentValues);
        return id;
    }

    public List<TournamentSerieData> getTournamentSeriesInformation(long tournamentId) {
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                   "SELECT " +
                        TournamentSerieConsts.TABLE_NAME + "." + TournamentSerieConsts.ID_COLUMN_NAME + ", " +
                        TournamentSerieConsts.TABLE_NAME + "." + TournamentSerieConsts.SERIE_INDEX_COLUMN_NAME + ", " +
                        TournamentSerieArrowConsts.TABLE_NAME + "." + TournamentSerieArrowConsts.SCORE_COLUMN_NAME + ", " +
                        TournamentSerieArrowConsts.TABLE_NAME + "." + TournamentSerieArrowConsts.X_POSITION_COLUMN_NAME + ", " +
                        TournamentSerieArrowConsts.TABLE_NAME + "." + TournamentSerieArrowConsts.Y_POSITION_COLUMN_NAME + " " +
                    "FROM " +  TournamentSerieArrowConsts.TABLE_NAME  + " " +
                    "JOIN " + TournamentSerieConsts.TABLE_NAME + " " +
                        "ON " + TournamentSerieConsts.TABLE_NAME + "." + TournamentSerieConsts.ID_COLUMN_NAME + " = " + TournamentSerieArrowConsts.TABLE_NAME + "." + TournamentSerieArrowConsts.SERIE_ID_COLUMN_NAME + " " +
                    "WHERE " +
                        TournamentSerieArrowConsts.TABLE_NAME + "." + TournamentSerieArrowConsts.TOURNAMENT_ID_COLUMN_NAME + " = ?" +
                    "ORDER BY " + TournamentSerieConsts.TABLE_NAME + "." + TournamentSerieConsts.SERIE_INDEX_COLUMN_NAME,

                new String[]{String.valueOf(tournamentId)}
        );

        List<TournamentSerieData> res = new ArrayList<>();
        TournamentSerieData currentSerie = null;
        while (cursor.moveToNext()) {
            int serieId = cursor.getInt(0);
            if (currentSerie == null || currentSerie.id != serieId) {
                currentSerie = new TournamentSerieData();
                res.add(currentSerie);

                currentSerie.id = cursor.getInt(0);
                currentSerie.index = cursor.getInt(1);
                currentSerie.arrows = new ArrayList<>();
                currentSerie.totalScore = 0;
            }

            TournamentSerieArrowData arrowData = new TournamentSerieArrowData();
            currentSerie.arrows.add(arrowData);
            arrowData.score = cursor.getInt(2);
            currentSerie.totalScore += arrowData.score;
            arrowData.x = cursor.getInt(3);
            arrowData.y = cursor.getInt(4);
        }

        return res;
    }
}
