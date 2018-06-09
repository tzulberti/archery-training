package ar.com.tzulberti.archerytraining.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ar.com.tzulberti.archerytraining.database.DatabaseHelper;
import ar.com.tzulberti.archerytraining.helper.AppCache;
import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;
import ar.com.tzulberti.archerytraining.model.base.AbstractArrow;
import ar.com.tzulberti.archerytraining.model.base.ISerie;
import ar.com.tzulberti.archerytraining.model.base.ISerieContainer;
import ar.com.tzulberti.archerytraining.model.constrains.TournamentConstraint;
import ar.com.tzulberti.archerytraining.model.tournament.Tournament;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerieArrow;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerie;

/**
 * DAO used to manage all the information related to tournaments
 *
 * Created by tzulberti on 5/17/17.
 */
public class TournamentDAO extends BaseArrowSeriesDAO {


    public TournamentDAO(DatabaseHelper databaseHelper) {
        super(databaseHelper);
    }

    @Override
    protected AbstractArrow getArrowsTable() {
        return new TournamentSerieArrow();
    }

    @Override
    protected ISerie getSeriesTable() {
        return new TournamentSerie();
    }

    @Override
    protected ISerieContainer getContainerTable() {return new Tournament();}

    public List<Tournament> getExistingTournaments() {
        List<Tournament> res = new ArrayList<>();
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " +
                        Tournament.ID_COLUMN_NAME + ", " +
                        Tournament.NAME_COLUMN_NAME + ", " +
                        Tournament.DATETIME_COLUMN_NAME + ", " +
                        Tournament.TOTAL_SCORE_COLUMN_NAME  + ", " +
                        Tournament.IS_TOURNAMENT_DATA_COLUMN_NAME + ", " +
                        Tournament.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME + " " +
                "FROM " + Tournament.TABLE_NAME + " " +
                "ORDER BY " + Tournament.DATETIME_COLUMN_NAME + " DESC",
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
        cursor.close();
        return res;
    }


