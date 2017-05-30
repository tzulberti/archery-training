package ar.com.tzulberti.archerytraining.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ar.com.tzulberti.archerytraining.consts.TournamentConsts;
import ar.com.tzulberti.archerytraining.consts.TournamentSerieArrowConsts;
import ar.com.tzulberti.archerytraining.consts.TournamentSerieConsts;
import ar.com.tzulberti.archerytraining.helper.DatabaseHelper;
import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;
import ar.com.tzulberti.archerytraining.model.tournament.Tournament;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerieArrow;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerie;

/**
 * Created by tzulberti on 5/17/17.
 */

public class TournamentDAO {

    private DatabaseHelper databaseHelper;

    public TournamentDAO(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public List<Tournament> getExistingTournaments() {
        List<Tournament> res = new ArrayList<>();
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                String.format(
                        "SELECT %s, %s, %s, " +
                            "%s, %s " +
                        "FROM %s " +
                        "ORDER BY %s DESC",
                        TournamentConsts.ID_COLUMN_NAME, TournamentConsts.NAME_COLUMN_NAME, TournamentConsts.DATETIME_COLUMN_NAME,
                                TournamentConsts.TOTAL_SCORE_COLUMN_NAME, TournamentConsts.IS_TOURNAMENT_DATA_COLUMN_NAME,
                        TournamentConsts.TABLE_NAME,
                        TournamentConsts.DATETIME_COLUMN_NAME
                ),
                null
        );

        while (cursor.moveToNext()) {
            Tournament tournament = new Tournament(
                    cursor.getInt(0),
                    cursor.getString(1),
                    DatetimeHelper.databaseValueToDate(cursor.getLong(2))
            );
            tournament.totalScore = cursor.getInt(3);
            tournament.isTournament = (cursor.getInt(4) == 1);
            res.add(tournament);

        }
        return res;
    }


