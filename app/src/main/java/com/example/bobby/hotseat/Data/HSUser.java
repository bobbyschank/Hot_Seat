package com.example.bobby.hotseat.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bobby on 7/11/16.
 */
public class HSUser {

    private String mIdToken;
    private String mDisplayName;
    private String mEmail;
    private Map<String, String> mFriendsHash;

    public HSUser() {
        // Default constructor required for calls to DataSnapshot.getValue(HotSeatUser.class)
    }

    public HSUser(String idToken, String name, String email) {
        mIdToken = idToken;
        mDisplayName = name;
        mEmail = email;
        mFriendsHash = new HashMap<>();
    }

    public void addFriend(String friendIdToken, String friendDisplayName) {
        mFriendsHash.put(friendIdToken, friendDisplayName);
    }

    public void removeFriend(String friendIdToken) {
        mFriendsHash.remove(friendIdToken);
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public Map<String, String> getFriendsHash() {
        return mFriendsHash;
    }

    public String getIdToken() {
        return mIdToken;
    }

    public void setIdToken(String midToken) {
        this.mIdToken = midToken;
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
