package com.vovamiller_97.pioneer;

import android.content.Context;
import android.content.pm.PackageManager;
import android.Manifest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.camera.core.ImageCapture;
import androidx.camera.view.CameraView;
import androidx.core.content.ContextCompat;

public class CameraFragment extends Fragment implements ImageCapture.OnImageSavedListener {

    private static final int PERMISSION_REQUEST_CODE = 0;

    private OnInteractionListener mListener;
    private CameraView cameraView;
    private View takePictureButton;

    public static CameraFragment newInstance() {
        return new CameraFragment();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            requestPermission();
        }
    }

    private void requestPermission() {
        requestPermissions(
                new String[] {Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(
            final int requestCode,
            @NonNull final String[] permissions,
            @NonNull final int[] grantResults
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    Toast.makeText(getActivity(), R.string.need_permission, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), R.string.permission_in_settings, Toast.LENGTH_SHORT).show();
                }
                mListener.finishCameraFragment();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void startCamera() {
        cameraView = getView().findViewById(R.id.cameraView);
        cameraView.setCaptureMode(CameraView.CaptureMode.IMAGE);
        cameraView.bindToLifecycle(this);

        takePictureButton = getView().findViewById(R.id.takePictureButton);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                cameraView.takePicture(
                        generatePictureFile(),
                        AsyncTask.SERIAL_EXECUTOR,
                        CameraFragment.this
                );
            }
        });
    }

    @Override
    public void onImageSaved(@NonNull final File file) {
        mListener.onPhotoTaken(file);
    }

    @Override
    public void onError(
            @NonNull final ImageCapture.ImageCaptureError imageCaptureError,
            @NonNull final String message,
            @Nullable final Throwable cause) {
        mListener.finishCameraFragment();
    }

    private File generatePictureFile() {
        return new File(getActivity().getFilesDir(), UUID.randomUUID().toString());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnInteractionListener) {
            mListener = (OnInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnInteractionListener {
        // TODO: Update argument type and name
        void onPhotoTaken(@NonNull final File file);
        void finishCameraFragment();
    }
}
