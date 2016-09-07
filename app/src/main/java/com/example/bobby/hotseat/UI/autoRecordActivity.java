package com.example.bobby.hotseat.UI;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.bobby.hotseat.R;

public class AutoRecordActivity extends Activity {

    private static final String TAG = "AUTORECORDACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "XXXXXXXXXXXXX   AUTORECORD ACTIVITY ONCREATE");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_record);
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2VideoFragment.newInstance())
                    .commit();
        }
    }
    @Override
    public void onBackPressed() {
    }
}