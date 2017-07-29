package ar.com.tzulberti.archerytraining.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import ar.com.tzulberti.archerytraining.database.migrations.DatabaseMigration20;
import ar.com.tzulberti.archerytraining.database.migrations.DatabaseMigration21;
import ar.com.tzulberti.archerytraining.database.migrations.DatabaseMigration23;
import ar.com.tzulberti.archerytraining.database.migrations.DatabaseMigration27;
import ar.com.tzulberti.archerytraining.database.migrations.DatabaseMigration29;
import ar.com.tzulberti.archerytraining.database.migrations.DatabaseMigration4;
import ar.com.tzulberti.archerytraining.database.migrations.DatabaseMigration5;
import ar.com.tzulberti.archerytraining.database.migrations.DatabaseMigration6;
import ar.com.tzulberti.archerytraining.database.migrations.DatabaseMigration7;
import ar.com.tzulberti.archerytraining.database.migrations.DatabaseMigration10;
import ar.com.tzulberti.archerytraining.database.migrations.IDatbasseMigration;
import io.sentry.Sentry;

/**
 * Created by tzulberti on 4/18/17.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "archery_training.db";

    protected static final int DATABASE_VERSION = 30;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        TablesCreator tablesCreator = new TablesCreator();
        tablesCreator.createAll(db);

        InsertConstantValues insertConstantValues = new InsertConstantValues();
        insertConstantValues.insertAllFixtureData(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Get all the existing database migrations
        List<IDatbasseMigration> existingMigrations = new ArrayList<>();
        existingMigrations.add(new DatabaseMigration4());
        existingMigrations.add(new DatabaseMigration5());
        existingMigrations.add(new DatabaseMigration6());
        existingMigrations.add(new DatabaseMigration7());
        existingMigrations.add(new DatabaseMigration10());
        existingMigrations.add(new DatabaseMigration20());
        existingMigrations.add(new DatabaseMigration21());
        existingMigrations.add(new DatabaseMigration23());
        existingMigrations.add(new DatabaseMigration27());
        existingMigrations.add(new DatabaseMigration29());

        // make sure to disable any FK validation because the table might
        // create temporary table to drop the old schema table (SQLITE
        // doesn't allows us to drop a column)
        db.setForeignKeyConstraintsEnabled(false);

        for (IDatbasseMigration databaseMigration : existingMigrations) {
            int migrationVersion = databaseMigration.getCurentVersion();
            if (oldVersion <= migrationVersion && migrationVersion < newVersion) {
                try {
                    databaseMigration.executeMigration(db);
                } catch (Exception e) {
                    Sentry.capture(e);
                    throw e;
                }
            }
        }

    }


}

