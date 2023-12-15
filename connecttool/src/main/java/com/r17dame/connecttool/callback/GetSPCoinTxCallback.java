package com.r17dame.connecttool.callback;

import com.r17dame.connecttool.datamodel.CreateSPCoinResponse;
import com.r17dame.connecttool.datamodel.SPCoinTxResponse;

public interface GetSPCoinTxCallback {
    void callback(CreateSPCoinResponse value);
}
