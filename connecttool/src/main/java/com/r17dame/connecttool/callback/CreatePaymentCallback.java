package com.r17dame.connecttool.callback;

import com.r17dame.connecttool.datamodel.PaymentResponse;

public interface CreatePaymentCallback {

    void callback(PaymentResponse value);

}
