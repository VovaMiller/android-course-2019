package com.vovamiller_97.pioneer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vovamiller_97.pioneer.db.Note;
import com.vovamiller_97.pioneer.db.NoteRepository;

import java.lang.ref.WeakReference;
import java.util.List;


public class ListFragment extends Fragment {

    private NoteAdapter adapter;
    private OnInteractionListener mListener;

    public static ListFragment newInstance() {
        ListFragment fragment = new ListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final RecyclerView recyclerView = getView().findViewById(R.id.noteRecyclerView);
        final boolean isLandscape = getResources().getBoolean(R.bool.is_landscape);
        final boolean isPhone = getResources().getBoolean(R.bool.is_phone);

        if ((isPhone && isLandscape) || (!isPhone && !isLandscape)) {
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
        recyclerView.setHasFixedSize(true);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 10);

        adapter = new NoteAdapter(mListener);
        recyclerView.setAdapter(adapter);
        updateList();
    }

    public void updateList() {
        new UpdateAsyncTask(this).execute();
    }

    private static class UpdateAsyncTask extends AsyncTask<Void, Void, List<Note>> {
        private WeakReference<ListFragment> contextRef;

        public UpdateAsyncTask(ListFragment context) {
            contextRef = new WeakReference<>(context);
        }

        @Override
        protected List<Note> doInBackground(final Void... voids) {
            NoteRepository nr = new NoteRepository(App.getDatabaseHolder());
            return nr.loadAll();
        }

        @Override
        public void onPostExecute(final List<Note> notesList) {
            ListFragment context = contextRef.get();
            if (context != null) {
                super.onPostExecute(notesList);
                context.adapter.setNoteList(notesList);
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnInteractionListener) {
            mListener = (OnInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnInteractionListener {
        void onChooseNote(final long id);
    }

}
