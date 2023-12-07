package com.r17dame.connecttool.datamodel;

import com.google.gson.annotations.SerializedName;

public class PurchaseOrderOneResponse {

    @SerializedName("data")
    public PurchaseOrderListResponse.PurchaseOrderListObj data;

    @SerializedName("status")
    public String status;
    @SerializedName("message")
    public String message;
    @SerializedName("requestNumber")
    public String requestNumber;
}
