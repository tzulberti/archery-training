package ar.com.tzulberti.archerytraining.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import ar.com.tzulberti.archerytraining.database.DatabaseHelper;
import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;
import ar.com.tzulberti.archerytraining.model.series.ArrowsPerDayData;
import ar.com.tzulberti.archerytraining.model.series.ArrowsPerTrainingType;
import ar.com.tzulberti.archerytraining.model.series.SerieData;
import ar.com.tzulberti.archerytraining.model.series.DistanceTotalData;

/**
 * DAO used to save all the information for the series
 *
 * Created by tzulberti on 4/19/17.
 */
public class SerieDataDAO extends BaseDAO {

    public enum GroupByType implements Serializable {
        DAILY, HOURLY, NONE
    }

    public SerieDataDAO(DatabaseHelper databaseHelper) {
        super(databaseHelper);
    }


    public void addSerieData(int distance, int arrowAmount, SerieData.TrainingType trainingType) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SerieData.DISTANCE_COLUMN_NAME, distance);
        contentValues.put(SerieData.ARROWS_AMOUNT_COLUMN_NAME, arrowAmount);
        contentValues.put(SerieData.DATETIME_COLUMN_NAME, DatetimeHelper.getCurrentTime());
        contentValues.put(SerieData.TRAINING_TYPE_COLUMN_NAME, trainingType.getValue());

        db.insertOrThrow(SerieData.TABLE_NAME, null, contentValues);
    }

    public void deleteSerieId(int id) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        db.delete(
                SerieData.TABLE_NAME,
                String.format("%s = ?", SerieData.ID_COLUMN_NAME),
                new String[]{String.valueOf(id)}
        );
    }

    public List<SerieData> getLastValues(int limit) {
        ArrayList<SerieData> res = new ArrayList<>();
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();

        String sortOrder = SerieData.DATETIME_COLUMN_NAME + " DESC";
        Cursor cursor = db.query(
                SerieData.TABLE_NAME,                     // The table to query
                null,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder,                                 // The sort order
                String.valueOf(limit)
        );

        int idIndex = cursor.getColumnIndexOrThrow(SerieData.ID_COLUMN_NAME);
        int datetimeIndex = cursor.getColumnIndexOrThrow(SerieData.DATETIME_COLUMN_NAME);
        int distanceIndex = cursor.getColumnIndexOrThrow(SerieData.DISTANCE_COLUMN_NAME);
        int arrorsAmountIndex = cursor.getColumnIndexOrThrow(SerieData.ARROWS_AMOUNT_COLUMN_NAME);

        while (cursor.moveToNext()) {
            SerieData currentData = new SerieData();
            currentData.id = cursor.getInt(idIndex);
            currentData.distance = cursor.getInt(distanceIndex);
            currentData.arrowsAmount = cursor.getInt(arrorsAmountIndex);
            currentData.datetime = DatetimeHelper.databaseValueToDate(cursor.getLong(datetimeIndex));

            res.add(currentData);
        }
        cursor.close();
        return res;
    }

    /**
     * Returns the total number of arrows done today
     */
    public long getTotalArrowsForDate(long minDate, long maxDate) {
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                String.format(
                        "SELECT SUM(%s) " +
                                "FROM %s " +
                                "WHERE %s >= ? AND %s < ? ",
                        SerieData.ARROWS_AMOUNT_COLUMN_NAME,
                        SerieData.TABLE_NAME,
                        SerieData.DATETIME_COLUMN_NAME, SerieData.DATETIME_COLUMN_NAME
                ),
                new String[]{String.valueOf(DatetimeHelper.getTodayZeroHours()), String.valueOf(DatetimeHelper.getTomorrowZeroHours())}
        );

        long res = 0;
        while (cursor.moveToNext()) {
            res = cursor.getLong(0);
        }
        cursor.close();
        return res;
    }

    public List<ArrowsPerDayData> getDailyArrowsInformation(long minDate, long maxDate, GroupByType groupByType) {
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        int modifier = 0;
        switch (groupByType) {
            case DAILY:
                modifier = 86400;
                break;
            case HOURLY:
                modifier = 3600;
                break;
            case NONE:
                modifier = 1;
                break;
        }

        String sModifier = String.valueOf(modifier);
        Cursor cursor = db.rawQuery(
                "SELECT " +
                        SerieData.DATETIME_COLUMN_NAME + " / " + sModifier + ", " +
                    "SUM(" + SerieData.ARROWS_AMOUNT_COLUMN_NAME + ") " +
                "FROM  " + SerieData.TABLE_NAME + " " +
                "WHERE " +
                        SerieData.DATETIME_COLUMN_NAME + " >= ? " +
                    " AND " + SerieData.DATETIME_COLUMN_NAME + " < ? " +
                "GROUP BY " +
                        SerieData.DATETIME_COLUMN_NAME + " / " + sModifier + " " +
                "ORDER BY 1",
                new String[]{String.valueOf(minDate), String.valueOf(maxDate)}
        );

        List<ArrowsPerDayData> res = new ArrayList<>();
        while (cursor.moveToNext()) {
            ArrowsPerDayData arrowsPerDayData = new ArrowsPerDayData();
            arrowsPerDayData.day = DatetimeHelper.databaseValueToDate(cursor.getLong(0) * modifier);
            arrowsPerDayData.totalArrows = cursor.getInt(1);
            res.add(arrowsPerDayData);
        }
        cursor.close();
        return res;
    }

    /**
     * Returns the total number of arrows shoot today for the different
     * distances
     *
     * @return the total data for today
     */
    public List<DistanceTotalData> getTotalsForDistance(long startingDate, long endingDate) {
        List<DistanceTotalData> res = new ArrayList<>();
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                String.format(
                        "SELECT %s, SUM(%s), MAX(%s), COUNT(*) " +
                                "FROM %s " +
                                "WHERE %s >= ? AND %s < ? " +
                                "GROUP BY %s " +
                                "ORDER BY %s DESC",
                        SerieData.DISTANCE_COLUMN_NAME, SerieData.ARROWS_AMOUNT_COLUMN_NAME, SerieData.DATETIME_COLUMN_NAME,
                        SerieData.TABLE_NAME,
                        SerieData.DATETIME_COLUMN_NAME, SerieData.DATETIME_COLUMN_NAME,
                        SerieData.DISTANCE_COLUMN_NAME,
                        SerieData.DISTANCE_COLUMN_NAME
                ),
                new String[]{String.valueOf(startingDate), String.valueOf(endingDate)}
        );

        while (cursor.moveToNext()) {
            DistanceTotalData data = new DistanceTotalData();
            data.distance = cursor.getInt(0);
            data.totalArrows = cursor.getLong(1);
            data.lastUpdate = DatetimeHelper.databaseValueToDate(cursor.getLong(2));
            data.seriesAmount = cursor.getInt(3);
            res.add(data);
        }
        cursor.close();
        return res;
    }

    public List<ArrowsPerTrainingType> getTotalsForTrainingType(long startingDate, long endingDate) {
        List<ArrowsPerTrainingType> res = new ArrayList<>();
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT " +
                        SerieData.TRAINING_TYPE_COLUMN_NAME + ", " +
                    "SUM(" + SerieData.ARROWS_AMOUNT_COLUMN_NAME + "), " +
                    "COUNT(" + SerieData.ID_COLUMN_NAME + ") " +
                "FROM " + SerieData.TABLE_NAME + " " +
                "WHERE " +
                        SerieData.DATETIME_COLUMN_NAME + " >= ? " +
                    "AND " + SerieData.DATETIME_COLUMN_NAME + " < ? " +
                "GROUP BY " + SerieData.TRAINING_TYPE_COLUMN_NAME + " " +
                "ORDER BY " + SerieData.TRAINING_TYPE_COLUMN_NAME + " DESC",
                new String[]{String.valueOf(startingDate), String.valueOf(endingDate)}
        );

        while (cursor.moveToNext()) {
            ArrowsPerTrainingType data = new ArrowsPerTrainingType();
            data.trainingType = cursor.getInt(0);
            data.totalArrows = cursor.getInt(1);
            data.seriesAmount = cursor.getInt(2);
            res.add(data);
        }
        cursor.close();
        return res;
    }

}
