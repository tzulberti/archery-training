package ar.com.tzulberti.archerytraining.dao;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ar.com.tzulberti.archerytraining.database.DatabaseHelper;

import ar.com.tzulberti.archerytraining.model.base.AbstractArrow;
import ar.com.tzulberti.archerytraining.model.base.ISerie;
import ar.com.tzulberti.archerytraining.model.base.ISerieContainer;
import ar.com.tzulberti.archerytraining.model.common.ArrowsPerScore;
import ar.com.tzulberti.archerytraining.model.common.SeriesPerScore;
import ar.com.tzulberti.archerytraining.model.constrains.TournamentConstraint;


/**
 * Base DAO that has common logic for playoff and tournament based on the DAO
 *
 * Created by tzulberti on 6/26/17.
 */
public abstract class BaseArrowSeriesDAO extends BaseDAO {

    BaseArrowSeriesDAO(DatabaseHelper databaseHelper) {
        super(databaseHelper);
    }

    /**
     * Returns the model that contains the series information and that it
     * has a FK to the tournament constraints table
     */
    protected abstract ISerieContainer getContainerTable();

    /**
     * @return the table that has the arrows score data
     */
    protected abstract AbstractArrow getArrowsTable();

    /**
     * @return The table that has the series score data
     */
    protected abstract ISerie getSeriesTable();


    public List<SeriesPerScore> getSeriesPerScore(TournamentConstraint tournamentConstraint) {
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        ISerie serieConsts = this.getSeriesTable();
        ISerieContainer baseSerieContainerConsts = this.getContainerTable();

        String query =
                "SELECT " +
                    serieConsts.getTableName() + "." + serieConsts.getScoreColumnName() + ", " +
                    "COUNT(" + serieConsts.getTableName() + "." + serieConsts.getIdColumn() + ") " +
                "FROM " +  serieConsts.getTableName() + " " +
                "JOIN " + baseSerieContainerConsts.getTableName() + " " +
                    "ON " + baseSerieContainerConsts.getTableName() + "." + serieConsts.getIdColumn() + " = " + serieConsts.getTableName() + "." + serieConsts.getContainerIdColumnName() + " " +
                "WHERE " +
                        ISerieContainer.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME + " = ? " +
                "GROUP BY " +
                    serieConsts.getTableName() + "." + serieConsts.getScoreColumnName() + " " +
                "ORDER BY " +
                    serieConsts.getTableName() + "." + serieConsts.getScoreColumnName() + " DESC ";


        Cursor arrowsCursor = db.rawQuery(
                query,
                new String[]{String.valueOf(tournamentConstraint.id)}
        );
        List<SeriesPerScore> res = new ArrayList<>();
        while (arrowsCursor.moveToNext()) {
            SeriesPerScore seriesPerScore = new SeriesPerScore();
            seriesPerScore.serieScore = arrowsCursor.getInt(0);
            seriesPerScore.seriesAmount = arrowsCursor.getInt(1);
            res.add(seriesPerScore);
        }
        arrowsCursor.close();

        return res;
    }


    public List<ArrowsPerScore> getArrowsPerScore(TournamentConstraint tournamentConstraint) {
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        AbstractArrow serieArrowConsts = this.getArrowsTable();
        ISerie serieConsts = this.getSeriesTable();
        ISerieContainer baseSerieContainerConsts = this.getContainerTable();

        Cursor arrowsCursor = db.rawQuery(
                "SELECT " +
                        serieArrowConsts.getTableName() + "." + AbstractArrow.IS_X_COLUMN_NAME + ", " +
                        serieArrowConsts.getTableName() + "." + AbstractArrow.SCORE_COLUMN_NAME + ", " +
                        "COUNT(" + serieArrowConsts.getTableName() + "." + AbstractArrow.ID_COLUMN_NAME + ") " +
                "FROM " +  serieArrowConsts.getTableName() + " " +
                "JOIN " + serieConsts.getTableName() + " " +
                    "ON " + serieArrowConsts.getTableName() + "." + serieArrowConsts.getSerieColumnName() + " = " + serieConsts.getTableName() + "." + serieConsts.getIdColumn() + " " +
                "JOIN " + baseSerieContainerConsts.getTableName() + " " +
                    "ON " + baseSerieContainerConsts.getTableName() + "." + baseSerieContainerConsts.getIdColumn() + " = " + serieConsts.getTableName() + "." + serieConsts.getContainerIdColumnName() + " " +
                "WHERE " +
                        ISerieContainer.TOURNAMENT_CONSTRAINT_ID_COLUMN_NAME + " = ? " +
                "GROUP BY " +
                        serieArrowConsts.getTableName() + "." + AbstractArrow.IS_X_COLUMN_NAME + ", " +
                        serieArrowConsts.getTableName() + "." + AbstractArrow.SCORE_COLUMN_NAME + " " +
                "ORDER BY " +
                        serieArrowConsts.getTableName() + "." + AbstractArrow.IS_X_COLUMN_NAME + " DESC , " +
                        serieArrowConsts.getTableName() + "." + AbstractArrow.SCORE_COLUMN_NAME +  " DESC ",

                new String[]{String.valueOf(tournamentConstraint.id)}
        );
        List<ArrowsPerScore> res = new ArrayList<>();
        int currentScore = -1;
        while (arrowsCursor.moveToNext()) {
            ArrowsPerScore arrowsPerScore = new ArrowsPerScore();
            arrowsPerScore.isX = (arrowsCursor.getInt(0) == 1);
            arrowsPerScore.score = arrowsCursor.getInt(1);
            arrowsPerScore.arrowsAmount = arrowsCursor.getInt(2);

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
        arrowsCursor.close();

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
