package ar.com.tzulberti.archerytraining.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import ar.com.tzulberti.archerytraining.database.DatabaseHelper;

/**
 * Base DAO used with common functions that might be used by other DAOs
 */
public class BaseDAO {

    DatabaseHelper databaseHelper;

    public BaseDAO(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public boolean checkIfExists(String tableName, long id ) {
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
            "SELECT 1 FROM " + tableName + " WHERE id = ?",
            new String[] {String.valueOf(id)}
        );
        boolean res = cursor.getCount() > 0;
        cursor.close();
        return res;
    }
}
