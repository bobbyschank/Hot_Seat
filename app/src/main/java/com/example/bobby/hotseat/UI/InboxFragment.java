package com.example.bobby.hotseat.UI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.bobby.hotseat.Data.Sponse;
import com.example.bobby.hotseat.R;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by bobby on 6/6/16.
 */

public class InboxFragment extends Fragment {


    private static final String TAG = InboxFragment.class.getSimpleName();

    private RecyclerView mInboxRecyclerView;
    TextView emptyTextView;

    public static final int MEDIA_TYPE_IMAGE = 2;
    public static final int MEDIA_TYPE_VIDEO = 3;
    public boolean isLoaded = false;

    protected String mCurrentUser;

    Firebase mRef = new Firebase("https://hot-seat-28ddb.firebaseio.com/users/"
            + MainActivity.currentUser.getIdToken()
            + "/sponses");// TODO Don't use public static currentUser

    private DatabaseReference mDatabase;

    static FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Firebase.setAndroidContext(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        mInboxRecyclerView = (RecyclerView) rootView.findViewById(R.id.inboxRecycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mInboxRecyclerView.setLayoutManager(layoutManager);
        //mInboxRecyclerView.setHasFixedSize(true);

        emptyTextView = (TextView) rootView.findViewById(R.id.emptyInbox);


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

                        arrangeDisplay(sponse);

                        Log.d(TAG, "In Populate View Holder.");
                        ((TextView) InboxViewHolder.mAuthorView).setText((CharSequence) sponse.getDisplayName());

                        Log.d(TAG, "TIMESTAMP:            " + sponse.getTimeStamp() + i);
                        Log.d(TAG, "STATUS:            " + sponse.getStatus());

                        ((TextView) InboxViewHolder.mTimeView).setText((CharSequence) sponse.getTimeStamp());

                        final String key = this.getRef(i).getKey();

                        inboxViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String uri = sponse.getUri();

                                Log.d(TAG, "UNIQUE KEY:          " + key);

                                File file = createFile(key, MEDIA_TYPE_VIDEO);

                                if (sponse.getStatus() == 1) {
                                    try {
                                        goVid(file, Uri.parse(uri));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    try {
                                        mRef.child(key).child("status").setValue(1);
                                        if (loadSponse(file, uri)){
                                            mRef.child(key).child("status").setValue(2);
                                        }



                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }



                                }



                            }
                        });
                    }
                };

        Log.d(TAG, "Adapter created");
        mInboxRecyclerView.setAdapter(adapter);
        Log.d(TAG, "Adapter set");

        Log.d(TAG, "ITEM COUNT" + adapter.getItemCount());

        return rootView;
    }

    public static class InboxViewHolder
            extends RecyclerView.ViewHolder {
        static TextView mAuthorView;
        static TextView mTimeView;
        static ProgressBar mInboxProgress;
        static TextView mLoadIndicator;

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

    private boolean loadSponse(final File file, final String uri) throws IOException {

        StorageReference storageRef = storage.getReferenceFromUrl(uri);
        final boolean[] b = new boolean[0];

        final File localFile = file;
        //Uri uri = Uri.fromFile(file);
        storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                Log.d(TAG, "LOCAL TEMP FILE CREATED");
                b[0] = true;

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                b[0] = false;
                // Handle any errors
            }
        });
        return b[0];

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

        Uri realUri = Uri.fromFile(file);
        Uri thisUri = Uri.parse("/storage/emulated/0/Pictures/Sponse/VID_20160809_131105.mp4");

        Log.d(TAG, "START THAT VIDEO" + uri);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setDataAndType(Uri.fromFile(file), "video/*");
        startActivity(intent);
    }

    private void arrangeDisplay(Sponse sponse) {
        switch (sponse.getStatus()) {
            case 0:{
                Log.d(TAG, "In CASE 0");
                InboxViewHolder.mInboxProgress.setVisibility(View.INVISIBLE);
                ((TextView) InboxViewHolder.mLoadIndicator).setText((CharSequence) "tap to load");
                return;
            }
            case 1:{
                Log.d(TAG, "In CASE 1.");
                InboxViewHolder.mInboxProgress.setVisibility(View.VISIBLE);
                ((TextView) InboxViewHolder.mLoadIndicator).setText((CharSequence) "loading");
                return;
            }
            case 2:{
                Log.d(TAG, "In CASE 2.");
                InboxViewHolder.mInboxProgress.setVisibility(View.INVISIBLE);
                ((TextView) InboxViewHolder.mLoadIndicator).setText((CharSequence) "tap to view");
                return;
            }

        }

    }
}