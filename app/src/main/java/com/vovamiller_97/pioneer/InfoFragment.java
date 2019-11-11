package com.vovamiller_97.pioneer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.vovamiller_97.pioneer.db.Note;
import com.vovamiller_97.pioneer.db.NoteRepository;

import java.lang.ref.WeakReference;


public class InfoFragment extends Fragment {

    private static final String ID_KEY = "ID_KEY";
    private static final String IMG_KEY = "IMG_KEY";

    private long mId;
    private String imgPath;

    public InfoFragment() {}

    public static InfoFragment newInstance(long id, final String imgPath) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putLong(ID_KEY, id);
        args.putString(IMG_KEY, imgPath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mId = getArguments().getLong(ID_KEY);
            imgPath = getArguments().getString(IMG_KEY);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            // Fragment has just been created for the first time.
            // Load note's light info from DB and then load note image.
            new LoadNoteAsyncTask(this, mId).execute();
        } else {
            // Fragment has been recreated.
            // Load the note image again.
            new LoadImageAsyncTask(this, imgPath).execute();
        }
    }

    // Load note from DB.
    private static class LoadNoteAsyncTask extends AsyncTask<Void, Void, Note> {
        private WeakReference<InfoFragment> contextRef;
        private long mId;

        public LoadNoteAsyncTask(InfoFragment context, long mId) {
            contextRef = new WeakReference<>(context);
            this.mId = mId;
        }

        @Override
        protected Note doInBackground(final Void... voids) {
            // Load note from DB.
            NoteRepository nr = new NoteRepository(App.getDatabaseHolder());
            return nr.loadNote(mId);
        }

        @Override
        public void onPostExecute(final Note note) {
            InfoFragment context = contextRef.get();
            if (context == null) return;
            View view = context.getView();
            if (view == null) return;
            super.onPostExecute(note);
            if (note != null) {
                final EditText editTextView = view.findViewById(R.id.textInfo);
                editTextView.setText(note.getText());
                new InfoFragment.LoadImageAsyncTask(context, note.getImage()).execute();
            }
        }
    }

    // Load image from internal storage.
    private static class LoadImageAsyncTask extends AsyncTask<Void, Void, Bitmap> {
        private WeakReference<InfoFragment> contextRef;
        private String imgPath;

        public LoadImageAsyncTask(InfoFragment context, String imgPath) {
            contextRef = new WeakReference<>(context);
            this.imgPath = imgPath;
        }

        @Override
        protected Bitmap doInBackground(final Void... voids) {
            if (imgPath.length() > 0) {
                return AppUtils.getBitmap(imgPath);
            }
            return null;
        }

        @Override
        public void onPostExecute(final Bitmap bitmap) {
            InfoFragment context = contextRef.get();
            if (context == null) return;
            View view = context.getView();
            if (view == null) return;
            super.onPostExecute(bitmap);
            final ImageView imgView = view.findViewById(R.id.imgInfo);
            final ProgressBar progressBar = view.findViewById(R.id.imgInfoLoading);
            if (bitmap != null) {
                imgView.setImageBitmap(bitmap);
            } else {
                imgView.setImageResource(R.drawable.ic_panorama_light_32dp);
            }
            imgView.animate().alpha(1.0f);
            progressBar.setVisibility(View.GONE);
        }
    }

}
