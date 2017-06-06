package ar.com.tzulberti.archerytraining.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ar.com.tzulberti.archerytraining.database.DatabaseHelper;
import ar.com.tzulberti.archerytraining.database.consts.ComputerPlayoffConfigurationConsts;
import ar.com.tzulberti.archerytraining.database.consts.PlayoffConsts;
import ar.com.tzulberti.archerytraining.database.consts.PlayoffSerieArrowConsts;
import ar.com.tzulberti.archerytraining.database.consts.PlayoffSerieConsts;

import ar.com.tzulberti.archerytraining.database.consts.TournamentSerieArrowConsts;
import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;
import ar.com.tzulberti.archerytraining.model.playoff.ComputerPlayOffConfiguration;
import ar.com.tzulberti.archerytraining.model.playoff.Playoff;
import ar.com.tzulberti.archerytraining.model.playoff.PlayoffSerie;
import ar.com.tzulberti.archerytraining.model.playoff.PlayoffSerieArrow;


/**
 * Created by tzulberti on 6/4/17.
 */

public class PlayoffDAO {

    private DatabaseHelper databaseHelper;

    public PlayoffDAO(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public Playoff createPlayoff(String name, int distance, ComputerPlayOffConfiguration computerPlayOffConfiguration) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        long currentTime = DatetimeHelper.getCurrentTime();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PlayoffConsts.NAME_COLUMN_NAME, name);
        contentValues.put(PlayoffConsts.DISTANCE_COLUMN_NAME, distance);
        contentValues.put(PlayoffConsts.DATETIME_COLUMN_NAME, currentTime);
        contentValues.put(PlayoffConsts.USER_PLAYOFF_SCORE_COLUMN_NAME, 0);
        contentValues.put(PlayoffConsts.OPPONENT_PLAYOFF_SCORE_COLUMN_NAME, 0);
        long playoffId = db.insert(PlayoffConsts.TABLE_NAME, null, contentValues);

        if (computerPlayOffConfiguration != null) {
            ContentValues computerConfiguration = new ContentValues();
            computerConfiguration.put(ComputerPlayoffConfigurationConsts.MAX_SCORE_COLUMN_NAME, computerPlayOffConfiguration.maxScore);
            computerConfiguration.put(ComputerPlayoffConfigurationConsts.MIN_SCORE_COLUMN_NAME, computerPlayOffConfiguration.minScore);
            computerConfiguration.put(ComputerPlayoffConfigurationConsts.PLAYOFF_ID_COLUMN_NAME, playoffId);
            db.insert(ComputerPlayoffConfigurationConsts.TABLE_NAME, null, computerConfiguration);
        }

