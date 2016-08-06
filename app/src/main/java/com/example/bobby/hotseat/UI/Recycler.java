package com.example.bobby.hotseat.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.bobby.hotseat.Data.Sponse;
import com.example.bobby.hotseat.Data.Strings;
import com.example.bobby.hotseat.R;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Recycler extends AppCompatActivity {

    private static final String TAG = Recycler.class.getSimpleName();

    Firebase mRef;
    private DatabaseReference mDatabase;

    RecyclerView mInboxRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        mInboxRecyclerView = (RecyclerView) findViewById(R.id.inboxRecycler);
        mInboxRecyclerView.setHasFixedSize(true);
        mInboxRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mRef = new Firebase("https://hot-seat-28ddb.firebaseio.com/users/"
                + MainActivity.currentUser.getIdToken()
                + "/sponses");// TODO Don't use public static currentUser

        final FirebaseRecyclerAdapter<Sponse, InboxViewHolder> adapter =
                new FirebaseRecyclerAdapter<Sponse, InboxViewHolder>(
                        Sponse.class,
                        //R.layout.recycler_list_item,
                        android.R.layout.two_line_list_item,
                        InboxViewHolder.class,
                        mRef)
                {
/*
                    @Override
                    public void onBindViewHolder(InboxViewHolder inboxViewHolder, int position, List<Object> payloads) {
                        super.onBindViewHolder(inboxViewHolder, position, payloads);
                    }
                    */

                    @Override
                    protected void populateViewHolder(InboxViewHolder inboxViewHolder, Sponse sponse, int i) {

                        Log.d(TAG, "In Populate View Holder.");
                        ((TextView) InboxViewHolder.mText).setText((CharSequence) sponse.getDisplayName());

                    }
                };

        Log.d(TAG, "Adapter created");
        mInboxRecyclerView.setAdapter(adapter);
        Log.d(TAG, "Adapter set");


    }


    public static class InboxViewHolder
            extends RecyclerView.ViewHolder implements View.OnClickListener {
        static TextView mText;


        public InboxViewHolder(View v) {
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
            int position = getAdapterPosition();
                Log.d(TAG, "CLICKED");
            }
    }

}




