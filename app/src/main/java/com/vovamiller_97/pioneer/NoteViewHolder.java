package com.vovamiller_97.pioneer;

import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vovamiller_97.pioneer.db.Note;
import com.vovamiller_97.pioneer.db.NoteRepository;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NoteViewHolder extends RecyclerView.ViewHolder {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy.MM.dd",
            Locale.ENGLISH);

    private ListFragment.OnInteractionListener mListener;
    private TextView noteTitleTextView;
    private TextView noteTextTextView;
    private TextView noteDateTextView;
    private ImageView noteImageView;
    private long id;

    public NoteViewHolder(final View itemView, ListFragment.OnInteractionListener listener) {
        super(itemView);
        mListener = listener;
        noteTitleTextView = itemView.findViewById(R.id.cardViewTitle);
        noteTextTextView = itemView.findViewById(R.id.cardViewText);
        noteDateTextView = itemView.findViewById(R.id.cardViewDate);
        noteImageView = itemView.findViewById(R.id.cardViewImg);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mListener.onChooseNote(id);
            }
        });
    }

    public void bind(final Note note) {
        noteTitleTextView.setText(note.getTitle());
        noteTextTextView.setText(note.getText());
        noteDateTextView.setText(SDF.format(note.getDate()));

        final String imagePath = note.getImage();
        if (imagePath.length() > 0) {
            new SetImgAsyncTask(this, imagePath).execute();

        } else {
            noteImageView.setImageResource(R.drawable.ic_panorama_light_32dp);
        }

        id = note.getId();
    }

    // Get compressed image and assign it to Image View.
    private static class SetImgAsyncTask extends AsyncTask<Void, Void, Bitmap> {
        private WeakReference<NoteViewHolder> contextRef;
        private String imgPath;

        public SetImgAsyncTask(NoteViewHolder context, String imgPath) {
            contextRef = new WeakReference<>(context);
            this.imgPath = imgPath;
        }

        @Override
        protected Bitmap doInBackground(final Void... voids) {
            return AppUtils.getBitmap(imgPath + CameraActivity.SUFFIX_COMPRESSED);
        }

        @Override
        public void onPostExecute(final Bitmap bitmap) {
            NoteViewHolder context = contextRef.get();
            if (context != null) {
                super.onPostExecute(bitmap);
                if (bitmap != null) {
                    context.noteImageView.setImageBitmap(bitmap);
                } else {
                    context.noteImageView.setImageResource(R.drawable.ic_panorama_light_32dp);
                }
            }
        }
    }

}
