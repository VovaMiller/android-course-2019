package com.vovamiller_97.pioneer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.vovamiller_97.pioneer.db.Note;
import com.vovamiller_97.pioneer.db.NoteGenerator;
import com.vovamiller_97.pioneer.db.NoteRepository;

import java.util.Date;

/**
 * This Fragment manages a single background task and retains itself across configuration changes.
 */
public class NewNoteTaskFragment extends Fragment {

    private TaskCallbacks mCallbacks;
    private Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof TaskCallbacks) {
            mCallbacks = (TaskCallbacks) context;
            mContext = context;
        } else {
            throw new RuntimeException(context.toString() + " must implement TaskCallbacks");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
        mContext = null;
    }

    private class NewNoteTask extends AsyncTask<Void, Void, Void> {
        private long lastModified;
        private String imgPath;

        public NewNoteTask(long lastModified, String imgPath) {
            this.lastModified = lastModified;
            this.imgPath = imgPath;
        }

        @Override
        protected Void doInBackground(Void... ignore) {
            // Adding a new note.
            Date date = new Date(lastModified);
            Note note = NoteGenerator.random(mContext, date, imgPath);
            NoteRepository nr = new NoteRepository(App.getDatabaseHolder());
            nr.create(note);
            return null;
        }

        @Override
        protected void onPostExecute(Void ignore) {
            if (mCallbacks != null) {
                mCallbacks.onPostExecute();
            }
        }
    }

    public void newTask(long lastModified, String imgPath) {
        new NewNoteTask(lastModified, imgPath).execute();
    }

    public interface TaskCallbacks {
        void onPostExecute();
    }
}