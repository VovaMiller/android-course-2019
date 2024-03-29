package com.vovamiller_97.pioneer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageCapture;
import androidx.camera.view.CameraView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.UUID;

public class CameraActivity extends AppCompatActivity implements ImageCapture.OnImageSavedListener {

    private static final int PERMISSION_REQUEST_CODE = 0;
    public static final int RESULT_REQUEST_CODE = 0;
    public static final String RESULT_KEY_DATE = "date";
    public static final String RESULT_KEY_PATH = "path";

    private CameraView cameraView;
    private View takePictureButton;
    private boolean shotTaken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        shotTaken = false;

        // Camera view.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            requestPermission();
        }

        // "Take shot" button.
        takePictureButton = findViewById(R.id.takePictureButton);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this,
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
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    Toast.makeText(this, R.string.need_permission, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.permission_in_settings, Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void startCamera() {
        cameraView = findViewById(R.id.cameraView);
        cameraView.setCaptureMode(CameraView.CaptureMode.IMAGE);
        cameraView.bindToLifecycle(this);

        takePictureButton = findViewById(R.id.takePictureButton);
        takePictureButton.setOnClickListener(view -> pressButton());
    }

    private void pressButton() {
        if (!shotTaken) {
            shotTaken = true;
            cameraView.takePicture(
                    generatePictureFile(),
                    AsyncTask.SERIAL_EXECUTOR,
                    CameraActivity.this
            );
            takePictureButton.animate()
                    .scaleX(1.5f)
                    .scaleY(1.5f)
                    .alpha(0.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            takePictureButton.setVisibility(View.GONE);
                        }
                    });
        }
    }

    @Override
    public void onImageSaved(@NonNull final File file) {
        // Save compressed copy of an image for thumbnails (preview).
        // This lets Picasso load thumbnails significantly faster.
        new CreateCompressedCopy(file, getResources(), getFilesDir()).execute();

        // Send info back to HostActivity and finish.
        Intent output = new Intent();
        output.putExtra(RESULT_KEY_DATE, file.lastModified());
        output.putExtra(RESULT_KEY_PATH, file.getAbsolutePath());
        setResult(RESULT_OK, output);
        finish();
    }

    // Compressed image copy creation (+rotation).
    private static class CreateCompressedCopy extends AsyncTask<Void, Void, Void> {
        private final File file;
        private final Resources resources;
        private final File filesDir;

        public CreateCompressedCopy(@NonNull File file,
                                    @NonNull final Resources resources,
                                    @NonNull final File filesDir) {
            this.file = file;
            this.resources = resources;
            this.filesDir = filesDir;
        }

        @Override
        protected Void doInBackground(final Void... voids) {
            AppUtils.saveCompressedCopy(file, resources, filesDir);
            return null;
        }
    }

    @Override
    public void onError(
            @NonNull final ImageCapture.ImageCaptureError imageCaptureError,
            @NonNull final String message,
            @Nullable final Throwable cause) {
        finish();
    }

    private File generatePictureFile() {
        return new File(getFilesDir(), UUID.randomUUID().toString());
    }

}