    public Tournament createTournament(String name, boolean isTournament, TournamentConstraint tournamentConstraint) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        long databaseTimestamp = DatetimeHelper.getCurrentTime();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ISerieContainer.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME, tournamentConstraint.id);
        contentValues.put(Tournament.NAME_COLUMN_NAME, name);
        contentValues.put(Tournament.DATETIME_COLUMN_NAME, DatetimeHelper.getCurrentTime());
        contentValues.put(Tournament.IS_TOURNAMENT_DATA_COLUMN_NAME, (isTournament) ? 1 : 0);
        long id = db.insertOrThrow(Tournament.TABLE_NAME, null, contentValues);

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
                        Tournament.NAME_COLUMN_NAME + ", " +
                        Tournament.DATETIME_COLUMN_NAME + ", " +
                        Tournament.TOTAL_SCORE_COLUMN_NAME + ", " +
                        Tournament.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME + ", " +
                        Tournament.IS_TOURNAMENT_DATA_COLUMN_NAME + " " +
                "FROM " +  Tournament.TABLE_NAME + " " +
                "WHERE " + Tournament.ID_COLUMN_NAME + "= ?",
                new String[]{String.valueOf(tournamentId)}
        );
        cursor.moveToFirst();
        Tournament res = new Tournament(tournamentId, cursor.getString(0), DatetimeHelper.databaseValueToDate(cursor.getLong(1)));
        res.totalScore = cursor.getInt(2);
        res.tournamentConstraintId = cursor.getInt(3);
        res.tournamentConstraint = AppCache.tournamentConstraintMap.get(res.tournamentConstraintId);
        res.isTournament = (cursor.getInt(4) == 1);
        cursor.close();
        return res;
    }

    public void getTournamentSeriesInformation(Tournament tournament) {

        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        String query = "SELECT " +
                TournamentSerie.TABLE_NAME + "." + TournamentSerie.ID_COLUMN_NAME + ", " +
                TournamentSerie.TABLE_NAME + "." + TournamentSerie.SERIE_INDEX_COLUMN_NAME + ", " +
                TournamentSerieArrow.TABLE_NAME + "." + TournamentSerieArrow.SCORE_COLUMN_NAME + ", " +
                TournamentSerieArrow.TABLE_NAME + "." + TournamentSerieArrow.X_POSITION_COLUMN_NAME + ", " +
                TournamentSerieArrow.TABLE_NAME + "." + TournamentSerieArrow.Y_POSITION_COLUMN_NAME + ", " +
                TournamentSerieArrow.TABLE_NAME + "." + TournamentSerieArrow.IS_X_COLUMN_NAME + ", " +
                TournamentSerieArrow.TABLE_NAME + "." + TournamentSerieArrow.ID_COLUMN_NAME + ", " +
                TournamentSerie.TABLE_NAME + "." + TournamentSerie.ROUND_INDEX_COLUMN_NAME + " " +
            "FROM " +  TournamentSerie.TABLE_NAME  + " " +
            "LEFT JOIN " + TournamentSerieArrow.TABLE_NAME + " " +
                "ON " + TournamentSerie.TABLE_NAME + "." + TournamentSerie.ID_COLUMN_NAME + " = " + TournamentSerieArrow.TABLE_NAME + "." + TournamentSerieArrow.SERIE_INDEX_COLUMN_NAME + " " +
            "WHERE " +
                TournamentSerie.TABLE_NAME + "." + TournamentSerie.TOURNAMENT_ID_COLUMN_NAME + " = ? " +
            "ORDER BY " +
                TournamentSerie.TABLE_NAME + "." + TournamentSerie.SERIE_INDEX_COLUMN_NAME + ", " +
                TournamentSerieArrow.TABLE_NAME + "." + TournamentSerieArrow.IS_X_COLUMN_NAME + " DESC, " +
                TournamentSerieArrow.TABLE_NAME + "." + TournamentSerieArrow.SCORE_COLUMN_NAME + " DESC, " +
                TournamentSerieArrow.TABLE_NAME + "." + TournamentSerieArrow.ID_COLUMN_NAME;

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(tournament.id)});



        TournamentSerie currentSerie = null;
        while (cursor.moveToNext()) {
            int serieId = cursor.getInt(0);
            if (currentSerie == null || currentSerie.id != serieId) {
                currentSerie = new TournamentSerie();

                currentSerie.tournament = tournament;
                currentSerie.id = cursor.getInt(0);
                currentSerie.index = cursor.getInt(1);
                currentSerie.arrows = new ArrayList<>();
                currentSerie.totalScore = 0;
                currentSerie.roundIndex = cursor.getInt(7);
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
        cursor.close();
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
                    "MAX(" + TournamentSerie.TABLE_NAME + "." + TournamentSerie.SERIE_INDEX_COLUMN_NAME + ") " +
                "FROM " + TournamentSerie.TABLE_NAME + " " +
                "WHERE " +
                    TournamentSerie.TABLE_NAME + "." + TournamentSerie.TOURNAMENT_ID_COLUMN_NAME + " = ?",
                new String[]{String.valueOf(tournamet.id)}
        );
        boolean hasData = cursor.moveToNext();
        int serieIndex = 1;
        if (hasData) {
            serieIndex = cursor.getInt(0) + 1;
        }
        cursor.close();
        int roundIndex = tournamet.getTournamentConstraint().getRoundIndex(serieIndex);

        ContentValues contentValues = new ContentValues();
        contentValues.put(TournamentSerie.TOURNAMENT_ID_COLUMN_NAME, tournamet.id);
        contentValues.put(TournamentSerie.SERIE_INDEX_COLUMN_NAME, serieIndex);
        contentValues.put(TournamentSerie.TOTAL_SCORE_COLUMN_NAME, 0);
        contentValues.put(TournamentSerie.ROUND_INDEX_COLUMN_NAME, roundIndex);

        long id = db.insertOrThrow(TournamentSerie.TABLE_NAME, null, contentValues);
        TournamentSerie res = new TournamentSerie();
        res.id = id;
        res.arrows = new ArrayList<>();
        res.index = serieIndex;
        res.totalScore = 0;
        res.tournament = tournamet;
        res.roundIndex = roundIndex;
        tournamet.series.add(res);
        return res;
    }


    /**
     * Updates the database with all the information of the tournament serie, and it
     * will return the same instance with all the ids of the object set
     *
     * @param tournamentSerie the serie to update
     */
    public void saveTournamentSerieInformation(TournamentSerie tournamentSerie) {
        // delete existing values for this serie and create new ones
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        boolean alreadyExistsTournamentSerie = this.checkIfExists(TournamentSerie.TABLE_NAME, tournamentSerie.id);

        if (alreadyExistsTournamentSerie) {
            // update the serie total score
            db.execSQL(
                "UPDATE " + TournamentSerie.TABLE_NAME + " " +
                "SET " +
                    TournamentSerie.TOTAL_SCORE_COLUMN_NAME + " = ?, " +
                    TournamentSerie.IS_SYNCED + " = 0 " +
                "WHERE " +
                    TournamentSerie.ID_COLUMN_NAME + " = ?",
                 new String[] {String.valueOf(tournamentSerie.totalScore), String.valueOf(tournamentSerie.id)}

            );

            // update the existing arrows information, but it needs to get the existing ones from
            // the database
            Cursor cursor = db.rawQuery(
                "SELECT " +
                    TournamentSerieArrow.SCORE_COLUMN_NAME + ", " +
                    TournamentSerieArrow.IS_X_COLUMN_NAME + ", " +
                    TournamentSerieArrow.ID_COLUMN_NAME + " " +
                "FROM " + TournamentSerieArrow.TABLE_NAME + " " +
                "WHERE " +
                    TournamentSerieArrow.SERIE_INDEX_COLUMN_NAME + " = ? " +
                "ORDER BY " +
                    TournamentSerieArrow.IS_X_COLUMN_NAME + " DESC, " +
                    TournamentSerieArrow.SCORE_COLUMN_NAME + " DESC, " +
                    TournamentSerieArrow.ID_COLUMN_NAME,
                new String[]{String.valueOf(tournamentSerie.id)}
            );
            int currentIndex = 0;
            while (cursor.moveToNext()) {
                int existingScore = cursor.getInt(0);
                boolean existingIsX = (cursor.getInt(1) == 1);
                int id = cursor.getInt(2);

                TournamentSerieArrow tournamentSerieArrow = tournamentSerie.arrows.get(currentIndex);
                tournamentSerieArrow.id = id;

                if (existingIsX != tournamentSerieArrow.isX || existingScore != tournamentSerieArrow.score) {
                    // if one of the values are different, then it should be updated
                    db.execSQL(
                        "UPDATE " + TournamentSerieArrow.TABLE_NAME + " " +
                        "SET " +
                            TournamentSerieArrow.SCORE_COLUMN_NAME + " = ?, " +
                            TournamentSerieArrow.IS_X_COLUMN_NAME + " = ?, " +
                            TournamentSerieArrow.IS_SYNCED + " = 0 " +
                        "WHERE " +
                            TournamentSerieArrow.ID_COLUMN_NAME + " = ?",
                        new String[] {String.valueOf(tournamentSerieArrow.score), String.valueOf(tournamentSerieArrow.isX ? 1 : 0), String.valueOf(id)}
                    );
                }

                currentIndex += 1;
            }
            cursor.close();

            // create the missing values
            while (currentIndex < tournamentSerie.arrows.size()) {
                ContentValues contentValuesArrow = new ContentValues();
                TournamentSerieArrow serieArrowData = tournamentSerie.arrows.get(currentIndex);
                contentValuesArrow.put(TournamentSerieArrow.TOURNAMENT_ID_COLUMN_NAME, tournamentSerie.tournament.id);
                contentValuesArrow.put(TournamentSerieArrow.SERIE_INDEX_COLUMN_NAME, tournamentSerie.id);
                contentValuesArrow.put(TournamentSerieArrow.SCORE_COLUMN_NAME, serieArrowData.score);
                contentValuesArrow.put(TournamentSerieArrow.X_POSITION_COLUMN_NAME, serieArrowData.xPosition);
                contentValuesArrow.put(TournamentSerieArrow.Y_POSITION_COLUMN_NAME, serieArrowData.yPosition);
                contentValuesArrow.put(TournamentSerieArrow.IS_X_COLUMN_NAME, serieArrowData.isX);
                serieArrowData.id = db.insertOrThrow(TournamentSerieArrow.TABLE_NAME, null, contentValuesArrow);
                currentIndex += 1;
            }

        } else {
            // create the serie and its arrows
            ContentValues contentValues = new ContentValues();
            contentValues.put(TournamentSerie.TOURNAMENT_ID_COLUMN_NAME, tournamentSerie.tournament.id);
            contentValues.put(TournamentSerie.SERIE_INDEX_COLUMN_NAME, tournamentSerie.index);
            contentValues.put(TournamentSerie.TOTAL_SCORE_COLUMN_NAME, tournamentSerie.totalScore);
            contentValues.put(TournamentSerie.ROUND_INDEX_COLUMN_NAME, tournamentSerie.roundIndex);
            tournamentSerie.id = db.insertOrThrow(TournamentSerie.TABLE_NAME, null, contentValues);

            for (TournamentSerieArrow serieArrowData : tournamentSerie.arrows) {
                ContentValues contentValuesArrow = new ContentValues();
                contentValuesArrow.put(TournamentSerieArrow.TOURNAMENT_ID_COLUMN_NAME, tournamentSerie.tournament.id);
                contentValuesArrow.put(TournamentSerieArrow.SERIE_INDEX_COLUMN_NAME, tournamentSerie.id);
                contentValuesArrow.put(TournamentSerieArrow.SCORE_COLUMN_NAME, serieArrowData.score);
                contentValuesArrow.put(TournamentSerieArrow.X_POSITION_COLUMN_NAME, serieArrowData.xPosition);
                contentValuesArrow.put(TournamentSerieArrow.Y_POSITION_COLUMN_NAME, serieArrowData.yPosition);
                contentValuesArrow.put(TournamentSerieArrow.IS_X_COLUMN_NAME, serieArrowData.isX);
                serieArrowData.id = db.insertOrThrow(TournamentSerieArrow.TABLE_NAME, null, contentValuesArrow);
            }
        }

        // update the tournament totals
        Cursor cursor = db.rawQuery(
            "SELECT SUM(" + TournamentSerie.TOTAL_SCORE_COLUMN_NAME + ") " +
            "FROM " + TournamentSerie.TABLE_NAME + " " +
            "WHERE " +
                TournamentSerie.TOURNAMENT_ID_COLUMN_NAME + " = ?",
            new String[]{String.valueOf(tournamentSerie.tournament.id)}
        );
        cursor.moveToNext();
        int newSeriesTotal = cursor.getInt(0);
        cursor.close();

        db.execSQL(
            "UPDATE " + Tournament.TABLE_NAME + " " +
            "SET " +
                    Tournament.TOTAL_SCORE_COLUMN_NAME + " = ?, " +
                    Tournament.IS_SYNCED + " = 0 " +
            "WHERE " +
                    Tournament.ID_COLUMN_NAME + "= ?",
            new String[]{String.valueOf(newSeriesTotal), String.valueOf(tournamentSerie.tournament.id)}
        );
        tournamentSerie.tournament.totalScore = newSeriesTotal;

    }



    /**
     * Deletes the existing tournament with all the series/arrows information
     * @param tournamentId the id of the tournament to delete
     */
    public void deleteTournament(long tournamentId) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();

        db.delete(
                TournamentSerieArrow.TABLE_NAME,
                TournamentSerieArrow.TOURNAMENT_ID_COLUMN_NAME + "= ? ",
                new String[]{String.valueOf(tournamentId)}
        );

        db.delete(
                TournamentSerie.TABLE_NAME,
                TournamentSerie.TOURNAMENT_ID_COLUMN_NAME + "= ? ",
                new String[]{String.valueOf(tournamentId)}
        );

        db.delete(
                Tournament.TABLE_NAME,
                Tournament.ID_COLUMN_NAME + "= ? ",
                new String[]{String.valueOf(tournamentId)}
        );
    }

    public void deleteSerie(TournamentSerie tournamentSerie) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        db.delete(
                TournamentSerieArrow.TABLE_NAME,
                TournamentSerieArrow.SERIE_INDEX_COLUMN_NAME + "= ? ",
                new String[]{String.valueOf(tournamentSerie.id)}
        );

        db.delete(
                TournamentSerie.TABLE_NAME,
                TournamentSerie.ID_COLUMN_NAME + "= ? ",
                new String[]{String.valueOf(tournamentSerie.id)}
        );

        // remove the serie from the tournament to update the database changes
        tournamentSerie.getContainer().getSeries().remove(tournamentSerie.index -1 );

    }
}
