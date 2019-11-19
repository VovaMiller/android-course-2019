package com.vovamiller_97.pioneer;

import android.app.Application;

import com.vovamiller_97.pioneer.db.DatabaseHolder;

public class App extends Application {

    private static DatabaseHolder databaseHolder;

    @Override
    public void onCreate() {
        super.onCreate();
        databaseHolder = new DatabaseHolder(this);
    }

    public static DatabaseHolder getDatabaseHolder() {
        return databaseHolder;
    }
}
