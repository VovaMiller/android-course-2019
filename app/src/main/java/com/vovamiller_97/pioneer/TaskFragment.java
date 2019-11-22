package com.vovamiller_97.pioneer;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.squareup.picasso.Picasso;
import com.vovamiller_97.pioneer.db.Note;
import com.vovamiller_97.pioneer.db.NoteRepository;

import java.io.File;
import java.io.IOException;

/**
 * This Fragment manages some background tasks and retains itself across configuration changes.
 */
public class TaskFragment extends Fragment {

    private TaskCallbacks mCallbacks;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof TaskCallbacks) {
            mCallbacks = (TaskCallbacks) context;
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
    }

    private class NewNoteTask extends AsyncTask<Void, Void, Void> {
        private long lastModified;
        private String imgPath;
        private String text;

        public NewNoteTask(long lastModified, final String imgPath, final String text) {
            this.lastModified = lastModified;
            this.imgPath = imgPath;
            this.text = text;
        }

        @Override
        protected Void doInBackground(Void... ignore) {
            // Adding a new note.
            Note note = new Note();
            note.setText(text);
            note.setDate(lastModified);
            note.setImage(imgPath);
            NoteRepository nr = new NoteRepository(App.getDatabaseHolder());
            nr.create(note);
            return null;
        }

        @Override
        protected void onPostExecute(Void ignore) {
            if (mCallbacks != null) {
                mCallbacks.onPostExecuteNewNote();
            }
        }
    }

    private class UpdateTextTask extends AsyncTask<Void, Void, Boolean> {
        private long id;
        private String newText;

        public UpdateTextTask(long id, final String newText) {
            this.id = id;
            this.newText = newText;
        }

        @Override
        protected Boolean doInBackground(Void... ignore) {
            NoteRepository nr = new NoteRepository(App.getDatabaseHolder());
            return nr.updateText(id, newText);
        }

        @Override
        protected void onPostExecute(Boolean failed) {
            if (failed == null) failed = true;
            if (mCallbacks != null) mCallbacks.onPostExecuteUpdateText(failed);
        }
    }

    private class LoadBitmapTask extends AsyncTask<Void, Void, Bitmap> {
        private final long lastModified;
        private final String imgPath;

        public LoadBitmapTask(long lastModified, final String imgPath) {
            this.lastModified = lastModified;
            this.imgPath = imgPath;
        }

        @Override
        protected Bitmap doInBackground(Void... ignore) {
            Bitmap bitmap;
            try {
                bitmap = Picasso.get().load(new File(imgPath)).get();
            } catch (IOException e) {
                bitmap = null;
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (mCallbacks != null) mCallbacks.onBitmapLoaded(lastModified, imgPath, bitmap);
        }
    }

    public void newNote(long lastModified, final String imgPath, final String text) {
        new NewNoteTask(lastModified, imgPath, text).execute();
    }

    public void updateText(long id, final String newText) {
        new UpdateTextTask(id, newText).execute();
    }

    public void loadBitmap(long lastModified, final String imgPath) {
        new LoadBitmapTask(lastModified, imgPath).execute();
    }

    public void recognizeText(long lastModified, final String imgPath, @NonNull final Bitmap bitmap) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        detector.processImage(image)
                .addOnSuccessListener(firebaseVisionText -> mCallbacks.onTextRecognized(
                        lastModified, imgPath, firebaseVisionText.getText()))
                .addOnFailureListener(e -> mCallbacks.onTextRecognized(
                        lastModified, imgPath, null));
    }

    public interface TaskCallbacks {
        void onPostExecuteNewNote();
        void onPostExecuteUpdateText(boolean failed);
        void onBitmapLoaded(long lastModified, final String imgPath, final Bitmap bitmap);
        void onTextRecognized(long lastModified, final String imgPath, final String text);
    }
}