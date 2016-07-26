package com.example.bobby.hotseat.Data;

import java.util.HashMap;

/**
 * Created by bobby on 6/8/16.
 */
public class Friend {
    private String mIdToken;
    private String mDisplayName;
    private String mEmail;

    private boolean mSelected;
    private HashMap<String, String> mFriendsHash;

    public Friend() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Friend(String idToken, String name, String email) {
        mIdToken = idToken;
        mDisplayName = name;
        mEmail = email;
        mSelected = false;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
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
