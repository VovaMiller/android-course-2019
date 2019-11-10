package com.vovamiller_97.pioneer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageCapture;
import androidx.camera.view.CameraView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class CameraActivity extends AppCompatActivity implements ImageCapture.OnImageSavedListener {

    private static final int PERMISSION_REQUEST_CODE = 0;
    public static final String SUFFIX_COMPRESSED = "-comp";
    public static final int RESULT_REQUEST_CODE = 0;
    public static final String RESULT_KEY_DATE = "date";
    public static final String RESULT_KEY_PATH = "path";

    private CameraView cameraView;
    private View takePictureButton;
    private OrientationDetector mOrientationDetector;
    private int angle;
    private int shotAngle;
    private boolean shotTaken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        shotTaken = false;
        shotAngle = 0;

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
                pressButton();
            }
        });
    }

    private void pressButton() {
        if (!shotTaken) {
            shotAngle = angle;
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

    private File saveCompressedCopy(@NonNull final File file) {
        Bitmap bitmapOriginal = BitmapFactory.decodeFile(file.getAbsolutePath());
        float wOg = bitmapOriginal.getWidth();
        float hOg = bitmapOriginal.getHeight();
        float wCompDPI, hCompDPI;
        if (wOg >= hOg) {
            wCompDPI = 96f * (wOg / hOg);
            hCompDPI= 96f;
        } else {
            wCompDPI= 96f ;
            hCompDPI = 96f * (hOg / wOg);
        }
        Resources r = getResources();
        float wComp = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, wCompDPI, r.getDisplayMetrics());
        float hComp = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, hCompDPI, r.getDisplayMetrics());
        Bitmap bitmapCompressed = Bitmap.createScaledBitmap(
                bitmapOriginal, Math.round(wComp), Math.round(hComp), true);
        File fileCompressed = new File(getFilesDir(), file.getName() + SUFFIX_COMPRESSED);
        try (FileOutputStream out = new FileOutputStream(fileCompressed.getAbsolutePath())) {
            bitmapCompressed.compress(Bitmap.CompressFormat.JPEG, 80, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileCompressed;
    }

    @Override
    public void onImageSaved(@NonNull final File file) {
        // Save compressed copy of an image for thumbnails.
        File compressedFile = saveCompressedCopy(file);

        // Rotate the image by modifying metadata.
        setExifRotation(file, shotAngle);
        setExifRotation(compressedFile, shotAngle);

        // Send info back to HostActivity and finish.
        Intent output = new Intent();
        output.putExtra(RESULT_KEY_DATE, file.lastModified());
        output.putExtra(RESULT_KEY_PATH, file.getAbsolutePath());
        setResult(RESULT_OK, output);
        finish();
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
