package ar.com.tzulberti.archerytraining.database.migrations;

import android.database.sqlite.SQLiteDatabase;

/**
 * Interface used when a database migration is missing.
 *
 * Created by tzulberti on 6/3/17.
 */
public interface IDatabaseMigration {

    /**
     * @return the database version once that this migration is executed
     */
    int getCurentVersion();

    /**
     * Executes the migration. If there is any kind of error then it should throw an
     * exception
     *
     * @param db the database connection used to execute all the required alter
     */
    void executeMigration(SQLiteDatabase db);
}
