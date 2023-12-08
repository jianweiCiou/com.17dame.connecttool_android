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

import com.google.gson.Gson;
import com.r17dame.connecttool.callback.ACPAYPayWithPrimeCallback;
import com.r17dame.connecttool.callback.ConnectCallback;
import com.r17dame.connecttool.callback.CreatePaymentCallback;
import com.r17dame.connecttool.callback.CreatePurchaseOrderCallback;
import com.r17dame.connecttool.callback.GetPurchaseOrderListCallback;
import com.r17dame.connecttool.callback.GetUserCardsCallback;
import com.r17dame.connecttool.datamodel.ConnectToken;
import com.r17dame.connecttool.callback.ConnectTokenCall;
import com.r17dame.connecttool.callback.MeCallback;
import com.r17dame.connecttool.datamodel.MeInfo;
import com.r17dame.connecttool.datamodel.PayWithPrimeRespone;
import com.r17dame.connecttool.datamodel.PaymentResponse;
import com.r17dame.connecttool.datamodel.PurchaseOrder;
import com.r17dame.connecttool.callback.PurchaseOrderCallback;
import com.r17dame.connecttool.datamodel.PurchaseOrderListResponse;
import com.r17dame.connecttool.datamodel.PurchaseOrderOneResponse;
import com.r17dame.connecttool.datamodel.PurchaseOrderRequest;
import com.r17dame.connecttool.datamodel.UserCard;

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

    Button rechargeButton;
    Button transferSPButton;
    Button createPurchaseOrderButton;
    Button GetPurchaseOrderListButton;
    Button GetPurchaseOrderButton;


    Button ConsumeSPButton;
    Button OpenConsumeSPButton;

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
                    "",
                    "",
                    "",
                    "");

            _connectTool.connectBasic = new ConnectBasic(
                    "",
                    "",
                    "",
                    "",
                    "");

            _connectTool.CreateAccountInitData(
                    "",
                    "");

            // deepLink
            Intent appLinkIntent = getIntent();
            Uri appLinkData = appLinkIntent.getData();
            if (appLinkData != null && appLinkData.isHierarchical()) {

                // get Access token
                if (appLinkData.getQueryParameterNames().contains("code")) {
                    _connectTool.code = appLinkData.getQueryParameter("code");

                    _connectTool.GetConnectToken_Coroutine(new ConnectTokenCall() {
                        @Override
                        public void callbackConnectToken(ConnectToken value) {
                            _connectCallbackText.setText("ConnectToken callback : " + value.access_token);
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

                    _connectTool.createPayment(new CreatePaymentCallback() {
                        @Override
                        public void callback(PaymentResponse value) {
                            Log.v(TAG, "PaymentResponse callback : " + value);
                            // status : PaymentSuccess or failure
                            String status = "PaymentSuccess";
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

            /*
             * Payment
             * */
            ConsumeSPButton = findViewById(R.id.ConsumeSPButton);
            ConsumeSPButton.setOnClickListener(view -> {
                try {
                    int spCoin = 5; //
                    int rebate = 3;
                    String orderNo = UUID.randomUUID().toString();
                    _connectTool.createPayment(new CreatePaymentCallback() {
                        @Override
                        public void callback(PaymentResponse value) {
                            Log.v(TAG, "PaymentResponse callback : " + value);
                        }
                    }, spCoin, rebate, orderNo);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            });


            // Open Consume URL
            OpenConsumeSPButton = findViewById(R.id.OpenConsumeSPButton);
            OpenConsumeSPButton.setOnClickListener(view -> {
                int consume_spCoin = 0;
                int consume_rebate = 0;
                String orderNo = UUID.randomUUID().toString();
                String GameName = "";
                String productName = "";
                _connectTool.OpenConsumeSPURL(consume_spCoin, consume_rebate, orderNo, GameName, productName);
            });


            // Get Purchase Order List
            GetPurchaseOrderListButton = findViewById(R.id.GetPurchaseOrderListButton);
            GetPurchaseOrderListButton.setOnClickListener(view -> {
                try {
                    _connectTool.GetPurchaseOrderList(new GetPurchaseOrderListCallback() {
                        @Override
                        public void callback(PurchaseOrderListResponse value) {
                            Log.v(TAG, "PurchaseOrderListResponse callback : " + value);
                        }
                    });
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            });

            GetPurchaseOrderButton = findViewById(R.id.GetPurchaseOrderButton);
            GetPurchaseOrderButton.setOnClickListener(view -> {
                try {
                    String tradeNo = "";
                    _connectTool.GetPurchaseOrderOne(new PurchaseOrderCallback() {
                        @Override
                        public void callback(PurchaseOrderOneResponse value) {
                            Log.v(TAG, "PurchaseOrderOneResponse callback : " + value);
                        }
                    }, tradeNo);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            });

            rechargeButton = findViewById(R.id.rechargeButton);
            rechargeButton.setOnClickListener(view -> {
                try {
                    _connectTool.GetMe_Coroutine(new MeCallback() {
                        @Override
                        public void callbackMeInfo(MeInfo value) {
                            Log.v(TAG, "MeInfo callback : " + value.status);
                            _connectTool.OpenRechargeURL();
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