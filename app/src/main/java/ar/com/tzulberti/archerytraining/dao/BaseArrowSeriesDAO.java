package ar.com.tzulberti.archerytraining.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ar.com.tzulberti.archerytraining.database.DatabaseHelper;
import ar.com.tzulberti.archerytraining.database.consts.BaseSerieArrowConsts;
import ar.com.tzulberti.archerytraining.database.consts.BaseSerieConsts;
import ar.com.tzulberti.archerytraining.database.consts.BaseSerieContainerConsts;
import ar.com.tzulberti.archerytraining.model.common.ArrowsPerScore;
import ar.com.tzulberti.archerytraining.model.common.SeriesPerScore;
import ar.com.tzulberti.archerytraining.model.constrains.TournamentConstraint;

/**
 * Created by tzulberti on 6/26/17.
 */

public abstract class BaseArrowSeriesDAO  {

    protected DatabaseHelper databaseHelper;

    public BaseArrowSeriesDAO(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Returns the table that contains the series information and that it
     * has a FK to the tournament constraints table
     */
    protected abstract BaseSerieContainerConsts getContainerTable();

    /**
     * Returns the table that has the arrows score data
     * @return
     */
    protected abstract BaseSerieArrowConsts getArrowsTable();

    /**
     * The table that has the series score data
     * @return
     */
    protected abstract BaseSerieConsts getSeriesTable();


    public List<SeriesPerScore> getSeriesPerScore(TournamentConstraint tournamentConstraint) {
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        BaseSerieConsts serieConsts = this.getSeriesTable();
        BaseSerieContainerConsts baseSerieContainerConsts = this.getContainerTable();

        String query =
                "SELECT " +
                    serieConsts.getTableName() + "." + serieConsts.getScoreColumnName() + ", " +
                    "COUNT(" + serieConsts.getTableName() + "." + serieConsts.ID_COLUMN_NAME + ") " +
                "FROM " +  serieConsts.getTableName() + " " +
                "JOIN " + baseSerieContainerConsts.getTableName() + " " +
                    "ON " + baseSerieContainerConsts.getTableName() + "." + BaseSerieContainerConsts.ID_COLUMN_NAME + " = " + serieConsts.getTableName() + "." + serieConsts.getContainerIdColumnName() + " " +
                "WHERE " +
                    BaseSerieContainerConsts.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME + " = ? " +
                "GROUP BY " +
                    serieConsts.getTableName() + "." + serieConsts.getScoreColumnName() + " " +
                "ORDER BY " +
                    serieConsts.getTableName() + "." + serieConsts.getScoreColumnName() + " DESC ";


        Cursor playoffArrowsCursor = db.rawQuery(
                query,
                new String[]{String.valueOf(tournamentConstraint.id)}
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


    public List<ArrowsPerScore> getArrowsPerScore(TournamentConstraint tournamentConstraint) {
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        BaseSerieArrowConsts serieArrowConsts = this.getArrowsTable();
        BaseSerieConsts serieConsts = this.getSeriesTable();
        BaseSerieContainerConsts baseSerieContainerConsts = this.getContainerTable();

        Cursor playoffArrowsCursor = db.rawQuery(
                "SELECT " +
                        serieArrowConsts.getTableName() + "." + serieArrowConsts.IS_X_COLUMN_NAME + ", " +
                        serieArrowConsts.getTableName() + "." + serieArrowConsts.SCORE_COLUMN_NAME + ", " +
                        "COUNT(" + serieArrowConsts.getTableName() + "." + serieArrowConsts.ID_COLUMN_NAME + ") " +
                "FROM " +  serieArrowConsts.getTableName() + " " +
                "JOIN " + serieConsts.getTableName() + " " +
                    "ON " + serieArrowConsts.getTableName() + "." + serieArrowConsts.getSerieColumnName() + " = " + serieConsts.getTableName() + "." + BaseSerieConsts.ID_COLUMN_NAME + " " +
                "JOIN " + baseSerieContainerConsts.getTableName() + " " +
                    "ON " + baseSerieContainerConsts.getTableName() + "." + BaseSerieContainerConsts.ID_COLUMN_NAME + " = " + serieConsts.getTableName() + "." + serieConsts.getContainerIdColumnName() + " " +
                "WHERE " +
                        BaseSerieContainerConsts.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME + " = ? " +
                "GROUP BY " +
                        serieArrowConsts.getTableName() + "." + serieArrowConsts.IS_X_COLUMN_NAME + ", " +
                        serieArrowConsts.getTableName() + "." + serieArrowConsts.SCORE_COLUMN_NAME + " " +
                "ORDER BY " +
                        serieArrowConsts.getTableName() + "." + serieArrowConsts.IS_X_COLUMN_NAME + " DESC , " +
                        serieArrowConsts.getTableName() + "." + serieArrowConsts.SCORE_COLUMN_NAME +  " DESC ",

                new String[]{String.valueOf(tournamentConstraint.id)}
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
}
