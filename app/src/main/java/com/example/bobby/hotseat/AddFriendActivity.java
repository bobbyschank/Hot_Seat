package com.example.bobby.hotseat;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AddFriendActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    Button mFindByEmailButton;
    TextView mEmailField;
    ListView mFriendsListView;
    String mEmail;

    Firebase mRef;
    private DatabaseReference mDatabase;

    private String userID;
    //private HashMap<String, String> mFriends = new HashMap<>();
    private Map<String, String> mFriendsHash = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);


        Intent intent = getIntent();
        userID = intent.getStringExtra(Strings.KEY_USERID);

        mEmailField = (TextView) findViewById(R.id.findByEmailField);
        mFindByEmailButton = (Button) findViewById(R.id.findByEmailButton);
        mFindByEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mEmail = mEmailField.getText().toString();
                //findFriendByEmail(mEmail);

                Log.d(TAG, "CLICKED!!!" + mEmail);
                findFriendByEmail(mEmail);
            }

        });
        mFriendsListView = (ListView) findViewById(R.id.friendsListView);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mRef = new Firebase("https://hot-seat-28ddb.firebaseio.com");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Log.d(TAG, "onStart");
        Firebase friendsRef = mRef.child(Strings.KEY_USERS).child(userID).child(Strings.KEY_FRIENDSHASH);
        FirebaseListAdapter<String> adapter =
                new FirebaseListAdapter<String>(this, String.class, android.R.layout.simple_list_item_1, friendsRef.orderByValue()) {
                    @Override
                    protected void populateView(View view, String s, int i) {
                        TextView textView = (TextView)view.findViewById(android.R.id.text1);
                        textView.setText(s);
                    }
                };
        mFriendsListView.setAdapter(adapter);

        mDatabase.child(Strings.KEY_USERS)
                .child(userID)
                .child(Strings.KEY_FRIENDSHASH)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Object o = dataSnapshot.getValue(Object.class);
                                mFriendsHash = (HashMap) o;
                                Log.w(TAG, "FRIENDSSSSSSS      " + o);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                            }
                        });

    }

    private void findFriendByEmail(final String friendEmail) {
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "FIND    FRIEND \n\n");
                String email = dataSnapshot.child("email").getValue(String.class);
                Log.d(TAG, "onChildAdded:" + dataSnapshot.child("email").getValue(String.class));
                if (email.equalsIgnoreCase(friendEmail)) {
                    Log.d(TAG, "MATCH!!!!!" + email);
                    String idToken = dataSnapshot.child("idToken").getValue(String.class);
                    String displayName = dataSnapshot.child("displayName").getValue(String.class);

                    mFriendsHash.put(idToken, displayName);

                    mDatabase.child(Strings.KEY_USERS).child(userID).child(Strings.KEY_FRIENDSHASH).setValue(mFriendsHash);

                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddFriendActivity.this);
                    builder.setMessage(R.string.friend_email_not_found)
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    //TODO Send invite to entered email
                }
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
}