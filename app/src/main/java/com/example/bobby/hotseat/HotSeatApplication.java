package com.example.bobby.hotseat;

import com.firebase.client.Firebase;

/**
 * Created by bobby on 5/31/16.
 */

public class HotSeatApplication extends android.app.Application {

    @Override
    public  void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
