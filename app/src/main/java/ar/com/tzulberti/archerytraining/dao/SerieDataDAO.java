package ar.com.tzulberti.archerytraining.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ar.com.tzulberti.archerytraining.database.consts.SerieInformationConsts;
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
public class SerieDataDAO {

    public enum GroupByType implements Serializable {
        DAILY, HOURLY, NONE
    }

    private DatabaseHelper databaseHelper;

    public SerieDataDAO(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }



    public long addSerieData(int distance, int arrowAmount, SerieInformationConsts.TrainingType trainingType) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SerieInformationConsts.DISTANCE_COLUMN_NAME, distance);
        contentValues.put(SerieInformationConsts.ARROWS_AMOUNT_COLUMN_NAME, arrowAmount);
        contentValues.put(SerieInformationConsts.DATETIME_COLUMN_NAME, DatetimeHelper.getCurrentTime());
        contentValues.put(SerieInformationConsts.TRAINING_TYPE_COLUMN_NAME, trainingType.getValue());

        return db.insertOrThrow(SerieInformationConsts.TABLE_NAME, null, contentValues);
    }

    public long deleteSerieId(int id) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        return db.delete(
                SerieInformationConsts.TABLE_NAME,
                String.format("%s = ?", SerieInformationConsts.ID_COLUMN_NAME),
                new String[]{String.valueOf(id)}
        );
    }

    public List<SerieData> getLastValues(int limit) {
        ArrayList<SerieData> res = new ArrayList<>();
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();

        String sortOrder = SerieInformationConsts.DATETIME_COLUMN_NAME + " DESC";
        Cursor cursor = db.query(
                SerieInformationConsts.TABLE_NAME,                     // The table to query
                null,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder,                                 // The sort order
                String.valueOf(limit)
        );

        int idIndex = cursor.getColumnIndexOrThrow(SerieInformationConsts.ID_COLUMN_NAME);
        int datetimeIndex = cursor.getColumnIndexOrThrow(SerieInformationConsts.DATETIME_COLUMN_NAME);
        int distanceIndex = cursor.getColumnIndexOrThrow(SerieInformationConsts.DISTANCE_COLUMN_NAME);
        int arrorsAmountIndex = cursor.getColumnIndexOrThrow(SerieInformationConsts.ARROWS_AMOUNT_COLUMN_NAME);

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
                        SerieInformationConsts.ARROWS_AMOUNT_COLUMN_NAME,
                        SerieInformationConsts.TABLE_NAME,
                        SerieInformationConsts.DATETIME_COLUMN_NAME, SerieInformationConsts.DATETIME_COLUMN_NAME
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
                    SerieInformationConsts.DATETIME_COLUMN_NAME + " / " + sModifier + ", " +
                    "SUM(" + SerieInformationConsts.ARROWS_AMOUNT_COLUMN_NAME + ") " +
                "FROM  " + SerieInformationConsts.TABLE_NAME + " " +
                "WHERE " +
                    SerieInformationConsts.DATETIME_COLUMN_NAME + " >= ? " +
                    " AND " + SerieInformationConsts.DATETIME_COLUMN_NAME + " < ? " +
                "GROUP BY " +
                    SerieInformationConsts.DATETIME_COLUMN_NAME + " / " + sModifier + " " +
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
                        SerieInformationConsts.DISTANCE_COLUMN_NAME, SerieInformationConsts.ARROWS_AMOUNT_COLUMN_NAME, SerieInformationConsts.DATETIME_COLUMN_NAME,
                        SerieInformationConsts.TABLE_NAME,
                        SerieInformationConsts.DATETIME_COLUMN_NAME, SerieInformationConsts.DATETIME_COLUMN_NAME,
                        SerieInformationConsts.DISTANCE_COLUMN_NAME,
                        SerieInformationConsts.DISTANCE_COLUMN_NAME
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
                    SerieInformationConsts.TRAINING_TYPE_COLUMN_NAME + ", " +
                    "SUM(" + SerieInformationConsts.ARROWS_AMOUNT_COLUMN_NAME + "), " +
                    "COUNT(" + SerieInformationConsts.ID_COLUMN_NAME + ") " +
                "FROM " + SerieInformationConsts.TABLE_NAME + " " +
                "WHERE " +
                    SerieInformationConsts.DATETIME_COLUMN_NAME + " >= ? " +
                    "AND " + SerieInformationConsts.DATETIME_COLUMN_NAME + " < ? " +
                "GROUP BY " + SerieInformationConsts.TRAINING_TYPE_COLUMN_NAME + " " +
                "ORDER BY " + SerieInformationConsts.TRAINING_TYPE_COLUMN_NAME + " DESC",
                new String[]{String.valueOf(startingDate), String.valueOf(endingDate)}
        );

        while (cursor.moveToNext()) {
            ArrowsPerTrainingType data = new ArrowsPerTrainingType();
            data.trainingType = cursor.getInt(0);
            data.totalArrows = cursor.getInt(1);
            data.seriesAmount = cursor.getInt(2);
            res.add(data);
        }
        return res;
    }

}
