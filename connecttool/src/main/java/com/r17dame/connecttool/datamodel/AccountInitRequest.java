package com.r17dame.connecttool.datamodel;

public class AccountInitRequest {
    public String email;
    public String password;

    public AccountInitRequest(String _email, String _password) {
        email = _email;
        password = _password;
    }
}