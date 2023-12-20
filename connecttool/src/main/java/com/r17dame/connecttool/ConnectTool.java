package com.r17dame.connecttool;

import java.security.*;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.r17dame.connecttool.callback.ACPAYPayWithPrimeCallback;
import com.r17dame.connecttool.callback.CreatePaymentCallback;
import com.r17dame.connecttool.callback.CreatePurchaseOrderCallback;
import com.r17dame.connecttool.callback.GetPurchaseOrderListCallback;
import com.r17dame.connecttool.callback.GetSPCoinTxCallback;
import com.r17dame.connecttool.callback.GetUserCardsCallback;
import com.r17dame.connecttool.datamodel.ACPAYPayWithPrimeRequest;
import com.r17dame.connecttool.datamodel.AccountInitRequest;
import com.r17dame.connecttool.callback.ConnectCallback;
import com.r17dame.connecttool.datamodel.ConnectToken;
import com.r17dame.connecttool.callback.ConnectTokenCall;
import com.r17dame.connecttool.callback.MeCallback;
import com.r17dame.connecttool.datamodel.CreateOrderRequest;
import com.r17dame.connecttool.datamodel.CreatePurchaseOrderRequest;
import com.r17dame.connecttool.datamodel.CreateSPCoinResponse;
import com.r17dame.connecttool.datamodel.MeInfo;
import com.r17dame.connecttool.datamodel.PayWithPrimeRespone;
import com.r17dame.connecttool.datamodel.PurchaseOrder;
import com.r17dame.connecttool.callback.PurchaseOrderCallback;
import com.r17dame.connecttool.datamodel.PurchaseOrderListRequest;
import com.r17dame.connecttool.datamodel.PurchaseOrderListResponse;
import com.r17dame.connecttool.datamodel.PurchaseOrderOneRequest;
import com.r17dame.connecttool.datamodel.PurchaseOrderOneResponse;
import com.r17dame.connecttool.datamodel.PurchaseOrderRequest;
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
    public PurchaseOrderRequest purchaseOrderRequest;
    // REQUEST
    public AccountInitRequest accountRequestData;
    SharedPreferences pref;
    // Interface
    APIInterface apiInterface;
    public String referralCode = "Or16888";
    // Payment
    public String spCoinItemPriceId = "";
    public String payMethod = "1";
    public String prime = "";
    public String code;
    public String state = "";
    public String requestNumber = "";
    public String RSAstr = "";
    public String access_token = "";
    public String refresh_token = "";
    public String redirect_uri = "";
    String payMentBaseurl = "https://gamar18portal.azurewebsites.net";


    // constructors
    public ConnectTool(Context context,
                       String _state,
                       String _requestNumber,
                       String _redirect_uri,
                       String _RSAstr) throws Exception {

        this.context = context;
        this.pref = ((Activity) context).getSharedPreferences("ConnectToolP", Context.MODE_PRIVATE);

        // init
        state = _state;
        requestNumber = _requestNumber;
        redirect_uri = _redirect_uri;
        RSAstr = _RSAstr;
    }

    public void UpdateRequestNumber(String _requestNumber) {
        requestNumber = _requestNumber;
    }

    public void SendRegisterData(ConnectCallback callback) {
        apiInterface = APIClient.getHostClient().create(APIInterface.class);

        //A-Signature
        String ts = String.valueOf(System.currentTimeMillis() / 1000);
        String msg = accountRequestData.email + ts;

        MessageDigest digest = null;
        String asignHex = "";
        try {
            digest = MessageDigest.getInstance("SHA-256");
            digest.update(msg.getBytes());
            asignHex = bytesToHexString(digest.digest()).toLowerCase();

        } catch (NoSuchAlgorithmException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        Call<Void> call1 = apiInterface.sendRegister(
                ts,
                asignHex,
                accountRequestData.email,
                accountRequestData.password,
                referralCode,
                connectBasic.Game_id);

        call1.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) {
                    callback.callbackCheck(true);
                } else {
                    callback.callbackCheck(false);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                call.cancel();
                callback.callbackCheck(false);
            }
        });
    }

    public boolean SendLoginData(ConnectCallback callback) {
        apiInterface = APIClient.getHostClient().create(APIInterface.class);

        //產生A-Signature
        String ts = String.valueOf(System.currentTimeMillis() / 1000);
        String msg = accountRequestData.email + ts;

        MessageDigest digest = null;
        String asignHex = "";
        try {
            digest = MessageDigest.getInstance("SHA-256");
            digest.update(msg.getBytes());

            asignHex = bytesToHexString(digest.digest()).toLowerCase();
        } catch (NoSuchAlgorithmException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        Call<Void> call1 = apiInterface.sendLoginData(
                ts,
                asignHex,
                accountRequestData.email,
                accountRequestData.password,
                connectBasic.Game_id,
                referralCode);

        call1.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) {
                    callback.callbackCheck(true);
                } else {
                    callback.callbackCheck(false);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                call.cancel();
                callback.callbackCheck(false);
            }
        });
        return false;
    }


    public boolean SendLogout(ConnectCallback callback) {
        apiInterface = APIClient.getHostClient().create(APIInterface.class);

        //產生A-Signature
        String ts = String.valueOf(System.currentTimeMillis() / 1000);
        String msg = accountRequestData.email + ts;

        MessageDigest digest = null;
        String asignHex = "";
        try {
            digest = MessageDigest.getInstance("SHA-256");
            digest.update(msg.getBytes());

            asignHex = bytesToHexString(digest.digest()).toLowerCase();
        } catch (NoSuchAlgorithmException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        Call<Void> call1 = apiInterface.sendLoginData(
                ts,
                asignHex,
                accountRequestData.email,
                accountRequestData.password,
                connectBasic.Game_id,
                referralCode);

        call1.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) {
                    callback.callbackCheck(true);
                } else {
                    callback.callbackCheck(false);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                call.cancel();
                callback.callbackCheck(false);
            }
        });
        return false;
    }


    // utility function
    private static String bytesToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public void OpenAuthorizeURL() {
        String url = payMentBaseurl + "/connect/Authorize?response_type=code&client_id=" + connectBasic.client_id + "&redirect_uri=" + redirect_uri + "&scope=game+offline_access&state=" + state;
        Log.v(TAG, "AuthorizeURL " + url);
        Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("" + url));
        startActivity(urlIntent);
    }


    /**
     * Rounds a given number to a given number of digits.
     *
     * @param currencyCode - <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/tree/main?tab=readme-ov-file#currency-code">Currency Code table</a>
     * @see <a href="https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/README.md#open-recharge-page">說明</a>
     */
    public void OpenRechargeURL(String currencyCode, String _notifyUrl, String state) {
        String notifyUrl = (_notifyUrl.equals("")) ? "none_notifyUrl" : _notifyUrl;
        String url = payMentBaseurl + "/member/recharge/" + Uri.encode(connectBasic.X_Developer_Id) + "/" + Uri.encode(redirect_uri) + "/2/" + currencyCode + "/" + Uri.encode(notifyUrl) + "/" + Uri.encode(state) + "/" + Uri.encode(referralCode);
        Log.v(TAG, "OpenRechargeURL " + url);
        Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("" + url));
        startActivity(urlIntent);
    }

    //   // http://192.168.56.1:5173/member/consumesp/r17dame%3A%2F%2Fconnectlink/2/07d5c223-c8ba-44f5-b9db-86001886da8d/Game%20Name/ss86001886da8d/Ten%20diamonds/20/10
    public void OpenConsumeSPURL(int consume_spCoin, int consume_rebate, String orderNo, String GameName, String productName, String _notifyUrl, String state) {

        String notifyUrl = (_notifyUrl.equals("")) ? "none_notifyUrl" : _notifyUrl;
        String url = payMentBaseurl + "/member/consumesp/" + Uri.encode(connectBasic.X_Developer_Id) + "/" + Uri.encode(redirect_uri) + "/2/" + Uri.encode(connectBasic.Game_id) + "/" + Uri.encode(GameName) + "/" + Uri.encode(orderNo) + "/" + Uri.encode(productName) + "/" + consume_spCoin + "/" + consume_rebate + "/" + Uri.encode(notifyUrl) + "/" + Uri.encode(state) + "/" + Uri.encode(referralCode);

        Log.v(TAG, "OpenConsumeSPURL " + url);
        Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("" + url));
        startActivity(urlIntent);
    }

    public void OpenRegisterURL() {
        String _redirect_uri = redirect_uri + "?accountBackType=Register";
        String url = payMentBaseurl + "/account/AppRegister/" + connectBasic.Game_id + "/" + referralCode + "?returnUrl=" + Uri.encode(_redirect_uri);
        Log.v(TAG, "OpenRegisterURL " + url);
        Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("" + url));
        startActivity(urlIntent);
    }

    public void OpenLoginURL() {
        String _redirect_uri = redirect_uri + "?accountBackType=Login";
        String url = payMentBaseurl + "/account/AppLogin/" + connectBasic.Game_id + "/" + referralCode + "?returnUrl=" + Uri.encode(_redirect_uri);
        Log.v(TAG, "OpenLoginURL " + url);
        Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("" + url));
        startActivity(urlIntent);
    }

    public void OpenLogoutURL() {
        String _redirect_uri = redirect_uri + "?accountBackType=Logout";
        String url = payMentBaseurl + "/account/logout?returnUrl=" + Uri.encode(_redirect_uri);
        Log.v(TAG, "OpenLogoutURL " + url);
        Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("" + url));
        startActivity(urlIntent);
    }

    public void OpenConsumeSPResultURL(String status) {
        String purchase_state = pref.getString(String.valueOf(R.string.purchase_state), "");
        String CreatePayment_date = pref.getString(String.valueOf(R.string.CreatePayment_date), "");
        String url = payMentBaseurl + "/member/consumespresult/" + status + "/" + CreatePayment_date + "/" + purchase_state;

        Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("" + url));
        startActivity(urlIntent);
    }

    public void Open3DSURL(String url) {
        Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("" + url));
        startActivity(urlIntent);
    }

    private void startActivity(Intent intent) {
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    // INIT REQUEST
    public void CreateAccountInitData(String _email, String _password) {
        this.accountRequestData = new AccountInitRequest(
                _email, _password
        );
    }

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void GetMe_Coroutine(MeCallback callback) throws NoSuchAlgorithmException {
        apiInterface = APIClient.getGame_api_hostClient().create(APIInterface.class);

        String timestamp = Tool.getTimestamp();
        String signdata = "?RequestNumber=" + requestNumber + "&Timestamp=" + timestamp;

        String X_Signature = "";
        try {
            X_Signature = Tool.getxSignature(signdata, RSAstr);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // headers data
        access_token = pref.getString(String.valueOf(R.string.access_token), "");
        String authorization = "Bearer " + access_token;
        Call<MeInfo> call1 = apiInterface.getMeData(authorization, connectBasic.X_Developer_Id, X_Signature, requestNumber, timestamp);
        call1.enqueue(new Callback<MeInfo>() {
            @Override
            public void onResponse(Call<MeInfo> call, retrofit2.Response<MeInfo> response) {
                me = response.body();
                callback.callbackMeInfo(me);
            }

            @Override
            public void onFailure(Call<MeInfo> call, Throwable t) {
                call.cancel();
                callback.callbackMeInfo(null);
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void Get_SPCoin_tx(String transactionId, GetSPCoinTxCallback callback) throws NoSuchAlgorithmException {
        apiInterface = APIClient.getGame_api_hostClient().create(APIInterface.class);

        String timestamp = Tool.getTimestamp();
        String signdata = "?RequestNumber=" + requestNumber + "&Timestamp=" + timestamp;

        String X_Signature = "";
        try {
            X_Signature = Tool.getxSignature(signdata, RSAstr);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // headers data
        access_token = pref.getString(String.valueOf(R.string.access_token), "");
        String authorization = "Bearer " + access_token;

        String getUrl = APIClient.game_api_host + "/api/Payment/SPCoin/" + transactionId;

        Call<CreateSPCoinResponse> call1 = apiInterface.getSPCoinTx(getUrl, authorization, connectBasic.X_Developer_Id, X_Signature, requestNumber, timestamp);
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
     * Set the data to be brought back to App and Server.
     *
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void CreateSPCoinOrder(CreatePaymentCallback callback, int _spCoin, int _rebate, String _orderNo) throws NoSuchAlgorithmException {
        apiInterface = APIClient.getGame_api_hostClient().create(APIInterface.class);

        String CreatePayment_requestNumber = UUID.randomUUID().toString();
        String timestamp = Tool.getTimestamp();

        SharedPreferences.Editor editor = pref.edit();
        editor.putString(String.valueOf(R.string.CreatePayment_requestNumber), CreatePayment_requestNumber);
        editor.putString(String.valueOf(R.string.CreatePayment_orderNo), _orderNo);
        editor.putInt(String.valueOf(R.string.CreatePayment_spCoin), _spCoin);
        editor.putInt(String.valueOf(R.string.CreatePayment_rebate), _rebate);
        editor.putString(String.valueOf(R.string.CreatePayment_date), timestamp);
        editor.apply();

        // headers data
        access_token = pref.getString(String.valueOf(R.string.access_token), "");
        String authorization = "Bearer " + access_token;

        String p_X_Signature = CreateOrderRequest.get_X_Signature(pref, RSAstr, connectBasic.Game_id);
        CreateOrderRequest p_Request = CreateOrderRequest.getRequestBody(pref, connectBasic.Game_id);

        Call<CreateSPCoinResponse> call1 = apiInterface.createOrder(
                authorization,
                connectBasic.X_Developer_Id,
                p_X_Signature,
                p_Request);
        call1.enqueue(new Callback<CreateSPCoinResponse>() {
            @Override
            public void onResponse(Call<CreateSPCoinResponse> call, retrofit2.Response<CreateSPCoinResponse> response) {
                CreateSPCoinResponse payRes = response.body();
                callback.callback(payRes);
            }

            @Override
            public void onFailure(Call<CreateSPCoinResponse> call, Throwable t) {
                call.cancel();
                callback.callback(null);
            }
        });
    }

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
    public void GetUserCards(GetUserCardsCallback callback) throws NoSuchAlgorithmException {
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void ACPAYPayWithPrime(ACPAYPayWithPrimeCallback callback) throws NoSuchAlgorithmException {
        apiInterface = APIClient.getGame_api_hostClient().create(APIInterface.class);

        // headers data
        access_token = pref.getString(String.valueOf(R.string.access_token), "");
        String authorization = "Bearer " + access_token;
        Call<PayWithPrimeRespone> call1 = apiInterface.ACPAYPayWithPrime(
                authorization,
                connectBasic.X_Developer_Id,
                ACPAYPayWithPrimeRequest.get_X_Signature(pref, RSAstr), ACPAYPayWithPrimeRequest.getRequestBody(pref));
        call1.enqueue(new Callback<PayWithPrimeRespone>() {
            @Override
            public void onResponse(Call<PayWithPrimeRespone> call, retrofit2.Response<PayWithPrimeRespone> response) {
                callback.callback(response.body());
            }

            @Override
            public void onFailure(Call<PayWithPrimeRespone> call, Throwable t) {
                call.cancel();
                callback.callback(null);
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void CreatePurchaseOrder(CreatePurchaseOrderCallback callback, String _spCoinItemPriceId, String _payMethod, String _payGateway, String _prime) throws NoSuchAlgorithmException {
        apiInterface = APIClient.getGame_api_hostClient().create(APIInterface.class);

        SharedPreferences.Editor editor = pref.edit();
        access_token = pref.getString(String.valueOf(R.string.access_token), "");
        spCoinItemPriceId = _spCoinItemPriceId;
        payMethod = _payMethod;
        prime = _prime;
        requestNumber = UUID.randomUUID().toString();

        editor.putString(String.valueOf(R.string.purchase_payMethod), _payMethod);
        editor.putString(String.valueOf(R.string.purchase_prime), _prime);
        editor.putString(String.valueOf(R.string.purchase_payGateway), _payGateway);
        editor.apply();


        try {

            // headers data
            String authorization = "Bearer " + access_token;
            Call<PurchaseOrder> call1 = apiInterface.createPurchaseOrder(
                    authorization,
                    connectBasic.X_Developer_Id,
                    CreatePurchaseOrderRequest.get_X_Signature(pref, RSAstr), CreatePurchaseOrderRequest.getRequestBody(pref));
            call1.enqueue(new Callback<PurchaseOrder>() {
                @Override
                public void onResponse(Call<PurchaseOrder> call, retrofit2.Response<PurchaseOrder> response) {
                    PurchaseOrder order = response.body();

                    editor.putString(String.valueOf(R.string.purchase_purchaseOrderId), order.data.purchaseOrderId);
                    editor.putString(String.valueOf(R.string.purchase_tradeNo), order.data.tradeNo);
                    editor.putString(String.valueOf(R.string.purchase_requestNumber), requestNumber);
                    editor.apply();

                    try {
                        callback.callback(order);
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onFailure(Call<PurchaseOrder> call, Throwable t) {
                    call.cancel();
                    try {
                        callback.callback(null);
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // AccountPage Callback
    public void AccountPageEvent(String accountBackType) {
        if (accountBackType.equals("Register")) {
            OpenAuthorizeURL();
        }
        if (accountBackType.equals("Login")) {
            OpenAuthorizeURL();
        }
        if (accountBackType.equals("Logout")) {
            access_token = "";
            refresh_token = "";
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

    public void unity_sendOpenAuthorizeURL() {
        String url = APIClient.host + "/connect/Authorize?response_type=code&client_id=" + connectBasic.client_id + "&redirect_uri=" + redirect_uri + "&scope=game+offline_access&state=" + state;
        Log.v(TAG, "AuthorizeURL " + url);
        Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("" + url));
        startActivity(urlIntent);
//        try {
//            Intent myIntent = new Intent(this,Class.forName("com.r17dame.connecttool.CallBackActivity"));
//            startActivity(myIntent );
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
    }
}

