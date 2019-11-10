package com.vovamiller_97.pioneer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vovamiller_97.pioneer.db.Note;
import com.vovamiller_97.pioneer.db.NoteRepository;

public class HostActivity extends AppCompatActivity
        implements ListFragment.OnInteractionListener,
        InfoFragment.OnInteractionListener,
        NewNoteTaskFragment.TaskCallbacks {

    private static final String NOTE_ID_KEY = "NOTE_ID_KEY";
    private static final String TAG_LIST = "TAG_LIST";
    private static final String TAG_INFO = "TAG_INFO";
    private static final String TAG_TASK_NEW_NOTE = "TAG_TASK_NEW_NOTE";

    private Long noteId;
    private FloatingActionButton fab;
    private NewNoteTaskFragment mNewNoteTaskFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        FragmentManager fm = getSupportFragmentManager();
        mNewNoteTaskFragment = (NewNoteTaskFragment) fm.findFragmentByTag(TAG_TASK_NEW_NOTE);
        if (mNewNoteTaskFragment == null) {
            mNewNoteTaskFragment = new NewNoteTaskFragment();
            fm.beginTransaction().add(mNewNoteTaskFragment, TAG_TASK_NEW_NOTE).commit();
        }

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
            if (noteId == -1) {
                noteId = null;
            }
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
        startActivityForResult(cameraIntent, CameraActivity.RESULT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CameraActivity.RESULT_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                long lastModified = data.getLongExtra(CameraActivity.RESULT_KEY_DATE, 0);
                String imgPath = data.getStringExtra(CameraActivity.RESULT_KEY_PATH);

                // Create new note, add it to DB and update the list.
                mNewNoteTaskFragment.newTask(lastModified, imgPath);
            }
        }
    }

    // Update the list of notes (called from NewNoteTaskFragment).
    public void onPostExecute() {
        FragmentManager fm = getSupportFragmentManager();
        ListFragment fragmentList = (ListFragment) fm.findFragmentByTag(TAG_LIST);
        if (fragmentList != null) {
            fragmentList.updateList();
        }
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
        // Title is now updated directly from InfoFragment.
        // updateTitle();
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
        } else {
            outState.putLong(NOTE_ID_KEY, -1);
        }
        super.onSaveInstanceState(outState);
    }

    private void updateTitle() {
        boolean isLandscape = getResources().getBoolean(R.bool.is_landscape);
        boolean isPhone = getResources().getBoolean(R.bool.is_phone);
        if ((noteId == null) || (isLandscape && !isPhone)) {
            setTitle(R.string.title_main);
        } else {
            // see changeTitle(String title)
        }
    }

    // Title modification called from InfoFragment.
    public void changeTitle(String title) {
        boolean isLandscape = getResources().getBoolean(R.bool.is_landscape);
        boolean isPhone = getResources().getBoolean(R.bool.is_phone);
        if (!isLandscape || isPhone) {
            setTitle(title);
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