        return this.getCompletePlayoffData(playoffId);
    }

    public List<Playoff> getPlayoffs() {
        return null;
    }

    public void deleteSerie(long playoffSerieId) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        db.delete(
                PlayoffSerieArrowConsts.TABLE_NAME,
                PlayoffSerieArrowConsts.SERIE_ID_COLUMN_NAME + "= ? ",
                new String[]{String.valueOf(playoffSerieId)}
        );

        db.delete(
                PlayoffSerieConsts.TABLE_NAME,
                PlayoffSerieConsts.ID_COLUMN_NAME + "= ? ",
                new String[]{String.valueOf(playoffSerieId)}
        );
    }

    public PlayoffSerie createSerie(Playoff playoff) {
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT max(" + PlayoffSerieConsts.TABLE_NAME + "." + PlayoffSerieConsts.SERIE_INDEX_COLUMN_NAME + ") " +
                        "FROM " + PlayoffSerieConsts.TABLE_NAME + " " +
                        "WHERE " + PlayoffSerieConsts.TABLE_NAME + "." + PlayoffSerieConsts.PLAYOFF_ID_COLUMN_NAME + " = ?",
                new String[]{String.valueOf(playoff.id)}
        );
        boolean hasData = cursor.moveToNext();
        int serieIndex = 1;
        if (hasData) {
            serieIndex = cursor.getInt(0) + 1;
        }

        ContentValues contentValues = new ContentValues();

        contentValues.put(PlayoffSerieConsts.PLAYOFF_ID_COLUMN_NAME, playoff.id);
        contentValues.put(PlayoffSerieConsts.SERIE_INDEX_COLUMN_NAME, serieIndex);
        contentValues.put(PlayoffSerieConsts.USER_TOTAL_SCORE_COLUMN_NAME, 0);
        contentValues.put(PlayoffSerieConsts.OPPONENT_TOTAL_SCORE_COLUMN_NAME_COLUMN_NAME, 0);

        long id = db.insert(PlayoffSerieConsts.TABLE_NAME, null, contentValues);
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
        // delete existing values for this serie and create new ones
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();

        db.delete(
                PlayoffSerieArrowConsts.TABLE_NAME,
                PlayoffSerieArrowConsts.PLAYOFF_ID_COLUMN_NAME + "= ? AND " + PlayoffSerieArrowConsts.SERIE_ID_COLUMN_NAME + "= ?",
                new String[]{String.valueOf(playoffSerie.playoff.id), String.valueOf(playoffSerie.index)}
        );

        if (playoffSerie.id != 0) {
            // Update the playoff with the score of the current serie, and delete
            // the existing ones
            Cursor cursor = db.rawQuery(
                    String.format("SELECT %s, %s FROM %s WHERE %s = ?",
                            PlayoffSerieConsts.OPPONENT_TOTAL_SCORE_COLUMN_NAME_COLUMN_NAME, PlayoffSerieConsts.USER_TOTAL_SCORE_COLUMN_NAME,
                            PlayoffSerieConsts.TABLE_NAME,
                            PlayoffSerieConsts.ID_COLUMN_NAME
                    ),
                    new String[]{String.valueOf(playoffSerie.id)}
            );
            cursor.moveToNext();

            int databaseOpponentScore = cursor.getInt(0);
            int userOpponentScore = cursor.getInt(0);

            int diffOpponentScore = 0;
            int diffUserScore = 0;
            if (databaseOpponentScore > userOpponentScore) {
                diffOpponentScore = 2;
            } else if (databaseOpponentScore < userOpponentScore) {
                diffUserScore = 2;
            } else {
                diffOpponentScore = 1;
                diffUserScore = 1;
            }

            db.execSQL(
                    String.format("UPDATE %s SET %s = %s - ?, %s = %s - ? WHERE %s = ?",
                            PlayoffSerieConsts.TABLE_NAME,
                            PlayoffSerieConsts.USER_TOTAL_SCORE_COLUMN_NAME, PlayoffSerieConsts.USER_TOTAL_SCORE_COLUMN_NAME,
                            PlayoffSerieConsts.OPPONENT_TOTAL_SCORE_COLUMN_NAME_COLUMN_NAME, PlayoffSerieConsts.OPPONENT_TOTAL_SCORE_COLUMN_NAME_COLUMN_NAME,
                            PlayoffSerieConsts.ID_COLUMN_NAME),
                    new String[]{String.valueOf(diffUserScore), String.valueOf(diffOpponentScore), String.valueOf(playoffSerie.playoff.id)}
            );
            playoffSerie.playoff.opponentPlayoffScore -= diffOpponentScore;
            playoffSerie.playoff.userPlayoffScore -= userOpponentScore;

            // delete the serie information
            db.delete(
                    PlayoffSerieConsts.TABLE_NAME,
                    PlayoffSerieConsts.ID_COLUMN_NAME + "= ?",
                    new String[]{String.valueOf(playoffSerie.id)}
            );

        }

        // create the serie information
        ContentValues contentValues = new ContentValues();
        contentValues.put(PlayoffSerieConsts.PLAYOFF_ID_COLUMN_NAME, playoffSerie.playoff.id);
        contentValues.put(PlayoffSerieConsts.SERIE_INDEX_COLUMN_NAME, playoffSerie.index);
        contentValues.put(PlayoffSerieConsts.USER_TOTAL_SCORE_COLUMN_NAME, playoffSerie.userTotalScore);
        contentValues.put(PlayoffSerieConsts.OPPONENT_TOTAL_SCORE_COLUMN_NAME_COLUMN_NAME, playoffSerie.opponentTotalScore);
        playoffSerie.id = db.insert(PlayoffSerieConsts.TABLE_NAME, null, contentValues);

        for (PlayoffSerieArrow serieArrowData : playoffSerie.arrows) {
            ContentValues contentValuesArrow = new ContentValues();
            contentValuesArrow.put(PlayoffSerieArrowConsts.PLAYOFF_ID_COLUMN_NAME, playoffSerie.playoff.id);
            contentValuesArrow.put(PlayoffSerieArrowConsts.SERIE_ID_COLUMN_NAME, playoffSerie.id);
            contentValuesArrow.put(PlayoffSerieArrowConsts.SCORE_COLUMN_NAME, serieArrowData.score);
            contentValuesArrow.put(PlayoffSerieArrowConsts.X_POSITION_COLUMN_NAME, serieArrowData.xPosition);
            contentValuesArrow.put(PlayoffSerieArrowConsts.Y_POSITION_COLUMN_NAME, serieArrowData.yPosition);
            contentValuesArrow.put(PlayoffSerieArrowConsts.IS_X_COLUMN_NAME, serieArrowData.isX);
            serieArrowData.id = db.insert(PlayoffSerieArrowConsts.TABLE_NAME, null, contentValuesArrow);
        }

        // update the user and opponent total score
        int userSerieScoreDiff = 0;
        int opponentSerieScoreDiff = 0;
        if (playoffSerie.opponentTotalScore > playoffSerie.userTotalScore) {
            opponentSerieScoreDiff = 2;
        } else if (playoffSerie.opponentTotalScore < playoffSerie.userTotalScore) {
            userSerieScoreDiff = 2;
        } else {
            userSerieScoreDiff = 1;
            opponentSerieScoreDiff = 1;
        }
        db.execSQL(
                String.format("UPDATE %s SET %s = %s + ?, %s = %s + ? WHERE %s = ?",
                        PlayoffConsts.TABLE_NAME,
                        PlayoffConsts.OPPONENT_PLAYOFF_SCORE_COLUMN_NAME, PlayoffConsts.OPPONENT_PLAYOFF_SCORE_COLUMN_NAME,
                        PlayoffConsts.USER_PLAYOFF_SCORE_COLUMN_NAME, PlayoffConsts.USER_PLAYOFF_SCORE_COLUMN_NAME,
                        PlayoffConsts.ID_COLUMN_NAME),
                new String[]{String.valueOf(opponentSerieScoreDiff), String.valueOf(String.valueOf(userSerieScoreDiff)), String.valueOf(playoffSerie.playoff.id)}
        );

        // update the current instance of the tournament information
        playoffSerie.playoff.opponentPlayoffScore += opponentSerieScoreDiff;
        playoffSerie.playoff.userPlayoffScore += userSerieScoreDiff;
    }

    public void deletePlayoff(long playoffId) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();

        db.delete(
                PlayoffSerieArrowConsts.TABLE_NAME,
                PlayoffSerieArrowConsts.PLAYOFF_ID_COLUMN_NAME + "= ? ",
                new String[]{String.valueOf(playoffId)}
        );

        db.delete(
                PlayoffSerieConsts.TABLE_NAME,
                PlayoffSerieConsts.PLAYOFF_ID_COLUMN_NAME + "= ? ",
                new String[]{String.valueOf(playoffId)}
        );

        db.delete(
                ComputerPlayoffConfigurationConsts.TABLE_NAME,
                ComputerPlayoffConfigurationConsts.PLAYOFF_ID_COLUMN_NAME + "= ? ",
                new String[]{String.valueOf(playoffId)}
        );

        db.delete(
                PlayoffConsts.TABLE_NAME,
                PlayoffConsts.ID_COLUMN_NAME + "= ? ",
                new String[]{String.valueOf(playoffId)}
        );
    }


    public Playoff getCompletePlayoffData(long playoffId) {
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        Cursor playoffCursor = db.rawQuery(
                String.format(
                        "SELECT %s, %s, %s, " +
                                "%s, %s " +
                                "FROM %s " +
                                "WHERE %s = ?",
                        PlayoffConsts.NAME_COLUMN_NAME, PlayoffConsts.DATETIME_COLUMN_NAME, PlayoffConsts.DISTANCE_COLUMN_NAME,
                        PlayoffConsts.USER_PLAYOFF_SCORE_COLUMN_NAME, PlayoffConsts.OPPONENT_PLAYOFF_SCORE_COLUMN_NAME,
                        PlayoffConsts.TABLE_NAME,
                        PlayoffConsts.ID_COLUMN_NAME
                ),
                new String[]{String.valueOf(playoffId)}
        );
        playoffCursor.moveToFirst();
        Playoff playoff = new Playoff();
        playoff.id = playoffId;
        playoff.name = playoffCursor.getString(0);
        playoff.datetime = DatetimeHelper.databaseValueToDate(playoffCursor.getLong(1));
        playoff.distance = playoffCursor.getInt(2);
        playoff.userPlayoffScore = playoffCursor.getInt(3);
        playoff.opponentPlayoffScore = playoffCursor.getInt(4);


        // Check if the playoff has a ComputerPlayoffConfiguration
        Cursor computerPlayoffConfigurationCursor = db.rawQuery(
                String.format(
                        "SELECT %s, %s, %s " +
                        "FROM %s " +
                        "WHERE %s = ?",
                        ComputerPlayoffConfigurationConsts.ID_COLUMN_NAME,
                        ComputerPlayoffConfigurationConsts.MAX_SCORE_COLUMN_NAME,
                        ComputerPlayoffConfigurationConsts.MIN_SCORE_COLUMN_NAME,
                        ComputerPlayoffConfigurationConsts.TABLE_NAME,
                        ComputerPlayoffConfigurationConsts.PLAYOFF_ID_COLUMN_NAME
                ),
                new String[]{String.valueOf(playoffId)}
        );
        boolean hasData = computerPlayoffConfigurationCursor.moveToFirst();
        if (hasData) {
            ComputerPlayOffConfiguration computerPlayOffConfiguration = new ComputerPlayOffConfiguration();
            computerPlayOffConfiguration.id = computerPlayoffConfigurationCursor.getInt(0);
            computerPlayOffConfiguration.maxScore = computerPlayoffConfigurationCursor.getInt(1);
            computerPlayOffConfiguration.minScore = computerPlayoffConfigurationCursor.getInt(2);
            computerPlayOffConfiguration.playoff = playoff;
            playoff.computerPlayOffConfiguration = computerPlayOffConfiguration;
        }


        Cursor serieDataCursor = db.rawQuery(
                "SELECT " +
                        PlayoffSerieConsts.TABLE_NAME + "." + PlayoffSerieConsts.ID_COLUMN_NAME + ", " +
                        PlayoffSerieConsts.TABLE_NAME + "." + PlayoffSerieConsts.SERIE_INDEX_COLUMN_NAME + ", " +
                        PlayoffSerieConsts.TABLE_NAME + "." + PlayoffSerieConsts.OPPONENT_TOTAL_SCORE_COLUMN_NAME_COLUMN_NAME + ", " +
                        PlayoffSerieArrowConsts.TABLE_NAME + "." + PlayoffSerieArrowConsts.SCORE_COLUMN_NAME + ", " +
                        PlayoffSerieArrowConsts.TABLE_NAME + "." + PlayoffSerieArrowConsts.X_POSITION_COLUMN_NAME + ", " +
                        PlayoffSerieArrowConsts.TABLE_NAME + "." + PlayoffSerieArrowConsts.Y_POSITION_COLUMN_NAME + ", " +
                        PlayoffSerieArrowConsts.TABLE_NAME + "." + PlayoffSerieArrowConsts.IS_X_COLUMN_NAME + " " +
                        "FROM " +  PlayoffSerieArrowConsts.TABLE_NAME  + " " +
                        "JOIN " + PlayoffSerieConsts.TABLE_NAME + " " +
                        "ON " + PlayoffSerieConsts.TABLE_NAME + "." + PlayoffSerieConsts.ID_COLUMN_NAME + " = " + PlayoffSerieArrowConsts.TABLE_NAME + "." + PlayoffSerieArrowConsts.SERIE_ID_COLUMN_NAME + " " +
                        "WHERE " +
                            PlayoffSerieArrowConsts.TABLE_NAME + "." + PlayoffSerieArrowConsts.PLAYOFF_ID_COLUMN_NAME + " = ?" +
                        "ORDER BY " + PlayoffSerieConsts.TABLE_NAME + "." + PlayoffSerieConsts.SERIE_INDEX_COLUMN_NAME,

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

            PlayoffSerieArrow arrowData = new PlayoffSerieArrow();
            currentSerie.arrows.add(arrowData);
            arrowData.score = serieDataCursor.getInt(2);
            arrowData.xPosition = serieDataCursor.getInt(3);
            arrowData.yPosition = serieDataCursor.getInt(4);
            arrowData.isX = (serieDataCursor.getInt(5) == 1);
            currentSerie.userTotalScore += arrowData.score;
        }

        return playoff;
    }
}
