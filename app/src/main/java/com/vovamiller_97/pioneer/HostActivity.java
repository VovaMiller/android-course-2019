package com.vovamiller_97.pioneer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;

public class HostActivity extends AppCompatActivity implements EventListener {

    private static final String NOTE_ID_KEY = "NOTE_ID_KEY";
    private static final String TAG_LIST = "TAG_LIST";
    private static final String TAG_INFO = "TAG_INFO";

    private String noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        if (savedInstanceState == null) {
            noteId = null;
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.hostActivityContainer, ListFragment.newInstance(), TAG_LIST)
                    .addToBackStack(null)
                    .commit();
        } else {
            noteId = savedInstanceState.getString(NOTE_ID_KEY, null);
            boolean isPhone = getResources().getBoolean(R.bool.is_phone);
            if (!isPhone && (noteId != null)) {
                laptopOrientationChanged();
            }
        }

        updateTitle();
    }

    public void onChooseNote(final String id) {
        FragmentManager fm = getSupportFragmentManager();
        int backStackSize = fm.getBackStackEntryCount();

        if ((backStackSize == 1) || (backStackSize == 2)) {
            if (backStackSize == 2) {
                fm.popBackStack();
            }
            boolean isLandscape = getResources().getBoolean(R.bool.is_landscape);
            boolean isPhone = getResources().getBoolean(R.bool.is_phone);
            int containerId;
            if (isLandscape && !isPhone) {
                containerId = R.id.hostActivityContainer2;
            } else {
                containerId = R.id.hostActivityContainer;
            }
            fm.beginTransaction()
                    .setCustomAnimations(
                            R.anim.enter_from_right,
                            R.anim.exit_to_left,
                            R.anim.enter_from_left,
                            R.anim.exit_to_right)
                    .replace(containerId, InfoFragment.newInstance(id), TAG_INFO)
                    .addToBackStack(null)
                    .commit();

        } else {
            Log.w("FragmentManager", "backStackSize == " + backStackSize);
            Log.w("FragmentManager", "onChooseNote: backStackSize isn't in {1, 2}");
        }

        noteId = id;
        updateTitle();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        int backStackSize = fm.getBackStackEntryCount();

        if (backStackSize == 1) {
            finish();
        } else if (backStackSize == 2) {
            super.onBackPressed();
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

    private void laptopOrientationChanged() {
        FragmentManager fm = getSupportFragmentManager();
        boolean isLandscape = getResources().getBoolean(R.bool.is_landscape);
        Fragment fragmentInfo = fm.findFragmentByTag(TAG_INFO);
        if (fragmentInfo == null) {
            fragmentInfo = InfoFragment.newInstance(noteId);
            Log.d("FragmentManager", "Old InfoFragment wasn't found!");
        } else {
            Fragment.SavedState savedState = fm.saveFragmentInstanceState(fragmentInfo);
            fragmentInfo = InfoFragment.newInstance(noteId);
            fragmentInfo.setInitialSavedState(savedState);
        }
        fm.popBackStack();
        int container_id;
        if (isLandscape) {
            container_id = R.id.hostActivityContainer2;
        } else {
            container_id = R.id.hostActivityContainer;
        }
        fm.beginTransaction()
                .setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.enter_from_left,
                        R.anim.exit_to_right)
                .replace(container_id, fragmentInfo, TAG_INFO)
                .addToBackStack(null)
                .commit();
    }
}
