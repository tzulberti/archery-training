package ar.com.tzulberti.archerytraining.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ar.com.tzulberti.archerytraining.database.consts.BaseSerieArrowConsts;
import ar.com.tzulberti.archerytraining.database.consts.BaseSerieConsts;
import ar.com.tzulberti.archerytraining.database.consts.BaseSerieContainerConsts;
import ar.com.tzulberti.archerytraining.database.consts.TournamentConsts;
import ar.com.tzulberti.archerytraining.database.consts.TournamentSerieArrowConsts;
import ar.com.tzulberti.archerytraining.database.consts.TournamentSerieConsts;
import ar.com.tzulberti.archerytraining.database.DatabaseHelper;
import ar.com.tzulberti.archerytraining.helper.AppCache;
import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;
import ar.com.tzulberti.archerytraining.model.constrains.TournamentConstraint;
import ar.com.tzulberti.archerytraining.model.tournament.Tournament;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerieArrow;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerie;

/**
 * Created by tzulberti on 5/17/17.
 */

public class TournamentDAO extends BaseArrowSeriesDAO {


    public TournamentDAO(DatabaseHelper databaseHelper) {
        super(databaseHelper);
    }

    @Override
    protected BaseSerieArrowConsts getArrowsTable() {
        return new TournamentSerieArrowConsts();
    }

    @Override
    protected BaseSerieConsts getSeriesTable() {
        return new TournamentSerieConsts();
    }

    @Override
    protected BaseSerieContainerConsts getContainerTable() {return new TournamentConsts();}

