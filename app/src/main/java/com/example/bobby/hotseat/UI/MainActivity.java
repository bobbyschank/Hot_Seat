package com.example.bobby.hotseat.UI;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bobby.hotseat.Adapters.SectionsPagerAdapter;
import com.example.bobby.hotseat.Data.HSUser;
import com.example.bobby.hotseat.Data.Strings;
import com.example.bobby.hotseat.R;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView mDisplayName;

    public static File trialFile;

    public static RecyclerView mInboxRecyclerView;

    FirebaseAuth mAuth;
    Firebase mRef;
    private DatabaseReference mDatabase;

    private String userID;

    public static HSUser currentUser;

    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int TAKE_VIDEO_REQUEST = 1;

    public static final int MEDIA_TYPE_IMAGE = 2;
    public static final int MEDIA_TYPE_VIDEO = 3;

    int mDurationLimit = 10;
    int mVideoQuality = 0; // 0 = low, 1 = high

    protected Uri mMediaUri;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */

    private ViewPager mViewPager;


    FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDisplayName = (TextView) findViewById(R.id.displayNameView);

        //mInboxRecyclerView = (RecyclerView) findViewById(R.id.inboxRecycler);
        //mInboxRecyclerView.setHasFixedSize(true);
        //mInboxRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();

        Log.d(TAG, "We are in Main Activity, On Create");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    // User is signed in
                    userID = firebaseUser.getUid();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + firebaseUser.getUid());

                    String displayName = firebaseUser.getDisplayName();
                    String email = firebaseUser.getEmail();
                    //setDisplayName(firebaseUser);

                    currentUser = new HSUser(userID, displayName, email);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    navigateToLogin();
                }
                // ...
            }
        };

        Log.d(TAG, "Action");

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }


    private void setDisplayName(FirebaseUser firebaseUser) {
        mDisplayName.setText("THIS USER");
    }

    private void navigateToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void navigateToMessage(View v) {
        Intent intent = new Intent(MainActivity.this, MessageActivity.class);
        startActivity(intent);
    }

    public void startPhoto(View v) {
        launchPhoto();
    }

    public void startVideo(View v) {
        launchVideo();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mRef = new Firebase("https://hot-seat-28ddb.firebaseio.com");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth.addAuthStateListener(mAuthListener);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded\n\n");
                HSUser hSUser = dataSnapshot.getValue(HSUser.class);
                Log.d(TAG, "onChildAdded:" + hSUser);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Cancelled.");
            }

        };
        mDatabase.child(Strings.KEY_USERS).addChildEventListener(childEventListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_log_out:{
                FirebaseAuth.getInstance().signOut();
                return true;}

            case R.id.action_add_friends:{
                Intent intent = new Intent(this, AddFriendActivity.class);
                intent.putExtra(Strings.KEY_USERID, userID);
                startActivity(intent);
                return true;}

            case R.id.action_recycler:{
                Intent intent = new Intent(this, Recycler.class);
                startActivity(intent);
                return true;}

            case R.id.action_settings:
                return true;
            default: {};
        }

        return super.onOptionsItemSelected(item);
    }

    public void launchPhoto() {

        Intent launchPhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        if (mMediaUri == null) {
            Toast.makeText(MainActivity.this, R.string.error_external_storage, Toast.LENGTH_SHORT).show();
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

        mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);

        if (mMediaUri == null) {
            Toast.makeText(MainActivity.this, R.string.error_external_storage, Toast.LENGTH_SHORT).show();
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
            String appName = MainActivity.this.getString(R.string.app_name);
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

            Log.d(TAG, "FILE::" + Uri.fromFile(mediaFile));

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