package com.r17dame.connecttool.datamodel;

import com.google.gson.annotations.SerializedName;

public class ConnectToken {
    @SerializedName("access_token")
    public String access_token;
    @SerializedName("token_type")
    public String token_type;
    @SerializedName("expires_in")
    public String expires_in;
//    @SerializedName("scope")
//    public String scope;
    @SerializedName("refresh_token")
    public String refresh_token;

    public ConnectToken(String access_token,
                        String token_type,
                        String expires_in,
                        String refresh_token) {
        this.access_token = access_token;
        this.token_type = token_type;
        this.expires_in = expires_in;
        this.refresh_token = refresh_token;
    }
}
