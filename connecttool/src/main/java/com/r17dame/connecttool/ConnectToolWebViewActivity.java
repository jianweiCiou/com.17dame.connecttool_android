package com.r17dame.connecttool;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;
import com.r17dame.connecttool.datamodel.CreateSPCoinResponse;

public class ConnectToolWebViewActivity extends AppCompatActivity {

    private ConnectToolBroadcastReceiver connectReceiver;
    public Context context;

    ConnectTool _connectTool;
    private final static String TAG = "ConnectTool test";
    String url = "";
    WebView connectWebView;

    SharedPreferences pref;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_tool_web_view);

        // init callback
        this.context = this;

        CookieManager.getInstance().setAcceptCookie(true);

        this.pref = ((Activity) context).getSharedPreferences("ConnectToolP", Context.MODE_PRIVATE);
        String redirect_uri = pref.getString(String.valueOf(R.string.redirect_uri), "");
        String RSAstr = pref.getString(String.valueOf(R.string.RSAstr), "");
        String X_Developer_Id = pref.getString(String.valueOf(R.string.X_Developer_Id), "");
        String client_secret = pref.getString(String.valueOf(R.string.client_secret), "");
        String Game_id = pref.getString(String.valueOf(R.string.Game_id), "");

        // init connectTool
        _connectTool = new ConnectTool(
                context, redirect_uri,
                RSAstr,
                X_Developer_Id, client_secret, Game_id);

        // Init registerReceiver
        IntentFilter itFilter = new IntentFilter("com.r17dame.CONNECT_ACTION");
        connectReceiver = new ConnectToolBroadcastReceiver();
        registerReceiver(connectReceiver, itFilter);

        // 開網頁
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            url = extras.getString("url");
        }

        // 設定網頁
        connectWebView = (WebView) findViewById(R.id.ConnectWebView);
        connectWebView.loadUrl(url);
        WebSettings webSettings = connectWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);


        // ADD js
        connectWebView.addJavascriptInterface(this, "Android");

        connectWebView.setWebViewClient(new WebViewClient() {
            @SuppressLint("JavascriptInterface")
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.v(TAG, "onPageFinished " + url);

                // Set localStorage
                String RSAstr = pref.getString(String.valueOf(R.string.RSAstr), "");
                String _me = pref.getString(String.valueOf(R.string.me), "");
                String _access_token = pref.getString(String.valueOf(R.string.access_token), "");
                String ClientID = pref.getString(String.valueOf(R.string.X_Developer_Id), "");
                String Secret = pref.getString(String.valueOf(R.string.client_secret), "");
                String requestNumber = pref.getString(String.valueOf(R.string.requestNumber), "");
                String redirect_uri = pref.getString(String.valueOf(R.string.redirect_uri), "");

                // 填充資訊
                connectWebView.loadUrl("javascript:localStorage.setItem('Secret','" + Secret + "');");
                connectWebView.loadUrl("javascript:localStorage.setItem('ClientID','" + ClientID + "');");
                connectWebView.loadUrl("javascript:localStorage.setItem('me','" + _me + "');");
                connectWebView.loadUrl("javascript:localStorage.setItem('access_token','" + _access_token + "');");
                connectWebView.loadUrl("javascript:localStorage.setItem('RSAstr','" + RSAstr + "');");
                connectWebView.loadUrl("javascript:localStorage.setItem('requestNumber','" + requestNumber + "');");
                connectWebView.loadUrl("javascript:localStorage.setItem('redirect_uri','" + redirect_uri + "');");

                // 設定登入測試
//                String Input_Email = "";
//                String Input_Password = "";
//                String Input_ConfirmPassword = "";
//                // 註冊新
//                if (url.contains("AppRegister") ) {
//                      Input_Email = "";
//                }
//                //燈入
//                if (url.contains("AppLogin") ) {
//                    Input_Email = "";
//                }
//                Input_Password = "";
//                Input_ConfirmPassword = "";
//                connectWebView.loadUrl("javascript:(function(){document.getElementById('Input_Email').value = '" + Input_Email + "';})()");
//                connectWebView.loadUrl("javascript:(function(){document.getElementById('Input_Password').value = '" + Input_Password + "';})()");
//                connectWebView.loadUrl("javascript:(function(){document.getElementById('Input_ConfirmPassword').value = '" + Input_ConfirmPassword + "';})()");

                // 設定遊戲註冊
                if (url.contains("/Account/Login")) {
                    String AppRegisterUrl = "'/account/AppRegister/" + Uri.encode(_connectTool.connectBasic.Game_id) + "/" + Uri.encode(_connectTool.referralCode) + "?returnUrl=" + Uri.encode(_connectTool.redirect_uri) + "'";
                    connectWebView.loadUrl("javascript:(function(){document.getElementById('goToRegister').href=" + AppRegisterUrl + ";})()");
                }
                Uri appLinkData = Uri.parse(url);
                // Oauth 回應
                if (url.contains("Account/connectlink")) {
                    // auth
                    // Open by Account Page (Register, Login) :
                    if (appLinkData.getQueryParameterNames().contains("accountBackType")) {
                        String accountBackType = appLinkData.getQueryParameter("accountBackType");
                        Log.v(TAG, "accountBackType  " + accountBackType);

                        if (accountBackType.equals("Register")) {
                            Intent it = new Intent("com.r17dame.CONNECT_ACTION");
                            it.putExtra("accountBackType", "Register");
                            sendBroadcast(it);
                            finish();
                        }
                        if (accountBackType.equals("Login")) {
                            Intent it = new Intent("com.r17dame.CONNECT_ACTION");
                            it.putExtra("accountBackType", "Login");
                            sendBroadcast(it);
                            finish();
                        }
                    }

                    if (appLinkData.getQueryParameterNames().contains("code")) {
                        String code = appLinkData.getQueryParameter("code");
                        Intent it = new Intent("com.r17dame.CONNECT_ACTION");
                        it.putExtra("accountBackType", "Authorize");
                        it.putExtra("code", code);
                        sendBroadcast(it);
                        finish();
                    }

                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && connectWebView.canGoBack()) {//如果按下返回鍵&能後退為true
            connectWebView.goBack();//返回上一頁
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(connectReceiver);
    }

    @JavascriptInterface
    public void sendFinish() {
        // Want to call this via the vue.js app
        finish();
    }

    // 註冊結束
    @JavascriptInterface
    public void CompleteRegistration() {
        Intent it = new Intent("com.r17dame.CONNECT_ACTION");
        it.putExtra("accountBackType", "Register");
        sendBroadcast(it);
        finish();
    }

    @JavascriptInterface
    public void CompleteLogin() {
        Intent it = new Intent("com.r17dame.CONNECT_ACTION");
        it.putExtra("accountBackType", "Login");
        sendBroadcast(it);
        finish();
    }

    // 消費
    @JavascriptInterface
    public void appLinkDataCallBack_CompleteConsumeSP(String consumeSPresponseData) {
        // Want to call this via the vue.js app

        Gson gson = new Gson();
        CreateSPCoinResponse SPCoinResponse = gson.fromJson(consumeSPresponseData, CreateSPCoinResponse.class);

        Intent it = new Intent("com.r17dame.CONNECT_ACTION");
        it.putExtra("accountBackType", "CompleteConsumeSP");
        it.putExtra("CompleteConsumeSP", consumeSPresponseData);
        it.putExtra("consume_transactionId", SPCoinResponse.data.transactionId);
        it.putExtra("consume_status", SPCoinResponse.data.orderStatus);
        sendBroadcast(it);
        Log.v(TAG, "consumeSPresponseData " + consumeSPresponseData);
        finish();
    }

    // 購買
    @JavascriptInterface
    public void appLinkDataCallBack_CompletePurchase(String state, String TradeNo, String PurchaseOrderId) {
        // Want to call this via the vue.js app

        Intent it = new Intent("com.r17dame.CONNECT_ACTION");
        it.putExtra("accountBackType", "CompletePurchase");
        it.putExtra("state", state);
        it.putExtra("TradeNo", TradeNo);
        it.putExtra("PurchaseOrderId", PurchaseOrderId);
        sendBroadcast(it);

        Log.v(TAG, "purchaseOrderOneResponseData " + state);
        Log.v(TAG, "purchaseOrderOneResponseData " + TradeNo);
        Log.v(TAG, "purchaseOrderOneResponseData " + PurchaseOrderId);
        finish();
    }
}