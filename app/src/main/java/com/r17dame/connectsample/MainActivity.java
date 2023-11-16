package com.r17dame.connectsample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.security.NoSuchAlgorithmException;

import com.r17dame.connecttool.ConnectBasic;
import com.r17dame.connecttool.ConnectTool;
import com.r17dame.connecttool.datamodel.ConnectCallback;
import com.r17dame.connecttool.datamodel.ConnectToken;
import com.r17dame.connecttool.datamodel.ConnectTokenCall;
import com.r17dame.connecttool.datamodel.MeCallback;
import com.r17dame.connecttool.datamodel.MeInfo;

public class MainActivity extends AppCompatActivity {

    ConnectTool _connectTool;

    Button RegisterButton;
    Button getConnectAuthorizeButton;
    Button postConnectTokenButton;
    Button postConnectRefreshTokenButton;
    Button getMeButton;
    Button LoginButton;

    private final static String TAG = "HTTPURLCONNECTION test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            // Init tool
            _connectTool = new ConnectTool(this, "", "", "", "");
            _connectTool.connectBasic = new ConnectBasic("", "", "", "", "");

            // deepLink
            Intent appLinkIntent = getIntent();
            String appLinkAction = appLinkIntent.getAction();
            Uri appLinkData = appLinkIntent.getData();
            if (appLinkData != null && appLinkData.isHierarchical()) {
                String uri = this.getIntent().getDataString();
                _connectTool.code = appLinkData.getQueryParameter("code");
            }

            _connectTool.CreateAccountInitData(
                    "",
                    "");

            RegisterButton = findViewById(R.id.RegisterButton);
            RegisterButton.setOnClickListener(view -> {
                _connectTool.SendRegisterData(new ConnectCallback() {
                    @Override
                    public void callbackCheck(boolean value) {
                        Log.v(TAG, "RegisterData callback : " + value);
                    }
                });
            });

            LoginButton = findViewById(R.id.LoginButton);
            LoginButton.setOnClickListener(view -> {
                _connectTool.SendLoginData(new ConnectCallback() {
                    @Override
                    public void callbackCheck(boolean value) {
                        Log.v(TAG, "LoginData callback : " + value);
                    }
                });
            });

            getConnectAuthorizeButton = findViewById(R.id.getConnectAuthorizeButton);
            getConnectAuthorizeButton.setOnClickListener(view -> {
                _connectTool.OpenAuthorizeURL();
            });

            postConnectTokenButton = findViewById(R.id.postConnectTokenButton);
            postConnectTokenButton.setOnClickListener(view -> {
                _connectTool.GetConnectToken_Coroutine(new ConnectTokenCall() {

                    @Override
                    public void callbackConnectToken(ConnectToken value) {
                        Log.v(TAG, "ConnectToken callback : " + value.access_token);
                    }
                });
            });

            postConnectRefreshTokenButton = findViewById(R.id.postConnectRefreshTokenButton);
            postConnectRefreshTokenButton.setOnClickListener(view -> {
                _connectTool.GetRefreshToken_Coroutine(new ConnectTokenCall() {

                    @Override
                    public void callbackConnectToken(ConnectToken value) {
                        Log.v(TAG, "RefreshToken callback : " + value.access_token);
                    }
                });
            });

            getMeButton = findViewById(R.id.getMeButton);
            getMeButton.setOnClickListener(view -> {
                try {
                    _connectTool.GetMe_Coroutine(new MeCallback() {
                        @Override
                        public void callbackMeInfo(MeInfo value) {
                            Log.v(TAG, "MeInfo callback : " + value.status);
                        }
                    });
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}