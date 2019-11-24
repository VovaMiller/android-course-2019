package com.vovamiller_97.pioneer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;
import com.vovamiller_97.pioneer.db.Note;

import java.io.File;


public class InfoFragment extends Fragment {

    private static final String IMG_KEY = "IMG_KEY";
    private static final String TEXT_KEY = "TEXT_KEY";

    private String mImgPath;
    private String mText;
    private ImageView imgView;
    private ProgressBar progressBar;

    public InfoFragment() {}

    public static InfoFragment newInstance(final Note note) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putString(IMG_KEY, note.getImage());
        args.putString(TEXT_KEY, note.getText());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImgPath = getArguments().getString(IMG_KEY);
            mText = getArguments().getString(TEXT_KEY);
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

        imgView = view.findViewById(R.id.imgInfo);
        progressBar = view.findViewById(R.id.imgInfoLoading);

        if (savedInstanceState == null) {
            // Fragment has just been created for the first time.
            final EditText editTextView = view.findViewById(R.id.textInfo);
            editTextView.setText(mText);
        }
        loadImage(mImgPath);
    }

    private void loadImage(final String imgPath) {
        Picasso
                .get()
                .load(new File(imgPath))
                .fit()
                .centerInside()
                .into(imgView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        imgView.animate().alpha(1.0f);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                        imgView.setImageResource(R.drawable.ic_panorama_light_32dp);
                        imgView.animate().alpha(1.0f);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

}
