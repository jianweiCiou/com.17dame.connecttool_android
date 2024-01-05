package com.r17dame.connecttool.callback;

import android.os.Build;

import androidx.annotation.RequiresApi;

public interface ConnectWebCallback {

    @RequiresApi(api = Build.VERSION_CODES.O)
    void appLinkData(String url);
}
