package com.vovamiller_97.pioneer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class InfoFragment extends Fragment {

    private static final String ID_KEY = "ID_KEY";

    private String mId;

    public InfoFragment() {}

    public static InfoFragment newInstance(String id) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putString(ID_KEY, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mId = getArguments().getString(ID_KEY);
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

        final Note note = NoteRepository.getNoteById(mId);

        final TextView textView = getView().findViewById(R.id.textInfo);
        textView.setText(note.getText());

        final ImageView imgView = getView().findViewById(R.id.imgInfo);
        imgView.setImageResource(note.getDrawableIdRes());
    }
}
