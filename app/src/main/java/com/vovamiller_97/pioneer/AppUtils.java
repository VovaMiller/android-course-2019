package com.vovamiller_97.pioneer;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AppUtils {

    public static final String SUFFIX_COMPRESSED_COPY = "-comp";

    public static void copyExifData(final String fromPath, final String toPath) {
        try {
            ExifInterface fromExif = new ExifInterface(fromPath);
            String exifOrientation = fromExif.getAttribute(ExifInterface.TAG_ORIENTATION);

            if (exifOrientation != null) {
                ExifInterface toExif = new ExifInterface(toPath);
                toExif.setAttribute(ExifInterface.TAG_ORIENTATION, exifOrientation);
                toExif.saveAttributes();
            }
        } catch (IOException e) {
            Log.e("AppUtils", "copyExifData failed!");
            e.printStackTrace();
        }
    }

    public static void saveCompressedCopy(@NonNull final File file,
                                    @NonNull final Resources resources,
                                    @NonNull final File filesDir) {
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
        float wComp = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, wCompDPI, resources.getDisplayMetrics());
        float hComp = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, hCompDPI, resources.getDisplayMetrics());
        Bitmap bitmapCompressed = Bitmap.createScaledBitmap(
                bitmapOriginal, Math.round(wComp), Math.round(hComp), true);
        File fileCompressed = new File(filesDir, file.getName() + SUFFIX_COMPRESSED_COPY);
        try (FileOutputStream out = new FileOutputStream(fileCompressed.getAbsolutePath())) {
            bitmapCompressed.compress(Bitmap.CompressFormat.JPEG, 80, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        AppUtils.copyExifData(file.getAbsolutePath(), fileCompressed.getAbsolutePath());
    }

}
