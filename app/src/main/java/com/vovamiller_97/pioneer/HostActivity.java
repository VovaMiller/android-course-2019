package com.vovamiller_97.pioneer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HostActivity extends AppCompatActivity
        implements ListFragment.OnInteractionListener,
        TaskFragment.TaskCallbacks {

    private static final String NOTE_ID_KEY = "NOTE_ID_KEY";
    private static final String TAG_LIST = "TAG_LIST";
    private static final String TAG_INFO = "TAG_INFO";
    private static final String TAG_TASK_NEW_NOTE = "TAG_TASK_NEW_NOTE";

    private Long noteId;
    private FloatingActionButton fab;
    private TaskFragment mTaskFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        // Launch special fragment for proper processing of background tasks.
        FragmentManager fm = getSupportFragmentManager();
        mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_NEW_NOTE);
        if (mTaskFragment == null) {
            mTaskFragment = new TaskFragment();
            fm.beginTransaction().add(mTaskFragment, TAG_TASK_NEW_NOTE).commit();
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

        setTitle(R.string.title_main);
        setListeners();
        updateFloatingButtonState();
    }

    private void setListeners() {
        fab.setOnClickListener(view -> onClickFAB());
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

                // Start the chain of note creation:
                // 1) Load bitmap.
                // 2) Recognize text.
                // 3) Create note itself.
                mTaskFragment.loadBitmap(lastModified, imgPath);
            }
        }
    }

    // Callback from TaskFragment after loading bitmap (note creation, step 1 of 3).
    public void onBitmapLoaded(long lastModified, final String imgPath, final Bitmap bitmap) {
        if (bitmap == null) {
            mTaskFragment.newNote(lastModified, imgPath, "");
        } else {
            mTaskFragment.recognizeText(lastModified, imgPath, bitmap);
        }
    }

    // Callback from TaskFragment after recognizing text from the image (note creation, step 2 of 3).
    public void onTextRecognized(long lastModified, final String imgPath, final String text) {
        String textReady = text;
        if (textReady == null) {
            Toast.makeText(this, R.string.textRecognitionFailed, Toast.LENGTH_SHORT).show();
            textReady = "";
        }
        mTaskFragment.newNote(lastModified, imgPath, textReady);
    }

    // Callback from TaskFragment after creating a note (note creation, step 3 of 3).
    public void onPostExecuteNewNote() {
        updateList();
    }

    // Callback from TaskFragment after updating a note's text.
    public void onPostExecuteUpdateText(boolean failed) {
        if (failed) {
            Toast.makeText(this,
                    getString(R.string.noteSaveFail),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,
                    getString(R.string.noteSaveSuccess),
                    Toast.LENGTH_SHORT).show();
            updateList();
        }
    }

    // Update the list of notes.
    private void updateList() {
        FragmentManager fm = getSupportFragmentManager();
        ListFragment fragmentList = (ListFragment) fm.findFragmentByTag(TAG_LIST);
        if (fragmentList != null) {
            fragmentList.updateList();
        }
    }

    public void onChooseNote(final long id, final String imgPath) {
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
                .replace(R.id.hostActivityContainer2, InfoFragment.newInstance(id, imgPath), TAG_INFO)
                .addToBackStack(null)
                .commit();

        noteId = id;
        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        int backStackSize = fm.getBackStackEntryCount();

        if (backStackSize == 1) {
            // We see the list only.
            finish();
        } else if (backStackSize == 2) {
            // A note is opened.
            super.onBackPressed();
            fab.setVisibility(View.VISIBLE);
            noteId = null;
            invalidateOptionsMenu();
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

    private void updateFloatingButtonState() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment_info = fm.findFragmentByTag(TAG_INFO);
        if (fragment_info != null) {
            fab.setVisibility(View.GONE);
        } else {
            fab.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_app, menu);
        MenuItem saveNoteButton = menu.findItem(R.id.saveNoteButton);
        if (saveNoteButton != null) {
            saveNoteButton.setVisible(noteId != null);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveNoteButton:
                onSaveNoteButtonPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onSaveNoteButtonPressed() {
        EditText editText = findViewById(R.id.textInfo);
        if ((noteId != null) && (editText != null)) {
            String newText = editText.getText().toString();
            mTaskFragment.updateText(noteId, newText);
        } else {
            onPostExecuteUpdateText(true);
        }
    }

}
