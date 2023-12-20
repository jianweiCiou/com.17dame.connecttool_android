package com.r17dame.connecttool;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.r17dame.connecttool.callback.ACPAYPayWithPrimeCallback;
import com.r17dame.connecttool.callback.ConnectCallback;
import com.r17dame.connecttool.callback.CreatePaymentCallback;
import com.r17dame.connecttool.callback.CreatePurchaseOrderCallback;
import com.r17dame.connecttool.callback.GetPurchaseOrderListCallback;
import com.r17dame.connecttool.callback.GetSPCoinTxCallback;
import com.r17dame.connecttool.datamodel.ConnectToken;
import com.r17dame.connecttool.callback.ConnectTokenCall;
import com.r17dame.connecttool.callback.MeCallback;
import com.r17dame.connecttool.datamodel.CreateSPCoinResponse;
import com.r17dame.connecttool.datamodel.MeInfo;
import com.r17dame.connecttool.datamodel.PayWithPrimeRespone;
import com.r17dame.connecttool.datamodel.PurchaseOrder;
import com.r17dame.connecttool.callback.PurchaseOrderCallback;
import com.r17dame.connecttool.datamodel.PurchaseOrderListResponse;
import com.r17dame.connecttool.datamodel.PurchaseOrderOneResponse;
import com.r17dame.connecttool.datamodel.SPCoinTxResponse;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class ConnectToolSampleActivity extends AppCompatActivity {
    ConnectTool _connectTool;

    Button RegisterButton;
    Button getConnectAuthorizeButton;
    Button postConnectTokenButton;
    Button postConnectRefreshTokenButton;
    Button getMeButton;
    Button LoginButton;

    // page access
    Button Register_pageButton;
    Button Login_pageButton;
    Button LogoutButton;

    Button rechargeButton;
    Button transferSPButton;
    Button createPurchaseOrderButton;
    Button GetPurchaseOrderListButton;
    Button GetPurchaseOrderButton;


    Button ConsumeSPButton;
    Button OpenConsumeSPButton;
    Button QueryConsumeSPButton;
    Button GetUserCardsButton;

    TextView _connectCallbackText;
    TextView _textViewMeInfo;
    TextView _ConsumeSPInfoTextView;
    TextView _payment_tradeNoTextView;

    private final static String TAG = "ConnectTool test";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_tool_sample);

        _connectCallbackText = findViewById(R.id.ConnectCallbackText);
        _textViewMeInfo = findViewById(R.id.textViewMeInfo);
        _ConsumeSPInfoTextView = findViewById(R.id.ConsumeSPInfo);

        try {
            // Init tool
            _connectTool = new ConnectTool(
                    this,
                    "RnJpIEp1bCAp",
                    "ebe4ae28-dda1-499d-bdbc-1066ce080a6f",
                    "r17dame://connectlink",
                    "-----BEGIN RSA PRIVATE KEY-----\n" +
                            "MIIEowIBAAKCAQEAudt2mFGvEEmpqFsYhdq+UrA04UoumRcB7LYx7VO4QWYzDYwNBWuMM3XfGqDSd3bC9R8uzPy2oeENa55F6R9HTk4aa7TSIVYsMp1Ipn/SMhs3snBoE9UzmUTezNKYvEZQ8yLdItAFfnql3pho7iM52h+dvdhNaPNq4W5CGqMdkOzbHcFEZMkmb0HCZJ33Iwti5gdqhrJVIA+ELGS7H/CIbOy4X6kT/9owukqSNKScBZikIHRfONMyBN4IhBbEMXRisiTz/nXoLdXP7p7/Q6gP9lIskIPWGaydncQARTKFisMkUxqM/gXcoQhXKR78MMIouyvZNuMzh4K9sxFQpamf2QIDAQABAoIBAAvpqI+u0f0bRkfyPtTQO9lqTe660IMjhGNlx4wiDPIJg5rfWoj5vGzNUKrmRVc+7NQarDybXiFrdBltIFIG8o0ZrdEwL9/PJC9tAWvFTvXDOi4HzRlTAGcB5mipKae3t2yTftUXvE0VE238cDgDWKvwfC3foBFADkkQxiVARfQ0qQes5Q6KvHIxZROzQnIpTScw8ZFdiKE1ilvytViLZeVzS+aCrXw3XTa75NU6AUAYdUmZlSlYnxuLCVuDgDBZ6TC94ZWXddv+emFWcf3lfV8MB4p6zLkW1w3jGJ47/JVfiHSvr6lKJbgUnZmzj4zDRgPnB327jrpPoF8QivV+Y5kCgYEA1yYArMdMVPYyy8BlB5rd5SOB6BL0odmdMG7tXAQsyLaINqpc3/RDRdPTry3wiGAyf0HVdAeDt9yZLmtH6hpkFogsVMilxbdLqMXd8qTdMczRJ8Mj1InsnY/fWOcaKDdQs0CA5CJYB69IX+82rxczvTltlsDxDB+A9b5LSFioa38CgYEA3SWqxVDf+QduKOnWzxrnCuns3N9MeBMpwBY3Dx2rYrPsPs4Lueb43e8M/qqg/jApufj567Ruh8H/kO6fvbMm9JxGZ9M5riLn3CuWoich8EpT4qSvZu+zUL0dVyBiYWMfXexcNUq6ckjWP+QmtiU1B9Bq4uWR1h3hCJg08lv0gKcCgYAgM5bsRVQeb08BAgXdEofdsOfTpWqqAtktE51BJXrSe8d9bxhBiNy8ycyoLpcOwl8sft0E5c8IKONgeDwmRNbwLGd+NR3iruGLHDpxA837ky1G50UonZAlsQ/7zXMzy7uvaJsiCiXk2I5blYE4yZ871imZ47zwVJLHtTitVl+23wKBgQCje3UC6Qap0hRdqoBiGkEykDvKDEk7eu8iUUniosxP6zJ6O1fv1g+kAVRZ70mUn4Y5NRWMaZZMRd3oBn+QfSAPNHfXyQ6a7LL60D5LISK1wDzDD3ubXRfyV9uYzRftZpmJlXGU8+lhEvdPxBnaDSdm32wk0BE/eFcjQ2HgyJm3gQKBgHv38GPMFMjbk0hULRMF6doKb8lHuuJoona4cC4mh5BUye5On5u8BH+ZrKP1i2W+Ttkva1kxb+V28BWnzROs3pXR6gYel7Yz18n87IjUD+NFWMdRmsNHKMwj15jbK9ZjUuum6afjHuQfkwfyE3JV0rjGI5rrisrMYmGxfjnBcX48\n" +
                            "-----END RSA PRIVATE KEY-----\n");


            _connectTool.connectBasic = new ConnectBasic(
                    "ebe4ae28-dda1-499d-bdbc-1066ce080a6f",
                    "ebe4ae28-dda1-499d-bdbc-1066ce080a6f",
                    "AQAAAAIAAAAQKK7k66HdnUm9vBBmzggKb+l99746/ADCj911GtHQaAgjxcxUGjnwDDY+Ao57SfwV",
                    "07d5c223-c8ba-44f5-b9db-86001886da8d",
                    "smart887");


            _connectTool.CreateAccountInitData(
                    "jianwei.ciou@gmail.com",
                    "Abc-12356");

            // deepLink
            Intent appLinkIntent = getIntent();
            Uri appLinkData = appLinkIntent.getData();
            if (appLinkData != null && appLinkData.isHierarchical()) {


                // Open by Account Page :
                if (appLinkData.getQueryParameterNames().contains("accountBackType")) {
                    String accountBackType = appLinkData.getQueryParameter("accountBackType");
                    Log.v(TAG, "accountBackType  " + accountBackType);
                    if (accountBackType.equals("Register")) {
                        /*
                         * App-side add functions.
                         */
                        Toast.makeText(getApplicationContext(), "註冊回應", Toast.LENGTH_SHORT).show();
                    }
                    if (accountBackType.equals("Login")) {
                        /*
                         * App-side add functions.
                         */
                        Toast.makeText(getApplicationContext(), "登入回應", Toast.LENGTH_SHORT).show();
                    }
                    if (accountBackType.equals("Logout")) {
                        /*
                         * App-side add functions.
                         */
                        Toast.makeText(getApplicationContext(), "登出回應", Toast.LENGTH_SHORT).show();
                    }
                    _connectTool.AccountPageEvent(accountBackType);
                }

                // Complete purchase of SP Coin
                if (appLinkData.getQueryParameterNames().contains("purchase_state")) {
                    String purchase_state = appLinkData.getQueryParameter("purchase_state");
                    Log.v(TAG, "purchase_state :" + purchase_state);

                    Toast.makeText(getApplicationContext(), "purchase_state : " + purchase_state, Toast.LENGTH_SHORT).show();
                }

                // Complete consumption of SP Coin
                if (appLinkData.getQueryParameterNames().contains("consume_state")) {
                    String consume_state = appLinkData.getQueryParameter("consume_state");
                    Log.v(TAG, "consume_state :" + consume_state);
                    Toast.makeText(getApplicationContext(), "consume_state : " + consume_state, Toast.LENGTH_SHORT).show();
                }


                // get Access token
                if (appLinkData.getQueryParameterNames().contains("code")) {
                    _connectTool.code = appLinkData.getQueryParameter("code");

                    _connectTool.GetConnectToken_Coroutine(new ConnectTokenCall() {
                        @Override
                        public void callbackConnectToken(ConnectToken value) throws NoSuchAlgorithmException {
                            _connectCallbackText.setText("ConnectToken callback : " + value.access_token);

                            _connectTool.GetMe_Coroutine(new MeCallback() {
                                @Override
                                public void callbackMeInfo(MeInfo value) {
                                    Log.v(TAG, "MeInfo callback : " + value.status);
                                }
                            });
                        }
                    });
                }


                // CreatePurchaseOrder
                if (appLinkData.getQueryParameterNames().contains("spCoinItemPriceId") && appLinkData.getQueryParameterNames().contains("payMethod")) {
                    String spCoinItemPriceId = appLinkData.getQueryParameter("spCoinItemPriceId");
                    String payMethod = appLinkData.getQueryParameter("payMethod");
                    String payGateway = appLinkData.getQueryParameter("payGateway");
                    String prime = appLinkData.getQueryParameter("prime");
                    try {
                        _connectTool.CreatePurchaseOrder(new CreatePurchaseOrderCallback() {
                            @Override
                            public void callback(PurchaseOrder value) throws NoSuchAlgorithmException {
                                Log.v(TAG, "PurchaseOrder callback : " + value);
                                if (value.status == 1000) {
                                    //_connectTool.ACPAYPayWithPrime();
                                    // 回傳 requestNumber tradeNo prime layout(1:PC / 2:mobile)
                                    _connectTool.ACPAYPayWithPrime(new ACPAYPayWithPrimeCallback() {
                                        @Override
                                        public void callback(PayWithPrimeRespone value) {
                                            Log.v(TAG, value.data.url);
                                            _connectTool.Open3DSURL(value.data.url);
                                        }
                                    });
                                }
                            }
                        }, spCoinItemPriceId, payMethod, payGateway, prime);
                    } catch (NoSuchAlgorithmException e) {
                        Log.v(TAG, "NoSuchAlgorithmException : " + e);
                        throw new RuntimeException(e);
                    }
                }

                // Payment
                if (appLinkData.getQueryParameterNames().contains("consumesp") &&
                        appLinkData.getQueryParameterNames().contains("consume_spCoin") &&
                        appLinkData.getQueryParameterNames().contains("consume_rebate") &&
                        appLinkData.getQueryParameterNames().contains("orderNo")) {

                    String consume_spCoin = appLinkData.getQueryParameter("consume_spCoin");
                    String consume_rebate = appLinkData.getQueryParameter("consume_rebate");

                    int spCoin = Integer.valueOf(consume_spCoin);
                    int rebate = Integer.valueOf(consume_rebate);
                    String orderNo = appLinkData.getQueryParameter("orderNo");

                    _connectTool.CreateSPCoinOrder(new CreatePaymentCallback() {
                        @Override
                        public void callback(CreateSPCoinResponse value) {
                            Log.v(TAG, "PaymentResponse transactionId : " + value.data.transactionId);
                            Log.v(TAG, "PaymentResponse orderStatus : " + value.data.orderStatus);
                            // status : PaymentSuccess or failure
                            String status = (value.data.orderStatus.equals("Completed")) ? "PaymentSuccess" : "Failure";
                            _connectTool.OpenConsumeSPResultURL(status);
                        }
                    }, spCoin, rebate, orderNo);
                }
            }

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

            /**
             * Page access
             * */
            //頁面登入
            Register_pageButton = findViewById(R.id.Register_pageButton);
            Register_pageButton.setOnClickListener(view -> {
                _connectTool.OpenRegisterURL();
            });

            //頁面登入
            Login_pageButton = findViewById(R.id.Login_pageButton);
            Login_pageButton.setOnClickListener(view -> {
                _connectTool.OpenLoginURL();
            });

            //頁面登出
            LogoutButton = findViewById(R.id.LogoutButton);
            LogoutButton.setOnClickListener(view -> {
                _connectTool.OpenLogoutURL();
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
                            Toast.makeText(getApplicationContext(), value.data.email, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            });

            /*
             * Purchase
             * */
            // 購買 SPCoin
            rechargeButton = findViewById(R.id.rechargeButton);
            rechargeButton.setOnClickListener(view -> {
                String notifyUrl = "";// NotifyUrl is a URL customized by the game developer
                String state = "Custom state";// Custom state ,
                // Step1. Set notifyUrl and state,
                _connectTool.set_purchase_notifyData(notifyUrl, state);

                // Step2. Set currencyCode
                String currencyCode = "2";

                // Step3. Open Recharge Page
                _connectTool.OpenRechargeURL(currencyCode, notifyUrl, state);
            });

            // Get Purchase Order List
            GetPurchaseOrderListButton = findViewById(R.id.GetPurchaseOrderListButton);
            GetPurchaseOrderListButton.setOnClickListener(view -> {
                try {
                    _connectTool.GetPurchaseOrderList(new GetPurchaseOrderListCallback() {
                        @Override
                        public void callback(PurchaseOrderListResponse value) {
                            Log.v(TAG, "PurchaseOrderListResponse callback : " + value);
                            Toast.makeText(getApplicationContext(), "All Purchase : " + value.data.length, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            });

            GetPurchaseOrderButton = findViewById(R.id.GetPurchaseOrderButton);
            GetPurchaseOrderButton.setOnClickListener(view -> {
                try {
                    String tradeNo = "T2023121800000058";
                    _connectTool.GetPurchaseOrderOne(new PurchaseOrderCallback() {
                        @Override
                        public void callback(PurchaseOrderOneResponse value) {
                            Log.v(TAG, "PurchaseOrderOneResponse callback : " + value);
                            Toast.makeText(getApplicationContext(), "Purchase tradeNo : " + value.data.tradeNo, Toast.LENGTH_SHORT).show();
                        }
                    }, tradeNo);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            });


            /*
             * ConsumeSP
             * */
            // App 內購買，不跳轉頁面
            ConsumeSPButton = findViewById(R.id.ConsumeSPButton);
            ConsumeSPButton.setOnClickListener(view -> {
                try {
                    String notifyUrl = "";// NotifyUrl is a URL customized by the game developer
                    String state = "Custom state";// Custom state ,

                    // Step1. Set notifyUrl and state,
                    _connectTool.set_purchase_notifyData(notifyUrl, state);

                    int spCoin = 20;
                    int rebate = 0;
                    String orderNo = UUID.randomUUID().toString();
                    _connectTool.CreateSPCoinOrder(new CreatePaymentCallback() {
                        @Override
                        public void callback(CreateSPCoinResponse value) {
                            Log.v(TAG, "CreateSPCoinResponse orderStatus : " + value.data.orderStatus);

                            Toast.makeText(getApplicationContext(), "SPCoin " + value.data.orderNo + " : " + value.data.orderStatus, Toast.LENGTH_SHORT).show();
                        }
                    }, spCoin, rebate, orderNo);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            });


            // 跳轉頁面確認
            // Open Consume URL
            OpenConsumeSPButton = findViewById(R.id.OpenConsumeSPButton);
            OpenConsumeSPButton.setOnClickListener(view -> {

                String notifyUrl = "";// NotifyUrl is a URL customized by the game developer
                String state = "Custom state";// Custom state ,

                // Step1. Set notifyUrl and state,
                _connectTool.set_purchase_notifyData(notifyUrl, state);

                int consume_spCoin = 50;
                int consume_rebate = 20;
                String orderNo = UUID.randomUUID().toString();
                String GameName = "Good 18 Game";
                String productName = "10 of the best diamonds";
                _connectTool.OpenConsumeSPURL(consume_spCoin, consume_rebate, orderNo, GameName, productName, notifyUrl, state);
            });

            // 查詢使用點數訂單 交易編號
            // 測試參考 : "transactionId":"T2023121500000030","orderNo":"b427a826-4101-4172-8694-9e0ee868b9ab"
            QueryConsumeSPButton = findViewById(R.id.QueryConsumeSPButton);
            QueryConsumeSPButton.setOnClickListener(view -> {
                try {
                    String transactionId = "b427a826-4101-4172-8694-9e0ee868b9ab";

                    _connectTool.Get_SPCoin_tx(transactionId, new GetSPCoinTxCallback() {
                        @Override
                        public void callback(CreateSPCoinResponse value) {
                            Log.v(TAG, "SPCoinTxResponse callback : " + value.data.orderStatus);

                            Toast.makeText(getApplicationContext(), "SPCoin " + value.data.orderNo + " : " + value.data.orderStatus, Toast.LENGTH_SHORT).show();
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