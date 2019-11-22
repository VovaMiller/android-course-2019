package com.vovamiller_97.pioneer;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


public class DeleteDialogFragment extends DialogFragment {

    private OnInteractionListener mListener;
    private long noteId;

    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.dialog_delete_title)
                .setMessage(R.string.dialog_delete_text)
                .setPositiveButton(
                        R.string.dialog_delete_ok,
                        (dialog, id) -> mListener.onDeleteDialogConfirmed(noteId))
                .setNegativeButton(
                        R.string.dialog_delete_cancel,
                        null);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnInteractionListener) {
            mListener = (OnInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement DeleteDialogFragment.OnInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnInteractionListener {
        void onDeleteDialogConfirmed(final long id);
    }

}
