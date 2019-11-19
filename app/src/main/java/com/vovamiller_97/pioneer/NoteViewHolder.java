package com.vovamiller_97.pioneer;

import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vovamiller_97.pioneer.db.Note;

import java.io.File;
import java.text.SimpleDateFormat;
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
    private String title;
    private String imgPath;

    public NoteViewHolder(final View itemView, ListFragment.OnInteractionListener listener) {
        super(itemView);
        mListener = listener;
        noteTitleTextView = itemView.findViewById(R.id.cardViewTitle);
        noteTextTextView = itemView.findViewById(R.id.cardViewText);
        noteDateTextView = itemView.findViewById(R.id.cardViewDate);
        noteImageView = itemView.findViewById(R.id.cardViewImg);
        itemView.setOnClickListener(view -> mListener.onChooseNote(id, title, imgPath));
    }

    public void bind(final Note note) {
        noteTitleTextView.setText(note.getTitle());
        noteTextTextView.setText(note.getText());
        noteDateTextView.setText(SDF.format(note.getDate()));

        final String imagePath = note.getImage();
        if (imagePath.length() > 0) {
            loadImage(imagePath, true);
        } else {
            noteImageView.setImageResource(R.drawable.ic_panorama_light_32dp);
        }

        id = note.getId();
        title = note.getTitle();
        imgPath = imagePath;
    }

    private void loadImage(final String imgPath, boolean compressed) {
        String imgPathTarget;
        if (compressed) {
            imgPathTarget = imgPath + AppUtils.SUFFIX_COMPRESSED_COPY;
        } else {
            imgPathTarget = imgPath;
        }
        Picasso
                .get()
                .load(new File(imgPathTarget))
                .config(Bitmap.Config.RGB_565)
                .fit()
                .centerCrop()
                .into(noteImageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {}

                    @Override
                    public void onError(Exception e) {
                        if (compressed) {
                            // If we can't load compressed copy, try to load original image.
                            loadImage(imgPath, false);
                        } else {
                            e.printStackTrace();
                            noteImageView.setImageResource(R.drawable.ic_panorama_light_32dp);
                        }
                    }
                });
    }

}
