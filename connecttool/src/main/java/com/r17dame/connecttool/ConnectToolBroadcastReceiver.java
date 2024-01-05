package com.r17dame.connecttool;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class ConnectToolBroadcastReceiver extends BroadcastReceiver {
    public interface ConnectToolReceiverCallback {
        public void connectToolPageBack(Intent intent, String accountBackType);
    }

    private static ConnectToolReceiverCallback connectToolReceiverCallback;

    public void registerCallback(ConnectToolReceiverCallback Callback) {
        this.connectToolReceiverCallback = Callback;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(Context context, Intent intent) {
        String backType = intent.getStringExtra("accountBackType");

        String TAG = "onReceive test";
        Log.v(TAG, "onReceive backType : " + backType);
        connectToolReceiverCallback.connectToolPageBack(intent, backType);
    }
}