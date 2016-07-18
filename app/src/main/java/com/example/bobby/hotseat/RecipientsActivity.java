package com.example.bobby.hotseat;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class RecipientsActivity extends AppCompatActivity {

    private static final String TAG = FriendsFragment.class.getSimpleName();

    protected String mCurrentUser;
    protected List<String> mFriends;

    FirebaseAuth mAuth;
    Firebase mRef;
    private DatabaseReference mDatabase;

    ListView mFriendsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipients);
        mFriendsListView = (ListView) findViewById(R.id.friendsList);
        mFriendsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mRef = new Firebase("https://hot-seat-28ddb.firebaseio.com");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Firebase friendsRef = mRef.child(Strings.KEY_USERS)
                .child(MainActivity.currentUser.getIdToken())
                .child(Strings.KEY_FRIENDSHASH); // TODO Don't use public static currentUser

        int i = 0;

        FirebaseListAdapter<String> adapter =
                new FirebaseListAdapter<String>(this, String.class,
                        android.R.layout.simple_list_item_checked,
                        //R.layout.friend_list_item,
                        friendsRef.orderByValue()) {
                    int i = 0;
                    @Override
                    protected void populateView(View view, String s, int i) {
                        ((TextView) view.findViewById(android.R.id.text1)).setText(s);
                    }
                };

        if (adapter != null) {
            // setListAdapter(adapter);
            mFriendsListView.setAdapter(adapter);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.error_message)
                    .setTitle(R.string.error_title)
                    .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
