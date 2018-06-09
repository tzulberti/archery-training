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
import ar.com.tzulberti.archerytraining.model.playoff.HumanPlayoffConfiguration;
import ar.com.tzulberti.archerytraining.model.playoff.ComputerPlayOffConfiguration;
import ar.com.tzulberti.archerytraining.model.playoff.Playoff;
import ar.com.tzulberti.archerytraining.model.playoff.PlayoffSerie;
import ar.com.tzulberti.archerytraining.model.playoff.PlayoffSerieArrow;


/**
 * DAO used for all things related with the playoff
 *
 * Created by tzulberti on 6/4/17.
 */
public class PlayoffDAO extends BaseArrowSeriesDAO {

    public PlayoffDAO(DatabaseHelper databaseHelper) {
        super(databaseHelper);
    }

    @Override
    protected AbstractArrow getArrowsTable() {
        return new PlayoffSerieArrow();
    }

    @Override
    protected ISerie getSeriesTable() { return new PlayoffSerie();}

    @Override
    protected ISerieContainer getContainerTable() {return new Playoff();}


    public Playoff createPlayoff(String name, ComputerPlayOffConfiguration computerPlayOffConfiguration,
                                 TournamentConstraint tournamentConstraint, HumanPlayoffConfiguration humanPlayoffConfiguration) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        long currentTime = DatetimeHelper.getCurrentTime();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Playoff.NAME_COLUMN_NAME, name);
        contentValues.put(Playoff.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME, tournamentConstraint.id);
        contentValues.put(Playoff.DATETIME_COLUMN_NAME, currentTime);
        contentValues.put(Playoff.USER_PLAYOFF_SCORE_COLUMN_NAME, 0);
        contentValues.put(Playoff.OPPONENT_PLAYOFF_SCORE_COLUMN_NAME, 0);
        long playoffId = db.insertOrThrow(Playoff.TABLE_NAME, null, contentValues);

        if (computerPlayOffConfiguration != null) {
            ContentValues computerConfiguration = new ContentValues();
            computerConfiguration.put(ComputerPlayOffConfiguration.MAX_SCORE_COLUMN_NAME, computerPlayOffConfiguration.maxScore);
            computerConfiguration.put(ComputerPlayOffConfiguration.MIN_SCORE_COLUMN_NAME, computerPlayOffConfiguration.minScore);
            computerConfiguration.put(ComputerPlayOffConfiguration.PLAYOFF_ID_COLUMN_NAME, playoffId);
            db.insertOrThrow(ComputerPlayOffConfiguration.TABLE_NAME, null, computerConfiguration);
        } else {

            ContentValues humanConfiguration = new ContentValues();
            humanConfiguration.put(HumanPlayoffConfiguration.OPPONENT_NAME_COLUMN_NAME, humanPlayoffConfiguration.opponentName);
            humanConfiguration.put(HumanPlayoffConfiguration.PLAYOFF_ID_COLUMN_NAME, playoffId);
            db.insertOrThrow(HumanPlayoffConfiguration.TABLE_NAME, null, humanConfiguration);
        }

        return this.getCompletePlayoffData(playoffId);
    }

    public List<Playoff> getPlayoffs() {
        List<Playoff> res = new ArrayList<>();
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        Cursor playoffCursor = db.rawQuery(
            "SELECT " +
                Playoff.TABLE_NAME + "." + Playoff.NAME_COLUMN_NAME + ", " +
                Playoff.TABLE_NAME + "." + Playoff.DATETIME_COLUMN_NAME + ", " +
                Playoff.TABLE_NAME + "." + Playoff.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME + ", " +
                Playoff.TABLE_NAME + "." + Playoff.USER_PLAYOFF_SCORE_COLUMN_NAME + ", " +
                Playoff.TABLE_NAME + "." + Playoff.OPPONENT_PLAYOFF_SCORE_COLUMN_NAME + ", " +
                Playoff.TABLE_NAME + "." + Playoff.ID_COLUMN_NAME + ", " +
                ComputerPlayOffConfiguration.TABLE_NAME + "." + ComputerPlayOffConfiguration.ID_COLUMN_NAME + ", " +
                ComputerPlayOffConfiguration.TABLE_NAME + "." + ComputerPlayOffConfiguration.MIN_SCORE_COLUMN_NAME + ", " +
                ComputerPlayOffConfiguration.TABLE_NAME + "." + ComputerPlayOffConfiguration.MAX_SCORE_COLUMN_NAME + ", " +
                HumanPlayoffConfiguration.TABLE_NAME + "." + HumanPlayoffConfiguration.ID_COLUMN_NAME + ", " +
                HumanPlayoffConfiguration.TABLE_NAME + "." + HumanPlayoffConfiguration.OPPONENT_NAME_COLUMN_NAME + " " +
            "FROM " +  Playoff.TABLE_NAME + " " +
            "LEFT JOIN " + ComputerPlayOffConfiguration.TABLE_NAME + " " +
                "ON " + Playoff.TABLE_NAME + "." + Playoff.ID_COLUMN_NAME + " = " + ComputerPlayOffConfiguration.TABLE_NAME + "." + ComputerPlayOffConfiguration.PLAYOFF_ID_COLUMN_NAME + " " +
            "LEFT JOIN " + HumanPlayoffConfiguration.TABLE_NAME + " " +
                "ON " + Playoff.TABLE_NAME + "." + Playoff.ID_COLUMN_NAME + " = " + HumanPlayoffConfiguration.TABLE_NAME + "." + HumanPlayoffConfiguration.PLAYOFF_ID_COLUMN_NAME + " " +
            "ORDER BY " + Playoff.TABLE_NAME + "." + Playoff.DATETIME_COLUMN_NAME + " DESC ",
            null
        );
        while (playoffCursor.moveToNext()) {
            Playoff playoff = new Playoff();
            res.add(playoff);
            playoff.name = playoffCursor.getString(0);
            playoff.datetime = DatetimeHelper.databaseValueToDate(playoffCursor.getLong(1));
            playoff.tournamentConstraintId = playoffCursor.getInt(2);
            playoff.tournamentConstraint = AppCache.tournamentConstraintMap.get(playoff.tournamentConstraintId);
            playoff.userPlayoffScore = playoffCursor.getInt(3);
            playoff.opponentPlayoffScore = playoffCursor.getInt(4);
            playoff.id = playoffCursor.getInt(5);

            if (playoffCursor.isNull(6)) {
                // it is a human opponent
                HumanPlayoffConfiguration humanPlayoffConfiguration = new HumanPlayoffConfiguration();
                humanPlayoffConfiguration.id = playoffCursor.getInt(9);
                humanPlayoffConfiguration.opponentName = playoffCursor.getString(10);
                playoff.humanPlayoffConfiguration = humanPlayoffConfiguration;
            } else {
                ComputerPlayOffConfiguration ComputerPlayOffConfiguration = new ComputerPlayOffConfiguration();
                ComputerPlayOffConfiguration.id = playoffCursor.getInt(6);
                ComputerPlayOffConfiguration.minScore = playoffCursor.getInt(7);
                ComputerPlayOffConfiguration.maxScore = playoffCursor.getInt(8);
                ComputerPlayOffConfiguration.playoff = playoff;
                playoff.computerPlayOffConfiguration = ComputerPlayOffConfiguration;
            }
        }
        playoffCursor.close();

        return res;
    }

    public void deleteSerie(long playoffSerieId) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        db.delete(
                PlayoffSerieArrow.TABLE_NAME,
                PlayoffSerieArrow.SERIE_ID_COLUMN_NAME + "= ? ",
                new String[]{String.valueOf(playoffSerieId)}
        );

        db.delete(
                PlayoffSerie.TABLE_NAME,
                PlayoffSerie.ID_COLUMN_NAME + "= ? ",
                new String[]{String.valueOf(playoffSerieId)}
        );
    }

    public PlayoffSerie createSerie(Playoff playoff) {
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT max(" + PlayoffSerie.TABLE_NAME + "." + PlayoffSerie.SERIE_INDEX_COLUMN_NAME + ") " +
                        "FROM " + PlayoffSerie.TABLE_NAME + " " +
                        "WHERE " + PlayoffSerie.TABLE_NAME + "." + PlayoffSerie.PLAYOFF_ID_COLUMN_NAME + " = ?",
                new String[]{String.valueOf(playoff.id)}
        );
        boolean hasData = cursor.moveToNext();
        int serieIndex = 1;
        if (hasData) {
            serieIndex = cursor.getInt(0) + 1;
        }
        cursor.close();

        ContentValues contentValues = new ContentValues();

        contentValues.put(PlayoffSerie.PLAYOFF_ID_COLUMN_NAME, playoff.id);
        contentValues.put(PlayoffSerie.SERIE_INDEX_COLUMN_NAME, serieIndex);
        contentValues.put(PlayoffSerie.USER_TOTAL_SCORE_COLUMN_NAME, 0);
        contentValues.put(PlayoffSerie.OPPONENT_TOTAL_SCORE_COLUMN_NAME_COLUMN_NAME, 0);

        long id = db.insertOrThrow(PlayoffSerie.TABLE_NAME, null, contentValues);
        PlayoffSerie res = new PlayoffSerie();
        res.id = id;
        res.arrows = new ArrayList<>();
        res.index = serieIndex;
        res.userTotalScore = 0;
        res.opponentTotalScore = 0;
        res.playoff = playoff;
        playoff.series.add(res);
        return res;
    }

    public void updateSerie(PlayoffSerie playoffSerie) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        boolean alreadyExistsPlayoffSerie = this.checkIfExists(PlayoffSerie.TABLE_NAME, playoffSerie.id);

        if (alreadyExistsPlayoffSerie) {
            // update the serie total score
            db.execSQL(
                    "UPDATE " + PlayoffSerie.TABLE_NAME + " " +
                    "SET " +
                        PlayoffSerie.USER_TOTAL_SCORE_COLUMN_NAME + " = ?, " +
                        PlayoffSerie.OPPONENT_TOTAL_SCORE_COLUMN_NAME_COLUMN_NAME + " = ?, " +
                        PlayoffSerie.IS_SYNCED + " = 0 " +
                    "WHERE " +
                        PlayoffSerie.ID_COLUMN_NAME + " = ?",
                    new String[] {String.valueOf(playoffSerie.userTotalScore), String.valueOf(playoffSerie.opponentTotalScore), String.valueOf(playoffSerie.id)}

            );

            // update the existing arrows information, but it needs to get the existing ones from
            // the database
            Cursor cursor = db.rawQuery(
                    "SELECT " +
                            PlayoffSerieArrow.SCORE_COLUMN_NAME + ", " +
                            PlayoffSerieArrow.IS_X_COLUMN_NAME + ", " +
                            PlayoffSerieArrow.ID_COLUMN_NAME + " " +
                    "FROM " + PlayoffSerieArrow.TABLE_NAME + " " +
                    "WHERE " +
                            PlayoffSerieArrow.SERIE_ID_COLUMN_NAME + " = ? " +
                    "ORDER BY " +
                            PlayoffSerieArrow.IS_X_COLUMN_NAME + " DESC, " +
                            PlayoffSerieArrow.SCORE_COLUMN_NAME + " DESC, " +
                            PlayoffSerieArrow.ID_COLUMN_NAME,
                    new String[]{String.valueOf(playoffSerie.id)}
            );

            int currentIndex = 0;
            while (cursor.moveToNext()) {
                int existingScore = cursor.getInt(0);
                boolean existingIsX = (cursor.getInt(1) == 1);
                int id = cursor.getInt(2);

                PlayoffSerieArrow playoffSerieArrow = playoffSerie.arrows.get(currentIndex);
                playoffSerieArrow.id = id;

                if (existingIsX != playoffSerieArrow.isX || existingScore != playoffSerieArrow.score) {
                    // if one of the values are different, then it should be updated
                    db.execSQL(
                            "UPDATE " + PlayoffSerieArrow.TABLE_NAME + " " +
                            "SET " +
                                    PlayoffSerieArrow.SCORE_COLUMN_NAME + " = ?, " +
                                    PlayoffSerieArrow.IS_X_COLUMN_NAME + " = ?, " +
                                    PlayoffSerieArrow.IS_SYNCED + " = 0 " +
                            "WHERE " +
                                    PlayoffSerieArrow.ID_COLUMN_NAME + " = ?",
                            new String[] {String.valueOf(playoffSerieArrow.score), String.valueOf(playoffSerieArrow.isX ? 1 : 0), String.valueOf(id)}
                    );
                }

                currentIndex += 1;
            }
            cursor.close();

            // create the missing values
            while (currentIndex < playoffSerie.arrows.size()) {

                ContentValues contentValuesArrow = new ContentValues();
                PlayoffSerieArrow serieArrowData = playoffSerie.arrows.get(currentIndex);
                contentValuesArrow.put(PlayoffSerieArrow.PLAYOFF_ID_COLUMN_NAME, playoffSerie.playoff.id);
                contentValuesArrow.put(PlayoffSerieArrow.SERIE_ID_COLUMN_NAME, playoffSerie.id);
                contentValuesArrow.put(PlayoffSerieArrow.SCORE_COLUMN_NAME, serieArrowData.score);
                contentValuesArrow.put(PlayoffSerieArrow.X_POSITION_COLUMN_NAME, serieArrowData.xPosition);
                contentValuesArrow.put(PlayoffSerieArrow.Y_POSITION_COLUMN_NAME, serieArrowData.yPosition);
                contentValuesArrow.put(PlayoffSerieArrow.IS_X_COLUMN_NAME, serieArrowData.isX);
                serieArrowData.id = db.insertOrThrow(PlayoffSerieArrow.TABLE_NAME, null, contentValuesArrow);
                currentIndex += 1;
            }

        } else {
            // create the serie and its arrows
            ContentValues contentValues = new ContentValues();
            contentValues.put(PlayoffSerie.PLAYOFF_ID_COLUMN_NAME, playoffSerie.playoff.id);
            contentValues.put(PlayoffSerie.SERIE_INDEX_COLUMN_NAME, playoffSerie.index);
            contentValues.put(PlayoffSerie.OPPONENT_TOTAL_SCORE_COLUMN_NAME_COLUMN_NAME, playoffSerie.opponentTotalScore);
            contentValues.put(PlayoffSerie.USER_TOTAL_SCORE_COLUMN_NAME, playoffSerie.userTotalScore);
            playoffSerie.id = db.insertOrThrow(PlayoffSerie.TABLE_NAME, null, contentValues);

            for (PlayoffSerieArrow serieArrowData : playoffSerie.arrows) {
                ContentValues contentValuesArrow = new ContentValues();
                contentValuesArrow.put(PlayoffSerieArrow.PLAYOFF_ID_COLUMN_NAME, playoffSerie.playoff.id);
                contentValuesArrow.put(PlayoffSerieArrow.SERIE_ID_COLUMN_NAME, playoffSerie.id);
                contentValuesArrow.put(PlayoffSerieArrow.SCORE_COLUMN_NAME, serieArrowData.score);
                contentValuesArrow.put(PlayoffSerieArrow.X_POSITION_COLUMN_NAME, serieArrowData.xPosition);
                contentValuesArrow.put(PlayoffSerieArrow.Y_POSITION_COLUMN_NAME, serieArrowData.yPosition);
                contentValuesArrow.put(PlayoffSerieArrow.IS_X_COLUMN_NAME, serieArrowData.isX);
                serieArrowData.id = db.insertOrThrow(PlayoffSerieArrow.TABLE_NAME, null, contentValuesArrow);
            }
        }

        // update the tournament totals
        Cursor playoffScoreCursor = db.rawQuery(
                "SELECT " +
                    "SUM(CASE " +
                        "WHEN " + PlayoffSerie.USER_TOTAL_SCORE_COLUMN_NAME + " > " + PlayoffSerie.OPPONENT_TOTAL_SCORE_COLUMN_NAME_COLUMN_NAME + " THEN 2 " +
                        "WHEN " + PlayoffSerie.USER_TOTAL_SCORE_COLUMN_NAME + " = " + PlayoffSerie.OPPONENT_TOTAL_SCORE_COLUMN_NAME_COLUMN_NAME + " THEN 1 " +
                        "ELSE 0 " +
                    " END ), " +
                    "SUM(CASE " +
                        "WHEN " + PlayoffSerie.OPPONENT_TOTAL_SCORE_COLUMN_NAME_COLUMN_NAME + " > " + PlayoffSerie.USER_TOTAL_SCORE_COLUMN_NAME + " THEN 2 " +
                        "WHEN " + PlayoffSerie.OPPONENT_TOTAL_SCORE_COLUMN_NAME_COLUMN_NAME + " = " + PlayoffSerie.USER_TOTAL_SCORE_COLUMN_NAME + " THEN 1 " +
                        "ELSE 0 " +
                    " END ) " +
                "FROM " + PlayoffSerie.TABLE_NAME + " " +
                "WHERE " +
                        PlayoffSerie.PLAYOFF_ID_COLUMN_NAME + " = ?",
                new String[]{String.valueOf(playoffSerie.playoff.id)}
        );
        playoffScoreCursor.moveToNext();

        int userTotalScore = playoffScoreCursor.getInt(0);
        int opponentTotalScore = playoffScoreCursor.getInt(1);
        playoffScoreCursor.close();

        db.execSQL(
                "UPDATE " + Playoff.TABLE_NAME + " " +
                "SET " +
                        Playoff.OPPONENT_PLAYOFF_SCORE_COLUMN_NAME + " =  ?, " +
                        Playoff.USER_PLAYOFF_SCORE_COLUMN_NAME + " =  ?, " +
                        Playoff.IS_SYNCED + " = 0 " +
                "WHERE " +
                        Playoff.ID_COLUMN_NAME + " = ?",
                new String[]{String.valueOf(opponentTotalScore), String.valueOf(userTotalScore), String.valueOf(playoffSerie.playoff.id)}
        );

        // update the current instance of the tournament information
        playoffSerie.playoff.opponentPlayoffScore = opponentTotalScore;
        playoffSerie.playoff.userPlayoffScore = userTotalScore;

    }

    public void deletePlayoff(long playoffId) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();

        db.delete(
                PlayoffSerieArrow.TABLE_NAME,
                PlayoffSerieArrow.PLAYOFF_ID_COLUMN_NAME + "= ? ",
                new String[]{String.valueOf(playoffId)}
        );

        db.delete(
                PlayoffSerie.TABLE_NAME,
                PlayoffSerie.PLAYOFF_ID_COLUMN_NAME + "= ? ",
                new String[]{String.valueOf(playoffId)}
        );

        db.delete(
                ComputerPlayOffConfiguration.TABLE_NAME,
                ComputerPlayOffConfiguration.PLAYOFF_ID_COLUMN_NAME + "= ? ",
                new String[]{String.valueOf(playoffId)}
        );

        db.delete(
                HumanPlayoffConfiguration.TABLE_NAME,
                HumanPlayoffConfiguration.PLAYOFF_ID_COLUMN_NAME + "= ? ",
                new String[]{String.valueOf(playoffId)}
        );

        db.delete(
                Playoff.TABLE_NAME,
                Playoff.ID_COLUMN_NAME + "= ? ",
                new String[]{String.valueOf(playoffId)}
        );
    }


    public Playoff getCompletePlayoffData(long playoffId) {
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();

        Cursor playoffCursor = db.rawQuery(
                "SELECT " +
                        Playoff.TABLE_NAME + "." + Playoff.NAME_COLUMN_NAME + ", " +
                        Playoff.TABLE_NAME + "." + Playoff.DATETIME_COLUMN_NAME + ", " +
                        Playoff.TABLE_NAME + "." + Playoff.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME + ", " +
                        Playoff.TABLE_NAME + "." + Playoff.USER_PLAYOFF_SCORE_COLUMN_NAME + ", " +
                        Playoff.TABLE_NAME + "." + Playoff.OPPONENT_PLAYOFF_SCORE_COLUMN_NAME + ", " +
                        Playoff.TABLE_NAME + "." + Playoff.ID_COLUMN_NAME + ", " +
                        ComputerPlayOffConfiguration.TABLE_NAME + "." + ComputerPlayOffConfiguration.ID_COLUMN_NAME + ", " +
                        ComputerPlayOffConfiguration.TABLE_NAME + "." + ComputerPlayOffConfiguration.MIN_SCORE_COLUMN_NAME + ", " +
                        ComputerPlayOffConfiguration.TABLE_NAME + "." + ComputerPlayOffConfiguration.MAX_SCORE_COLUMN_NAME + ", " +
                        HumanPlayoffConfiguration.TABLE_NAME + "." + HumanPlayoffConfiguration.ID_COLUMN_NAME + ", " +
                        HumanPlayoffConfiguration.TABLE_NAME + "." + HumanPlayoffConfiguration.OPPONENT_NAME_COLUMN_NAME + " " +
                "FROM " +  Playoff.TABLE_NAME + " " +
                "LEFT JOIN " + ComputerPlayOffConfiguration.TABLE_NAME + " " +
                    "ON " + Playoff.TABLE_NAME + "." + Playoff.ID_COLUMN_NAME + " = " + ComputerPlayOffConfiguration.TABLE_NAME + "." + ComputerPlayOffConfiguration.PLAYOFF_ID_COLUMN_NAME + " " +
                "LEFT JOIN " + HumanPlayoffConfiguration.TABLE_NAME + " " +
                    "ON " + Playoff.TABLE_NAME + "." + Playoff.ID_COLUMN_NAME + " = " + HumanPlayoffConfiguration.TABLE_NAME + "." + HumanPlayoffConfiguration.PLAYOFF_ID_COLUMN_NAME + " " +
                "WHERE "  +
                    Playoff.TABLE_NAME + "." + Playoff.ID_COLUMN_NAME + " = ?",
                new String[]{String.valueOf(playoffId)}
        );
        playoffCursor.moveToFirst();
        Playoff playoff = new Playoff();
        playoff.id = playoffId;
        playoff.name = playoffCursor.getString(0);
        playoff.datetime = DatetimeHelper.databaseValueToDate(playoffCursor.getLong(1));
        playoff.tournamentConstraintId = playoffCursor.getInt(2);
        playoff.tournamentConstraint = AppCache.tournamentConstraintMap.get(playoff.tournamentConstraintId);
        playoff.userPlayoffScore = playoffCursor.getInt(3);
        playoff.opponentPlayoffScore = playoffCursor.getInt(4);

        if (playoffCursor.isNull(6)) {
            // it is a human opponent
            HumanPlayoffConfiguration humanPlayoffConfiguration = new HumanPlayoffConfiguration();
            humanPlayoffConfiguration.id = playoffCursor.getInt(9);
            humanPlayoffConfiguration.opponentName = playoffCursor.getString(10);
            playoff.humanPlayoffConfiguration = humanPlayoffConfiguration;
        } else {
            ComputerPlayOffConfiguration ComputerPlayOffConfiguration = new ComputerPlayOffConfiguration();
            ComputerPlayOffConfiguration.id = playoffCursor.getInt(6);
            ComputerPlayOffConfiguration.minScore = playoffCursor.getInt(7);
            ComputerPlayOffConfiguration.maxScore = playoffCursor.getInt(8);
            ComputerPlayOffConfiguration.playoff = playoff;
            playoff.computerPlayOffConfiguration = ComputerPlayOffConfiguration;
        }

        playoffCursor.close();


        Cursor serieDataCursor = db.rawQuery(
                "SELECT " +
                        PlayoffSerie.TABLE_NAME + "." + PlayoffSerie.ID_COLUMN_NAME + ", " +
                        PlayoffSerie.TABLE_NAME + "." + PlayoffSerie.SERIE_INDEX_COLUMN_NAME + ", " +
                        PlayoffSerie.TABLE_NAME + "." + PlayoffSerie.OPPONENT_TOTAL_SCORE_COLUMN_NAME_COLUMN_NAME + ", " +
                        PlayoffSerieArrow.TABLE_NAME + "." + PlayoffSerieArrow.SCORE_COLUMN_NAME + ", " +
                        PlayoffSerieArrow.TABLE_NAME + "." + PlayoffSerieArrow.X_POSITION_COLUMN_NAME + ", " +
                        PlayoffSerieArrow.TABLE_NAME + "." + PlayoffSerieArrow.Y_POSITION_COLUMN_NAME + ", " +
                        PlayoffSerieArrow.TABLE_NAME + "." + PlayoffSerieArrow.IS_X_COLUMN_NAME + ", " +
                        PlayoffSerieArrow.TABLE_NAME + "." + PlayoffSerieArrow.ID_COLUMN_NAME + " " +
                "FROM " +  PlayoffSerie.TABLE_NAME  + " " +
                "LEFT JOIN " + PlayoffSerieArrow.TABLE_NAME + " " +
                    "ON " + PlayoffSerie.TABLE_NAME + "." + PlayoffSerie.ID_COLUMN_NAME + " = " + PlayoffSerieArrow.TABLE_NAME + "." + PlayoffSerieArrow.SERIE_ID_COLUMN_NAME + " " +
                "WHERE " +
                        PlayoffSerie.TABLE_NAME + "." + PlayoffSerie.PLAYOFF_ID_COLUMN_NAME + " = ?" +
                "ORDER BY " +
                    PlayoffSerie.TABLE_NAME + "." + PlayoffSerie.SERIE_INDEX_COLUMN_NAME + ", " +
                    PlayoffSerieArrow.TABLE_NAME + "." + PlayoffSerie.ID_COLUMN_NAME,
                new String[]{String.valueOf(playoffId)}
        );

        playoff.series = new ArrayList<>();
        PlayoffSerie currentSerie = null;
        while (serieDataCursor.moveToNext()) {
            int serieId = serieDataCursor.getInt(0);
            if (currentSerie == null || currentSerie.id != serieId) {
                currentSerie = new PlayoffSerie();
                playoff.series.add(currentSerie);

                currentSerie.playoff = playoff;
                currentSerie.id = serieDataCursor.getInt(0);
                currentSerie.index = serieDataCursor.getInt(1);
                currentSerie.opponentTotalScore = serieDataCursor.getInt(2);
                currentSerie.arrows = new ArrayList<>();
                currentSerie.userTotalScore = 0;
            }

            if (serieDataCursor.isNull(7)) {
                // if the arrowData id is null, then there is no arrow data on the left JOIN
                continue;
            }
            PlayoffSerieArrow arrowData = new PlayoffSerieArrow();
            currentSerie.arrows.add(arrowData);
            arrowData.score = serieDataCursor.getInt(3);
            arrowData.xPosition = serieDataCursor.getInt(4);
            arrowData.yPosition = serieDataCursor.getInt(5);
            arrowData.isX = (serieDataCursor.getInt(6) == 1);
            arrowData.id = serieDataCursor.getInt(7);
            currentSerie.userTotalScore += arrowData.score;
        }
        serieDataCursor.close();

        return playoff;
    }

}
