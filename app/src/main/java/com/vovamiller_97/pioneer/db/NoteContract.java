package com.vovamiller_97.pioneer.db;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import androidx.annotation.NonNull;

public class NoteContract {

    public static final String TABLE_NAME = "notes_table";

    public interface Columns extends BaseColumns {
        String TEXT = "name";
        String IMAGE = "image";
        String DATE = "date";
    }

    private NoteContract() {}

    public static void createTable(@NonNull final SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE_NAME
                        + " ( "
                        + Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + Columns.TEXT + " TEXT NOT NULL,"
                        + Columns.IMAGE + " TEXT NOT NULL,"
                        + Columns.DATE + " INTEGER NOT NULL"
                        + " );"
        );
    }
}
