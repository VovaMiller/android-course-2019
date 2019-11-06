package com.vovamiller_97.pioneer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

public class HostActivity extends AppCompatActivity
        implements ListFragment.OnInteractionListener,CameraFragment.OnInteractionListener {

    private static final String NOTE_ID_KEY = "NOTE_ID_KEY";
    private static final String TAG_LIST = "TAG_LIST";
    private static final String TAG_INFO = "TAG_INFO";
    private static final String TAG_CAMERA = "TAG_CAMERA";

    private String noteId;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        fab = findViewById(R.id.fabOpenCamera);

        if (savedInstanceState == null) {
            noteId = null;
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.hostActivityContainer, ListFragment.newInstance(), TAG_LIST)
                    .addToBackStack(null)
                    .commit();
        } else {
            noteId = savedInstanceState.getString(NOTE_ID_KEY, null);
        }

        setListeners();
        updateTitle();
        updateFloatingButtonState();
    }

    private void setListeners() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });
    }

    public void onChooseNote(final String id) {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag(TAG_INFO) != null) {
            fm.popBackStack();
        } else {
            fab.setVisibility(View.GONE);
        }

        fm.beginTransaction()
                .setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.enter_from_left,
                        R.anim.exit_to_right
                )
                .replace(R.id.hostActivityContainer2, InfoFragment.newInstance(id), TAG_INFO)
                .addToBackStack(null)
                .commit();

        noteId = id;
        updateTitle();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        int backStackSize = fm.getBackStackEntryCount();

        if (backStackSize == 1) {
            // We see the list only.
            finish();
        } else if (backStackSize == 2) {
            // Either a note or camera is opened.
            super.onBackPressed();
            fab.setVisibility(View.VISIBLE);
            noteId = null;
            updateTitle();
        } else {
            Log.w("FragmentManager", "backStackSize == " + backStackSize);
            Log.w("FragmentManager", "onBackPressed: backStackSize isn't in {1, 2}");
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (noteId != null) {
            outState.putString(NOTE_ID_KEY, noteId);
        }
        super.onSaveInstanceState(outState);
    }

    private void updateTitle() {
        boolean isLandscape = getResources().getBoolean(R.bool.is_landscape);
        boolean isPhone = getResources().getBoolean(R.bool.is_phone);
        if ((noteId == null) || (isLandscape && !isPhone)) {
            setTitle(R.string.title_main);
        } else {
            Note note = NoteRepository.getNoteById(noteId);
            if (note != null) {
                setTitle(note.getTitle());
            }
        }
    }

    private void updateFloatingButtonState() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment_info = fm.findFragmentByTag(TAG_INFO);
        Fragment fragment_camera = fm.findFragmentByTag(TAG_CAMERA);
        if ((fragment_info != null) || (fragment_camera != null)) {
            fab.setVisibility(View.GONE);
        } else {
            fab.setVisibility(View.VISIBLE);
        }
    }

    private void openCamera() {
        fab.setVisibility(View.GONE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.hostActivityContainerAll, CameraFragment.newInstance(), TAG_CAMERA)
                .addToBackStack(null)
                .commit();
    }

    public void onPhotoTaken(@NonNull final File file) {
        String message = "New Photo: \"" + file.getName() + "\"";
        Log.d("CAMCAM", message);
//        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        // https://stackoverflow.com/questions/3875184/cant-create-handler-inside-thread-that-has-not-called-looper-prepare
    }

    public void finishCameraFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(TAG_CAMERA);
        if (fragment != null) {
            onBackPressed();
        }
    }
}
