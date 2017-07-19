package com.android.samll.aidl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ControllerActivity extends Activity {

    private View.OnClickListener mStartListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            startService(new Intent("com.example.android.apis.app.REMOTE_SERVICE"));
        }
    };

    private View.OnClickListener mStopListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            stopService(new Intent("com.example.android.apis.app.REMOTE_SERVICE"));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);
        Button button = (Button) findViewById(R.id.start);
        button.setOnClickListener(mStartListener);
        button = (Button) findViewById(R.id.stop);
        button.setOnClickListener(mStopListener);
    }
}
