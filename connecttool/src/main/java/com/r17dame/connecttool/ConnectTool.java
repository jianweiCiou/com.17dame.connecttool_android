package com.r17dame.connecttool;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.r17dame.connecttool.callback.AuthorizeCallback;
import com.r17dame.connecttool.callback.GetPurchaseOrderListCallback;
import com.r17dame.connecttool.callback.GetSPCoinTxCallback;
import com.r17dame.connecttool.callback.GetUserCardsCallback;
import com.r17dame.connecttool.datamodel.AuthorizeInfo;
import com.r17dame.connecttool.datamodel.ConnectToken;
import com.r17dame.connecttool.callback.ConnectTokenCall;
import com.r17dame.connecttool.callback.MeCallback;
import com.r17dame.connecttool.datamodel.CreateSPCoinResponse;
import com.r17dame.connecttool.datamodel.MeInfo;
import com.r17dame.connecttool.callback.PurchaseOrderCallback;
import com.r17dame.connecttool.datamodel.PurchaseOrderListRequest;
import com.r17dame.connecttool.datamodel.PurchaseOrderListResponse;
import com.r17dame.connecttool.datamodel.PurchaseOrderOneRequest;
import com.r17dame.connecttool.datamodel.PurchaseOrderOneResponse;
import com.r17dame.connecttool.datamodel.Tool;
import com.r17dame.connecttool.datamodel.UserCard;
import com.r17dame.connecttool.datamodel.UserCardRequest;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConnectTool {
    private final static String TAG = "ConnectTool test";
    private static Activity unityActivity;
    Context context;
    Intent i;
    // basic info
    public ConnectBasic connectBasic;
    public ConnectToken tokenData;
    // User
    public MeInfo me;
    SharedPreferences pref;
    // Interface
    APIInterface apiInterface;
    public String referralCode = "Or16888";
    public String code;
    public String RSAstr;
    public String access_token = "";
    public String refresh_token = "";
    public String redirect_uri;
    String payMentBaseurl = "https://gamar18portal.azurewebsites.net";

    // constructors
    public ConnectTool(Context context,
                       String _redirect_uri,
                       String _RSAstr,
                       String _X_Developer_Id,
                       String _client_secret,
                       String _Game_id) {

        this.context = context;
        this.pref = ((Activity) context).getSharedPreferences("ConnectToolP", Context.MODE_PRIVATE);

        // init
        redirect_uri = _redirect_uri;
        RSAstr = _RSAstr;

        connectBasic = new ConnectBasic(
                _X_Developer_Id,
                _X_Developer_Id,
                _client_secret,
                _Game_id);

        String _me = pref.getString(String.valueOf(R.string.me), "");
        if(!_me.equals(  "")){
            Gson gson = new Gson();
            me = gson.fromJson(_me, MeInfo.class);
        }

        String _access_token = pref.getString(String.valueOf(R.string.access_token), "");
        if(!_access_token.equals(  "")){
            access_token = _access_token;
        }

        String _refresh_token = pref.getString(String.valueOf(R.string.refresh_token), "");
        if(!_refresh_token.equals(  "")){
            refresh_token = _refresh_token;
        }
    }

    /**
     * Obtain user information and update access_token
     * @param state - Please fill in what you want to verify,state can be query through redirect_uri.
     * @see <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/README.md#openauthorizeurl">Description </a>
     */
    public void OpenAuthorizeURL(String state) {
        String url = payMentBaseurl + "/connect/Authorize?response_type=code&client_id=" + connectBasic.client_id + "&redirect_uri=" + redirect_uri + "&scope=game+offline_access&state=" + state;
        Log.v(TAG, "AuthorizeURL " + url);
        Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("" + url));
        startActivity(urlIntent);
    }

    /**
     * Open SP Coin Recharge page.
     * @param currencyCode - <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/tree/main?tab=readme-ov-file#currency-code">Currency Code table</a>
     * @param _notifyUrl - <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/README.md#notifyurl--state">NotifyUrl is a URL customized by the game developer. We will post NotifyUrl automatically when the purchase is completed</a>
     * @param state - <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/README.md#notifyurl--state">State is customized by game developer, which will be returned to game app after purchase complete. (Deeplink QueryParameter => purchase_state)</a>
     * @see <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/README.md#open-recharge-page">Description</a>
     */
    public void OpenRechargeURL(String currencyCode, String _notifyUrl, String state) {
        String notifyUrl = (_notifyUrl.equals("")) ? "none_notifyUrl" : _notifyUrl;
        String url = payMentBaseurl + "/member/recharge/" + Uri.encode(connectBasic.X_Developer_Id) + "/" + Uri.encode(redirect_uri) + "/2/" + currencyCode + "/" + Uri.encode(notifyUrl) + "/" + Uri.encode(state) + "/" + Uri.encode(referralCode);
        Log.v(TAG, "OpenRechargeURL " + url);
        Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("" + url));
        startActivity(urlIntent);
    }

    /**
     * Open ConsumeSP page.
     * @param consume_spCoin - SP Coin
     * @param consume_rebate - Rebate
     * @param orderNo - Must be unique,Game developers customize
     * @param GameName - GameName
     * @param productName - Product Name
     * @param _notifyUrl - <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/README.md#notifyurl--state">NotifyUrl is a URL customized by the game developer. We will post NotifyUrl automatically when the purchase is completed</a>
     * @param state - <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/README.md#notifyurl--state">State is customized by game developer, which will be returned to game app after purchase complete. (Deeplink QueryParameter => purchase_state)</a>
     * @see <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/README.md#open-consumesp-page">Description</a>
     */
    public void OpenConsumeSPURL( int consume_spCoin, int consume_rebate, String orderNo, String GameName, String productName, String _notifyUrl, String state) {

        String notifyUrl = (_notifyUrl.equals("")) ? "none_notifyUrl" : _notifyUrl;
        String url = payMentBaseurl + "/member/consumesp/" +
                Uri.encode(connectBasic.X_Developer_Id) + "/" +
                Uri.encode(redirect_uri) + "/2/" +
                Uri.encode(connectBasic.Game_id) + "/" +
                Uri.encode(GameName) + "/" + Uri.encode(orderNo) + "/" +
                Uri.encode(productName) + "/" +
                consume_spCoin + "/" +
                consume_rebate + "/" +
                Uri.encode(notifyUrl) + "/" +
                Uri.encode(state) + "/" +
                Uri.encode(referralCode) ;

        Log.v(TAG, "OpenConsumeSPURL " + url);
        Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("" + url));
        startActivity(urlIntent);
    }

    /**
     * Open Register page.
      * @see <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/README.md#openregisterurl-openloginurl">Description</a>
     */
    public void OpenRegisterURL() {
        String _redirect_uri = redirect_uri + "?accountBackType=Register";
        String url = payMentBaseurl + "/account/AppRegister/" + connectBasic.Game_id + "/" + referralCode + "?returnUrl=" + Uri.encode(_redirect_uri);
        Log.v(TAG, "OpenRegisterURL " + url);
        Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("" + url));
        startActivity(urlIntent);
    }

    /**
     * Open Login page.
     * @see <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/README.md#openregisterurl-openloginurl">Description</a>
     */
    public void OpenLoginURL() {
        String _redirect_uri = redirect_uri + "?accountBackType=Login";
        String url = payMentBaseurl + "/account/AppLogin/" + connectBasic.Game_id + "/" + referralCode + "?returnUrl=" + Uri.encode(_redirect_uri);
        Log.v(TAG, "OpenLoginURL " + url);
        Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("" + url));
        startActivity(urlIntent);
    }

    private void startActivity(Intent intent) {
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    /**
     * Get access_tokn.
     * @param connectTokenCall -
     * @see <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/README.md#openregisterurl-openloginurl">Description</a>
     */
    public void GetConnectToken_Coroutine(ConnectTokenCall connectTokenCall) {
        apiInterface = APIClient.getHostClient().create(APIInterface.class);

        Call<ConnectToken> call1 = apiInterface.getConnectToken(code,
                connectBasic.client_id,
                connectBasic.client_secret,
                redirect_uri,
                "authorization_code");
        call1.enqueue(new Callback<ConnectToken>() {
            @Override
            public void onResponse(Call<ConnectToken> call, Response<ConnectToken> response) {
                tokenData = response.body();
                access_token = tokenData.access_token;
                refresh_token = tokenData.refresh_token;

                SharedPreferences.Editor editor = pref.edit();
                editor.putString(String.valueOf(R.string.access_token), tokenData.access_token);
                editor.putString(String.valueOf(R.string.refresh_token), tokenData.refresh_token);
                editor.apply();

                try {
                    connectTokenCall.callbackConnectToken(tokenData);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(Call<ConnectToken> call, Throwable t) {
                call.cancel();
                try {
                    connectTokenCall.callbackConnectToken(null);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void GetRefreshToken_Coroutine(ConnectTokenCall connectTokenCall) {
        apiInterface = APIClient.getHostClient().create(APIInterface.class);

        Call<ConnectToken> call1 = apiInterface.getRefreshTokenData(refresh_token,
                connectBasic.client_id,
                connectBasic.client_secret,
                redirect_uri,
                "refresh_token");
        call1.enqueue(new Callback<ConnectToken>() {
            @Override
            public void onResponse(Call<ConnectToken> call, Response<ConnectToken> response) {
                tokenData = response.body();
                access_token = tokenData.access_token;
                refresh_token = tokenData.refresh_token;

                SharedPreferences.Editor editor = pref.edit();
                editor.putString(String.valueOf(R.string.access_token), tokenData.access_token);
                editor.putString(String.valueOf(R.string.refresh_token), tokenData.refresh_token);
                editor.apply();

                try {
                    connectTokenCall.callbackConnectToken(tokenData);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(Call<ConnectToken> call, Throwable t) {
                call.cancel();
                try {
                    connectTokenCall.callbackConnectToken(null);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * Get MeInfo
     * @param _GetMeRequestNumber - App-side-RequestNumber(UUID)
     * @param callback -
     * @see <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/README.md#query-consumesp-by-transactionid">說明</a>
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void GetMe_Coroutine(UUID _GetMeRequestNumber,MeCallback callback) throws NoSuchAlgorithmException {
        apiInterface = APIClient.getGame_api_hostClient().create(APIInterface.class);

        String timestamp = Tool.getTimestamp();
        String signdata = "?RequestNumber=" + _GetMeRequestNumber + "&Timestamp=" + timestamp;

        String X_Signature;
        try {
            X_Signature = Tool.getxSignature(signdata, RSAstr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // headers data
        access_token = pref.getString(String.valueOf(R.string.access_token), "");
        String authorization = "Bearer " + access_token;
        Call<MeInfo> call1 = apiInterface.getMeData(authorization, connectBasic.X_Developer_Id, X_Signature, _GetMeRequestNumber.toString(), timestamp);
        call1.enqueue(new Callback<MeInfo>() {
            @Override
            public void onResponse(Call<MeInfo> call, retrofit2.Response<MeInfo> response) {
                me = response.body();

                Gson gson = new Gson();
                String json = gson.toJson(me);

                SharedPreferences.Editor editor = pref.edit();
                editor.putString(String.valueOf(R.string.me), json);
                editor.apply();

                callback.callbackMeInfo(me);
            }

            @Override
            public void onFailure(Call<MeInfo> call, Throwable t) {
                call.cancel();
                callback.callbackMeInfo(null);
            }
        });
    }

    /**
     * Get Consumption by transactionId.
     * @param queryConsumeSP_requestNumber - App-side-RequestNumber(UUID)
     * @param transactionId - consumption id.
     * @param callback - Consumption Response
     * @see <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/README.md#query-consumesp-by-transactionid">說明</a>
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void Get_SPCoin_tx(UUID queryConsumeSP_requestNumber,String transactionId, GetSPCoinTxCallback callback) throws NoSuchAlgorithmException {
        apiInterface = APIClient.getGame_api_hostClient().create(APIInterface.class);

        String timestamp = Tool.getTimestamp();
        String signdata = "?RequestNumber=" + queryConsumeSP_requestNumber + "&Timestamp=" + timestamp;

        String X_Signature;
        try {
            X_Signature = Tool.getxSignature(signdata, RSAstr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // headers data
        access_token = pref.getString(String.valueOf(R.string.access_token), "");
        String authorization = "Bearer " + access_token;

        String getUrl = APIClient.game_api_host + "/api/Payment/SPCoin/" + transactionId;

        Call<CreateSPCoinResponse> call1 = apiInterface.getSPCoinTx(getUrl, authorization, connectBasic.X_Developer_Id, X_Signature, queryConsumeSP_requestNumber.toString(), timestamp);
        call1.enqueue(new Callback<CreateSPCoinResponse>() {
            @Override
            public void onResponse(Call<CreateSPCoinResponse> call, retrofit2.Response<CreateSPCoinResponse> response) {
                CreateSPCoinResponse tx = response.body();
                callback.callback(tx);
            }

            @Override
            public void onFailure(Call<CreateSPCoinResponse> call, Throwable t) {
                call.cancel();
                callback.callback(null);
            }
        });
    }

    /**
     * Set notifyUrl and state
     * @param notifyUrl - NotifyUrl is a URL customized by the game developer.
     * @param state     -
     * @see <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/README.md#open-recharge-page">說明</a>
     */
    public void set_purchase_notifyData(String notifyUrl, String state) {
        SharedPreferences.Editor editor = pref.edit();

        // notifyUrl
        editor.putString(String.valueOf(R.string.purchase_notifyUrl), notifyUrl);

        // state
        editor.putString(String.valueOf(R.string.purchase_state), state);

        editor.apply();
    }

    /**
     * Get user PurchaseOrder list
     * @param callback     -
     * @see <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/README.md#getpurchaseorderlist">說明</a>
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void GetPurchaseOrderList(GetPurchaseOrderListCallback callback) throws NoSuchAlgorithmException {
        apiInterface = APIClient.getGame_api_hostClient().create(APIInterface.class);

        SharedPreferences.Editor editor = pref.edit();
        String purchaseOrderList_requestNumber = UUID.randomUUID().toString();
        editor.putString(String.valueOf(R.string.purchaseOrderList_requestNumber), purchaseOrderList_requestNumber);
        editor.apply();

        // headers data
        access_token = pref.getString(String.valueOf(R.string.access_token), "");
        String authorization = "Bearer " + access_token;

        Call<PurchaseOrderListResponse> call1 = apiInterface.GetPurchaseOrderList(
                authorization,
                connectBasic.X_Developer_Id,
                PurchaseOrderListRequest.get_X_Signature(pref, RSAstr),
                PurchaseOrderListRequest.getRequestBody(pref));
        call1.enqueue(new Callback<PurchaseOrderListResponse>() {
            @Override
            public void onResponse(Call<PurchaseOrderListResponse> call, retrofit2.Response<PurchaseOrderListResponse> response) {
                callback.callback(response.body());
            }

            @Override
            public void onFailure(Call<PurchaseOrderListResponse> call, Throwable t) {
                call.cancel();
                callback.callback(null);
            }
        });
    }

    /**
     * Get a single SP Coin order via tradeNo.
     * @param callback -
     * @param tradeNo -
     * @see <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/README.md#getpurchaseorderone">說明</a>
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void GetPurchaseOrderOne(PurchaseOrderCallback callback, String tradeNo) throws NoSuchAlgorithmException {
        apiInterface = APIClient.getGame_api_hostClient().create(APIInterface.class);

        SharedPreferences.Editor editor = pref.edit();
        String purchaseOrderOne_requestNumber = UUID.randomUUID().toString();
        editor.putString(String.valueOf(R.string.purchaseOrderOne_requestNumber), purchaseOrderOne_requestNumber);
        editor.putString(String.valueOf(R.string.purchaseOrderOne_tradeNo), tradeNo);
        editor.apply();

        // headers data
        access_token = pref.getString(String.valueOf(R.string.access_token), "");
        String authorization = "Bearer " + access_token;

        Call<PurchaseOrderOneResponse> call1 = apiInterface.GetPurchaseOrderOne(
                authorization,
                connectBasic.X_Developer_Id,
                PurchaseOrderOneRequest.get_X_Signature(pref, RSAstr),
                PurchaseOrderOneRequest.getRequestBody(pref));
        call1.enqueue(new Callback<PurchaseOrderOneResponse>() {
            @Override
            public void onResponse(Call<PurchaseOrderOneResponse> call, retrofit2.Response<PurchaseOrderOneResponse> response) {
                callback.callback(response.body());
            }

            @Override
            public void onFailure(Call<PurchaseOrderOneResponse> call, Throwable t) {
                call.cancel();
                callback.callback(null);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void GetUserCards(GetUserCardsCallback callback) {
        apiInterface = APIClient.getGame_api_hostClient().create(APIInterface.class);

        String getUserCards_requestNumber = UUID.randomUUID().toString();

        SharedPreferences.Editor editor = pref.edit();
        editor.putString(String.valueOf(R.string.getUserCards_requestNumber), getUserCards_requestNumber);
        editor.apply();

        // headers data
        access_token = pref.getString(String.valueOf(R.string.access_token), "");
        String authorization = "Bearer " + access_token;

        Call<UserCard[]> call1 = apiInterface.GetUserCards(
                authorization,
                connectBasic.X_Developer_Id,
                UserCardRequest.get_X_Signature(pref, RSAstr),
                UserCardRequest.getRequestBody(pref));
        call1.enqueue(new Callback<UserCard[]>() {
            @Override
            public void onResponse(Call<UserCard[]> call, retrofit2.Response<UserCard[]> response) {
                callback.callback(response.body());
            }

            @Override
            public void onFailure(Call<UserCard[]> call, Throwable t) {
                call.cancel();
                callback.callback(null);
            }
        });
    }

    // AccountPage Callback
    public void AccountPageEvent(String authorizeState,String accountBackType) {
        if (accountBackType.equals("Register")) {
            OpenAuthorizeURL(authorizeState);
        }
        if (accountBackType.equals("Login")) {
            OpenAuthorizeURL(authorizeState);
        }
        if (accountBackType.equals("Logout")) {
            access_token = "";
            refresh_token = "";
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void appLinkDataCallBack_CompletePurchase(Uri appLinkData,PurchaseOrderCallback appLinkcallback ) {
        // Complete purchase of SP Coin
        if (appLinkData.getQueryParameterNames().contains("purchase_state")) {
            String purchase_state = appLinkData.getQueryParameter("purchase_state");
            String TradeNo = appLinkData.getQueryParameter("TradeNo");
            String PurchaseOrderId = appLinkData.getQueryParameter("PurchaseOrderId");
            Log.v(TAG, "purchase_state :" + purchase_state);
            Log.v(TAG, "TradeNo :" + TradeNo);
            Log.v(TAG, "PurchaseOrderId :" + PurchaseOrderId);

            // 取得購買 SPCoin 資料
            try {
                GetPurchaseOrderOne(value -> {
                    Log.v(TAG, "PurchaseOrderOneResponse callback : " + value);

                    appLinkcallback.callback(value);
                    return value;
                }, TradeNo);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public void appLinkDataCallBack_CompleteConsumeSP(Uri appLinkData,GetSPCoinTxCallback appLinkcallback ) {
        if (appLinkData.getQueryParameterNames().contains("consume_transactionId")) {
            String consume_transactionId = appLinkData.getQueryParameter("consume_transactionId");
            String consume_status = appLinkData.getQueryParameter("consume_status");

            assert consume_status != null;
            if(consume_status.equals("success")){
                Log.v(TAG, "consume_transactionId :" + consume_transactionId);
                // get Consume Response example

                // 取得消費 SPCoin 資料
                try {
                    UUID queryConsumeSP_requestNumber = UUID.fromString( "73da5d8e-9fd6-11ee-8c90-0242ac120002"); // App-side-RequestNumber(UUID)
                    Get_SPCoin_tx(queryConsumeSP_requestNumber, consume_transactionId, value -> {
                        Log.v(TAG, "SPCoinTxResponse callback : " + value.data.orderStatus);

                        Gson gson = new Gson();

                        String CreateSPCoinResponseString = gson.toJson(value);
                        Log.v(TAG, "CreateSPCoinResponseString   : " + CreateSPCoinResponseString);

                        appLinkcallback.callback(value);
                     });
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void appLinkDataCallBack_OpenAuthorize(Uri appLinkData,UUID GetMe_RequestNumber ,AuthorizeCallback authCallback ) {
        if (appLinkData.getQueryParameterNames().contains("code") ) {
            code = appLinkData.getQueryParameter("code");
            String _state = appLinkData.getQueryParameter("state");

             GetConnectToken_Coroutine(connectTokenvalue -> {
                 String _access_token = connectTokenvalue.access_token;

                 GetMe_Coroutine(GetMe_RequestNumber, value -> {
                     AuthorizeInfo _auth = new AuthorizeInfo(value,connectTokenvalue,_state,_access_token);
                     authCallback.authCallback(_auth);
                 });
             });
        }

    }


    /*
     * Update for Unity 2018
     * */
    public static void receiveUnityActivity(Activity tActivity) {
        unityActivity = tActivity;
    }

    public void Toast(String msg) {
        Toast.makeText(unityActivity, msg, Toast.LENGTH_SHORT).show();
    }

}

