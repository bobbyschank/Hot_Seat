package com.example.bobby.hotseat.UI;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bobby.hotseat.Data.Strings;

import com.example.bobby.hotseat.R;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

/**
 * Created by bobby on 6/6/16.
 */
public class FriendsFragment extends ListFragment{

    private static final String TAG = FriendsFragment.class.getSimpleName();

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

       // mFriendsRecyclerView = (ListView) mFriendsRecyclerView.findViewById(R.id.friendsListView);

    }


    @Override
    public void onStart() {
        super.onStart();
        mRef = new Firebase("https://hot-seat-28ddb.firebaseio.com");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Firebase friendsRef = mRef.child(Strings.KEY_USERS)
                .child(MainActivity.currentUser.getIdToken())
                .child(Strings.KEY_FRIENDSHASH); // TODO Don't use public static currentUser
        Log.d(TAG, friendsRef.toString() + "");

        int i = 0;

        FirebaseListAdapter<String> adapter =
                new FirebaseListAdapter<String>(getActivity(), String.class,
                                            //android.R.layout.simple_list_item_checked,
                                            R.layout.friend_list_item,
                                            friendsRef.orderByValue()) {
                    int i = 0;
            @Override
            protected void populateView(View view, String s, int i) {
                ((TextView) view.findViewById(android.R.id.text1)).setText(s);

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
        //mFriendsRecyclerView.setAdapter(adapter);
    }
}