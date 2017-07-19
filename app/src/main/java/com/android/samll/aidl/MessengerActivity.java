package com.android.samll.aidl;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

public class MessengerActivity extends Activity {

    private final String TAG = MessengerActivity.class.getSimpleName();
    private Messenger mServiceMessenger;
    private Handler mClientHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x22:
                    Log.d(TAG, "MessengerActivity Messenger arg1:" + msg.arg1 + ", arg2:" + msg.arg2);
                    break;
            }
        }
    };
    private Messenger mClientMessenger = new Messenger(mClientHandler);

    private ServiceConnection mConn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServiceMessenger = new Messenger(service);
            Message msg = Message.obtain(null, 0x11, 22, 33);
            msg.replyTo = mClientMessenger;
            try {
                mServiceMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceMessenger = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        findViewById(R.id.bind_messenger).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindService(new Intent(MessengerService.class.getName()), mConn, BIND_AUTO_CREATE);
            }
        });
        findViewById(R.id.unbind_messenger).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unbindService(mConn);
            }
        });
    }
}
