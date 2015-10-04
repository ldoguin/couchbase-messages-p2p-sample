package com.couchbase.devxp.message;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.util.Log;

import java.io.IOException;

public class Application extends android.app.Application {
    public static String TAG = "Message";
    private static String DBNAME = "messages";
    private Database database = null;
    private Manager manager;

    public Database getDatabase() {
        if (database == null) {
            try {
                manager = new Manager(new AndroidContext(this), Manager.DEFAULT_OPTIONS);
                Log.d(TAG, "Created database manager");

                if (!Manager.isValidDatabaseName(DBNAME)) {
                    Log.e(TAG, "Bad database name");
                    return null;
                }

                try {
                    database = manager.getDatabase(DBNAME);
                    database.delete();
                    database = manager.getDatabase(DBNAME);
                    Log.d(TAG, "Database created");
                } catch (CouchbaseLiteException e) {
                    Log.e(TAG, "Database creation failed");
                    return null;
                }
            } catch (IOException e) {
                Log.e(TAG, "Failed to create database manager");
                return null;
            }

        }
        return database;
    }

    @Override
    public void onCreate() {
        // Load up the database on start, if this fails the app is of no use anyway
        if (getDatabase() == null) {

            Log.e(TAG, "Failed to initialize");
            return;
        }
        super.onCreate();
    }

}
