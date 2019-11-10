package com.vovamiller_97.pioneer.db;

import com.vovamiller_97.pioneer.R;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class NoteRepository {

    private DatabaseHolder databaseHolder;

    public NoteRepository(@NonNull final DatabaseHolder databaseHolder) {
        this.databaseHolder = databaseHolder;
    }

    public void create(@NonNull final Note note) {
        try {
            SQLiteDatabase database = databaseHolder.open();

            ContentValues contentValues = new ContentValues();
            contentValues.put(NoteContract.Columns.TEXT, note.getText());
            contentValues.put(NoteContract.Columns.TITLE, note.getTitle());
            contentValues.put(NoteContract.Columns.IMAGE, note.getImage());
            contentValues.put(NoteContract.Columns.DATE, note.getDateSerialized());

            database.insert(NoteContract.TABLE_NAME, null, contentValues);
        } finally {
            databaseHolder.close();
        }
    }

    public List<Note> loadAll() {
        List<Note> notesList = new ArrayList<>();
        Cursor cursor = null;
        try {
            SQLiteDatabase database = databaseHolder.open();

            cursor = database.query(
                    NoteContract.TABLE_NAME,
                    new String[] {
                            NoteContract.Columns._ID,
                            NoteContract.Columns.TEXT,
                            NoteContract.Columns.TITLE,
                            NoteContract.Columns.IMAGE,
                            NoteContract.Columns.DATE
                    },
                    null,
                    null,
                    null,
                    null,
                    null
            );

            while (cursor.moveToNext()) {
                Note note = new Note();
                note.setId(cursor.getLong(cursor.getColumnIndex(NoteContract.Columns._ID)));
                note.setText(cursor.getString(cursor.getColumnIndex(NoteContract.Columns.TEXT)));
                note.setTitle(cursor.getString(cursor.getColumnIndex(NoteContract.Columns.TITLE)));
                note.setImage(cursor.getString(cursor.getColumnIndex(NoteContract.Columns.IMAGE)));
                note.setDate(cursor.getLong(cursor.getColumnIndex(NoteContract.Columns.DATE)));
                notesList.add(note);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            databaseHolder.close();
        }

        return notesList;
    }

    public Note loadNote(long id) {
        Cursor cursor = null;
        Note note = null;
        try {
            SQLiteDatabase database = databaseHolder.open();

            cursor = database.query(
                    NoteContract.TABLE_NAME,
                    new String[] {
                            NoteContract.Columns._ID,
                            NoteContract.Columns.TEXT,
                            NoteContract.Columns.TITLE,
                            NoteContract.Columns.IMAGE,
                            NoteContract.Columns.DATE
                    },
                    "_id = " + id,
                    null,
                    null,
                    null,
                    null
            );

            if (cursor.moveToNext()) {
                note = new Note();
                note.setId(cursor.getLong(cursor.getColumnIndex(NoteContract.Columns._ID)));
                note.setText(cursor.getString(cursor.getColumnIndex(NoteContract.Columns.TEXT)));
                note.setTitle(cursor.getString(cursor.getColumnIndex(NoteContract.Columns.TITLE)));
                note.setImage(cursor.getString(cursor.getColumnIndex(NoteContract.Columns.IMAGE)));
                note.setDate(cursor.getLong(cursor.getColumnIndex(NoteContract.Columns.DATE)));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            databaseHolder.close();
        }

        return note;
    }

}
