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

public class PurchaseOrderListRequest {
    @SerializedName("requestNumber")
    public String requestNumber;
    @SerializedName("timestamp")
    public String timestamp;

    public static PurchaseOrderListRequest getRequestBody(SharedPreferences pref){
        String timestamp = Tool.getTimestamp();
        JSONObject requestObject = new JSONObject();

        String purchaseOrderList_requestNumber = pref.getString(String.valueOf(R.string.purchaseOrderList_requestNumber),"");

        try {
            requestObject.put("requestNumber", purchaseOrderList_requestNumber);
            requestObject.put("timestamp", timestamp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        return gson.fromJson(requestObject.toString(),PurchaseOrderListRequest.class);
    }

    public static String get_X_Signature(SharedPreferences pref,String RSAstr){
        Gson gson = new Gson();
        PurchaseOrderListRequest payWithPrimeRequest = PurchaseOrderListRequest.getRequestBody(pref);
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
