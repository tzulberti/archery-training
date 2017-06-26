package ar.com.tzulberti.archerytraining.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ar.com.tzulberti.archerytraining.database.DatabaseHelper;
import ar.com.tzulberti.archerytraining.database.consts.ComputerPlayoffConfigurationConsts;
import ar.com.tzulberti.archerytraining.database.consts.PlayoffConsts;
import ar.com.tzulberti.archerytraining.database.consts.PlayoffSerieArrowConsts;
import ar.com.tzulberti.archerytraining.database.consts.PlayoffSerieConsts;

import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;
import ar.com.tzulberti.archerytraining.helper.PlayoffHelper;
import ar.com.tzulberti.archerytraining.model.common.ArrowsPerScore;
import ar.com.tzulberti.archerytraining.model.common.SeriesPerScore;
import ar.com.tzulberti.archerytraining.model.playoff.PlayoffSerieScore;
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
        List<Playoff> res = new ArrayList<>();
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        Cursor playoffCursor = db.rawQuery(
            "SELECT " +
                PlayoffConsts.TABLE_NAME + "." + PlayoffConsts.NAME_COLUMN_NAME + ", " +
                PlayoffConsts.TABLE_NAME + "." + PlayoffConsts.DATETIME_COLUMN_NAME + ", " +
                PlayoffConsts.TABLE_NAME + "." + PlayoffConsts.DISTANCE_COLUMN_NAME + ", " +
                PlayoffConsts.TABLE_NAME + "." + PlayoffConsts.USER_PLAYOFF_SCORE_COLUMN_NAME + ", " +
                PlayoffConsts.TABLE_NAME + "." + PlayoffConsts.OPPONENT_PLAYOFF_SCORE_COLUMN_NAME + ", " +
                PlayoffConsts.TABLE_NAME + "." + PlayoffConsts.ID_COLUMN_NAME + ", " +
                ComputerPlayoffConfigurationConsts.TABLE_NAME + "." + ComputerPlayoffConfigurationConsts.ID_COLUMN_NAME + ", " +
                ComputerPlayoffConfigurationConsts.TABLE_NAME + "." + ComputerPlayoffConfigurationConsts.MIN_SCORE_COLUMN_NAME + ", " +
                ComputerPlayoffConfigurationConsts.TABLE_NAME + "." + ComputerPlayoffConfigurationConsts.MAX_SCORE_COLUMN_NAME + " " +
            "FROM " +  PlayoffConsts.TABLE_NAME + " " +
            "LEFT JOIN " + ComputerPlayoffConfigurationConsts.TABLE_NAME + " " +
                "ON " + PlayoffConsts.TABLE_NAME + "." + PlayoffConsts.ID_COLUMN_NAME + " = " + ComputerPlayoffConfigurationConsts.TABLE_NAME + "." + ComputerPlayoffConfigurationConsts.PLAYOFF_ID_COLUMN_NAME + " " +
            "ORDER BY " + PlayoffConsts.TABLE_NAME + "." + PlayoffConsts.DATETIME_COLUMN_NAME + " DESC ",
            null
        );
        while (playoffCursor.moveToNext()) {
            Playoff playoff = new Playoff();
            res.add(playoff);
            playoff.name = playoffCursor.getString(0);
            playoff.datetime = DatetimeHelper.databaseValueToDate(playoffCursor.getLong(1));
            playoff.distance = playoffCursor.getInt(2);
            playoff.userPlayoffScore = playoffCursor.getInt(3);
            playoff.opponentPlayoffScore = playoffCursor.getInt(4);
            playoff.id = playoffCursor.getInt(5);
            if (playoffCursor.getInt(6) >= 0) {
                ComputerPlayOffConfiguration computerPlayOffConfiguration = new ComputerPlayOffConfiguration();
                computerPlayOffConfiguration.id = playoffCursor.getInt(6);
                computerPlayOffConfiguration.minScore = playoffCursor.getInt(7);
                computerPlayOffConfiguration.maxScore = playoffCursor.getInt(8);
                computerPlayOffConfiguration.playoff = playoff;
                playoff.computerPlayOffConfiguration = computerPlayOffConfiguration;
            }
        }

        return res;
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

        int deletedArrows = db.delete(
                PlayoffSerieArrowConsts.TABLE_NAME,
                PlayoffSerieArrowConsts.PLAYOFF_ID_COLUMN_NAME + "= ? AND " + PlayoffSerieArrowConsts.SERIE_ID_COLUMN_NAME + "= ?",
                new String[]{String.valueOf(playoffSerie.playoff.id), String.valueOf(playoffSerie.id)}
        );

        if (playoffSerie.id != 0) {
            if (deletedArrows > 0) {
                // Update the playoff with the score of the current serie, and delete
                // the existing ones, only if the deleted serie had data
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
                int databaseUserScore = cursor.getInt(1);

                // reduce the database score values
                PlayoffSerieScore playoffSerieScore = PlayoffHelper.getScore(databaseUserScore, databaseOpponentScore);
                db.execSQL(
                        String.format("UPDATE %s SET %s = %s - ?, %s = %s - ? WHERE %s = ?",
                                PlayoffConsts.TABLE_NAME,
                                PlayoffConsts.USER_PLAYOFF_SCORE_COLUMN_NAME, PlayoffConsts.USER_PLAYOFF_SCORE_COLUMN_NAME,
                                PlayoffConsts.OPPONENT_PLAYOFF_SCORE_COLUMN_NAME, PlayoffConsts.OPPONENT_PLAYOFF_SCORE_COLUMN_NAME,
                                PlayoffConsts.ID_COLUMN_NAME),
                        new String[]{
                                String.valueOf(playoffSerieScore.userPoints),
                                String.valueOf(playoffSerieScore.opponentPoints),
                                String.valueOf(playoffSerie.playoff.id)
                        }
                );


                playoffSerie.playoff.opponentPlayoffScore -= playoffSerieScore.opponentPoints;
                playoffSerie.playoff.userPlayoffScore -= playoffSerieScore.userPoints;
            }

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
        PlayoffSerieScore updatePlayoffSerieScore = PlayoffHelper.getScore(playoffSerie.userTotalScore, playoffSerie.opponentTotalScore);

        db.execSQL(
                String.format("UPDATE %s SET %s = %s + ?, %s = %s + ? WHERE %s = ?",
                        PlayoffConsts.TABLE_NAME,
                        PlayoffConsts.OPPONENT_PLAYOFF_SCORE_COLUMN_NAME, PlayoffConsts.OPPONENT_PLAYOFF_SCORE_COLUMN_NAME,
                        PlayoffConsts.USER_PLAYOFF_SCORE_COLUMN_NAME, PlayoffConsts.USER_PLAYOFF_SCORE_COLUMN_NAME,
                        PlayoffConsts.ID_COLUMN_NAME),
                new String[]{String.valueOf(
                        updatePlayoffSerieScore.opponentPoints),
                        String.valueOf(String.valueOf(updatePlayoffSerieScore.userPoints)),
                        String.valueOf(playoffSerie.playoff.id)
                }
        );

        // update the current instance of the tournament information
        playoffSerie.playoff.opponentPlayoffScore += updatePlayoffSerieScore.opponentPoints;
        playoffSerie.playoff.userPlayoffScore += updatePlayoffSerieScore.userPoints;
        Log.e("foobar", String.format("%s - %s", playoffSerie.playoff.opponentPlayoffScore, playoffSerie.playoff.userPlayoffScore));
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
                        PlayoffSerieArrowConsts.TABLE_NAME + "." + PlayoffSerieArrowConsts.IS_X_COLUMN_NAME + ", " +
                        PlayoffSerieArrowConsts.TABLE_NAME + "." + PlayoffSerieArrowConsts.ID_COLUMN_NAME + " " +
                "FROM " +  PlayoffSerieConsts.TABLE_NAME  + " " +
                "LEFT JOIN " + PlayoffSerieArrowConsts.TABLE_NAME + " " +
                    "ON " + PlayoffSerieConsts.TABLE_NAME + "." + PlayoffSerieConsts.ID_COLUMN_NAME + " = " + PlayoffSerieArrowConsts.TABLE_NAME + "." + PlayoffSerieArrowConsts.SERIE_ID_COLUMN_NAME + " " +
                "WHERE " +
                    PlayoffSerieArrowConsts.TABLE_NAME + "." + PlayoffSerieArrowConsts.PLAYOFF_ID_COLUMN_NAME + " = ?" +
                "ORDER BY " +
                    PlayoffSerieConsts.TABLE_NAME + "." + PlayoffSerieConsts.SERIE_INDEX_COLUMN_NAME + ", " +
                    PlayoffSerieArrowConsts.TABLE_NAME + "." + PlayoffSerieConsts.ID_COLUMN_NAME,
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

        return playoff;
    }


    public List<ArrowsPerScore> getArrowsPerScore() {
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        Cursor playoffArrowsCursor = db.rawQuery(
                "SELECT " +
                        PlayoffSerieArrowConsts.TABLE_NAME + "." + PlayoffSerieArrowConsts.IS_X_COLUMN_NAME + ", " +
                        PlayoffSerieArrowConsts.TABLE_NAME + "." + PlayoffSerieArrowConsts.SCORE_COLUMN_NAME + ", " +
                        "COUNT(" + PlayoffSerieArrowConsts.TABLE_NAME + "." + PlayoffSerieArrowConsts.ID_COLUMN_NAME + ") " +
                "FROM " +  PlayoffSerieArrowConsts.TABLE_NAME + " " +
                "GROUP BY " +
                        PlayoffSerieArrowConsts.TABLE_NAME + "." + PlayoffSerieArrowConsts.IS_X_COLUMN_NAME + ", " +
                        PlayoffSerieArrowConsts.TABLE_NAME + "." + PlayoffSerieArrowConsts.SCORE_COLUMN_NAME + " " +
                "ORDER BY " +
                        PlayoffSerieArrowConsts.TABLE_NAME + "." + PlayoffSerieArrowConsts.IS_X_COLUMN_NAME + " DESC , " +
                        PlayoffSerieArrowConsts.TABLE_NAME + "." + PlayoffSerieArrowConsts.SCORE_COLUMN_NAME +  " DESC ",
                null
        );
        List<ArrowsPerScore> res = new ArrayList<>();
        int currentScore = -1;
        while (playoffArrowsCursor.moveToNext()) {
            ArrowsPerScore arrowsPerScore = new ArrowsPerScore();
            arrowsPerScore.isX = (playoffArrowsCursor.getInt(0) == 1);
            arrowsPerScore.score = playoffArrowsCursor.getInt(1);
            arrowsPerScore.arrowsAmount = playoffArrowsCursor.getInt(2);

            if (currentScore == -1) {
                // check if the user never hit an X or he never got an 10, 9... etc
                if (! arrowsPerScore.isX) {
                    ArrowsPerScore missingXData = new ArrowsPerScore();
                    missingXData.isX = true;
                    missingXData.score = 10;
                    missingXData.arrowsAmount = 0;
                    res.add(missingXData);
                }

                // add the other values that it might be missing like the 10, 9, etc...
                for (int arrowScore = 10; arrowScore > arrowsPerScore.score; arrowScore--) {
                    ArrowsPerScore missingArrowScoreData = new ArrowsPerScore();
                    missingArrowScoreData.isX = false;
                    missingArrowScoreData.score = arrowScore;
                    missingArrowScoreData.arrowsAmount = 0;
                    res.add(missingArrowScoreData);
                }

            } else if (currentScore > arrowsPerScore.score + 1) {
                // check that there is no missing values
                for (int i = 1; i + arrowsPerScore.score < currentScore; i++) {
                    ArrowsPerScore missingArrowScoreData = new ArrowsPerScore();
                    missingArrowScoreData.isX = false;
                    missingArrowScoreData.score = arrowsPerScore.score + i;
                    missingArrowScoreData.arrowsAmount = 0;
                    res.add(missingArrowScoreData);
                }
            }
            currentScore = arrowsPerScore.score;
            res.add(arrowsPerScore);
        }

        if (currentScore > 0) {
            // the the min score was something like 3 so I have to add the missing values
            for (int missingScore = currentScore - 1; missingScore >= 0; missingScore--) {
                ArrowsPerScore missingArrowData = new ArrowsPerScore();
                missingArrowData.isX = false;
                missingArrowData.score = missingScore;
                missingArrowData.arrowsAmount = 0;
                res.add(missingArrowData);
            }
        }

        // must reverse list because if not on the chart the lower score are going
        // to be shown on top
        List<ArrowsPerScore> chartRes = new ArrayList<>();
        for (ArrowsPerScore arrowsPerScore : res) {
            chartRes.add(0, arrowsPerScore);
        }
        return chartRes;
    }


    public List<SeriesPerScore> getSeriesPerScore() {
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        Cursor playoffArrowsCursor = db.rawQuery(
                "SELECT " +
                        PlayoffSerieConsts.TABLE_NAME + "." + PlayoffSerieConsts.USER_TOTAL_SCORE_COLUMN_NAME + ", " +
                        "COUNT(" + PlayoffSerieConsts.TABLE_NAME + "." + PlayoffSerieConsts.ID_COLUMN_NAME + ") " +
                "FROM " +  PlayoffSerieConsts.TABLE_NAME + " " +
                "GROUP BY " +
                        PlayoffSerieConsts.TABLE_NAME + "." + PlayoffSerieConsts.USER_TOTAL_SCORE_COLUMN_NAME + " " +
                "ORDER BY " +
                        PlayoffSerieConsts.TABLE_NAME + "." + PlayoffSerieConsts.USER_TOTAL_SCORE_COLUMN_NAME + " DESC ",
                null
        );
        List<SeriesPerScore> res = new ArrayList<>();
        while (playoffArrowsCursor.moveToNext()) {
            SeriesPerScore seriesPerScore = new SeriesPerScore();
            seriesPerScore.serieScore = playoffArrowsCursor.getInt(0);
            seriesPerScore.seriesAmount = playoffArrowsCursor.getInt(1);
            res.add(seriesPerScore);
        }

        return res;
    }
}
