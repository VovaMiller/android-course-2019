package com.vovamiller_97.pioneer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AppSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "NotesDatabase.db";
    private static final int VERSION = 1;

    public AppSQLiteOpenHelper(@Nullable final Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(@NonNull final SQLiteDatabase db) {
        NoteContract.createTable(db);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {}
}
