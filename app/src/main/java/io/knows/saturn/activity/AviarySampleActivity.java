package io.knows.saturn.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.aviary.android.feather.sdk.AviaryIntent;
import com.aviary.android.feather.sdk.AviaryVersion;
import com.aviary.android.feather.sdk.internal.Constants;
import com.aviary.android.feather.sdk.internal.headless.utils.MegaPixels;
import com.aviary.android.feather.sdk.internal.utils.DecodeUtils;
import com.aviary.android.feather.sdk.internal.utils.ImageInfo;
import com.aviary.android.feather.sdk.utils.AviaryIntentConfigurationValidator;

import java.io.File;

import io.knows.saturn.R;

public class AviarySampleActivity extends Activity {
    public static final String LOG_TAG = AviarySampleActivity.class.getName();
    private static final int ACTION_REQUEST_GALLERY = 99;
    private static final int ACTION_REQUEST_FEATHER = 100;
    private static final int EXTERNAL_STORAGE_UNAVAILABLE = 1;
    /** Folder name on the sdcard where the images will be saved * */
    private static final String FOLDER_NAME = "aviary-sample";
    Button mGalleryButton;
    Button mEditButton;
    ImageView mImage;
    View mImageContainer;
    String mOutputFilePath;
    Uri mImageUri;
    int imageWidth, imageHeight;
    File mGalleryFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_post);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imageWidth = (int) ((float) metrics.widthPixels / 1.5);
        imageHeight = (int) ((float) metrics.heightPixels / 1.5);

        mGalleryButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pickFromGallery();
                    }
                });

        mEditButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mImageUri != null) {
                            startFeather(mImageUri);
                        }
                    }
                });

        mImageContainer.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = pickRandomImage();
                        if (uri != null) {
                            Log.d(LOG_TAG, "image uri: " + uri);
                            loadAsync(uri);
                        }
                    }
                });

        mImageContainer.setLongClickable(false);

        mGalleryFolder = createFolders();
        registerForContextMenu(mImageContainer);

        // pre-load the cds service
        Intent cdsIntent = AviaryIntent.createCdsInitIntent(getBaseContext());
        startService(cdsIntent);

        printConfiguration();

        // verify the CreativeSDKImage configuration
        try {
            AviaryIntentConfigurationValidator.validateConfiguration(this);
        } catch (Throwable e) {
            new AlertDialog.Builder(this).setTitle("Error")
                    .setMessage(e.getMessage()).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getIntent() != null) {
            handleIntent(getIntent());
            setIntent(new Intent());
        }
    }

    /**
     * Handle the incoming {@link Intent}
     */
    private void handleIntent(Intent intent) {
        final String action = intent.getAction();

        if (null != action) {
            if (Intent.ACTION_SEND.equals(action)) {
                Bundle extras = intent.getExtras();
                if (extras != null && extras.containsKey(Intent.EXTRA_STREAM)) {
                    Uri uri = (Uri) extras.get(Intent.EXTRA_STREAM);
                    loadAsync(uri);
                }
            } else if (Intent.ACTION_VIEW.equals(action)) {
                Uri data = intent.getData();
                loadAsync(data);
            }
        }
    }

    /**
     * Load the incoming Image
     *
     * @param uri
     */
    private void loadAsync(final Uri uri) {
        Drawable toRecycle = mImage.getDrawable();
        if (toRecycle != null && toRecycle instanceof BitmapDrawable) {
            if (((BitmapDrawable) mImage.getDrawable()).getBitmap() != null) {
                ((BitmapDrawable) mImage.getDrawable()).getBitmap().recycle();
            }
        }
        mImage.setImageDrawable(null);
        mImageUri = null;

        DownloadAsync task = new DownloadAsync();
        task.execute(uri);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOutputFilePath = null;
    }

    /**
     * Delete a file without throwing any exception
     *
     * @param path
     * @return
     */
    private boolean deleteFileNoThrow(String path) {
        File file;
        try {
            file = new File(path);
        } catch (NullPointerException e) {
            return false;
        }

        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mGalleryButton = (Button) findViewById(R.id.button1);
        mEditButton = (Button) findViewById(R.id.button2);
        mImage = ((ImageView) findViewById(R.id.image));
        mImageContainer = findViewById(R.id.image_container);
    }

    @Override
    /**
     * This method is called when feather has completed ( ie. user clicked on "done" or just exit the activity without saving ).
     * <br />
     * If user clicked the "done" button you'll receive RESULT_OK as resultCode, RESULT_CANCELED otherwise.
     *
     * @param requestCode
     * 	- it is the code passed with startActivityForResult
     * @param resultCode
     * 	- result code of the activity launched ( it can be RESULT_OK or RESULT_CANCELED )
     * @param data
     * 	- the result data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ACTION_REQUEST_GALLERY:
                    // user chose an image from the gallery
                    loadAsync(data.getData());
                    break;

                case ACTION_REQUEST_FEATHER:

                    boolean changed = true;

                    if (null != data) {
                        Bundle extra = data.getExtras();
                        if (null != extra) {
                            // image was changed by the user?
                            changed = extra
                                    .getBoolean(Constants.EXTRA_OUT_BITMAP_CHANGED);
                        }
                    }

                    if (!changed) {
                        Log.w(
                                LOG_TAG,
                                "User did not modify the image, but just clicked on 'Done' button");
                    }

                    // send a notification to the media scanner
                    updateMedia(mOutputFilePath);

                    // update the preview with the result
                    loadAsync(data.getData());
                    mOutputFilePath = null;
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            switch (requestCode) {
                case ACTION_REQUEST_FEATHER:

                    // delete the result file, if exists
                    if (mOutputFilePath != null) {
                        deleteFileNoThrow(mOutputFilePath);
                        mOutputFilePath = null;
                    }
                    break;
            }
        }
    }

    /**
     * Given an Uri load the bitmap into the current ImageView and resize it to
     * fit the image container size
     *
     * @param uri
     */
    @SuppressWarnings ("deprecation")
    private boolean setImageURI(final Uri uri, final Bitmap bitmap) {
        mImage.setImageBitmap(bitmap);
        mImage.setBackgroundDrawable(null);

        mEditButton.setEnabled(true);
        mImageUri = uri;

        return true;
    }

    /**
     * We need to notify the MediaScanner when a new file is created. In this
     * way all the gallery applications will be notified too.
     *
     * @param filepath
     */
    private void updateMedia(String filepath) {
        MediaScannerConnection.scanFile(
                getApplicationContext(),
                new String[]{filepath}, null, null);
    }

    /**
     * Pick a random image from the user gallery
     *
     * @return
     */
    @SuppressWarnings ("unused")
    private Uri pickRandomImage() {
        Cursor c = getContentResolver().query(
                Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{ImageColumns._ID, ImageColumns.DATA},
                ImageColumns.SIZE + ">?", new String[]{"90000"}, null);
        Uri uri = null;

        if (c != null) {
            int total = c.getCount();
            int position = (int) (Math.random() * total);
            if (total > 0) {
                if (c.moveToPosition(position)) {
                    String data = c.getString(
                            c.getColumnIndex(Images.ImageColumns.DATA));
                    long id = c.getLong(
                            c.getColumnIndex(Images.ImageColumns._ID));
                    uri = Uri.parse(data);
                }
            }
            c.close();
        }
        return uri;
    }

    /**
     * Return a new image file. Name is based on the current time. Parent folder
     * will be the one created with createFolders
     *
     * @return
     * @see #createFolders()
     */
    private File getNextFileName() {
        if (mGalleryFolder != null) {
            if (mGalleryFolder.exists()) {
                File file = new File(
                        mGalleryFolder, "aviary_"
                        + System.currentTimeMillis() + ".jpg");
                return file;
            }
        }
        return null;
    }

    /**
     * Once you've chosen an image you can start the feather activity
     *
     * @param uri
     */
    @SuppressWarnings ("deprecation")
    private void startFeather(Uri uri) {
        Log.d(LOG_TAG, "uri: " + uri);

        // first check the external storage availability
        if (!isExternalStorageAvailable()) {
            showDialog(EXTERNAL_STORAGE_UNAVAILABLE);
            return;
        }

        // create a temporary file where to store the resulting image
        File file = getNextFileName();

        if (null != file) {
            mOutputFilePath = file.getAbsolutePath();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(android.R.string.dialog_alert_title)
                    .setMessage("Failed to create a new File").show();
            return;
        }

        // Create the intent needed to start feather
        Intent newIntent = new AviaryIntent.Builder(this).setData(uri)
                .withOutput(Uri.parse("file://" + mOutputFilePath))
                .withOutputFormat(Bitmap.CompressFormat.JPEG)
                .withOutputSize(MegaPixels.Mp5).withNoExitConfirmation(true)
                .saveWithNoChanges(true).withPreviewSize(1024)
                .build();

        // ..and start feather
        startActivityForResult(newIntent, ACTION_REQUEST_FEATHER);

    }

    /**
     * Check the external storage status
     *
     * @return
     */
    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Start the activity to pick an image from the user gallery
     */
    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        Intent chooser = Intent.createChooser(intent, "Choose a Picture");
        startActivityForResult(chooser, ACTION_REQUEST_GALLERY);
    }

    private File createFolders() {
        File baseDir;

        if (android.os.Build.VERSION.SDK_INT < 8) {
            baseDir = Environment.getExternalStorageDirectory();
        } else {
            baseDir = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        }

        if (baseDir == null) {
            return Environment.getExternalStorageDirectory();
        }

        Log.d(LOG_TAG, "Pictures folder: " + baseDir.getAbsolutePath());
        File aviaryFolder = new File(baseDir, FOLDER_NAME);

        if (aviaryFolder.exists()) {
            return aviaryFolder;
        }
        if (aviaryFolder.mkdirs()) {
            return aviaryFolder;
        }

        return Environment.getExternalStorageDirectory();
    }

    class DownloadAsync extends AsyncTask<Uri, Void, Bitmap> implements
            OnCancelListener {
        ProgressDialog mProgress;
        private Uri mUri;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgress = new ProgressDialog(AviarySampleActivity.this);
            mProgress.setIndeterminate(true);
            mProgress.setCancelable(true);
            mProgress.setMessage("Loading image...");
            mProgress.setOnCancelListener(this);
            mProgress.show();
        }

        @Override
        protected Bitmap doInBackground(Uri... params) {
            mUri = params[0];

            Bitmap bitmap = null;

            while (mImageContainer.getWidth() < 1) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            final int w = mImageContainer.getWidth();
            Log.d(LOG_TAG, "width: " + w);
            ImageInfo info = new ImageInfo();
            bitmap = DecodeUtils.decode(
                    AviarySampleActivity.this, mUri, imageWidth,
                    imageHeight, info);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);

            if (mProgress.getWindow() != null) {
                mProgress.dismiss();
            }

            if (result != null) {
                setImageURI(mUri, result);
            } else {
                Toast.makeText(
                        AviarySampleActivity.this,
                        "Failed to load image " + mUri, Toast.LENGTH_SHORT)
                        .show();
            }
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            Log.i(LOG_TAG, "onProgressCancel");
            this.cancel(true);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.i(LOG_TAG, "onCancelled");
        }
    }

    private void printConfiguration() {
        Log.v(
                LOG_TAG, "VERSION: " + AviaryVersion.VERSION_NAME + " - "
                        + AviaryVersion.VERSION_CODE);
    }
}

