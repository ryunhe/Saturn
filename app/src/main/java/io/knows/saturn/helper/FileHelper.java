package io.knows.saturn.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
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
    public static Bitmap getSampleBitmapWithSize(Context context, Uri uri, int reqWidth, int reqHeight) throws IOException {
        String path = uri.getPath();

        BitmapFactory.Options outDimens = new BitmapFactory.Options();
        outDimens.inJustDecodeBounds = true; // the decoder will return null (no bitmap)

        BitmapFactory.decodeFile(path, outDimens);

        BitmapFactory.Options outBitmap = new BitmapFactory.Options();
        outBitmap.inJustDecodeBounds = false; // the decoder will return a bitmap
        outBitmap.inSampleSize = calculateInSampleSize(outDimens, reqWidth, reqHeight);

        Bitmap bitmap = BitmapFactory.decodeFile(path, outBitmap);

        return bitmap;
    }

    public static Bitmap getBitmapWithSize(Context context, Uri uri, int reqWidth, int reqHeight) throws IOException {
        Bitmap bitmap = getSampleBitmapWithSize(context, uri, reqWidth, reqHeight);
        return ThumbnailUtils.extractThumbnail(bitmap, reqWidth, reqHeight);
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
        String fileName = "saturn_tmp_" + timeStamp + ".jpg";
        String state = Environment.getExternalStorageState();
        File dir;
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        } else {
            dir = context.getCacheDir();
        }
        return new File(dir, fileName);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
