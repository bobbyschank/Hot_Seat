package com.example.bobby.hotseat;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity {

    private static final String TAG = CameraActivity.class.getSimpleName();

    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int TAKE_VIDEO_REQUEST = 1;

    public static final int MEDIA_TYPE_IMAGE = 2;
    public static final int MEDIA_TYPE_VIDEO = 3;

    int mDurationLimit = 10;
    int mVideoQuality = 0; // 0 = low, 1 = high

    protected Uri mMediaUri;
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Intent intent = getIntent();
        i = intent.getIntExtra(Strings.KEY_MEDIA, -1);
    }

    @Override
    protected void onResume() {
        super.onResume();

        switch (i) {
            case 0:
                launchPhoto();

            case 1:
                launchVideo();

            case -1:
                Log.d(TAG, "error reading media type.");
        }

    }

    public void launchPhoto() {

        Intent launchPhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        if (mMediaUri == null) {
            Toast.makeText(CameraActivity.this, R.string.error_external_storage, Toast.LENGTH_SHORT).show();
        } else {
            launchPhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
            startActivityForResult(launchPhotoIntent, TAKE_PHOTO_REQUEST);
        }

    }




    public void launchVideo() {

        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
        videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, mDurationLimit);
        videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, mVideoQuality);
        startActivityForResult(videoIntent, TAKE_VIDEO_REQUEST);

        mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
        if (mMediaUri == null) {
            Toast.makeText(CameraActivity.this, R.string.error_external_storage, Toast.LENGTH_SHORT).show();
        } else {
            videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
            startActivityForResult(videoIntent, TAKE_VIDEO_REQUEST);
        }

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // Add to gallery

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(mMediaUri);
            sendBroadcast(mediaScanIntent);

            // Navigate to recipients Activity
            Intent recipientsIntent = new Intent(this, RecipientsActivity.class);
            recipientsIntent.setData(mMediaUri);
            startActivity(recipientsIntent);

        } else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(this, R.string.general_error, Toast.LENGTH_LONG).show();
        }

    }

    private Uri getOutputMediaFileUri(int mediaType) {
        if (isExternalStorageAvailable()) {
            // Get the external storage directory
            String appName = CameraActivity.this.getString(R.string.app_name);
            File mediaStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    appName);

            // Create our subdirectory
            if (! mediaStorageDir.exists()) {
                if (mediaStorageDir.mkdirs()) {
                    Log.e(TAG, "FAILED TO CREATE DIRECTORY.");
                    return null;
                }
            }

            // Create a file name

            // Create the file
            File mediaFile;
            Date now = new Date();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);

            String path = mediaStorageDir.getPath() + File.separator;
            if (mediaType == MEDIA_TYPE_IMAGE) {
                mediaFile = new File(path + "IMG_" + timeStamp + ".jpg");
            }
            else if (mediaType == MEDIA_TYPE_VIDEO) {
                mediaFile = new File(path + "VID_" + timeStamp + ".mp4");
            }
            else {return null;}

            // Return the file's URI

            Log.d(TAG, "FILE::::" + Uri.fromFile(mediaFile));
            return Uri.fromFile(mediaFile);

        }
        else {
            return null;
        }
    }

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }
}
