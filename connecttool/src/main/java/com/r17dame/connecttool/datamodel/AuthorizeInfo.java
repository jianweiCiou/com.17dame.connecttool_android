package com.r17dame.connecttool.datamodel;
public class AuthorizeInfo {
    public MeInfo meInfo;
    public ConnectToken connectToken;
    public String state;
    public String access_token;


    public AuthorizeInfo(MeInfo value, ConnectToken connectvalue,String _state, String _accessToken) {
        meInfo  = value;
        connectToken = connectvalue;
        state = _state;
        access_token = _accessToken;
    }
}

