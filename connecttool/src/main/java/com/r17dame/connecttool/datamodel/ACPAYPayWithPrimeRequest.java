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

public class ACPAYPayWithPrimeRequest {
  @SerializedName("requestNumber")
  public String requestNumber;
  @SerializedName("timestamp")
  public String timestamp;
  @SerializedName("tradeNo")
  public String tradeNo;
  @SerializedName("prime")
  public String prime;
  @SerializedName("layout")
  public String layout;


  public static ACPAYPayWithPrimeRequest getRequestBody(SharedPreferences pref){
    // 回傳 requestNumber tradeNo prime layout(1:PC / 2:mobile)
    String layout = "1";

    String timestamp = Tool.getTimestamp();

    String purchase_tradeNo = pref.getString(String.valueOf(R.string.purchase_tradeNo),"");
    String purchase_requestNumber = pref.getString(String.valueOf(R.string.purchase_requestNumber),"");
    String purchase_prime= pref.getString(String.valueOf(R.string.purchase_prime),"");

    JSONObject requestObject = new JSONObject();
    try {
      requestObject.put("requestNumber", purchase_requestNumber);
      requestObject.put("timestamp", timestamp);
      requestObject.put("tradeNo", purchase_tradeNo);
      requestObject.put("prime", purchase_prime);
      requestObject.put("layout", layout);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    Gson gson = new Gson();
    return gson.fromJson(requestObject.toString(),ACPAYPayWithPrimeRequest.class);
  }

  public static String get_X_Signature(SharedPreferences pref,String RSAstr){
    Gson gson = new Gson();
    ACPAYPayWithPrimeRequest payWithPrimeRequest = ACPAYPayWithPrimeRequest.getRequestBody(pref);
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