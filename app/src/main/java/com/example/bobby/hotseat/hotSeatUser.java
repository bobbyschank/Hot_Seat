package com.example.bobby.hotseat;

import java.util.HashMap;

/**
 * Created by bobby on 6/7/16.
 */

public class HotSeatUser {

    private String mIdToken;
    private String mDisplayName;
    private String mEmail;
    private HashMap<String, Boolean> mFriendsHash;

    public HotSeatUser() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public HotSeatUser(String idToken, String name, String email) {
        mIdToken = idToken;
        mDisplayName = name;
        mEmail = email;
        mFriendsHash = new HashMap<>();
    }

    public void addFriend(String friendIdToken) {
        mFriendsHash.put(friendIdToken, true);
    }

    public void removeFriend(String friendIdToken) {
        mFriendsHash.remove(friendIdToken);
    }

    public String getIdToken() {
        return mIdToken;
    }

    public void setIdToken(String midToken) {
        this.mIdToken = midToken;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(String name) {
        mDisplayName = name;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }
}
