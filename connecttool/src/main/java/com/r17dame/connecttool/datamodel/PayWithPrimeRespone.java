package com.r17dame.connecttool.datamodel;

import com.google.gson.annotations.SerializedName;

public class PayWithPrimeRespone {
    @SerializedName("data")
    public PayWithPrimeData data;
    @SerializedName("status")
    public int status;
    @SerializedName("message")
    public String message;
    @SerializedName("detailMessage")
    public String detailMessage;
    @SerializedName("requestNumber")
    public String requestNumber;

    public class PayWithPrimeData{
        @SerializedName("url")
        public String url;
    }
}
