package com.android.samll.aidl;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BindingActivity extends Activity {

    private final String TAG = BindingActivity.class.getSimpleName();
    IRemoteService mService = null;
    ISecondary mSecondaryService = null;
    Button mKillButton;
    TextView mCallbackText;
    private boolean mIsBound;

    private View.OnClickListener mBindListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            mCallbackText.setText("Binding.");
            bindService(new Intent(IRemoteService.class.getName()), mConnection, Context.BIND_AUTO_CREATE);
            bindService(new Intent(ISecondary.class.getName()), mSecondaryConnection, Context.BIND_AUTO_CREATE);
            mIsBound = true;
        }
    };

    private View.OnClickListener mUnbindListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mIsBound) {
                if (mService != null) {
                    try {
                        mService.unregisterCallback(mCallback);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                unbindService(mConnection);
                unbindService(mSecondaryConnection);
                mKillButton.setEnabled(false);
                mIsBound = false;
                mCallbackText.setText("Unbinding.");
            }
        }
    };

    private View.OnClickListener mKillListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mSecondaryService != null) {
                try {
                    int pid = mSecondaryService.getPid();
                    Process.killProcess(pid);
                    mCallbackText.setText("Killed service process.");
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                    Toast.makeText(BindingActivity.this, R.string.remote_call_failed, Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remote_service_binding);
        Button button = (Button) findViewById(R.id.bind);
        button.setOnClickListener(mBindListener);
        button = (Button) findViewById(R.id.unbind);
        button.setOnClickListener(mUnbindListener);
        mKillButton = (Button) findViewById(R.id.kill);
        mKillButton.setOnClickListener(mKillListener);
        mKillButton.setEnabled(false);
        mCallbackText = (TextView) findViewById(R.id.callback);
        mCallbackText.setText("Not attached.");
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(TAG, "Service type " + service.getClass().getSimpleName() + " type == null?" + (service == null));
            mService = IRemoteService.Stub.asInterface(service);
            Log.d(TAG, "mService type " + mService.getClass().getSimpleName());
            mKillButton.setEnabled(true);
            mCallbackText.setText("Attached.");
            try {
                mService.registerCallback(mCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Toast.makeText(BindingActivity.this, R.string.remote_service_connected, Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            mKillButton.setEnabled(false);
            mCallbackText.setText("Disconnected.");
            Toast.makeText(BindingActivity.this, R.string.remote_service_disconnected, Toast.LENGTH_SHORT).show();
        }
    };

    private ServiceConnection mSecondaryConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mSecondaryService = ISecondary.Stub.asInterface(service);
            mKillButton.setEnabled(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            mSecondaryService = null;
            mKillButton.setEnabled(false);
        }
    };


    private IRemoteServiceCallback mCallback = new IRemoteServiceCallback.Stub() {

        @Override
        public void valueChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(BUMP_MSG, value, 0));
        }
    };

    private static final int BUMP_MSG = 1;

    private Handler mHandler = new Handler() {
        
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BUMP_MSG:
                    mCallbackText.setText("Received from service: " + msg.arg1);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }

    };
}
