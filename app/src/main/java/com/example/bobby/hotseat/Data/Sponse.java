package com.example.bobby.hotseat.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by bobby on 7/30/16.
 */

@JsonIgnoreProperties(ignoreUnknown=true)

public class Sponse {
    private String mIdToken;
    private String mDisplayName;
    private String mUri;
    private String mTimeStamp;
    private int mStatus;

    public Sponse() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Sponse(String idToken, String name, String uri) {
        mIdToken = idToken;
        mDisplayName = name;
        mUri = uri;
        Date date =  new Date();
        String timeStamp = new SimpleDateFormat("MMM d,  h:mm a", Locale.US).format(date);
        // TODO Under a week, show day of week, under a day, show time.
        // Maybe three different strings? or manipulate in inbox fragment
        mTimeStamp = timeStamp;
        mStatus = 0;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public void setTimeStamp(String timeStamp) {
        mTimeStamp = timeStamp;
    }

    public String getTimeStamp() {return mTimeStamp;}

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
