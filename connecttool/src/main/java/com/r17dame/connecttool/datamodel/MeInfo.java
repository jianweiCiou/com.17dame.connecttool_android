package com.r17dame.connecttool.datamodel;

import com.google.gson.annotations.SerializedName;

public class MeInfo {
    @SerializedName("data")
    public MeData data;
    @SerializedName("status")
    public int status;
    @SerializedName("message")
    public String message;
    @SerializedName("requestNumber")
    public String requestNumber;

    public class MeData {
        @SerializedName("userId")
        public String userId;
        @SerializedName("email")
        public String email;
        @SerializedName("nickName")
        public String nickName;
        @SerializedName("avatarUrl")
        public String avatarUrl;
        @SerializedName("spCoin")
        public String spCoin;
        @SerializedName("rebate")
        public String rebate;
    }
}