    public Tournament createTournament(String name, int distance, int targetSize, boolean isOutdoor, boolean isTournament) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        long databaseTimestamp = DatetimeHelper.getCurrentTime();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TournamentConsts.DISTANCE_COLUMN_NAME, distance);
        contentValues.put(TournamentConsts.NAME_COLUMN_NAME, name);
        contentValues.put(TournamentConsts.DATETIME_COLUMN_NAME, DatetimeHelper.getCurrentTime());
        contentValues.put(TournamentConsts.TARGET_SIZE_COLUMN_NAME, targetSize);
        contentValues.put(TournamentConsts.IS_OUTDOOR_COLUMN_NAME, (isOutdoor) ? 1 : 0);
        contentValues.put(TournamentConsts.IS_TOURNAMENT_DATA_COLUMN_NAME, (isTournament) ? 1 : 0);
        long id = db.insert(TournamentConsts.TABLE_NAME, null, contentValues);

        Tournament res = new Tournament(id, name, DatetimeHelper.databaseValueToDate(databaseTimestamp));
        res.isOutdoor = isOutdoor;
        res.isTournament = isTournament;
        res.targetSize = targetSize;
        res.distance = distance;
        return res;
    }

    public List<TournamentSerie> getTournamentSeriesInformation(Tournament tournament) {

        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                   "SELECT " +
                        TournamentSerieConsts.TABLE_NAME + "." + TournamentSerieConsts.ID_COLUMN_NAME + ", " +
                        TournamentSerieConsts.TABLE_NAME + "." + TournamentSerieConsts.SERIE_INDEX_COLUMN_NAME + ", " +
                        TournamentSerieArrowConsts.TABLE_NAME + "." + TournamentSerieArrowConsts.SCORE_COLUMN_NAME + ", " +
                        TournamentSerieArrowConsts.TABLE_NAME + "." + TournamentSerieArrowConsts.X_POSITION_COLUMN_NAME + ", " +
                        TournamentSerieArrowConsts.TABLE_NAME + "." + TournamentSerieArrowConsts.Y_POSITION_COLUMN_NAME + ", " +
                           TournamentSerieArrowConsts.TABLE_NAME + "." + TournamentSerieArrowConsts.IS_X_COLUMN_NAME + " " +
                    "FROM " +  TournamentSerieArrowConsts.TABLE_NAME  + " " +
                    "JOIN " + TournamentSerieConsts.TABLE_NAME + " " +
                        "ON " + TournamentSerieConsts.TABLE_NAME + "." + TournamentSerieConsts.ID_COLUMN_NAME + " = " + TournamentSerieArrowConsts.TABLE_NAME + "." + TournamentSerieArrowConsts.SERIE_ID_COLUMN_NAME + " " +
                    "WHERE " +
                        TournamentSerieArrowConsts.TABLE_NAME + "." + TournamentSerieArrowConsts.TOURNAMENT_ID_COLUMN_NAME + " = ?" +
                    "ORDER BY " + TournamentSerieConsts.TABLE_NAME + "." + TournamentSerieConsts.SERIE_INDEX_COLUMN_NAME,

                new String[]{String.valueOf(tournament.id)}
        );

        List<TournamentSerie> res = new ArrayList<>();
        TournamentSerie currentSerie = null;
        while (cursor.moveToNext()) {
            int serieId = cursor.getInt(0);
            if (currentSerie == null || currentSerie.id != serieId) {
                currentSerie = new TournamentSerie();
                res.add(currentSerie);

                currentSerie.tournament = tournament;
                currentSerie.id = cursor.getInt(0);
                currentSerie.index = cursor.getInt(1);
                currentSerie.arrows = new ArrayList<>();
                currentSerie.totalScore = 0;
                tournament.series.add(currentSerie);
            }

            TournamentSerieArrow arrowData = new TournamentSerieArrow();
            currentSerie.arrows.add(arrowData);
            arrowData.score = cursor.getInt(2);
            currentSerie.totalScore += arrowData.score;

            arrowData.xPosition = cursor.getInt(3);
            arrowData.yPosition = cursor.getInt(4);
            arrowData.isX = (cursor.getInt(5) == 1);
        }

        return res;
    }

    /**
     * Creates a new serie for the current tournament.
     *
     * If it can not create more series (there are already the max number of series
     * for that tournament), then it will return null;
     *
     * @param tournamet the tournament for which create a new serie
     * @return the new serie if it is possible to create one, else NULL
     */
    public TournamentSerie createNewSerie(Tournament tournamet) {
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT max(" + TournamentSerieConsts.TABLE_NAME + "." + TournamentSerieConsts.SERIE_INDEX_COLUMN_NAME + ") " +
                        "FROM " + TournamentSerieConsts.TABLE_NAME + " " +
                        "WHERE " + TournamentSerieConsts.TABLE_NAME + "." + TournamentSerieConsts.TOURNAMENT_ID_COLUMN_NAME + " = ?",
                new String[]{String.valueOf(tournamet.id)}
        );
        boolean hasData = cursor.moveToNext();
        int serieIndex = 1;
        if (hasData) {
            serieIndex = cursor.getInt(0) + 1;
        }
        System.err.println(String.format("Has Data: %s, serieIndex: %s, IsOutdoor: %s", hasData, serieIndex, tournamet.isOutdoor));

        if (tournamet.isOutdoor && serieIndex > 12) {
            // already has the max number of series for this outdoor tournament
            return null;
        } else if (!tournamet.isOutdoor && serieIndex > 20) {
            // already has the max number of series for the indoor tournament
            return null;
        }

        ContentValues contentValues = new ContentValues();

        contentValues.put(TournamentSerieConsts.TOURNAMENT_ID_COLUMN_NAME, tournamet.id);
        contentValues.put(TournamentSerieConsts.SERIE_INDEX_COLUMN_NAME, serieIndex);
        contentValues.put(TournamentSerieConsts.TOTAL_SCORE_COLUMN_NAME, 0);

        long id = db.insert(TournamentSerieConsts.TABLE_NAME, null, contentValues);
        TournamentSerie res = new TournamentSerie();
        res.id = id;
        res.arrows = new ArrayList<>();
        res.index = serieIndex;
        res.totalScore = 0;
        res.tournament = tournamet;
        tournamet.series.add(res);
        return res;
    }


    /**
     * Updates the database with all the information of the tournament serie, and it
     * will return the same instance with all the ids of the object set
     *
     * @param tournamentSerie
     * @return
     */
    public TournamentSerie saveTournamentSerieInformation(TournamentSerie tournamentSerie) {
        // delete existing values for this serie and create new ones
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();



        db.delete(
                TournamentSerieConsts.TABLE_NAME,
                TournamentSerieConsts.TOURNAMENT_ID_COLUMN_NAME + "= ? AND " + TournamentSerieConsts.SERIE_INDEX_COLUMN_NAME + "= ?",
                new String[]{String.valueOf(tournamentSerie.tournament.id), String.valueOf(tournamentSerie.index)}
        );

        if (tournamentSerie.id != 0) {
            // Update the tournament with the score of the current serie
            Cursor cursor = db.rawQuery(
                    String.format("SELECT SUM(%s) FROM %s WHERE %s = ?",
                            TournamentSerieConsts.TOTAL_SCORE_COLUMN_NAME, TournamentSerieConsts.TABLE_NAME, TournamentSerieConsts.ID_COLUMN_NAME),
                    new String[]{String.valueOf(tournamentSerie.id)}
            );
            cursor.moveToNext();

            int databaseSerieTotalScore = cursor.getInt(0);
            db.execSQL(
                String.format("UPDATE %s SET %s = %s - ? WHERE %s = ?",
                        TournamentConsts.TABLE_NAME, TournamentConsts.TOTAL_SCORE_COLUMN_NAME, TournamentConsts.TOTAL_SCORE_COLUMN_NAME, TournamentConsts.ID_COLUMN_NAME),
                new String[]{String.valueOf(databaseSerieTotalScore), String.valueOf(tournamentSerie.tournament.id)}
            );
            tournamentSerie.tournament.totalScore -= databaseSerieTotalScore;


            // delete the arrow information is there is any
            db.delete(
                    TournamentSerieArrowConsts.TABLE_NAME,
                    TournamentSerieArrowConsts.SERIE_ID_COLUMN_NAME + "= ?",
                    new String[]{String.valueOf(tournamentSerie.id)}
            );
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(TournamentSerieConsts.TOURNAMENT_ID_COLUMN_NAME, tournamentSerie.tournament.id);
        contentValues.put(TournamentSerieConsts.SERIE_INDEX_COLUMN_NAME, tournamentSerie.index);
        contentValues.put(TournamentSerieConsts.TOTAL_SCORE_COLUMN_NAME, tournamentSerie.totalScore);
        tournamentSerie.id = db.insert(TournamentSerieConsts.TABLE_NAME, null, contentValues);

        for (TournamentSerieArrow serieArrowData : tournamentSerie.arrows) {
            ContentValues contentValuesArrow = new ContentValues();
            contentValuesArrow.put(TournamentSerieArrowConsts.TOURNAMENT_ID_COLUMN_NAME, tournamentSerie.tournament.id);
            contentValuesArrow.put(TournamentSerieArrowConsts.SERIE_ID_COLUMN_NAME, tournamentSerie.id);
            contentValuesArrow.put(TournamentSerieArrowConsts.SCORE_COLUMN_NAME, serieArrowData.score);
            contentValuesArrow.put(TournamentSerieArrowConsts.X_POSITION_COLUMN_NAME, serieArrowData.xPosition);
            contentValuesArrow.put(TournamentSerieArrowConsts.Y_POSITION_COLUMN_NAME, serieArrowData.yPosition);
            contentValuesArrow.put(TournamentSerieArrowConsts.IS_X_COLUMN_NAME, serieArrowData.isX);
            serieArrowData.id = db.insert(TournamentSerieArrowConsts.TABLE_NAME, null, contentValuesArrow);
        }

        // update the tournament information
        db.execSQL(
                String.format("UPDATE %s SET %s = %s + ? WHERE %s = ?",
                        TournamentConsts.TABLE_NAME, TournamentConsts.TOTAL_SCORE_COLUMN_NAME, TournamentConsts.TOTAL_SCORE_COLUMN_NAME, TournamentConsts.ID_COLUMN_NAME),
                new String[]{String.valueOf(tournamentSerie.totalScore), String.valueOf(tournamentSerie.tournament.id)}
        );
        System.err.println(String.format("SerieTotalScore: %s, TournamentTotalScore: %s", tournamentSerie.totalScore, tournamentSerie.tournament.totalScore));

        // update the current instance of the tournament information
        tournamentSerie.tournament.totalScore += tournamentSerie.totalScore;

        return tournamentSerie;
    }

    public Tournament getTournamentInformation(long tournamentId) {
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                String.format(
                        "SELECT %s, %s, %s, " +
                                "%s, %s " +
                        "FROM %s " +
                        "WHERE %s = ?",
                        TournamentConsts.NAME_COLUMN_NAME, TournamentConsts.DATETIME_COLUMN_NAME, TournamentConsts.DISTANCE_COLUMN_NAME,
                            TournamentConsts.IS_OUTDOOR_COLUMN_NAME, TournamentConsts.TOTAL_SCORE_COLUMN_NAME,
                        TournamentConsts.TABLE_NAME,
                        TournamentConsts.ID_COLUMN_NAME
                ),
                new String[]{String.valueOf(tournamentId)}
        );
        cursor.moveToFirst();
        Tournament res = new Tournament(tournamentId, cursor.getString(0), DatetimeHelper.databaseValueToDate(cursor.getLong(1)));
        res.distance = cursor.getInt(2);
        res.isOutdoor = (cursor.getInt(3) == 1);
        res.totalScore = cursor.getInt(4);
        return res;
    }

    /**
     * Deletes the existing tournament with all the series/arrows information
     * @param tournamentId the id of the tournament to delete
     */
    public void deleteTournament(long tournamentId) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();

        db.delete(
                TournamentSerieArrowConsts.TABLE_NAME,
                TournamentSerieArrowConsts.TOURNAMENT_ID_COLUMN_NAME + "= ? ",
                new String[]{String.valueOf(tournamentId)}
        );

        db.delete(
                TournamentSerieConsts.TABLE_NAME,
                TournamentSerieConsts.TOURNAMENT_ID_COLUMN_NAME + "= ? ",
                new String[]{String.valueOf(tournamentId)}
        );

        db.delete(
                TournamentConsts.TABLE_NAME,
                TournamentConsts.ID_COLUMN_NAME + "= ? ",
                new String[]{String.valueOf(tournamentId)}
        );
    }
}
