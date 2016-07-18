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

public class VideoActivity extends AppCompatActivity {

    private static final String TAG = VideoActivity.class.getSimpleName();

    protected Uri mMediaUri;

/*

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        mMediaUri = getOutputMediaFileUri(MainActivity.MEDIA_TYPE_VIDEO);
        if (mMediaUri == null) {
            Toast.makeText(VideoActivity.this, R.string.error_external_storage, Toast.LENGTH_SHORT).show();
        } else {
            takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
            takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, mDurationLimit);
            takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, mVideoQuality);
            startActivityForResult(takeVideoIntent, MainActivity.TAKE_VIDEO_REQUEST);
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

        } else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(this, R.string.general_error, Toast.LENGTH_LONG).show();
        }

    }

    private Uri getOutputMediaFileUri(int mediaType) {
        if (isExternalStorageAvailable()) {
            // Get the external storage directory
            String appName = VideoActivity.this.getString(R.string.app_name);
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
            if (mediaType == MainActivity.MEDIA_TYPE_IMAGE) {
                mediaFile = new File(path + "IMG_" + timeStamp + ".jpg");
            }
            else if (mediaType == MainActivity.MEDIA_TYPE_VIDEO) {
                mediaFile = new File(path + "VID_" + timeStamp + ".mp4");
            }
            else {return null;}

            // Return the file's URI

            Log.d(TAG, "VIDEO FILE::::" + Uri.fromFile(mediaFile));
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
    */
}

