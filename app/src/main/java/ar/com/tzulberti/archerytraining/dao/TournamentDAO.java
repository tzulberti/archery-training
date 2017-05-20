package ar.com.tzulberti.archerytraining.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.security.cert.Extension;
import java.util.ArrayList;
import java.util.List;

import ar.com.tzulberti.archerytraining.consts.SerieInformationConsts;
import ar.com.tzulberti.archerytraining.consts.TournamentConsts;
import ar.com.tzulberti.archerytraining.helper.DatabaseHelper;
import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;
import ar.com.tzulberti.archerytraining.model.tournament.ExistingTournamentData;

/**
 * Created by tzulberti on 5/17/17.
 */

public class TournamentDAO {

    private DatabaseHelper databaseHelper;

    public TournamentDAO(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public List<ExistingTournamentData> getExistingTournaments() {
        List<ExistingTournamentData> res = new ArrayList<>();
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                String.format(
                        "SELECT %s, %s, %s " +
                        "FROM %s " +
                        "ORDER BY %s DESC",
                        TournamentConsts.ID_COLUMN_NAME, TournamentConsts.NAME_COLUMN_NAME, TournamentConsts.DATETIME_COLUMN_NAME,
                        TournamentConsts.TABLE_NAME,
                        TournamentConsts.DATETIME_COLUMN_NAME
                ),
                null
        );

        while (cursor.moveToNext()) {
            res.add(new ExistingTournamentData(
                    cursor.getInt(0),
                    cursor.getString(1),
                    DatetimeHelper.databaseValueToDate(cursor.getLong(2))
            ));
        }
        return res;
    }


    public long createTournament(String name, int distance, int targetSize, boolean isTournament, boolean isOutdoor) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TournamentConsts.DISTANCE_COLUMN_NAME, distance);
        contentValues.put(TournamentConsts.NAME_COLUMN_NAME, name);
        contentValues.put(TournamentConsts.DATETIME_COLUMN_NAME, DatetimeHelper.getCurrentTime());
        contentValues.put(TournamentConsts.TARGET_SIZE_COLUMN_NAME, targetSize);
        contentValues.put(TournamentConsts.IS_OUTDOOR_COLUMN_NAME, isOutdoor);
        contentValues.put(TournamentConsts.IS_TOURNAMENT_DATA_COLUMN_NAME, isTournament);
        long id = db.insert(TournamentConsts.TABLE_NAME, null, contentValues);
        return id;
    }
}
