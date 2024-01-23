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

public class CreatePurchaseOrderRequest {
    @SerializedName("requestNumber")
    public String requestNumber;
    @SerializedName("timestamp")
    public String timestamp;
    @SerializedName("spCoinItemPriceId")
    public String spCoinItemPriceId;
    @SerializedName("payGateway")
    public int payGateway;
    @SerializedName("payMethod")
    public int payMethod;
    @SerializedName("state")
    public String state;
    @SerializedName("notifyUrl")
    public String notifyUrl;


    public static CreatePurchaseOrderRequest getRequestBody(SharedPreferences pref){

        String timestamp = Tool.getTimestamp();

        String spCoinItemPriceId = pref.getString(String.valueOf(R.string.purchase_spCoinItemPriceId),"");
        String purchase_requestNumber = pref.getString(String.valueOf(R.string.purchase_requestNumber),"");
        String purchase_payGateway = pref.getString(String.valueOf(R.string.purchase_payGateway),"");
        String purchase_payMethod= pref.getString(String.valueOf(R.string.purchase_payMethod),"");
        String purchase_notifyUrl= pref.getString(String.valueOf(R.string.purchase_notifyUrl),"");
        String state= pref.getString(String.valueOf(R.string.purchase_state),"");

        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("requestNumber", purchase_requestNumber);
            requestObject.put("timestamp", timestamp);
            requestObject.put("spCoinItemPriceId", spCoinItemPriceId);
            requestObject.put("payGateway", purchase_payGateway);
            requestObject.put("payMethod", purchase_payMethod);
            requestObject.put("state", state);
            requestObject.put("notifyUrl", purchase_notifyUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        return gson.fromJson(requestObject.toString(),CreatePurchaseOrderRequest.class);
    }

    public static String get_X_Signature(SharedPreferences pref,String RSAstr){
        Gson gson = new Gson();
        CreatePurchaseOrderRequest payWithPrimeRequest = CreatePurchaseOrderRequest.getRequestBody(pref);
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
