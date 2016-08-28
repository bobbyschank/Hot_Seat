package com.example.bobby.hotseat.Data;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bobby.hotseat.UI.InboxFragment;
import com.example.bobby.hotseat.UI.MainActivity;
import com.example.bobby.hotseat.UI.RecipientsActivity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by bobby on 7/30/16.
 */

@JsonIgnoreProperties(ignoreUnknown=true)

public class Sponse {

    private static final String TAG = Sponse.class.getSimpleName();

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private StorageReference storageRef = storage.getReferenceFromUrl("gs://hot-seat-28ddb.appspot.com");

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    Firebase mRef = new Firebase("https://hot-seat-28ddb.firebaseio.com/users/"
            + MainActivity.currentUser.getIdToken()
            + "/sponses");// TODO Don't use public static currentUser

    private String mIdToken;
    private String mDisplayName;
    private String mUri;
    private String mTimeStamp;
    private int mStatus;

    public Sponse() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Sponse(String idToken, String name, String uri) {
        mIdToken = idToken;
        mDisplayName = name;
        mUri = uri;
        Date date =  new Date();
        String timeStamp = new SimpleDateFormat("MMM d,  h:mm a", Locale.US).format(date);
        // TODO Under a week, show day of week, under a day, show time.
        // Maybe three different strings? or manipulate in inbox fragment
        mTimeStamp = timeStamp;
        mStatus = 0;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public void setTimeStamp(String timeStamp) {
        mTimeStamp = timeStamp;
    }

    public String getTimeStamp() {return mTimeStamp;}

    public String getIdToken() {
        return mIdToken;
    }

    public void setIdToken(String idToken) {
        mIdToken = idToken;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(String displayName) {
        mDisplayName = displayName;
    }

    public String getUri() {
        return mUri;
    }

    public void setUri(String uri) {
        mUri = uri;
    }


    @Override
    public String toString() {
        return "Sponse{mIdToken='" + mIdToken + "\', mDisplayName='" + mDisplayName + "\', mUri='" + mUri + "'}";
    }

    public void uploadFileToStorage(Uri uri, final List<String> selectedFriendIds) {

        // File or Blob
        //Uri file = Uri.fromFile(new File("path/to/mountains.jpg"));

        String lastPathSeg = uri.getLastPathSegment();

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
        UploadTask uploadTask = storageRef.child(getIdToken()).child(contentPath).child(lastPathSeg).putFile(uri, metadata);

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
                //storageUri = downloadUrl;

                /*
               // Sponse sponse = new Sponse(MainActivity.currentUser.getIdToken(),
                        MainActivity.currentUser.getDisplayName(),
                        downloadUrl.toString());
                        */
                Sponse.this.setUri(downloadUrl.toString());



                Log.d(TAG, "SPONSE TIMESTAMP:     " + getTimeStamp());
                Log.d(TAG, downloadUrl + " UPLOAD COMPLETE");
                sendSponse(selectedFriendIds, Sponse.this);
            }
        });
    }

    public void sendSponse(List<String> selectedFriendIds, Sponse sponse) {
        for (String friendId : selectedFriendIds) {

            Log.d(TAG, "SPONSE ID: " + getIdToken());
            String key = mDatabase.child(Strings.KEY_USERS).child(friendId).child("sponses").push().getKey();

            //TODO See if childUpdates would be better than setValue. ChildUpdates currently not getting sponse to DB.
            /*
            HashMap<String, Object> result = new HashMap<>();
            result.put("uid", sponse.getIdToken());
            result.put("name", sponse.getDisplayName());
            result.put("uri", sponse.getUri().toString());

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/sponses/" + key, result);
            */

            mDatabase.child(Strings.KEY_USERS).child(friendId).child("sponses").child(key).setValue(sponse);
        }
    }

    public void loadSponse(final File file, final String key) throws IOException {

        StorageReference storageRef = storage.getReferenceFromUrl(this.getUri());
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

}