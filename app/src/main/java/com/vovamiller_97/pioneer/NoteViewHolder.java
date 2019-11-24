package com.vovamiller_97.pioneer;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.MenuItem;
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

    private Context mContext;
    private ListFragment.OnInteractionListener mListener;
    private TextView noteTextTextView;
    private TextView noteDateTextView;
    private ImageView noteImageView;
    private Note note;

    public NoteViewHolder(final View itemView, Context context, ListFragment.OnInteractionListener listener) {
        super(itemView);
        mContext = context;
        mListener = listener;
        noteTextTextView = itemView.findViewById(R.id.cardViewText);
        noteDateTextView = itemView.findViewById(R.id.cardViewDate);
        noteImageView = itemView.findViewById(R.id.cardViewImg);
        itemView.setOnClickListener(view -> mListener.onChooseNote(note));

        ImageView noteButtonMore = itemView.findViewById(R.id.cardViewMore);
        noteButtonMore.setOnClickListener(view -> onButtonMoreClicked(view, note.getId()));
    }

    public void bind(final Note note) {
        noteTextTextView.setText(note.getText());
        noteDateTextView.setText(SDF.format(note.getDate()));

        final String imagePath = note.getImage();
        if (imagePath.length() > 0) {
            loadImage(imagePath, true);
        } else {
            noteImageView.setImageResource(R.drawable.ic_panorama_light_32dp);
        }

        this.note = note;
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

    public void onButtonMoreClicked(View view, long id) {
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        popupMenu.inflate(R.menu.popupmenu);
        popupMenu.setOnMenuItemClickListener(item -> onPopupMenuItemClick(item, id));
        popupMenu.show();
    }

    private boolean onPopupMenuItemClick(MenuItem item, long id) {
        switch (item.getItemId()) {
            case R.id.popupShare:
                onButtonShareClicked();
                return true;
            case R.id.popupDelete:
                mListener.onDeleteDialogRequired(id);
                return true;
            default:
                return false;
        }
    }

    private void onButtonShareClicked() {
        final String text = note.getText();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        mContext.startActivity(sendIntent);
    }

}
