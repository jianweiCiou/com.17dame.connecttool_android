package com.r17dame.connecttool.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SPCoinTxResponse {
    @SerializedName("data")
    public SPCoinTxData data;
    @SerializedName("status")
    public int status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("detailMessage")
    @Expose
    public String detailMessage;
    @SerializedName("requestNumber")
    public String requestNumber;
    public class SPCoinTxData {
        @SerializedName("transactionId")
        public String transactionId;
        @SerializedName("orderNo")
        public String orderNo;

        @SerializedName("spCoin")
        public int spCoin;
        @SerializedName("rebate")
        public int rebate;
        @SerializedName("orderStatus")
        public int orderStatus;
        @SerializedName("state")
        @Expose
        public String state;
        @SerializedName("notifyUrl")
        @Expose
        public String notifyUrl;
        @SerializedName("sign")
        @Expose
        public String sign;
    }

}
