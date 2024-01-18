package com.r17dame.connecttool;

import android.content.Intent;

import static java.lang.Math.abs;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.app.Activity;
import android.webkit.WebView;
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
    public String scheme;
    String payMentBaseurl = "https://gamar18portal.azurewebsites.net";
    Boolean isRunAuthorize = false;
    Boolean isRunCompleteConsumeSP = false;
    Boolean isRunCompleteCompletePurchase = false;

    // constructors
    public ConnectTool(Context context, String _redirect_uri, String _RSAstr, String _X_Developer_Id, String _client_secret, String _Game_id) {

        this.context = context;
        this.pref = ((Activity) context).getSharedPreferences("ConnectToolP", Context.MODE_PRIVATE);

        // save basic
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(String.valueOf(R.string.redirect_uri), _redirect_uri);
        editor.putString(String.valueOf(R.string.RSAstr), _RSAstr);
        editor.putString(String.valueOf(R.string.X_Developer_Id), _X_Developer_Id);
        editor.putString(String.valueOf(R.string.client_secret), _client_secret);
        editor.putString(String.valueOf(R.string.Game_id), _Game_id);
        editor.apply();

        // get scheme
        Uri appLinkData = Uri.parse(_redirect_uri);
        scheme = appLinkData.getScheme();

        // init
        redirect_uri = _redirect_uri;
        RSAstr = _RSAstr;

        // connectBasic setting
        connectBasic = new ConnectBasic(_X_Developer_Id, _X_Developer_Id, _client_secret, _Game_id);

        // get data
        String _me = pref.getString(String.valueOf(R.string.me), "");
        if (!_me.equals("")) {
            Gson gson = new Gson();
            me = gson.fromJson(_me, MeInfo.class);
        }
        String _access_token = pref.getString(String.valueOf(R.string.access_token), "");
        if (!_access_token.equals("")) {
            access_token = _access_token;
        }
        String _refresh_token = pref.getString(String.valueOf(R.string.refresh_token), "");
        if (!_refresh_token.equals("")) {
            refresh_token = _refresh_token;
        }


        redirect_uri = payMentBaseurl + "/Account/connectlink";
    }


    private boolean _checkConstructorParametersComplete() {
        if (redirect_uri.equals("")) {
            Log.w(TAG, "No redirect_uri");
            return false;
        }
        if (RSAstr.equals("")) {
            Log.w(TAG, "No RSAstr");
            return false;
        }
        if (connectBasic.X_Developer_Id.equals("")) {
            Log.w(TAG, "No X_Developer_Id");
            return false;
        }
        if (connectBasic.client_secret.equals("")) {
            Log.w(TAG, "No client_secret");
            return false;
        }
        if (connectBasic.Game_id.equals("")) {
            Log.w(TAG, "No Game_id");
            return false;
        }
        return true;
    }

    /**
     * Obtain user information and update access_token
     *
     * @param state - Please fill in what you want to verify,state can be query through redirect_uri.
     * @see <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/README.md#openauthorizeurl">Description </a>
     */
    public void OpenAuthorizeURL(String state) {
        if (!_checkConstructorParametersComplete()) {
            return;
        }
        if (state.equals("")) {
            Log.w(TAG, "No state");
            return;
        }
        String url = payMentBaseurl + "/connect/Authorize?response_type=code&client_id=" + connectBasic.client_id + "&redirect_uri=" + redirect_uri + "&scope=game+offline_access&state=" + state;
        Log.v(TAG, "AuthorizeURL " + url);

        // Open connectWebView
        Intent intent = new Intent(context, ConnectToolWebViewActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
//        intent.putExtra("targetIntent",intent);

//        ConnectTool _tool = this;
//        intent.putExtra("tool",   _tool   );

//        Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("" + url));
//        startActivity(urlIntent);
    }


    /**
     * Open SP Coin Recharge page.
     *
     * @param currencyCode - <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/tree/main?tab=readme-ov-file#currency-code">Currency Code table</a>
     * @param _notifyUrl   - <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/README.md#notifyurl--state">NotifyUrl is a URL customized by the game developer. We will post NotifyUrl automatically when the purchase is completed</a>
     * @param state        - <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/README.md#notifyurl--state">State is customized by game developer, which will be returned to game app after purchase complete. (Deeplink QueryParameter => purchase_state)</a>
     * @see <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/README.md#open-recharge-page">Description</a>
     */
    public void OpenRechargeURL(String currencyCode, String _notifyUrl, String state) {
        if (!_checkConstructorParametersComplete()) {
            return;
        }
        if (currencyCode.equals("")) {
            Log.w(TAG, "No currencyCode");
            return;
        }
        if (state.equals("")) {
            Log.w(TAG, "No state");
            return;
        }
        String notifyUrl = (_notifyUrl.equals("")) ? "none_notifyUrl" : _notifyUrl;
        String url = payMentBaseurl +
                "/MemberRecharge/Recharge?X_Developer_Id=" +
                Uri.encode(connectBasic.X_Developer_Id) + "&accessScheme=" +
                Uri.encode(redirect_uri) + "&accessType=" +
                "2" + "&currencyCode=" +
                Tool.getCurrencyCode(currencyCode) + "&notifyUrl=" +
                Uri.encode(notifyUrl) + "&state=" +
                Uri.encode(state) + "&state=referralCode" +
                Uri.encode(referralCode);

        Log.v(TAG, "OpenRechargeURL " + url);

        if (isOverExpiresTs()) {
            // token 到期
            GetRefreshToken_Coroutine(value -> openhostPage(url));
        } else {
            openhostPage(url);
        }
    }

    private void openhostPage(String url) {
        if (url.equals("")) {
            Log.w(TAG, "No url");
            return;
        }
        Intent intent = new Intent(context, ConnectToolWebViewActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    /**
     * Open ConsumeSP page.
     *
     * @param consume_spCoin - SP Coin
     * @param orderNo        - Must be unique,Game developers customize
     * @param GameName       - GameName
     * @param productName    - Product Name
     * @param _notifyUrl     - <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/README.md#notifyurl--state">NotifyUrl is a URL customized by the game developer. We will post NotifyUrl automatically when the purchase is completed</a>
     * @param state          - <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/README.md#notifyurl--state">State is customized by game developer, which will be returned to game app after purchase complete. (Deeplink QueryParameter => purchase_state)</a>
     * @see <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/README.md#open-consumesp-page">Description</a>
     */
    public void OpenConsumeSPURL(int consume_spCoin, String orderNo, String GameName, String productName, String _notifyUrl, String state, String requestNumber) {
        if (!_checkConstructorParametersComplete()) {
            return;
        }
        if (orderNo.equals("")) {
            Log.w(TAG, "No OrderNo");
            return;
        }
        if (GameName.equals("")) {
            Log.w(TAG, "No GameName");
            return;
        }
        if (productName.equals("")) {
            Log.w(TAG, "No productName");
            return;
        }
        if (state.equals("")) {
            Log.w(TAG, "No state");
            return;
        }
        if (requestNumber.equals("")) {
            Log.w(TAG, "No state");
            return;
        }


        SharedPreferences.Editor editor = pref.edit();
        editor.putString(String.valueOf(R.string.requestNumber), requestNumber);
        editor.apply();

        String notifyUrl = (_notifyUrl.equals("")) ? "none_notifyUrl" : _notifyUrl;
        String url = payMentBaseurl + "/member/consumesp?xDeveloperId=" +
                Uri.encode(connectBasic.X_Developer_Id) + "&accessScheme=" +
                Uri.encode(redirect_uri) + "&accessType=" +
                "2" + "&gameId=" +
                Uri.encode(connectBasic.Game_id) + "&gameName=" +
                Uri.encode(GameName) + "&orderNo=" +
                Uri.encode(orderNo) + "&productName=" +
                Uri.encode(productName) + "&consumeSpCoin=" +
                abs(consume_spCoin) + "&consumeRebate=" +
                abs(0) + "&notifyUrl=" +
                Uri.encode(notifyUrl) + "&state=" +
                Uri.encode(state) + "&referralCode=" +
                Uri.encode(referralCode);

        Log.v(TAG, "OpenConsumeSPURL " + url);

        if (isOverExpiresTs()) {
            // token 到期
            GetRefreshToken_Coroutine(value -> openhostPage(url));
        } else {
            openhostPage(url);
        }
    }

    /**
     * Open Register page.
     *
     * @see <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/README.md#openregisterurl-openloginurl">Description</a>
     */
    public void OpenRegisterURL() {
        if (!_checkConstructorParametersComplete()) {
            return;
        }

        SharedPreferences.Editor editor = pref.edit();
        editor.putString(String.valueOf(R.string.access_token), "");
        editor.putString(String.valueOf(R.string.refresh_token), "");
        editor.apply();

        // payMentBaseurl = "https://4ed9-114-24-106-49.ngrok-free.app";
        String _redirect_uri = redirect_uri + "?accountBackType=Register";
        String url = payMentBaseurl + "/account/AppRegister/" + connectBasic.Game_id + "/" + referralCode + "?returnUrl=" + Uri.encode(_redirect_uri);
        Log.v(TAG, "OpenRegisterURL " + url);


        // Open WEbView
        Intent intent = new Intent(context, ConnectToolWebViewActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);

//        Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("" + url));
//        startActivity(urlIntent);
    }


    /**
     * 切換帳號
     *
     * @see <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/README.md#openregisterurl-openloginurl">Description</a>
     */
    public void SwitchAccountURL() {
        OpenLoginURL();
    }

    /**
     * Open Login page.
     *
     * @see <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/README.md#openregisterurl-openloginurl">Description</a>
     */
    public void OpenLoginURL() {
        if (!_checkConstructorParametersComplete()) {
            return;
        }

        SharedPreferences.Editor editor = pref.edit();
        editor.putString(String.valueOf(R.string.access_token), "");
        editor.putString(String.valueOf(R.string.refresh_token), "");
        editor.apply();

        String _redirect_uri = redirect_uri + "?accountBackType=Login";
        String url = payMentBaseurl + "/account/AppLogin/" + connectBasic.Game_id + "/" + referralCode + "?returnUrl=" + Uri.encode(_redirect_uri);
        Log.v(TAG, "OpenLoginURL " + url);

        // Open connectWebView
        Intent intent = new Intent(context, ConnectToolWebViewActivity.class);
        intent.putExtra("url", url);

        startActivity(intent);

//        Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("" + url));
//        startActivity(urlIntent);
    }

    private void startActivity(Intent intent) {
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    /**
     * Get access_tokn.
     *
     * @param connectTokenCall -
     * @see <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/README.md#openregisterurl-openloginurl">Description</a>
     */
    public void GetConnectToken_Coroutine(String _code, ConnectTokenCall connectTokenCall) {
        if (!_checkConstructorParametersComplete()) {
            return;
        }
        apiInterface = APIClient.getHostClient().create(APIInterface.class);

        Call<ConnectToken> call1 = apiInterface.getConnectToken(_code, connectBasic.client_id, connectBasic.client_secret, redirect_uri, "authorization_code");
        call1.enqueue(new Callback<ConnectToken>() {
            @Override
            public void onResponse(Call<ConnectToken> call, Response<ConnectToken> response) {
                tokenData = response.body();
                access_token = tokenData.access_token;
                refresh_token = tokenData.refresh_token;

                // 儲存時間
                saveExpiresTs(tokenData.expires_in);

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


    private Boolean isOverExpiresTs() {

        String expiresTs = pref.getString(String.valueOf(R.string.expiresTs), "");
        if (expiresTs.equals("")) {
            return true;
        } else {
            Double expiresTsDouble = Double.parseDouble(expiresTs);

            Long currentTs = System.currentTimeMillis() / 1000;
            Double currentTsDouble = Double.parseDouble(currentTs.toString());

            if (currentTsDouble > expiresTsDouble) {
                return true;
            } else {
                return false;
            }
        }

    }


    private void saveExpiresTs(String tokenData_expires_in) {
        // 取得最後的時間到期日，儲存
        Double expires_in = Double.parseDouble(tokenData_expires_in);
        expires_in = (expires_in) * 0.9;
        Long currentTs = System.currentTimeMillis() / 1000;
        String currentTssString = currentTs.toString();
        Double currentTsDouble = Double.parseDouble(currentTssString);
        Double expiresTs = currentTsDouble + expires_in;
        String expiresTsString = expiresTs.toString();

        SharedPreferences.Editor editor = pref.edit();
        editor.putString(String.valueOf(R.string.expiresTs), expiresTsString);
        editor.apply();
    }

    public void GetRefreshToken_Coroutine(ConnectTokenCall connectTokenCall) {
        if (!_checkConstructorParametersComplete()) {
            return;
        }
        apiInterface = APIClient.getHostClient().create(APIInterface.class);

        Call<ConnectToken> call1 = apiInterface.getRefreshTokenData(refresh_token, connectBasic.client_id, connectBasic.client_secret, redirect_uri, "refresh_token");
        call1.enqueue(new Callback<ConnectToken>() {
            @Override
            public void onResponse(Call<ConnectToken> call, Response<ConnectToken> response) {
                tokenData = response.body();
                access_token = tokenData.access_token;
                refresh_token = tokenData.refresh_token;

                // 儲存時間
                saveExpiresTs(tokenData.expires_in);

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
     *
     * @param _GetMeRequestNumber - App-side-RequestNumber(UUID)
     * @param callback            -
     * @see <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/README.md#query-consumesp-by-transactionid">說明</a>
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void GetMe_Coroutine(UUID _GetMeRequestNumber, MeCallback callback) throws NoSuchAlgorithmException {
        if (!_checkConstructorParametersComplete()) {
            return;
        }
        if (isOverExpiresTs()) {
            // token 到期
            GetRefreshToken_Coroutine(value -> getMeData(_GetMeRequestNumber, callback));
        } else {
            getMeData(_GetMeRequestNumber, callback);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getMeData(UUID _GetMeRequestNumber, MeCallback callback) throws NoSuchAlgorithmException {

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
     *
     * @param queryConsumeSP_requestNumber - App-side-RequestNumber(UUID)
     * @param transactionId                - consumption id.
     * @param callback                     - Consumption Response
     * @see <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/README.md#query-consumesp-by-transactionid">說明</a>
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void Get_SPCoin_tx(UUID queryConsumeSP_requestNumber, String transactionId, GetSPCoinTxCallback callback) throws NoSuchAlgorithmException {
        if (!_checkConstructorParametersComplete()) {
            return;
        }
        if (transactionId.equals("")) {
            Log.w(TAG, "No transactionId");
            return;
        }
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
     *
     * @param notifyUrl - NotifyUrl is a URL customized by the game developer.
     * @param state     -
     * @see <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/README.md#open-recharge-page">說明</a>
     */
    public void set_purchase_notifyData(String notifyUrl, String state) {
        if (!_checkConstructorParametersComplete()) {
            return;
        }
        if (state.equals("")) {
            Log.w(TAG, "No state");
            return;
        }
        SharedPreferences.Editor editor = pref.edit();

        // notifyUrl
        editor.putString(String.valueOf(R.string.purchase_notifyUrl), notifyUrl);

        // state
        editor.putString(String.valueOf(R.string.purchase_state), state);

        editor.apply();
    }

    /**
     * Get user PurchaseOrder list
     *
     * @param callback -
     * @see <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/README.md#getpurchaseorderlist">說明</a>
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void GetPurchaseOrderList(GetPurchaseOrderListCallback callback) throws NoSuchAlgorithmException {
        if (!_checkConstructorParametersComplete()) {
            return;
        }
        apiInterface = APIClient.getGame_api_hostClient().create(APIInterface.class);

        SharedPreferences.Editor editor = pref.edit();
        String purchaseOrderList_requestNumber = UUID.randomUUID().toString();
        editor.putString(String.valueOf(R.string.purchaseOrderList_requestNumber), purchaseOrderList_requestNumber);
        editor.apply();

        // headers data
        access_token = pref.getString(String.valueOf(R.string.access_token), "");
        String authorization = "Bearer " + access_token;

        Call<PurchaseOrderListResponse> call1 = apiInterface.GetPurchaseOrderList(authorization, connectBasic.X_Developer_Id, PurchaseOrderListRequest.get_X_Signature(pref, RSAstr), PurchaseOrderListRequest.getRequestBody(pref));
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
     *
     * @param callback -
     * @param tradeNo  -
     * @see <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/README.md#getpurchaseorderone">說明</a>
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void GetPurchaseOrderOne(PurchaseOrderCallback callback, String tradeNo) throws NoSuchAlgorithmException {
        if (!_checkConstructorParametersComplete()) {
            return;
        }
        if (tradeNo.equals("")) {
            Log.w(TAG, "No tradeNo");
            return;
        }

        apiInterface = APIClient.getGame_api_hostClient().create(APIInterface.class);

        SharedPreferences.Editor editor = pref.edit();
        String purchaseOrderOne_requestNumber = UUID.randomUUID().toString();
        editor.putString(String.valueOf(R.string.purchaseOrderOne_requestNumber), purchaseOrderOne_requestNumber);
        editor.putString(String.valueOf(R.string.purchaseOrderOne_tradeNo), tradeNo);
        editor.apply();

        // headers data
        access_token = pref.getString(String.valueOf(R.string.access_token), "");
        String authorization = "Bearer " + access_token;

        Call<PurchaseOrderOneResponse> call1 = apiInterface.GetPurchaseOrderOne(authorization, connectBasic.X_Developer_Id, PurchaseOrderOneRequest.get_X_Signature(pref, RSAstr), PurchaseOrderOneRequest.getRequestBody(pref));
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
        if (!_checkConstructorParametersComplete()) {
            return;
        }
        apiInterface = APIClient.getGame_api_hostClient().create(APIInterface.class);

        String getUserCards_requestNumber = UUID.randomUUID().toString();

        SharedPreferences.Editor editor = pref.edit();
        editor.putString(String.valueOf(R.string.getUserCards_requestNumber), getUserCards_requestNumber);
        editor.apply();

        // headers data
        access_token = pref.getString(String.valueOf(R.string.access_token), "");
        String authorization = "Bearer " + access_token;

        Call<UserCard[]> call1 = apiInterface.GetUserCards(authorization, connectBasic.X_Developer_Id, UserCardRequest.get_X_Signature(pref, RSAstr), UserCardRequest.getRequestBody(pref));
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
    public void AccountPageEvent(String authorizeState, String accountBackType) {
        if (accountBackType.equals("Register")) {
            OpenAuthorizeURL(authorizeState);
        }
        if (accountBackType.equals("Login")) {
            OpenAuthorizeURL(authorizeState);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void appLinkDataCallBack_CompletePurchase(Intent intent, PurchaseOrderCallback appLinkcallback) {
        if (isRunCompleteCompletePurchase.equals(false)) {
            isRunCompleteCompletePurchase = true;
            String TradeNo = intent.getStringExtra("TradeNo");
            // Complete purchase of SP Coin
            // 取得購買 SPCoin 資料
            try {
                GetPurchaseOrderOne(value -> {
                    isRunCompleteCompletePurchase = false;
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
    public void appLinkDataCallBack_CompleteConsumeSP(Intent intent, UUID queryConsumeSP_requestNumber, GetSPCoinTxCallback appLinkcallback) {
        if (isRunCompleteConsumeSP.equals(false)) {
            isRunCompleteConsumeSP = true;
            String consume_transactionId = intent.getStringExtra("consume_transactionId");
            String consume_status = intent.getStringExtra("consume_status");
            assert consume_status != null;
            if (consume_status.equals("Completed")) {
                // 取得消費 SPCoin 資料
                try {
                    Get_SPCoin_tx(queryConsumeSP_requestNumber, consume_transactionId, value -> {
                        isRunCompleteConsumeSP = false;
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
    public void appLinkDataCallBack_OpenAuthorize(Intent intent, String _state, UUID GetMe_RequestNumber, AuthorizeCallback authCallback) {

        if (isRunAuthorize.equals(false)) {
            isRunAuthorize = true;
            code = intent.getStringExtra("code");
            String _code = intent.getStringExtra("code");
            GetConnectToken_Coroutine(_code, connectTokenvalue -> {
                String _access_token = connectTokenvalue.access_token;
                GetMe_Coroutine(GetMe_RequestNumber, value -> {
                    isRunAuthorize = false;
                    AuthorizeInfo _auth = new AuthorizeInfo(value, connectTokenvalue, _state, _access_token);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void appLinkData(String url) {
        Log.v(TAG, "appLinkData url " + url);
        Uri appLinkData = Uri.parse(url);
        if (appLinkData != null && appLinkData.isHierarchical()) {
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
                AccountPageEvent(state, accountBackType);
            }
        }
    }

}

