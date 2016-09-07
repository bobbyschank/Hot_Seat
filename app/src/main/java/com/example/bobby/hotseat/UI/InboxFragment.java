package com.example.bobby.hotseat.UI;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bobby.hotseat.Data.Sponse;
import com.example.bobby.hotseat.Data.Strings;
import com.example.bobby.hotseat.NpaLinearLayoutManager;
import com.example.bobby.hotseat.R;
import com.example.bobby.hotseat.VideoPlayer;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.ui.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Comment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by bobby on 6/6/16.
 */

public class InboxFragment extends Fragment {


    private static final String TAG = InboxFragment.class.getSimpleName();

    private RecyclerView mInboxRecyclerView;
    TextView emptyTextView;

    private static final String MEDIA = "media";

    public static final int MEDIA_TYPE_IMAGE = 2;
    public static final int MEDIA_TYPE_VIDEO = 3;

    public static final int REQUEST_AUTORECORD = 4;

    private static final int LOCAL_VIDEO = 6;

    public boolean isLoaded = false;

    protected String mCurrentUser;

    protected static String sponseAuthor;

    Firebase mRef = new Firebase("https://hot-seat-28ddb.firebaseio.com/users/"
            + MainActivity.currentUser.getIdToken()
            + "/sponses");// TODO Don't use public static currentUser

    private DatabaseReference mDatabase;

    static FirebaseStorage storage = FirebaseStorage.getInstance();


