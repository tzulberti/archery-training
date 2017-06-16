package ar.com.tzulberti.archerytraining.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ar.com.tzulberti.archerytraining.database.consts.SerieInformationConsts;
import ar.com.tzulberti.archerytraining.database.DatabaseHelper;
import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;
import ar.com.tzulberti.archerytraining.model.ArrowsPerDayData;
import ar.com.tzulberti.archerytraining.model.SerieData;
import ar.com.tzulberti.archerytraining.model.DistanceTotalData;

/**
 * Created by tzulberti on 4/19/17.
 */

public class SerieDataDAO {

    private DatabaseHelper databaseHelper;

    public SerieDataDAO(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }


    public long addSerieData(int distance, int arrowAmount) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SerieInformationConsts.DISTANCE_COLUMN_NAME, distance);
        contentValues.put(SerieInformationConsts.ARROWS_AMOUNT_COLUMN_NAME, arrowAmount);
        contentValues.put(SerieInformationConsts.DATETIME_COLUMN_NAME, DatetimeHelper.getCurrentTime());
        long id = db.insert(SerieInformationConsts.TABLE_NAME, null, contentValues);
        if (id == -1) {
            // TODO check what to do in this case
        }
        return id;
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
        ArrayList<SerieData> res = new ArrayList<SerieData>();
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

        while (cursor.moveToNext()) {
            return cursor.getLong(0);
        }
        return 0;
    }

    public List<ArrowsPerDayData> getDailyArrowsInformation(long minDate, long maxDate) {
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " +
                        SerieInformationConsts.DATETIME_COLUMN_NAME + " / 86400, " +
                        "SUM(" + SerieInformationConsts.ARROWS_AMOUNT_COLUMN_NAME + ") " +
                "FROM  " + SerieInformationConsts.TABLE_NAME + " " +
                "WHERE " +
                        SerieInformationConsts.DATETIME_COLUMN_NAME + " >= ? " +
                        " AND " + SerieInformationConsts.DATETIME_COLUMN_NAME + " < ? " +
                "GROUP BY " +
                        SerieInformationConsts.DATETIME_COLUMN_NAME + " / 86400 " +
                "ORDER BY 1",
                new String[]{String.valueOf(minDate), String.valueOf(maxDate)}
        );

        List<ArrowsPerDayData> res = new ArrayList<>();
        while (cursor.moveToNext()) {
            ArrowsPerDayData arrowsPerDayData = new ArrowsPerDayData();
            arrowsPerDayData.day = DatetimeHelper.databaseValueToDate(cursor.getLong(0) * 86400);
            arrowsPerDayData.totalArrows = cursor.getInt(1);
            res.add(arrowsPerDayData);
        }
        return res;
    }

    /**
     * Returns the total number of arrows shoot today for the different
     * distances
     *
     * @return the total data for today
     */
    public List<DistanceTotalData> getTotalsForDistance(long startingDate, long endingDate) {
        List<DistanceTotalData> res = new ArrayList<DistanceTotalData>();
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
        return res;
    }

}
