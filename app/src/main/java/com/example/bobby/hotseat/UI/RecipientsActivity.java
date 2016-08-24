package com.example.bobby.hotseat.UI;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bobby.hotseat.Data.Friend;
import com.example.bobby.hotseat.Data.Sponse;
import com.example.bobby.hotseat.Data.Strings;
import com.example.bobby.hotseat.R;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipientsActivity extends AppCompatActivity {

    private static final String TAG = RecipientsActivity.class.getSimpleName();

    protected String mCurrentUser;
    public static List<Friend> mFriendList = new ArrayList<>(); // TODO static?
    public static List<Friend> selectedFriendsList = new ArrayList<>();

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    /*private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };*/

    FirebaseAuth mAuth;
    Firebase mRef;
    private DatabaseReference mDatabase;

    RecyclerView mFriendsRecyclerView;
    Button mSendButton;

    static int highlight;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    // Create a storage reference from our app
    StorageReference storageRef = storage.getReferenceFromUrl("gs://hot-seat-28ddb.appspot.com");
    StorageReference imagesRef = storageRef.child("images");

    Uri mMediaUri;
    Uri storageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipients);

        Intent intent = getIntent();
        mMediaUri = intent.getData();

        mFriendsRecyclerView = (RecyclerView) findViewById(R.id.friendsRecycler);
        mFriendsRecyclerView.setHasFixedSize(true);
        mFriendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mSendButton = (Button) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFileToStorage();
            }
        });

        Log.d(TAG, "mMediaUri.getLastPathSegment: " + mMediaUri.getLastPathSegment());

        highlight = ContextCompat.getColor(this, R.color.colorAccentOrangeLight);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mRef = new Firebase("https://hot-seat-28ddb.firebaseio.com/users/"+ MainActivity.currentUser.getIdToken() + "/" + Strings.KEY_FRIENDSHASH);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Firebase friendsRef = mRef.child(Strings.KEY_USERS)
                .child(MainActivity.currentUser.getIdToken())
                .child(Strings.KEY_FRIENDSHASH); // TODO Don't use public static currentUser

        final FirebaseRecyclerAdapter<String, FriendViewHolder> adapter =
                new FirebaseRecyclerAdapter<String, FriendViewHolder>(
                        String.class,
                        R.layout.recycler_list_item,
                        //android.R.layout.two_line_list_item,
                        FriendViewHolder.class,
                        mRef.orderByValue())
                 {

                    @Override
                    public void onBindViewHolder(FriendViewHolder holder, int position, List<Object> payloads) {
                        super.onBindViewHolder(holder, position, payloads);
                    }

                    @Override
                    protected void populateViewHolder(FriendViewHolder friendViewHolder, String name, int i) {
                        String key = this.getRef(i).getKey();
                        Log.d(TAG, "S1 = " + key);
                        ((TextView) friendViewHolder.mText).setText(name.toString());
                        Log.d(TAG, "VALUE = " + name);
                        Log.d(TAG, "KEY = " + mRef.child(name.toString()));
                        Log.d(TAG, "INT = " + i);
                        Friend friend = new Friend(key, name, null);
                        mFriendList.add(friend);
                    }
                };

        Log.d(TAG, "Adapter created");
        mFriendsRecyclerView.setAdapter(adapter);
        Log.d(TAG, "Adapter set");

    }

    public static class FriendViewHolder
                    extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mText;


        public FriendViewHolder(View v) {
            super(v);
            mText = (TextView) v.findViewById(R.id.friendName);
            v.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Friend selectedFriend = mFriendList.get(position);
            boolean selected = selectedFriend.isSelected();
            if (!selected) {
                mText.setBackgroundColor(highlight);
                selectedFriendsList.add(selectedFriend);
                mFriendList.get(position).setSelected(true);
            } else {
                mText.setBackgroundColor(Color.TRANSPARENT);
                selectedFriendsList.remove(selectedFriend);
                mFriendList.get(position).setSelected(false);
            }
            for (Friend f:selectedFriendsList) {
                Log.d(TAG, "CLICKED IN FRIENDVIEWHOLDER, " + f.getDisplayName().toString());
            }
        }
    }

    private void uploadFileToStorage() {

        // File or Blob
        //Uri file = Uri.fromFile(new File("path/to/mountains.jpg"));
        Uri file = mMediaUri;
        String lastPathSeg = mMediaUri.getLastPathSegment();
        String contentType = "Unknown Type";
        String contentPath = "Unknown Type";

        if (lastPathSeg.contains(".mp4")) {
            contentType = "video/mp4";
            contentPath = "Videos";
        } else if (lastPathSeg.contains(".jpg")) {
            contentType = "image/jpeg";
            contentPath  = "Images";
        } else {
            Log.d(TAG, "Error finding content type.");
        }

        // Create the file metadata
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType(contentType)
                .build();

        // Upload file and metadata to the path 'images/mountains.jpg'
        //UploadTask uploadTask = storageRef.child("images/"+file.getLastPathSegment()).putFile(file, metadata);
        UploadTask uploadTask = storageRef.child(contentPath).child(lastPathSeg).putFile(file, metadata);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                System.out.println("Upload is " + progress + "% done");
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("Upload is paused");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                storageUri = downloadUrl;
                Sponse sponse = new Sponse(MainActivity.currentUser.getIdToken(),
                                            MainActivity.currentUser.getDisplayName(),
                                            downloadUrl.toString());

                Log.d(TAG, "SPONSE TIMESTAMP:     " + sponse.getTimeStamp());

                Toast.makeText(RecipientsActivity.this, "Upload Complete", Toast.LENGTH_SHORT).show();
                Log.d(TAG, downloadUrl + "");
                sendSponse(sponse);
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // If there's an upload in progress, save the reference so you can query it later
        if (storageRef != null) {
            outState.putString("reference", storageRef.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // If there was an upload in progress, get its reference and create a new StorageReference
        final String stringRef = savedInstanceState.getString("reference");
        if (stringRef == null) {
            return;
        }
        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(stringRef);

        // Find all UploadTasks under this StorageReference (in this example, there should be one)
        List<UploadTask> tasks = storageRef.getActiveUploadTasks();
        if (tasks.size() > 0) {
            // Get the task monitoring the upload
            UploadTask task = tasks.get(0);

            // Add new listeners to the task using an Activity scope
            task.addOnSuccessListener(this, new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    Toast.makeText(RecipientsActivity.this, R.string.upload_complete, Toast.LENGTH_SHORT).show();
                }
            }); // TODO Handle this situation with send sponse.
        }
    }

    private void sendSponse(Sponse sponse) {
        for (Friend friend : selectedFriendsList) {

            Log.d(TAG, "SPONSE ID: " + sponse.getIdToken());
            String key = mDatabase.child(Strings.KEY_USERS).child(friend.getIdToken()).child("sponses").push().getKey();


            //TODO See if childUpdates would be better than setValue. ChildUpdates currently not getting sponse to DB.
            /*
            HashMap<String, Object> result = new HashMap<>();
            result.put("uid", sponse.getIdToken());
            result.put("name", sponse.getDisplayName());
            result.put("uri", sponse.getUri().toString());

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/sponses/" + key, result);
            */

            mDatabase.child(Strings.KEY_USERS).child(friend.getIdToken()).child("sponses").child(key).setValue(sponse);
        }
    }
}