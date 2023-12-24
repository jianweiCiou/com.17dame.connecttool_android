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

import com.r17dame.connecttool.callback.GetPurchaseOrderListCallback;
import com.r17dame.connecttool.callback.GetSPCoinTxCallback;
import com.r17dame.connecttool.datamodel.ConnectToken;
import com.r17dame.connecttool.callback.ConnectTokenCall;
import com.r17dame.connecttool.callback.MeCallback;
import com.r17dame.connecttool.datamodel.CreateSPCoinResponse;
import com.r17dame.connecttool.datamodel.MeInfo;
import com.r17dame.connecttool.callback.PurchaseOrderCallback;
import com.r17dame.connecttool.datamodel.PurchaseOrderListResponse;
import com.r17dame.connecttool.datamodel.PurchaseOrderOneResponse;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class ConnectToolSampleActivity extends AppCompatActivity {
    ConnectTool _connectTool;

    Button getConnectAuthorizeButton;
    Button postConnectRefreshTokenButton;
    Button getMeButton;

    // page access
    Button Register_pageButton;
    Button Login_pageButton;

    Button rechargeButton;
    Button GetPurchaseOrderListButton;
    Button GetPurchaseOrderButton;


    Button OpenConsumeSPButton;
    Button QueryConsumeSPButton;
    TextView _connectCallbackText;
    TextView _textViewMeInfo;
    TextView _ConsumeSPInfoTextView;

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
            _connectTool = new ConnectTool(
                    this,
                    "",
                    "",
                    "",
                    "",
                    "");

            // deepLink
            Intent appLinkIntent = getIntent();
            Uri appLinkData = appLinkIntent.getData();
            if (appLinkData != null && appLinkData.isHierarchical()) {
                Log.v(TAG, "appLinkData : " + appLinkData);

                // Open by Account Page (Register, Login) :
                if (appLinkData.getQueryParameterNames().contains("accountBackType")) {
                    String accountBackType = appLinkData.getQueryParameter("accountBackType");
                    Log.v(TAG, "accountBackType  " + accountBackType);

                    if (accountBackType.equals("Register")) {
                        /*
                         * App-side add functions.
                         */
                    }
                    if (accountBackType.equals("Login")) {
                        /*
                         * App-side add functions.
                         */
                    }
                    String state = "App-side-State";
                    _connectTool.AccountPageEvent(state,accountBackType);
                }

                // Complete purchase of SP Coin
                if (appLinkData.getQueryParameterNames().contains("purchase_state")) {
                    _connectTool.appLinkDataCallBack_CompletePurchase(appLinkData,new PurchaseOrderCallback() {
                        @Override
                        public PurchaseOrderOneResponse callback(PurchaseOrderOneResponse value) {
                            Log.v(TAG, "appLinkData PurchaseOrderOneResponse callback : " + value);
                            /*
                             * App-side add functions.
                             */
                            return value;
                        }
                    });
                }

                // Complete consumption of SP Coin
                if (appLinkData.getQueryParameterNames().contains("consume_transactionId")) {
                    _connectTool.appLinkDataCallBack_CompleteConsumeSP(appLinkData,new GetSPCoinTxCallback(){
                        @Override
                        public void callback(CreateSPCoinResponse value) {
                            /*
                             * App-side add functions.
                             */
                            Log.v(TAG, "appLinkData SPCoinTxResponse callback : " + value.data.orderStatus);
                        }
                    });
                }

                // get Access token
                if (appLinkData.getQueryParameterNames().contains("code") ) {
                    _connectTool.code = appLinkData.getQueryParameter("code");

                    _connectTool.GetConnectToken_Coroutine(new ConnectTokenCall() {
                        @Override
                        public void callbackConnectToken(ConnectToken value) throws NoSuchAlgorithmException {
                            _connectCallbackText.setText("ConnectToken callback : " + value.access_token);

                            UUID GetMe_RequestNumber = UUID.fromString("73da5d8e-9fd6-11ee-8c90-0242ac120002"); // App-side-RequestNumber(UUID)
                            _connectTool.GetMe_Coroutine(GetMe_RequestNumber,new MeCallback() {
                                @Override
                                public void callbackMeInfo(MeInfo value) {

                                    /*
                                     * App-side add functions.
                                     */ 
                                    Log.v(TAG, "MeInfo callback : " + value.status);
                                }
                            });
                        }
                    });
                }
            }

            /**
             * Page access
             * */
            //頁面註冊
            Register_pageButton = findViewById(R.id.Register_pageButton);
            Register_pageButton.setOnClickListener(view -> {
                _connectTool.OpenRegisterURL();
            });

            //頁面登入
            Login_pageButton = findViewById(R.id.Login_pageButton);
            Login_pageButton.setOnClickListener(view -> {
                _connectTool.OpenLoginURL();
            });

            getConnectAuthorizeButton = findViewById(R.id.getConnectAuthorizeButton);
            getConnectAuthorizeButton.setOnClickListener(view -> {
                String state = "App-side-State";
                _connectTool.OpenAuthorizeURL(state);
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
                    UUID GetMe_RequestNumber = UUID.fromString("73da5d8e-9fd6-11ee-8c90-0242ac120002"); // App-side-RequestNumber(UUID)
                    _connectTool.GetMe_Coroutine(GetMe_RequestNumber,new MeCallback() {
                        @Override
                        public void callbackMeInfo(MeInfo value) {
                            Log.v(TAG, "MeInfo callback : " + value.status);
                            Log.v(TAG, "MeInfo requestNumber : " + value.requestNumber);
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
                        public PurchaseOrderOneResponse callback(PurchaseOrderOneResponse value) {
                            Log.v(TAG, "PurchaseOrderOneResponse callback : " + value);
                            Toast.makeText(getApplicationContext(), "Purchase tradeNo : " + value.data.tradeNo, Toast.LENGTH_SHORT).show();
                            return value;
                        }
                    }, tradeNo);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            });

            // Open Consume URL
            OpenConsumeSPButton = findViewById(R.id.OpenConsumeSPButton);
            OpenConsumeSPButton.setOnClickListener(view -> {

                String notifyUrl = "";// NotifyUrl is a URL customized by the game developer
                String state = "Custom state";// Custom state ,
                // Step1. Set notifyUrl and state,
                _connectTool.set_purchase_notifyData(notifyUrl, state);

                int consume_spCoin = 50;
                int consume_rebate = 0;
                String orderNo = UUID.randomUUID().toString(); // orderNo is customized by the game developer
                String GameName = "Good 18 Game";
                String productName = "10 of the best diamonds";
                _connectTool.OpenConsumeSPURL(consume_spCoin, consume_rebate, orderNo, GameName, productName, notifyUrl, state);
            });

            // 查詢使用點數訂單 交易編號
            // 測試參考 : "transactionId":"T2023121500000030","orderNo":"b427a826-4101-4172-8694-9e0ee868b9ab"
            QueryConsumeSPButton = findViewById(R.id.QueryConsumeSPButton);
            QueryConsumeSPButton.setOnClickListener(view -> {
                try {
                    UUID queryConsumeSP_requestNumber = UUID.fromString( "73da5d8e-9fd6-11ee-8c90-0242ac120002"); // App-side-RequestNumber(UUID)
                    String transactionId = "b427a826-4101-4172-8694-9e0ee868b9ab";
                    _connectTool.Get_SPCoin_tx(queryConsumeSP_requestNumber,transactionId, new GetSPCoinTxCallback() {
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