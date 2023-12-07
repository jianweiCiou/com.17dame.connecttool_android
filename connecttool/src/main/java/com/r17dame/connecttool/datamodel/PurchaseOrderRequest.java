package com.r17dame.connecttool.datamodel;

import com.google.gson.annotations.SerializedName;

public class PurchaseOrderRequest {

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
}
