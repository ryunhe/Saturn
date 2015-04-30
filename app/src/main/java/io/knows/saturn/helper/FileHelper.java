package io.knows.saturn.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ryun on 15-4-30.
 */
public class FileHelper {
    public static Bitmap getBitmapFromUri(Context context, Uri uri) throws FileNotFoundException {
        InputStream input = context.getContentResolver().openInputStream(uri);
        return BitmapFactory.decodeStream(input);
    }

    public static Bitmap getBitmapFromUriWithSize(Context context, Uri uri, int size) throws FileNotFoundException {
        Bitmap bitmap = getBitmapFromUri(context, uri);
        float width = (float) bitmap.getWidth();
        float height = (float) bitmap.getHeight();
        if (width > height) {
            return ThumbnailUtils.extractThumbnail(bitmap, size, (int) (size / width * height));
        } else {
            return ThumbnailUtils.extractThumbnail(bitmap, (int) (size / height * width), size);
        }
    }

    public static void dumpBitmapToFile(Bitmap bitmap, File file) {
        FileOutputStream output;
        try {
            output = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output);
            output.flush();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File createTmpFile(Context context) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        String fileName = "saturn_tmp_" + timeStamp;
        String state = Environment.getExternalStorageState();
        File dir;
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        } else {
            dir = context.getCacheDir();
        }
        return new File(dir, fileName);
    }


}
