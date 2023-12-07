package com.r17dame.connecttool.datamodel;

import com.google.gson.annotations.SerializedName;

public class PurchaseOrder {
    @SerializedName("data")
    public OrderData data;
    @SerializedName("status")
    public int status;
    @SerializedName("message")
    public String message;
    @SerializedName("requestNumber")
    public String requestNumber;
    public class OrderData  {
        @SerializedName("purchaseOrderId")
        public String purchaseOrderId;
        @SerializedName("spCoinLogID")
        public String spCoinLogID;
        @SerializedName("sequence")
        public String sequence;
        @SerializedName("payGateway")
        public String payGateway;
        @SerializedName("payMethod")
        public String payMethod;

        @SerializedName("tradeNo")
        public String tradeNo;
        @SerializedName("refundNo")
        public int refundNo;

        @SerializedName("spCoinItemPriceId")
        public String spCoinItemPriceId;

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
        @SerializedName("gameId")
        public String gameId;
        @SerializedName("createdBy")
        public String createdBy;
        @SerializedName("createdOn")
        public String createdOn;
    }
}





