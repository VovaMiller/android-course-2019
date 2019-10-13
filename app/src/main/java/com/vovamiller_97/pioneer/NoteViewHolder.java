package com.vovamiller_97.pioneer;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class NoteViewHolder extends RecyclerView.ViewHolder {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy.MM.dd",
            Locale.ENGLISH);

    private TextView noteTitleTextView;
    private TextView noteTextTextView;
    private TextView noteDateTextView;
    private ImageView noteImageView;
    private String id;

    public NoteViewHolder(final View itemView) {
        super(itemView);
        noteTitleTextView = itemView.findViewById(R.id.cardViewTitle);
        noteTextTextView = itemView.findViewById(R.id.cardViewText);
        noteDateTextView = itemView.findViewById(R.id.cardViewDate);
        noteImageView = itemView.findViewById(R.id.cardViewImg);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Context context = v.getContext();
                context.startActivity(InfoActivity.getIntent(context, id));
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
