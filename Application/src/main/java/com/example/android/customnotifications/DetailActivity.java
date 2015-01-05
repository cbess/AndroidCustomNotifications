package com.example.android.customnotifications;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Represents the detail activity.
 */
public class DetailActivity extends Activity {
    public static final String TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        Log.i(TAG, "Created");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.w(TAG, "Destroyed");
    }
}
