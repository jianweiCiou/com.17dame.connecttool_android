package com.r17dame.connecttool;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.FrameLayout;

public class ConnectToolWebViewActivity extends Activity {

    // private ConnectToolBroadcastReceiver connectReceiver;
    public Context context;

    ConnectTool _connectTool;
    private final static String TAG = "ConnectTool test";
    String url = "";

    SharedPreferences pref;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_tool_web_view);

        // init callback
        this.context = this;

        // 初始 connectTool
        this.pref = ((Activity) context).getSharedPreferences("ConnectToolP", Context.MODE_PRIVATE);
        String redirect_uri = pref.getString(String.valueOf(R.string.redirect_uri), "");
        String RSAstr = pref.getString(String.valueOf(R.string.RSAstr), "");
        String X_Developer_Id = pref.getString(String.valueOf(R.string.X_Developer_Id), "");
        String client_secret = pref.getString(String.valueOf(R.string.client_secret), "");
        String Game_id = pref.getString(String.valueOf(R.string.Game_id), "");
        String culture = pref.getString(String.valueOf(R.string.culture), "");
        String currencyCode = pref.getString(String.valueOf(R.string.currencyCode), "");


        // init connectTool
        _connectTool = new ConnectTool(
                context, redirect_uri,
                RSAstr,
                X_Developer_Id, client_secret, Game_id,culture,currencyCode);

        // 開網頁
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            url = extras.getString("url");
        }


        // 設定 17dame 網頁
        _connectTool.connectCocos_webLayout = new FrameLayout(this);
        _connectTool.initConnectWebview(this,url); // init FrameLayout,Webview
        addContentView(_connectTool.connectCocos_webLayout,_connectTool.getFrameLayoutParams());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && _connectTool.connectWebView.canGoBack()) {//如果按下返回鍵&能後退為true
            _connectTool.connectWebView.goBack();//返回上一頁
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//            // 修正 Android 14+ 的廣播註冊
//            this.registerReceiver(connectReceiver, itFilter, RECEIVER_EXPORTED);
//        } else {
//            unregisterReceiver(connectReceiver);
//        }
    }
}