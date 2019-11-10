package com.vovamiller_97.pioneer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vovamiller_97.pioneer.db.Note;
import com.vovamiller_97.pioneer.db.NoteGenerator;
import com.vovamiller_97.pioneer.db.NoteRepository;

import java.io.File;
import java.util.Date;

public class HostActivity extends AppCompatActivity implements ListFragment.OnInteractionListener {

    private static final String NOTE_ID_KEY = "NOTE_ID_KEY";
    private static final String TAG_LIST = "TAG_LIST";
    private static final String TAG_INFO = "TAG_INFO";

    private Long noteId;
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
            noteId = savedInstanceState.getLong(NOTE_ID_KEY);
        }

        setListeners();
        updateTitle();
        updateFloatingButtonState();
    }

    private void setListeners() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickFAB();
            }
        });
    }

    private void onClickFAB() {
        Intent cameraIntent = new Intent(this, CameraActivity.class);
        startActivity(cameraIntent);
    }

    public void onChooseNote(final long id) {
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
            outState.putLong(NOTE_ID_KEY, noteId);
        }
        super.onSaveInstanceState(outState);
    }

    private void updateTitle() {
        boolean isLandscape = getResources().getBoolean(R.bool.is_landscape);
        boolean isPhone = getResources().getBoolean(R.bool.is_phone);
        if ((noteId == null) || (isLandscape && !isPhone)) {
            setTitle(R.string.title_main);
        } else {
            NoteRepository nr = new NoteRepository(App.getDatabaseHolder());
            final Note note = nr.loadNote(noteId);
            if (note != null) {
                setTitle(note.getTitle());
            }
        }
    }

    private void updateFloatingButtonState() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment_info = fm.findFragmentByTag(TAG_INFO);
        if (fragment_info != null) {
            fab.setVisibility(View.GONE);
        } else {
            fab.setVisibility(View.VISIBLE);
        }
    }

}
