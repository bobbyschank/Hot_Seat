package com.example.bobby.hotseat;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;
import com.firebase.ui.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

public class RecipientsActivity extends AppCompatActivity {

    private static final String TAG = RecipientsActivity.class.getSimpleName();

    protected String mCurrentUser;
    protected List<String> mFriends;

    FirebaseAuth mAuth;
    Firebase mRef;
    private DatabaseReference mDatabase;

    RecyclerView mFriendsRecyclerView;
    Button mSendButton;

    static int purple;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipients);
        mFriendsRecyclerView = (RecyclerView) findViewById(R.id.friendsRecycler);
        mFriendsRecyclerView.setHasFixedSize(true);
        mFriendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        mSendButton = (Button) findViewById(R.id.sendButton);
        Log.d(TAG, "CURRENT USER ID TOKEN" + MainActivity.currentUser.getIdToken());

        purple = ContextCompat.getColor(this, R.color.colorAccentPurple);


    }

    @Override
    protected void onStart() {
        super.onStart();
        mRef = new Firebase("https://hot-seat-28ddb.firebaseio.com/users/"+ MainActivity.currentUser.getIdToken() +"/friendsHash");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Firebase friendsRef = mRef.child(Strings.KEY_USERS)
                .child(MainActivity.currentUser.getIdToken())
                .child(Strings.KEY_FRIENDSHASH); // TODO Don't use public static currentUser

        final FirebaseRecyclerAdapter<Object, FriendViewHolder> adapter =
                new FirebaseRecyclerAdapter<Object, FriendViewHolder>(
                        Object.class,
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
                    protected void populateViewHolder(FriendViewHolder friendViewHolder, Object s, int i) {
                        int ii = 2;
                        String key = this.getRef(i).getKey();
                        Log.d(TAG, "S1 = " + key);
                        //HashMap hashMap = (HashMap) new HashMap<String, String>();
                        //hashMap = (HashMap) s;
                        ((TextView) friendViewHolder.mText).setText(s.toString());
                        ii = 5;
                        Log.d(TAG, "VALUE = " + s);
                        Log.d(TAG, "KEY = " + mRef.child(s.toString()));
                        Log.d(TAG, "INT = " + i);



                    }



                };
        Log.d(TAG, "Adapter created");
        mFriendsRecyclerView.setAdapter(adapter);
        Log.d(TAG, "Adapter set");


/*
        FirebaseListAdapter<String> adapter =
                new FirebaseListAdapter<String>(this, String.class,
                        //android.R.layout.simple_list_item_checked,
                        R.layout.friend_list_item,
                        friendsRef.orderByValue()) {
                    int i = 0;
                    @Override
                    protected void populateView(View view, String s, int i) {
                        ((TextView) view.findViewById(android.R.id.text1)).setText(s);
                    }
                };

        Log.d(TAG, "ITEMS" + adapter.getCount());

        if (adapter != null) {
            mFriendsRecyclerView.setAdapter(adapter);
            mFriendsRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Log.d(TAG, "ITEMS" + parent.getCount());

                    mSendButton.setVisibility(View.VISIBLE);
                }
            });
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.error_message)
                    .setTitle(R.string.error_title)
                    .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }*/
    }

    public static class FriendViewHolder
                    extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mText;

        public FriendViewHolder(View v) {
            super(v);
            mText = (TextView) v.findViewById(android.R.id.text1);
            v.setOnClickListener(this);
        }
        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            Log.d(TAG, "CLICKED IN FRIENDVIEWHOLDER, " + mText);
            mText.setBackgroundColor(purple);
            Log.d(TAG, "HEARD AT : " + getAdapterPosition() + "");
        }
    }
}