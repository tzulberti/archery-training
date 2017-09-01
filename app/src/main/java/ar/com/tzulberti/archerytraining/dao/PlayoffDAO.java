package ar.com.tzulberti.archerytraining.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ar.com.tzulberti.archerytraining.database.DatabaseHelper;
import ar.com.tzulberti.archerytraining.database.consts.BaseSerieArrowConsts;
import ar.com.tzulberti.archerytraining.database.consts.BaseSerieConsts;
import ar.com.tzulberti.archerytraining.database.consts.BaseSerieContainerConsts;
import ar.com.tzulberti.archerytraining.database.consts.ComputerPlayoffConfigurationConsts;
import ar.com.tzulberti.archerytraining.database.consts.HumanPlayoffConfigurationConsts;
import ar.com.tzulberti.archerytraining.database.consts.PlayoffConsts;
import ar.com.tzulberti.archerytraining.database.consts.PlayoffSerieArrowConsts;
import ar.com.tzulberti.archerytraining.database.consts.PlayoffSerieConsts;

import ar.com.tzulberti.archerytraining.helper.AppCache;
import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;
import ar.com.tzulberti.archerytraining.helper.PlayoffHelper;
import ar.com.tzulberti.archerytraining.model.constrains.TournamentConstraint;
import ar.com.tzulberti.archerytraining.model.playoff.HumanPlayoffConfiguration;
import ar.com.tzulberti.archerytraining.model.playoff.PlayoffSerieScore;
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
    protected BaseSerieArrowConsts getArrowsTable() {
        return new PlayoffSerieArrowConsts();
    }

    @Override
    protected BaseSerieConsts getSeriesTable() {
        return new PlayoffSerieConsts();
    }

    @Override
    protected BaseSerieContainerConsts getContainerTable() {return new PlayoffConsts();}


    public Playoff createPlayoff(String name, ComputerPlayOffConfiguration computerPlayOffConfiguration,
                                 TournamentConstraint tournamentConstraint, HumanPlayoffConfiguration humanPlayoffConfiguration) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        long currentTime = DatetimeHelper.getCurrentTime();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PlayoffConsts.NAME_COLUMN_NAME, name);
        contentValues.put(PlayoffConsts.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME, tournamentConstraint.id);
        contentValues.put(PlayoffConsts.DATETIME_COLUMN_NAME, currentTime);
        contentValues.put(PlayoffConsts.USER_PLAYOFF_SCORE_COLUMN_NAME, 0);
        contentValues.put(PlayoffConsts.OPPONENT_PLAYOFF_SCORE_COLUMN_NAME, 0);
        long playoffId = db.insertOrThrow(PlayoffConsts.TABLE_NAME, null, contentValues);

        if (computerPlayOffConfiguration != null) {
            ContentValues computerConfiguration = new ContentValues();
            computerConfiguration.put(ComputerPlayoffConfigurationConsts.MAX_SCORE_COLUMN_NAME, computerPlayOffConfiguration.maxScore);
            computerConfiguration.put(ComputerPlayoffConfigurationConsts.MIN_SCORE_COLUMN_NAME, computerPlayOffConfiguration.minScore);
            computerConfiguration.put(ComputerPlayoffConfigurationConsts.PLAYOFF_ID_COLUMN_NAME, playoffId);
            db.insertOrThrow(ComputerPlayoffConfigurationConsts.TABLE_NAME, null, computerConfiguration);
        } else {

            ContentValues humanConfiguration = new ContentValues();
            humanConfiguration.put(HumanPlayoffConfigurationConsts.OPPONENT_NAME_COLUMN_NAME, humanPlayoffConfiguration.opponentName);
            humanConfiguration.put(HumanPlayoffConfigurationConsts.PLAYOFF_ID_COLUMN_NAME, playoffId);
            db.insertOrThrow(HumanPlayoffConfigurationConsts.TABLE_NAME, null, humanConfiguration);
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
                PlayoffConsts.TABLE_NAME + "." + PlayoffConsts.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME + ", " +
                PlayoffConsts.TABLE_NAME + "." + PlayoffConsts.USER_PLAYOFF_SCORE_COLUMN_NAME + ", " +
                PlayoffConsts.TABLE_NAME + "." + PlayoffConsts.OPPONENT_PLAYOFF_SCORE_COLUMN_NAME + ", " +
                PlayoffConsts.TABLE_NAME + "." + PlayoffConsts.ID_COLUMN_NAME + ", " +
                ComputerPlayoffConfigurationConsts.TABLE_NAME + "." + ComputerPlayoffConfigurationConsts.ID_COLUMN_NAME + ", " +
                ComputerPlayoffConfigurationConsts.TABLE_NAME + "." + ComputerPlayoffConfigurationConsts.MIN_SCORE_COLUMN_NAME + ", " +
                ComputerPlayoffConfigurationConsts.TABLE_NAME + "." + ComputerPlayoffConfigurationConsts.MAX_SCORE_COLUMN_NAME + ", " +
                HumanPlayoffConfigurationConsts.TABLE_NAME + "." + HumanPlayoffConfigurationConsts.ID_COLUMN_NAME + ", " +
                HumanPlayoffConfigurationConsts.TABLE_NAME + "." + HumanPlayoffConfigurationConsts.OPPONENT_NAME_COLUMN_NAME + " " +
            "FROM " +  PlayoffConsts.TABLE_NAME + " " +
            "LEFT JOIN " + ComputerPlayoffConfigurationConsts.TABLE_NAME + " " +
                "ON " + PlayoffConsts.TABLE_NAME + "." + PlayoffConsts.ID_COLUMN_NAME + " = " + ComputerPlayoffConfigurationConsts.TABLE_NAME + "." + ComputerPlayoffConfigurationConsts.PLAYOFF_ID_COLUMN_NAME + " " +
            "LEFT JOIN " + HumanPlayoffConfigurationConsts.TABLE_NAME + " " +
                "ON " + PlayoffConsts.TABLE_NAME + "." + PlayoffConsts.ID_COLUMN_NAME + " = " + HumanPlayoffConfigurationConsts.TABLE_NAME + "." + HumanPlayoffConfigurationConsts.PLAYOFF_ID_COLUMN_NAME + " " +
            "ORDER BY " + PlayoffConsts.TABLE_NAME + "." + PlayoffConsts.DATETIME_COLUMN_NAME + " DESC ",
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

        long id = db.insertOrThrow(PlayoffSerieConsts.TABLE_NAME, null, contentValues);
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
        playoffSerie.id = db.insertOrThrow(PlayoffSerieConsts.TABLE_NAME, null, contentValues);

        for (PlayoffSerieArrow serieArrowData : playoffSerie.arrows) {
            ContentValues contentValuesArrow = new ContentValues();
            contentValuesArrow.put(PlayoffSerieArrowConsts.PLAYOFF_ID_COLUMN_NAME, playoffSerie.playoff.id);
            contentValuesArrow.put(PlayoffSerieArrowConsts.SERIE_ID_COLUMN_NAME, playoffSerie.id);
            contentValuesArrow.put(PlayoffSerieArrowConsts.SCORE_COLUMN_NAME, serieArrowData.score);
            contentValuesArrow.put(PlayoffSerieArrowConsts.X_POSITION_COLUMN_NAME, serieArrowData.xPosition);
            contentValuesArrow.put(PlayoffSerieArrowConsts.Y_POSITION_COLUMN_NAME, serieArrowData.yPosition);
            contentValuesArrow.put(PlayoffSerieArrowConsts.IS_X_COLUMN_NAME, serieArrowData.isX);
            serieArrowData.id = db.insertOrThrow(PlayoffSerieArrowConsts.TABLE_NAME, null, contentValuesArrow);
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
                HumanPlayoffConfigurationConsts.TABLE_NAME,
                HumanPlayoffConfigurationConsts.PLAYOFF_ID_COLUMN_NAME + "= ? ",
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
                "SELECT " +
                        PlayoffConsts.TABLE_NAME + "." + PlayoffConsts.NAME_COLUMN_NAME + ", " +
                        PlayoffConsts.TABLE_NAME + "." + PlayoffConsts.DATETIME_COLUMN_NAME + ", " +
                        PlayoffConsts.TABLE_NAME + "." + PlayoffConsts.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME + ", " +
                        PlayoffConsts.TABLE_NAME + "." + PlayoffConsts.USER_PLAYOFF_SCORE_COLUMN_NAME + ", " +
                        PlayoffConsts.TABLE_NAME + "." + PlayoffConsts.OPPONENT_PLAYOFF_SCORE_COLUMN_NAME + ", " +
                        PlayoffConsts.TABLE_NAME + "." + PlayoffConsts.ID_COLUMN_NAME + ", " +
                        ComputerPlayoffConfigurationConsts.TABLE_NAME + "." + ComputerPlayoffConfigurationConsts.ID_COLUMN_NAME + ", " +
                        ComputerPlayoffConfigurationConsts.TABLE_NAME + "." + ComputerPlayoffConfigurationConsts.MIN_SCORE_COLUMN_NAME + ", " +
                        ComputerPlayoffConfigurationConsts.TABLE_NAME + "." + ComputerPlayoffConfigurationConsts.MAX_SCORE_COLUMN_NAME + ", " +
                        HumanPlayoffConfigurationConsts.TABLE_NAME + "." + HumanPlayoffConfigurationConsts.ID_COLUMN_NAME + ", " +
                        HumanPlayoffConfigurationConsts.TABLE_NAME + "." + HumanPlayoffConfigurationConsts.OPPONENT_NAME_COLUMN_NAME + " " +
                "FROM " +  PlayoffConsts.TABLE_NAME + " " +
                "LEFT JOIN " + ComputerPlayoffConfigurationConsts.TABLE_NAME + " " +
                    "ON " + PlayoffConsts.TABLE_NAME + "." + PlayoffConsts.ID_COLUMN_NAME + " = " + ComputerPlayoffConfigurationConsts.TABLE_NAME + "." + ComputerPlayoffConfigurationConsts.PLAYOFF_ID_COLUMN_NAME + " " +
                "LEFT JOIN " + HumanPlayoffConfigurationConsts.TABLE_NAME + " " +
                    "ON " + PlayoffConsts.TABLE_NAME + "." + PlayoffConsts.ID_COLUMN_NAME + " = " + HumanPlayoffConfigurationConsts.TABLE_NAME + "." + HumanPlayoffConfigurationConsts.PLAYOFF_ID_COLUMN_NAME + " " +
                "WHERE "  +
                    PlayoffConsts.TABLE_NAME + "." + PlayoffConsts.ID_COLUMN_NAME + " = ?",
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
            ComputerPlayOffConfiguration computerPlayOffConfiguration = new ComputerPlayOffConfiguration();
            computerPlayOffConfiguration.id = playoffCursor.getInt(6);
            computerPlayOffConfiguration.minScore = playoffCursor.getInt(7);
            computerPlayOffConfiguration.maxScore = playoffCursor.getInt(8);
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

}
