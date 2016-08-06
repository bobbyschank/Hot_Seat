package com.example.bobby.hotseat.Data;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.format.Time;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * Created by bobby on 7/30/16.
 */

@JsonIgnoreProperties(ignoreUnknown=true)

public class Sponse {
    private String mIdToken;
    private String mDisplayName;
    private String mUri;
    //private Date mTimestamp;

    public Sponse() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Sponse(String idToken, String name, String uri) {
        mIdToken = idToken;
        mDisplayName = name;
        mUri = uri;
       // mTimestamp = new Date();
    }

    //public Date getTimestamp() {return mTimestamp;}

    //public void setTimestamp(Date timestamp) {mTimestamp = timestamp;}

    public String getIdToken() {
        return mIdToken;
    }

    public void setIdToken(String idToken) {
        mIdToken = idToken;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(String displayName) {
        mDisplayName = displayName;
    }

    public String getUri() {
        return mUri;
    }

    public void setUri(String uri) {
        mUri = uri;
    }



    @Override
    public String toString() {
        return "Sponse{mIdToken='" + mIdToken + "\', mDisplayName='" + mDisplayName + "\', mUri='" + mUri + "'}";
    }


}
