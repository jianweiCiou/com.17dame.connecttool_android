package com.r17dame.connecttool.callback;

import com.r17dame.connecttool.datamodel.PurchaseOrderOneResponse;

public interface PurchaseOrderCallback {
    PurchaseOrderOneResponse callback(PurchaseOrderOneResponse value);
}
