package com.vovamiller_97.pioneer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageCapture;
import androidx.camera.view.CameraView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;

import android.content.pm.PackageManager;
import android.Manifest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.vovamiller_97.pioneer.db.Note;
import com.vovamiller_97.pioneer.db.NoteGenerator;
import com.vovamiller_97.pioneer.db.NoteRepository;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

public class CameraActivity extends AppCompatActivity implements ImageCapture.OnImageSavedListener {

    private static final int PERMISSION_REQUEST_CODE = 0;

    // TODO: remove later
    private TextView textView;

    private CameraView cameraView;
    private View takePictureButton;
    private OrientationDetector mOrientationDetector;
    private int angle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        textView = findViewById(R.id.textViewTest);

        // Camera view.
        if (savedInstanceState == null) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                requestPermission();
            }
        }

        // "Take shot" button.
        takePictureButton = findViewById(R.id.takePictureButton);
        mOrientationDetector = new OrientationDetector(this) {
            @Override
            public int fromSensor(int sensorAngle) {
                return 360 - sensorAngle;
            }

            @Override
            public void onOrientationChanged(int rightAngle) {
                updateOrientation(rightAngle);
            }
        };
        angle = 0;
        mOrientationDetector.enable();
    }

    @Override
    protected void onDestroy() {
        mOrientationDetector.disable();
        super.onDestroy();
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
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                cameraView.takePicture(
                        generatePictureFile(),
                        AsyncTask.SERIAL_EXECUTOR,
                        CameraActivity.this
                );
            }
        });
    }

    private void setExifRotation(@NonNull final File file, int angle) {
        int nAngle = ((angle % 360) + 360) % 360;
        int orientation = ExifInterface.ORIENTATION_UNDEFINED;
        switch (nAngle) {
            case 0:
                orientation = ExifInterface.ORIENTATION_ROTATE_90;
                break;
            case 90:
                orientation = ExifInterface.ORIENTATION_NORMAL;
                break;
            case 180:
                orientation = ExifInterface.ORIENTATION_ROTATE_270;
                break;
            case 270:
                orientation = ExifInterface.ORIENTATION_ROTATE_180;
                break;
        }
        try {
            ExifInterface exifInterface = new ExifInterface(file.getAbsolutePath());
            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(orientation));
            exifInterface.saveAttributes();
        } catch (IOException e) {
            Log.e("CameraActivity", "IOException");
            e.printStackTrace();
        }
    }

    @Override
    public void onImageSaved(@NonNull final File file) {
        // Rotate the image by modifying metadata.
        setExifRotation(file, angle);

        // TODO: remove later
        String message = "New Photo: \"" + file.getName() + "\"";
        Log.d("CAMCAM", message);
        
        // Adding a new note.
        // TODO: for some reason the app crashes on external device after rotation with >=4 notes
//        Date date = new Date(file.lastModified());
//        Note note = NoteGenerator.random(this, date, file.getAbsolutePath());
//        NoteRepository nr = new NoteRepository(App.getDatabaseHolder());
//        nr.create(note);


//        Handler mainHandler = new Handler(Looper.getMainLooper());
//        Runnable myRunnable = new Runnable() {
//            @Override
//            public void run() {
//                // Updating the list of notes.
//                // TODO: update recyclerView content?
//                FragmentManager fm = getSupportFragmentManager();
//                // TODO: avoid cast
//                ListFragment fragment = (ListFragment) fm.findFragmentByTag(TAG_LIST);
//                fragment.updateList();
//            }
//        };
//        mainHandler.post(myRunnable);
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

    private void updateOrientation(int angle) {
        RotateAnimation rotate = new RotateAnimation(
                this.angle, angle,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setFillAfter(true);
        takePictureButton.startAnimation(rotate);

        this.angle = angle;
    }

}
