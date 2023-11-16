package com.r17dame.connecttool;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;
import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.app.Activity;

import com.r17dame.connecttool.datamodel.AccountInitRequest;
import com.r17dame.connecttool.datamodel.ConnectCallback;
import com.r17dame.connecttool.datamodel.ConnectToken;
import com.r17dame.connecttool.datamodel.ConnectTokenCall;
import com.r17dame.connecttool.datamodel.MeCallback;
import com.r17dame.connecttool.datamodel.MeInfo;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConnectTool  {
    Context context;
    Intent i;

    public String code;
    public String state = "";
    public String requestNumber = "";
    public String RSAstr = "";
    public String access_token = "";
    public String refresh_token = "";
    public String redirect_uri = "";

    // basic info
    public ConnectBasic connectBasic;
    public ConnectToken tokenData;
    // User
    public MeInfo me;
    // REQUEST
    public AccountInitRequest accountRequestData;

    // Interface
    APIInterface apiInterface;

    // constructors
    public ConnectTool(Context context,
                       String _state,
                       String _requestNumber,
                       String _redirect_uri,
                       String _RSAstr) throws Exception {

        this.context = context;

        // init
        state = _state;
        requestNumber = _requestNumber;
        redirect_uri = _redirect_uri;
        RSAstr = _RSAstr;
    }

    public void SendRegisterData(ConnectCallback callback) {
        apiInterface = APIClient.getHostClient().create(APIInterface.class);

        //A-Signature
        String ts = String.valueOf(System.currentTimeMillis()/1000);
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
                connectBasic.referralCode,
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
        String ts = String.valueOf(System.currentTimeMillis()/1000);
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
                connectBasic.referralCode);

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
        String url = APIClient.host + "/connect/Authorize?response_type=code&client_id=" + connectBasic.client_id + "&redirect_uri=" + redirect_uri + "&scope=game+offline_access&state=" + state;
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

                connectTokenCall.callbackConnectToken(tokenData);
            }

            @Override
            public void onFailure(Call<ConnectToken> call, Throwable t) {
                call.cancel();
                connectTokenCall.callbackConnectToken(null);
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

                connectTokenCall.callbackConnectToken(tokenData);
            }

            @Override
            public void onFailure(Call<ConnectToken> call, Throwable t) {
                call.cancel();
                connectTokenCall.callbackConnectToken(null);
            }
        });
    }

    public void GetMe_Coroutine(MeCallback callback) throws NoSuchAlgorithmException {
        apiInterface = APIClient.getGame_api_hostClient().create(APIInterface.class);
        String timestamp = getTimestamp();

        String signdata = "?RequestNumber=" + requestNumber + "&Timestamp=" + timestamp;
        String X_Signature = "";
        try {
            X_Signature = getxSignature(signdata);
            Log.v(TAG, "X_Signature " + X_Signature);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // headers data
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

    private String getTimestamp() {
        Date currentDate = new Date();
        System.out.println("Current Timestamp: " + currentDate.getTime() + " milliseconds");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String dateString = formatter.format(currentDate);
        return dateString + "Z";
    }

    public static String sign(String plainText, PrivateKey privateKey) throws Exception {
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(plainText.getBytes(UTF_8));

        byte[] signature = privateSignature.sign();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(signature);
        }
        return plainText;
    }

    public static PrivateKey stringtoprivatekey(String privateKeyString) {

        try {
            if (privateKeyString.contains("-----BEGIN PRIVATE KEY-----") || privateKeyString.contains("-----END PRIVATE KEY-----"))
                privateKeyString = privateKeyString.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "");
            if (privateKeyString.contains("-----BEGIN RSA PRIVATE KEY-----") || privateKeyString.contains("-----END RSA PRIVATE KEY-----"))
                privateKeyString = privateKeyString.replace("-----BEGIN RSA PRIVATE KEY-----", "").replace("-----END RSA PRIVATE KEY-----", "");

            privateKeyString = privateKeyString.replaceAll("\\r|\\n", "");
            byte[] privateKeyDER = new byte[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                privateKeyDER = Base64.getDecoder().decode(privateKeyString);
            }
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyDER));
            return privateKey;

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getxSignature(String data) throws Exception {
        PrivateKey privateKey = stringtoprivatekey(RSAstr);

        //Let's sign our message
        String signature = sign(data, privateKey);
        return signature;
    }

}

