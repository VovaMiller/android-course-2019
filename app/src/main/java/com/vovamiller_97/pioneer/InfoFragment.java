package com.vovamiller_97.pioneer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vovamiller_97.pioneer.db.Note;
import com.vovamiller_97.pioneer.db.NoteRepository;

import java.io.File;
import java.io.IOException;


public class InfoFragment extends Fragment {

    private static final String ID_KEY = "ID_KEY";

    private long mId;

    public InfoFragment() {}

    public static InfoFragment newInstance(long id) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putLong(ID_KEY, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mId = getArguments().getLong(ID_KEY);
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

        NoteRepository nr = new NoteRepository(App.getDatabaseHolder());
        final Note note = nr.loadNote(mId);

        if (note != null) {
            final TextView textView = getView().findViewById(R.id.textInfo);
            textView.setText(note.getText());

            final String imagePath = note.getImage();
            final ImageView imgView = getView().findViewById(R.id.imgInfo);
            if (imagePath.length() > 0) {
                Bitmap bitmap = AppUtils.getBitmap(imagePath);
                if (bitmap != null) {
                    imgView.setImageBitmap(bitmap);
                } else {
                    imgView.setImageResource(R.drawable.nodata);
                }
            } else {
                imgView.setImageResource(R.drawable.nodata);
            }
        }
    }
}
