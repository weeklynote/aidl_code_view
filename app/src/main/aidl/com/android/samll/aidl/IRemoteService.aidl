package com.android.samll.aidl;

import com.android.samll.aidl.IRemoteServiceCallback;

interface IRemoteService {

    void registerCallback(IRemoteServiceCallback cb);

    void unregisterCallback(IRemoteServiceCallback cb);
}
