package ar.com.tzulberti.archerytraining.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ar.com.tzulberti.archerytraining.database.DatabaseHelper;
import ar.com.tzulberti.archerytraining.database.consts.BowConsts;
import ar.com.tzulberti.archerytraining.database.consts.SightDistanceValueConsts;
import ar.com.tzulberti.archerytraining.model.bow.Bow;
import ar.com.tzulberti.archerytraining.model.bow.SightDistanceValue;

/**
 * Created by tzulberti on 6/12/17.
 */

public class BowDAO {

    private DatabaseHelper databaseHelper;

    public BowDAO(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }


    public List<Bow> getBowsInformation() {
        List<Bow> res = new ArrayList<>();

        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        Cursor bowCursor = db.rawQuery(
                "SELECT " +
                        BowConsts.TABLE_NAME + "." + BowConsts.NAME_COLUMN_NAME + ", " +
                        BowConsts.TABLE_NAME + "." + BowConsts.ID_COLUMN_NAME + ", " +
                        SightDistanceValueConsts.TABLE_NAME + "." + SightDistanceValueConsts.DISTANCE_COLUMN_NAME + ", " +
                        SightDistanceValueConsts.TABLE_NAME + "." + SightDistanceValueConsts.SIGHT_VALUE_COLUMN_NAME + ", " +
                        SightDistanceValueConsts.TABLE_NAME + "." + SightDistanceValueConsts.ID_COLUMN_NAME + "  " +
                        "FROM " +  BowConsts.TABLE_NAME + " " +
                        "LEFT JOIN " + SightDistanceValueConsts.TABLE_NAME + " " +
                        "ON " + BowConsts.TABLE_NAME + "." + BowConsts.ID_COLUMN_NAME + " = " + SightDistanceValueConsts.TABLE_NAME + "." + SightDistanceValueConsts.BOW_ID_COLUMN_NAME + " " +
                        "ORDER BY " +
                            BowConsts.TABLE_NAME + "." + BowConsts.ID_COLUMN_NAME + " DESC, " +
                            SightDistanceValueConsts.TABLE_NAME + "." + SightDistanceValueConsts.DISTANCE_COLUMN_NAME,
                null
        );

        Bow currentBow = null;
        while (bowCursor.moveToNext()) {
            long bowId = bowCursor.getLong(0);
            if (currentBow == null || bowId != currentBow.id) {
                currentBow = new Bow();
                currentBow.sightDistanceValues = new ArrayList<>();
                currentBow.id = bowId;
                currentBow.name = bowCursor.getString(1);

                res.add(currentBow);
            }

            SightDistanceValue sightDistanceValue = new SightDistanceValue();
            currentBow.sightDistanceValues.add(sightDistanceValue);
            sightDistanceValue.bow = currentBow;
            sightDistanceValue.distance = bowCursor.getInt(2);
            sightDistanceValue.sightValue = bowCursor.getFloat(3);
            sightDistanceValue.id = bowCursor.getInt(4);
        }

        return res;
    }

    public Bow createBow(String name, List<SightDistanceValue> sightValues) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(BowConsts.NAME_COLUMN_NAME, name);
        long bowId = db.insert(BowConsts.TABLE_NAME, null, contentValues);

        Bow res = new Bow();
        res.name = name;
        res.id = bowId;
        res.sightDistanceValues = sightValues;

        for (SightDistanceValue sightDistanceValue : sightValues) {
            ContentValues contentValuesSightDistance = new ContentValues();
            contentValuesSightDistance.put(SightDistanceValueConsts.BOW_ID_COLUMN_NAME, res.id);
            contentValuesSightDistance.put(SightDistanceValueConsts.DISTANCE_COLUMN_NAME, sightDistanceValue.distance);
            contentValuesSightDistance.put(SightDistanceValueConsts.SIGHT_VALUE_COLUMN_NAME, sightDistanceValue.sightValue);
            long sightId = db.insert(SightDistanceValueConsts.TABLE_NAME, null, contentValuesSightDistance);
            sightDistanceValue.id = sightId;
        }

        return res;
    }

    public void deleteBow(long bowId) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();

        db.delete(
                SightDistanceValueConsts.TABLE_NAME,
                SightDistanceValueConsts.BOW_ID_COLUMN_NAME + "= ? ",
                new String[]{String.valueOf(bowId)}
        );

        db.delete(
                BowConsts.TABLE_NAME,
                BowConsts.ID_COLUMN_NAME + "= ? ",
                new String[]{String.valueOf(bowId)}
        );
    }

}
