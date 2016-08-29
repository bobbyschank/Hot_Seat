package com.example.bobby.hotseat.UI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bobby.hotseat.Data.Sponse;
import com.example.bobby.hotseat.Data.Strings;

import com.example.bobby.hotseat.NpaLinearLayoutManager;
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

/**
 * Created by bobby on 6/6/16.
 */
public class ReactionsFragment extends Fragment{


    private static final String TAG = InboxFragment.class.getSimpleName();

    private RecyclerView mInboxRecyclerView;
    TextView emptyTextView;

    public static final int MEDIA_TYPE_IMAGE = 2;
    public static final int MEDIA_TYPE_VIDEO = 3;

    public static final int REQUEST_AUTORECORD = 4;
    public boolean isLoaded = false;

    protected String mCurrentUser;

    protected static String sponseAuthor;

    Firebase mRef = new Firebase("https://hot-seat-28ddb.firebaseio.com/users/"
            + MainActivity.currentUser.getIdToken()
            + "/" + Strings.KEY_REACTIONS);// TODO Don't use public static currentUser

    private DatabaseReference mDatabase;

    static FirebaseStorage storage = FirebaseStorage.getInstance();

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


        final FirebaseRecyclerAdapter<Sponse, InboxFragment.InboxViewHolder> adapter =
                new FirebaseRecyclerAdapter<Sponse, InboxFragment.InboxViewHolder>(
                        Sponse.class,
                        R.layout.inbox_item,
                        //android.R.layout.two_line_list_item,
                        InboxFragment.InboxViewHolder.class,
                        mRef)
                {

                    @Override
                    protected void populateViewHolder(InboxFragment.InboxViewHolder inboxViewHolder, final Sponse sponse, int i) {

                        emptyTextView.setVisibility(View.INVISIBLE);

                        Log.d(TAG, "In Populate View Holder.");
                        ((TextView) InboxFragment.InboxViewHolder.mAuthorView).setText((CharSequence) sponse.getDisplayName());

                        Log.d(TAG, "TIMESTAMP:            " + sponse.getTimeStamp() + i);
                        Log.d(TAG, "STATUS:            " + sponse.getStatus());

                        ((TextView) InboxFragment.InboxViewHolder.mTimeView).setText((CharSequence) sponse.getTimeStamp());

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
                                            sponse.loadSponse(file, key);
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
                                                viewReaction(file, Uri.parse(uri));
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

                InboxFragment.InboxViewHolder.mInboxProgress.setVisibility(View.INVISIBLE);
                ((TextView) InboxFragment.InboxViewHolder.mLoadIndicator).setText((CharSequence) "tap to view");

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

    public void viewReaction(File file, Uri uri) throws IOException {

        Log.d(TAG, "START THAT VIDEO" + uri);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setDataAndType(Uri.fromFile(file), "video/*");
        startActivityForResult(intent, REQUEST_AUTORECORD);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_AUTORECORD) {

            int i = resultCode;

            int ii = 0;

            if (resultCode == 0) // TODO get intent resultCode to recognize video completion
            {

                Log.d(TAG, "HOPEFULLY, END OF VIDEO.");

                // Navigate to Record Video
                //Intent autoRecordIntent = new Intent(getActivity(), AutoRecordActivity.class);
                //startActivity(autoRecordIntent);

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
                InboxFragment.InboxViewHolder.mInboxProgress.setVisibility(View.INVISIBLE);
                ((TextView) InboxFragment.InboxViewHolder.mLoadIndicator).setText((CharSequence) "tap to load");
                return;
            }
            case 1:{
                Log.d(TAG, "In CASE 1 DISPLAY SWITCH.");
                InboxFragment.InboxViewHolder.mInboxProgress.setVisibility(View.VISIBLE);
                ((TextView) InboxFragment.InboxViewHolder.mLoadIndicator).setText((CharSequence) "loading");
                return;
            }
            case 2:{
                Log.d(TAG, "In CASE 2 DISPLAY SWITCH.");
                InboxFragment.InboxViewHolder.mInboxProgress.setVisibility(View.INVISIBLE);
                ((TextView) InboxFragment.InboxViewHolder.mLoadIndicator).setText((CharSequence) "tap to view");
                return;
            }
        }
    }
}











    /*

public class FriendsFragment extends ListFragment{

    private static final String TAG = InboxFragment.class.getSimpleName();

    protected String mCurrentUser;
    protected List<String> mFriends;

    FirebaseAuth mAuth;
    Firebase mRef;
    private DatabaseReference mDatabase;

    ListView mFriendsListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        /*
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        */
/*

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

       // mCurrentUser = ;
       // mFriendsRelation = ;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // mInboxRecyclerView = (ListView) mInboxRecyclerView.findViewById(R.id.friendsListView);

    }


    @Override
    public void onStart() {
        super.onStart();
        mRef = new Firebase("https://hot-seat-28ddb.firebaseio.com");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Firebase friendsRef = mRef.child(Strings.KEY_USERS)
                .child(MainActivity.currentUser.getIdToken())
                .child("friendsHash"); // TODO Don't use public static currentUser
        Log.d(TAG, friendsRef.toString() + "");

        int i = 0;

        FirebaseListAdapter<String> adapter =
                new FirebaseListAdapter<String>(getActivity(),
                                            String.class,
                                            //android.R.layout.simple_list_item_1,
                                            R.layout.friend_list_item,
                                            friendsRef) {
                    @Override
                    public void populateView(View view,  String s, int i) {
                        ((TextView) view.findViewById(android.R.id.text1)).setText((CharSequence) s);
                    }

        };

        if (adapter != null) {

            setListAdapter(adapter);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.error_message)
                    .setTitle(R.string.error_title)
                    .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        //mInboxRecyclerView.setAdapter(adapter);
    }
}
*/
