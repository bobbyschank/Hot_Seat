package com.example.bobby.hotseat.Data;

import android.net.Uri;

import java.util.HashMap;

/**
 * Created by bobby on 7/30/16.
 */
public class Sponse {
    private String mIdToken;
    private String mDisplayName;
    private Uri mUri;

    public Sponse() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Sponse(String idToken, String name, Uri uri) {
        mIdToken = idToken;
        mDisplayName = name;
        mUri = uri;
    }
}
