package com.vovamiller_97.pioneer;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteViewHolder> {

    private ListFragment.OnInteractionListener mListener;
    private List<Note> noteList = new ArrayList<>();

    public NoteAdapter(ListFragment.OnInteractionListener listener) {
        super();
        mListener = listener;
    }

    public void setNoteList(final List<Note> noteList) {
        this.noteList = noteList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.note_list_item, parent,
                false
        );
        return new NoteViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final NoteViewHolder holder, final int position) {
        holder.bind(noteList.get(position));
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }
}