    public List<Tournament> getExistingTournaments() {
        List<Tournament> res = new ArrayList<>();
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " +
                        TournamentConsts.ID_COLUMN_NAME + ", " +
                        TournamentConsts.NAME_COLUMN_NAME + ", " +
                        TournamentConsts.DATETIME_COLUMN_NAME + ", " +
                        TournamentConsts.TOTAL_SCORE_COLUMN_NAME  + ", " +
                        TournamentConsts.IS_TOURNAMENT_DATA_COLUMN_NAME + ", " +
                        BaseSerieContainerConsts.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME + " " +
                "FROM " + TournamentConsts.TABLE_NAME + " " +
                "ORDER BY " + TournamentConsts.DATETIME_COLUMN_NAME + " DESC",
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
            tournament.tournamentConstraintId = cursor.getInt(5);
            tournament.tournamentConstraint = AppCache.tournamentConstraintMap.get(tournament.tournamentConstraintId);
            res.add(tournament);
        }
        return res;
    }


    public Tournament createTournament(String name, boolean isTournament, TournamentConstraint tournamentConstraint) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        long databaseTimestamp = DatetimeHelper.getCurrentTime();
        ContentValues contentValues = new ContentValues();
        contentValues.put(BaseSerieContainerConsts.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME, tournamentConstraint.id);
        contentValues.put(TournamentConsts.NAME_COLUMN_NAME, name);
        contentValues.put(TournamentConsts.DATETIME_COLUMN_NAME, DatetimeHelper.getCurrentTime());
        contentValues.put(TournamentConsts.IS_TOURNAMENT_DATA_COLUMN_NAME, (isTournament) ? 1 : 0);
        contentValues.put(TournamentConsts.IS_TOURNAMENT_DATA_COLUMN_NAME, (isTournament) ? 1 : 0);
        long id = db.insertOrThrow(TournamentConsts.TABLE_NAME, null, contentValues);

        Tournament res = new Tournament(id, name, DatetimeHelper.databaseValueToDate(databaseTimestamp));
        res.isTournament = isTournament;
        res.tournamentConstraintId = tournamentConstraint.id;
        res.tournamentConstraint = tournamentConstraint;
        return res;
    }

    public Tournament getTournamentInformation(long tournamentId) {
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " +
                        TournamentConsts.NAME_COLUMN_NAME + ", " +
                        TournamentConsts.DATETIME_COLUMN_NAME + ", " +
                        TournamentConsts.TOTAL_SCORE_COLUMN_NAME + ", " +
                        BaseSerieContainerConsts.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME + ", " +
                        TournamentConsts.IS_TOURNAMENT_DATA_COLUMN_NAME + " " +
                "FROM " +  TournamentConsts.TABLE_NAME + " " +
                "WHERE " + TournamentConsts.ID_COLUMN_NAME + "= ?",
                new String[]{String.valueOf(tournamentId)}
        );
        cursor.moveToFirst();
        Tournament res = new Tournament(tournamentId, cursor.getString(0), DatetimeHelper.databaseValueToDate(cursor.getLong(1)));
        res.totalScore = cursor.getInt(2);
        res.tournamentConstraintId = cursor.getInt(3);
        res.tournamentConstraint = AppCache.tournamentConstraintMap.get(res.tournamentConstraintId);
        res.isTournament = (cursor.getInt(4) == 1);
        return res;
    }

    public List<TournamentSerie> getTournamentSeriesInformation(Tournament tournament) {

        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        String query = "SELECT " +
                TournamentSerieConsts.TABLE_NAME + "." + TournamentSerieConsts.ID_COLUMN_NAME + ", " +
                TournamentSerieConsts.TABLE_NAME + "." + TournamentSerieConsts.SERIE_INDEX_COLUMN_NAME + ", " +
                TournamentSerieArrowConsts.TABLE_NAME + "." + TournamentSerieArrowConsts.SCORE_COLUMN_NAME + ", " +
                TournamentSerieArrowConsts.TABLE_NAME + "." + TournamentSerieArrowConsts.X_POSITION_COLUMN_NAME + ", " +
                TournamentSerieArrowConsts.TABLE_NAME + "." + TournamentSerieArrowConsts.Y_POSITION_COLUMN_NAME + ", " +
                TournamentSerieArrowConsts.TABLE_NAME + "." + TournamentSerieArrowConsts.IS_X_COLUMN_NAME + ", " +
                TournamentSerieArrowConsts.TABLE_NAME + "." + TournamentSerieArrowConsts.ID_COLUMN_NAME + " " +
            "FROM " +  TournamentSerieConsts.TABLE_NAME  + " " +
            "LEFT JOIN " + TournamentSerieArrowConsts.TABLE_NAME + " " +
                "ON " + TournamentSerieConsts.TABLE_NAME + "." + TournamentSerieConsts.ID_COLUMN_NAME + " = " + TournamentSerieArrowConsts.TABLE_NAME + "." + TournamentSerieArrowConsts.SERIE_INDEX_COLUMN_NAME + " " +
            "WHERE " +
                TournamentSerieConsts.TABLE_NAME + "." + TournamentSerieArrowConsts.TOURNAMENT_ID_COLUMN_NAME + " = ? " +
            "ORDER BY " +
                TournamentSerieConsts.TABLE_NAME + "." + TournamentSerieConsts.SERIE_INDEX_COLUMN_NAME + ", " +
                TournamentSerieArrowConsts.TABLE_NAME + "." + TournamentSerieArrowConsts.ID_COLUMN_NAME;

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(tournament.id)});


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

            if (cursor.isNull(6)) {
                // if the arrowData id is null, then it means that it couldn't do
                // the left join so the serie is empyt
                continue;
            }

            TournamentSerieArrow arrowData = new TournamentSerieArrow();
            arrowData.score = cursor.getInt(2);
            arrowData.xPosition = cursor.getInt(3);
            arrowData.yPosition = cursor.getInt(4);
            arrowData.isX = (cursor.getInt(5) == 1);
            arrowData.id = cursor.getInt(6);

            currentSerie.arrows.add(arrowData);
            currentSerie.totalScore += arrowData.score;
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
                "SELECT " +
                    "MAX(" + TournamentSerieConsts.TABLE_NAME + "." + TournamentSerieConsts.SERIE_INDEX_COLUMN_NAME + ") " +
                "FROM " + TournamentSerieConsts.TABLE_NAME + " " +
                "WHERE " +
                    TournamentSerieConsts.TABLE_NAME + "." + TournamentSerieConsts.TOURNAMENT_ID_COLUMN_NAME + " = ?",
                new String[]{String.valueOf(tournamet.id)}
        );
        boolean hasData = cursor.moveToNext();
        int serieIndex = 1;
        if (hasData) {
            serieIndex = cursor.getInt(0) + 1;
        }

        ContentValues contentValues = new ContentValues();

        contentValues.put(TournamentSerieConsts.TOURNAMENT_ID_COLUMN_NAME, tournamet.id);
        contentValues.put(TournamentSerieConsts.SERIE_INDEX_COLUMN_NAME, serieIndex);
        contentValues.put(TournamentSerieConsts.TOTAL_SCORE_COLUMN_NAME, 0);

        long id = db.insertOrThrow(TournamentSerieConsts.TABLE_NAME, null, contentValues);
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

        // delete the arrow information is there is any
        int deletedArrows = db.delete(
                TournamentSerieArrowConsts.TABLE_NAME,
                TournamentSerieArrowConsts.SERIE_INDEX_COLUMN_NAME + "= ?",
                new String[]{String.valueOf(tournamentSerie.id)}
        );

        if (tournamentSerie.id != 0 && deletedArrows > 0) {
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
        }

        // if the information already existed then delete it
        db.delete(
                TournamentSerieConsts.TABLE_NAME,
                TournamentSerieConsts.TOURNAMENT_ID_COLUMN_NAME + "= ? AND " + TournamentSerieConsts.SERIE_INDEX_COLUMN_NAME + "= ?",
                new String[]{String.valueOf(tournamentSerie.tournament.id), String.valueOf(tournamentSerie.index)}
        );

        ContentValues contentValues = new ContentValues();
        contentValues.put(TournamentSerieConsts.TOURNAMENT_ID_COLUMN_NAME, tournamentSerie.tournament.id);
        contentValues.put(TournamentSerieConsts.SERIE_INDEX_COLUMN_NAME, tournamentSerie.index);
        contentValues.put(TournamentSerieConsts.TOTAL_SCORE_COLUMN_NAME, tournamentSerie.totalScore);
        tournamentSerie.id = db.insertOrThrow(TournamentSerieConsts.TABLE_NAME, null, contentValues);

        for (TournamentSerieArrow serieArrowData : tournamentSerie.arrows) {
            ContentValues contentValuesArrow = new ContentValues();
            contentValuesArrow.put(TournamentSerieArrowConsts.TOURNAMENT_ID_COLUMN_NAME, tournamentSerie.tournament.id);
            contentValuesArrow.put(TournamentSerieArrowConsts.SERIE_INDEX_COLUMN_NAME, tournamentSerie.id);
            contentValuesArrow.put(TournamentSerieArrowConsts.SCORE_COLUMN_NAME, serieArrowData.score);
            contentValuesArrow.put(TournamentSerieArrowConsts.X_POSITION_COLUMN_NAME, serieArrowData.xPosition);
            contentValuesArrow.put(TournamentSerieArrowConsts.Y_POSITION_COLUMN_NAME, serieArrowData.yPosition);
            contentValuesArrow.put(TournamentSerieArrowConsts.IS_X_COLUMN_NAME, serieArrowData.isX);
            serieArrowData.id = db.insertOrThrow(TournamentSerieArrowConsts.TABLE_NAME, null, contentValuesArrow);
        }

        // update the tournament information
        db.execSQL(
                String.format("UPDATE %s SET %s = %s + ? WHERE %s = ?",
                        TournamentConsts.TABLE_NAME, TournamentConsts.TOTAL_SCORE_COLUMN_NAME, TournamentConsts.TOTAL_SCORE_COLUMN_NAME, TournamentConsts.ID_COLUMN_NAME),
                new String[]{String.valueOf(tournamentSerie.totalScore), String.valueOf(tournamentSerie.tournament.id)}
        );

        // update the current instance of the tournament information
        tournamentSerie.tournament.totalScore += tournamentSerie.totalScore;

        return tournamentSerie;
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

    public void deleteSerie(long tournamentSerieId) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        db.delete(
                TournamentSerieArrowConsts.TABLE_NAME,
                TournamentSerieArrowConsts.SERIE_INDEX_COLUMN_NAME + "= ? ",
                new String[]{String.valueOf(tournamentSerieId)}
        );

        db.delete(
                TournamentSerieConsts.TABLE_NAME,
                TournamentSerieConsts.ID_COLUMN_NAME + "= ? ",
                new String[]{String.valueOf(tournamentSerieId)}
        );
    }
}