    private SurfaceView mPreview;
    private SurfaceHolder holder;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Firebase.setAndroidContext(getActivity());
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * tied to {@link //Activity#onResume() Activity.onResume} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();


        final FirebaseRecyclerAdapter<Sponse, InboxViewHolder> adapter =
                new FirebaseRecyclerAdapter<Sponse, InboxViewHolder>(
                        Sponse.class,
                        R.layout.inbox_item,
                        //android.R.layout.two_line_list_item,
                        InboxViewHolder.class,
                        mRef)
                {

                    @Override
                    protected void populateViewHolder(InboxViewHolder inboxViewHolder, final Sponse sponse, int i) {

                        emptyTextView.setVisibility(View.INVISIBLE);

                        Log.d(TAG, "In Populate View Holder.");
                        ((TextView) InboxViewHolder.mAuthorView).setText((CharSequence) sponse.getDisplayName());

                        Log.d(TAG, "TIMESTAMP:            " + sponse.getTimeStamp() + i);
                        Log.d(TAG, "STATUS:            " + sponse.getStatus());

                        ((TextView) InboxViewHolder.mTimeView).setText((CharSequence) sponse.getTimeStamp());

                        final String key = this.getRef(i).getKey();

                        inboxViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d(TAG, "CLICK");

                                String uri = sponse.getUri();

                                Log.d(TAG, "UNIQUE KEY:          " + key);
                                File file = createFile(key, MEDIA_TYPE_VIDEO);

                                switch (sponse.getStatus()) {
                                    case 0: {
                                        Log.d(TAG, "In CASE 0");
                                        mRef.child(key).child("status").setValue(1);
                                        //arrangeDisplay(sponse);
                                        try {
                                            sponse.loadSponse(file, key, Strings.KEY_SPONSES);
                                            // loadSponse(file, uri, key, sponse);
                                            Log.d(TAG, "IN TRY");
                                            // mRef.child(key).child("status").setValue(2);

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            Log.d(TAG, "IN CATCH");
                                        }
                                        Log.d(TAG, "In CASE 0 NUMBER 2");
                                        return;
                                    }
                                    case 1: {
                                        Log.d(TAG, "In CASE 1.");
                                        return;
                                    }
                                    case 2: {
                                        Log.d(TAG, "In CASE 2.");
                                        try {
                                            if (file.exists()) {
                                                sponseAuthor = sponse.getIdToken();
                                                goVid(file, Uri.parse(uri));

                                                sponse.getIdToken();

                                                Log.d(TAG, "END GO VID TRY BLOCK");
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            Log.d(TAG, "Error playing video.");
                                        }
                                        return;
                                    }

                                }
                                //arrangeDisplay(sponse);
                            }
                        });
                        arrangeDisplay(sponse);
                        Log.d(TAG, "END OF POPULATE VIEWHOLDER");

                    }
                };
        Log.d(TAG, "Adapter created");
        mInboxRecyclerView.setAdapter(adapter);
        Log.d(TAG, "Adapter set");

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        mInboxRecyclerView = (RecyclerView) rootView.findViewById(R.id.inboxRecycler);
        RecyclerView.LayoutManager layoutManager = new NpaLinearLayoutManager(getActivity());
        boolean predictiveItemAnimations = layoutManager.supportsPredictiveItemAnimations();
        mInboxRecyclerView.setLayoutManager(layoutManager);
        //mInboxRecyclerView.setHasFixedSize(true);

        emptyTextView = (TextView) rootView.findViewById(R.id.emptyInbox);


        return rootView;
    }

    public static class InboxViewHolder
            extends RecyclerView.ViewHolder {
        static TextView mAuthorView;
        static TextView mTimeView;
        public static ProgressBar mInboxProgress;
        public static TextView mLoadIndicator;

        View mView;

        public InboxViewHolder(View v) {
            super(v);
            mAuthorView = (TextView) v.findViewById(R.id.authorView);
            mTimeView = (TextView) v.findViewById(R.id.timeStampView);
            mInboxProgress = (ProgressBar) v.findViewById(R.id.inboxProgress);
            mLoadIndicator = (TextView) v.findViewById(R.id.loadIndicator);

            mView = itemView;
        }
    }

    private void loadSponse(final File file, final String uri, final String key, final Sponse sponse) throws IOException {

        StorageReference storageRef = storage.getReferenceFromUrl(uri);
        //final boolean[] b = new boolean[0];

        int i = 0;

        storageRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                mRef.child(key).child("status").setValue(2);


                // Local temp file has been created
                Log.d(TAG, "LOCAL TEMP FILE CREATED");
                //b[0] = true;
                // i = 2;
               //arrangeDisplay(sponse);

                InboxViewHolder.mInboxProgress.setVisibility(View.INVISIBLE);
                ((TextView) InboxViewHolder.mLoadIndicator).setText((CharSequence) "tap to view");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //b[0] = false;
                // Handle any errors
            }
        });
    }

    private File createFile(String key, int mediaType) {


        String appName = getActivity().getString(R.string.app_name);
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                appName);

        // Create the file
        File file;

        String path = mediaStorageDir.getPath() + File.separator;
        if (mediaType == MEDIA_TYPE_IMAGE) {
            file = new File(path + "IMG_" + key + ".jpg");
        }
        else if (mediaType == MEDIA_TYPE_VIDEO) {
            file = new File(path + "VID_" + key + ".mp4");
        }
        else {return null;}

        return file;
    }

    public void goVid(File file, Uri uri) throws IOException {

        Log.d(TAG, "START THAT VIDEO" + uri);


        Intent intent = new Intent(getActivity(),
                VideoPlayer.class);
        intent.putExtra(MEDIA, LOCAL_VIDEO);
        intent.putExtra("fileUri", file.getAbsolutePath());
        //startActivity(intent);




        //Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        //intent.setDataAndType(Uri.fromFile(file), "video/*");
        startActivityForResult(intent, REQUEST_AUTORECORD);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_AUTORECORD) {

            if (resultCode == 0) // TODO get intent resultCode to recognize video completion
            {

                Log.d(TAG, "HOPEFULLY, END OF VIDEO.");

                // Navigate to Record Video
                Intent autoRecordIntent = new Intent(getActivity(), AutoRecordActivity.class);
                startActivity(autoRecordIntent);

            } else {
                Log.d(TAG, "RESULT NOT OK.");
                Toast.makeText(getActivity(), R.string.general_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "REQUEST NOT OK.");
        }
    }

    private void arrangeDisplay(Sponse sponse) {
        switch (sponse.getStatus()) {
            case 0:{
                Log.d(TAG, "In CASE 0 DISPLAY SWITCH");
                InboxViewHolder.mInboxProgress.setVisibility(View.INVISIBLE);
                ((TextView) InboxViewHolder.mLoadIndicator).setText((CharSequence) "tap to load");
                return;
            }
            case 1:{
                Log.d(TAG, "In CASE 1 DISPLAY SWITCH.");
                InboxViewHolder.mInboxProgress.setVisibility(View.VISIBLE);
                ((TextView) InboxViewHolder.mLoadIndicator).setText((CharSequence) "loading");
                return;
            }
            case 2:{
                Log.d(TAG, "In CASE 2 DISPLAY SWITCH.");
                InboxViewHolder.mInboxProgress.setVisibility(View.INVISIBLE);
                ((TextView) InboxViewHolder.mLoadIndicator).setText((CharSequence) "tap to view");
                return;
            }
        }
    }
}