package com.r17dame.connecttool.datamodel;

import com.google.gson.annotations.SerializedName;

public class PaymentResponse {
    @SerializedName("data")
    public PaymentData data;
    @SerializedName("status")
    public int status;
    @SerializedName("message")
    public String message;
    @SerializedName("detailMessage")
    public String detailMessage;

    @SerializedName("requestNumber")
    public String requestNumber;

    public class PaymentData {
        @SerializedName("transactionId")
        public String transactionId;
        @SerializedName("spCoin")
        public int spCoin;
        @SerializedName("rebate")
        public int rebate;
        @SerializedName("orderStatus")
        public String orderStatus;
    }
}
