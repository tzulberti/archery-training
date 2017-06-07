package ar.com.tzulberti.archerytraining.database.migrations;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by tzulberti on 6/3/17.
 */

public interface IDatbasseMigration {

    int getCurentVersion();

    void executeMigration(SQLiteDatabase db);
}
