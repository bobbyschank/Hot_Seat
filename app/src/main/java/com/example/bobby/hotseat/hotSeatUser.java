package com.example.bobby.hotseat;

import java.util.HashMap;

/**
 * Created by bobby on 6/7/16.
 */

public class hotSeatUser {

    private String mIDToken;
    private String mDisplayName;
    private String mEmail;
    private HashMap<String, String> mFriendsHash;

    public hotSeatUser() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public hotSeatUser(String iDToken, String name, String email) {
        mIDToken = iDToken;
        mDisplayName = name;
        mEmail = email;
    }

    public String getIDToken() {
        return mIDToken;
    }

    public void setIDToken(String IDToken) {
        mIDToken = IDToken;
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