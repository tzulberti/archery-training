package ar.com.tzulberti.archerytraining.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ar.com.tzulberti.archerytraining.database.DatabaseHelper;
import ar.com.tzulberti.archerytraining.model.bow.Bow;
import ar.com.tzulberti.archerytraining.model.bow.SightDistanceValue;

/**
 * DAO used to save all the information related to the bow
 *
 * Created by tzulberti on 6/12/17.
 */
public class BowDAO extends BaseDAO {

    public BowDAO(DatabaseHelper databaseHelper) {
        super(databaseHelper);
    }


    public List<Bow> getBowsInformation(long bowId) {
        List<Bow> res = new ArrayList<>();

        String query = "SELECT " +
                Bow.TABLE_NAME + "." + Bow.ID_COLUMN_NAME + ", " +
                Bow.TABLE_NAME + "." + Bow.NAME_COLUMN_NAME + ", " +
                SightDistanceValue.TABLE_NAME + "." + SightDistanceValue.DISTANCE_COLUMN_NAME + ", " +
                SightDistanceValue.TABLE_NAME + "." + SightDistanceValue.SIGHT_VALUE_COLUMN_NAME + ", " +
                SightDistanceValue.TABLE_NAME + "." + SightDistanceValue.ID_COLUMN_NAME + "  " +
            "FROM " +  Bow.TABLE_NAME + " " +
            "LEFT JOIN " + SightDistanceValue.TABLE_NAME + " " +
                "ON " + Bow.TABLE_NAME + "." + Bow.ID_COLUMN_NAME + " = " + SightDistanceValue.TABLE_NAME + "." + SightDistanceValue.BOW_ID_COLUMN_NAME + " " +
                ((bowId >= 0) ? "WHERE id = ? " :  "" )+
            "ORDER BY " +
                Bow.TABLE_NAME + "." + Bow.ID_COLUMN_NAME + " DESC, " +
                SightDistanceValue.TABLE_NAME + "." + SightDistanceValue.DISTANCE_COLUMN_NAME;

        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        Cursor bowCursor = db.rawQuery(
                query,
                (bowId >= 0) ? new String[]{String.valueOf(bowId)} : null
        );

        Bow currentBow = null;
        while (bowCursor.moveToNext()) {
            long databaseBowId = bowCursor.getLong(0);
            if (currentBow == null || databaseBowId != currentBow.id) {
                currentBow = new Bow();
                currentBow.sightDistanceValues = new ArrayList<>();
                currentBow.id = databaseBowId;
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
        bowCursor.close();
        return res;
    }

    public void createBow(String name, List<SightDistanceValue> sightValues) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Bow.NAME_COLUMN_NAME, name);
        long bowId = db.insertOrThrow(Bow.TABLE_NAME, null, contentValues);

        Bow res = new Bow();
        res.name = name;
        res.id = bowId;
        res.sightDistanceValues = sightValues;

        for (SightDistanceValue sightDistanceValue : sightValues) {
            ContentValues contentValuesSightDistance = new ContentValues();
            contentValuesSightDistance.put(SightDistanceValue.BOW_ID_COLUMN_NAME, res.id);
            contentValuesSightDistance.put(SightDistanceValue.DISTANCE_COLUMN_NAME, sightDistanceValue.distance);
            contentValuesSightDistance.put(SightDistanceValue.SIGHT_VALUE_COLUMN_NAME, sightDistanceValue.sightValue);
            sightDistanceValue.id = db.insertOrThrow(SightDistanceValue.TABLE_NAME, null, contentValuesSightDistance);
        }
    }

    public void deleteBow(long bowId) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();

        db.delete(
                SightDistanceValue.TABLE_NAME,
                SightDistanceValue.BOW_ID_COLUMN_NAME + "= ? ",
                new String[]{String.valueOf(bowId)}
        );

        db.delete(
                Bow.TABLE_NAME,
                Bow.ID_COLUMN_NAME + "= ? ",
                new String[]{String.valueOf(bowId)}
        );
    }

}
