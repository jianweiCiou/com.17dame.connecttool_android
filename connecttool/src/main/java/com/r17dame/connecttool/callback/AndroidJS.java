package com.r17dame.connecttool.callback;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import androidx.annotation.NonNull;

/*
 * REF  https://medium.com/%E7%A8%8B%E5%BC%8F%E8%A3%A1%E6%9C%89%E8%9F%B2/android-webview-interface-c59f81f4bbbd
 * */
public class AndroidJS {
    private final Context context;

    public AndroidJS(@NonNull Context context){
        this.context = context;
    }

    @JavascriptInterface
    public void helloWorld(){

        Toast.makeText(context,"Hello World",Toast.LENGTH_SHORT).show();
    }
}