package com.r17dame.connecttool.datamodel;

import com.google.gson.annotations.SerializedName;

public class PurchaseOrderListResponse {
    @SerializedName("data")
    public PurchaseOrderListObj[] data;

    @SerializedName("status")
    public String status;
    @SerializedName("message")
    public String message;
    @SerializedName("requestNumber")
    public String requestNumber;

    public class PurchaseOrderListObj {
        @SerializedName("payMethod")
        public int payMethod;
        @SerializedName("tradeNo")
        public String tradeNo;
        @SerializedName("refundNo")
        public String refundNo;
        @SerializedName("spCoin")
        public int spCoin;
        @SerializedName("rebate")
        public int rebate;
        @SerializedName("status")
        public int status;
        @SerializedName("currencyCode")
        public String currencyCode;
        @SerializedName("totalAmt")
        public int totalAmt;
        @SerializedName("createdOn")
        public String createdOn;
    }
}
