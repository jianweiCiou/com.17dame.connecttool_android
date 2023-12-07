package com.r17dame.connecttool.datamodel;

import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.r17dame.connecttool.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;

public class PaymentRequest {
    @SerializedName("requestNumber")
    public String requestNumber;
    @SerializedName("timestamp")
    public String timestamp;
    @SerializedName("gameId")
    public String gameId;
    @SerializedName("orderNo")
    public String orderNo;
    @SerializedName("spCoin")
    public String spCoin;
    @SerializedName("rebate")
    public String rebate;

    public static PaymentRequest getRequestBody(SharedPreferences pref,String _gameId){
        String timestamp = Tool.getTimestamp();
        JSONObject requestObject = new JSONObject();

        String CreatePayment_requestNumber = pref.getString(String.valueOf(R.string.CreatePayment_requestNumber),"");
        String CreatePayment_orderNo = pref.getString(String.valueOf(R.string.CreatePayment_orderNo),"");
        int CreatePayment_spCoin = pref.getInt(String.valueOf(R.string.CreatePayment_spCoin),0);
        int CreatePayment_rebate = pref.getInt(String.valueOf(R.string.CreatePayment_rebate),0);


        try {
            requestObject.put("requestNumber", CreatePayment_requestNumber);
            requestObject.put("timestamp", timestamp);
            requestObject.put("gameId", _gameId);
            requestObject.put("orderNo", CreatePayment_orderNo); // GameOrderNo 遊戲商的訂單編號
            requestObject.put("spCoin", CreatePayment_spCoin);
            requestObject.put("rebate", CreatePayment_rebate);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        return gson.fromJson(requestObject.toString(),PaymentRequest.class);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String get_X_Signature(SharedPreferences pref,String RSAstr,String _gameId){
        Gson gson = new Gson();
        PaymentRequest payWithPrimeRequest = PaymentRequest.getRequestBody(pref,_gameId);
        String requestString = gson.toJson(payWithPrimeRequest);

        String X_Signature = "";
        try {
            X_Signature = Tool.getxSignature(requestString,RSAstr);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return X_Signature;
    }
}
