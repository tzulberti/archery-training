package ar.com.tzulberti.archerytraining.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.tzulberti.archerytraining.helpers.DatabaseHelper;
import ar.com.tzulberti.archerytraining.helpers.DatetimeHelper;
import ar.com.tzulberti.archerytraining.model.SerieData;
import ar.com.tzulberti.archerytraining.model.TodaysTotalData;

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
        contentValues.put(DatabaseHelper.DISTANCE_COLUMN_NAME, distance);
        contentValues.put(DatabaseHelper.ARROWS_AMOUNT_COLUMN_NAME, arrowAmount);
        contentValues.put(DatabaseHelper.DATETIME_COLUMN_NAME, DatetimeHelper.getCurrentTime());
        long id = db.insert(DatabaseHelper.TABLE_NAME, null, contentValues);
        if (id == -1) {
            // TODO check what to do in this case
            System.err.println("-------------------------- Tuvo un error");
        }
        return id;
    }

    public List<SerieData> getLastValues(int limit) {
        ArrayList<SerieData> res = new ArrayList<SerieData>();
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();

        String sortOrder = DatabaseHelper.DATETIME_COLUMN_NAME + " DESC";
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_NAME,                     // The table to query
                null,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder,                                 // The sort order
                String.valueOf(limit)
        );

        int idIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.ID_COLUMN_NAME);
        int datetimeIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.DATETIME_COLUMN_NAME);
        int distanceIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.DISTANCE_COLUMN_NAME);
        int arrorsAmountIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.ARROWS_AMOUNT_COLUMN_NAME);

        while (cursor.moveToNext()) {
            SerieData currentData = new SerieData();
            currentData.id = cursor.getLong(idIndex);
            currentData.distance = cursor.getInt(distanceIndex);
            currentData.arrowsAmount = cursor.getInt(arrorsAmountIndex);
            currentData.datetime = new Date(cursor.getLong(datetimeIndex));

            res.add(currentData);
        }
        return res;
    }

    /**
     * Returns the total number of arrows done today
     */
    public long getTodayArrows() {
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                String.format(
                        "SELECT SUM(%s) " +
                        "FROM %s " +
                        "WHERE %s >= ? AND %s < ? ",
                        DatabaseHelper.DISTANCE_COLUMN_NAME,
                        DatabaseHelper.TABLE_NAME,
                        DatabaseHelper.DATETIME_COLUMN_NAME, DatabaseHelper.DATETIME_COLUMN_NAME
                ),
                new String[] {String.valueOf(DatetimeHelper.getTodayZeroHours()), String.valueOf(DatetimeHelper.getTomorrowZeroHours())}
        );

        while (cursor.moveToNext()) {
            return cursor.getLong(0);
        }
        return 0;
    }

    /**
     * Returns the total number of arrows shoot today for the different
     * distances
     *
     * @return the total data for today
     */
    public List<TodaysTotalData> getTodaysTotal() {
        List<TodaysTotalData> res = new ArrayList<TodaysTotalData>();
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();

        String sortOrder = DatabaseHelper.DATETIME_COLUMN_NAME + " DESC";
        Cursor cursor = db.rawQuery(
            String.format(
                    "SELECT %s, SUM(%s), MAX(%s) " +
                    "FROM %s " +
                    "WHERE %s >= ? AND %s < ? " +
                    "GROUP BY %s " +
                    "ORDER BY %s ",
                    DatabaseHelper.DISTANCE_COLUMN_NAME, DatabaseHelper.ARROWS_AMOUNT_COLUMN_NAME, DatabaseHelper.DATETIME_COLUMN_NAME,
                    DatabaseHelper.TABLE_NAME,
                    DatabaseHelper.DATETIME_COLUMN_NAME, DatabaseHelper.DATETIME_COLUMN_NAME,
                    DatabaseHelper.DISTANCE_COLUMN_NAME,
                    DatabaseHelper.DISTANCE_COLUMN_NAME
            ),
            new String[] {String.valueOf(DatetimeHelper.getTodayZeroHours()), String.valueOf(DatetimeHelper.getTomorrowZeroHours())}
        );

        while (cursor.moveToNext()) {
            TodaysTotalData data = new TodaysTotalData();
            data.distance = cursor.getInt(0);
            data.totalArrows = cursor.getLong(1);
            data.lastUpdate = new Date(cursor.getLong(2));
            res.add(data);
        }
        return res;
    };
}
