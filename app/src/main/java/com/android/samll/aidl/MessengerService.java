package com.android.samll.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class MessengerService extends Service {

    private final String TAG = MessengerService.class.getSimpleName();
    final Messenger mServiceMessenger = new Messenger(new MessengerHandler());

    @Override
    public IBinder onBind(Intent intent) {
        return mServiceMessenger.getBinder();
    }

    private class MessengerHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x11:
                    Log.d(TAG, "MessengerService Message arg1:" + msg.arg1 + ", arg2:" + msg.arg2);
                    Messenger client = msg.replyTo;
                    Message message = Message.obtain();
                    message.what = 0x22;
                    message.arg1 = 33;
                    message.arg2 = 44;
                    try {
                        client.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MessengerService onDestroy");
    }
}
