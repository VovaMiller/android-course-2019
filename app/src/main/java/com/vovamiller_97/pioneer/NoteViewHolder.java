package com.vovamiller_97.pioneer;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
    private String id;

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
        noteImageView.setImageResource(note.getDrawableIdRes());
        id = note.getId();
    }
}
