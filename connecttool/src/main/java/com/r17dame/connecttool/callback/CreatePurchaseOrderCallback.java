package com.r17dame.connecttool.callback;

import com.r17dame.connecttool.datamodel.PurchaseOrder;

import java.security.NoSuchAlgorithmException;

public interface CreatePurchaseOrderCallback {
    void callback(PurchaseOrder value) throws NoSuchAlgorithmException;
}
