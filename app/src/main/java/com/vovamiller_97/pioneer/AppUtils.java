package com.vovamiller_97.pioneer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import androidx.exifinterface.media.ExifInterface;

import java.io.IOException;

public class AppUtils {

    // Gets bitmap by a file path considering rotations in EXIF metadata.
    public static Bitmap getBitmap(String path) {
        try {
            int rotate = 0;
            ExifInterface exif;
            exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);
            return bitmap;
        } catch (IOException e) {
            Log.e("AppUtils", "IOException");
            e.printStackTrace();
            return null;
        }
    }

}
