package com.r17dame.connecttool.callback;

import com.r17dame.connecttool.datamodel.ConnectToken;

import java.security.NoSuchAlgorithmException;

public interface ConnectTokenCall {
    void callbackConnectToken(ConnectToken value) throws NoSuchAlgorithmException;
}
