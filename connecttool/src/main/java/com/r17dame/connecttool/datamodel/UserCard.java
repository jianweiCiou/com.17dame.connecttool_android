package com.r17dame.connecttool.datamodel;

import com.google.gson.annotations.SerializedName;

public class UserCard {

    @SerializedName("userCardId")
    public String userCardId;
    @SerializedName("userId")
    public String userId;
    @SerializedName("first6DigitOfPan")
    public String first6DigitOfPan;
    @SerializedName("last4DigitOfPan")
    public String last4DigitOfPan;
    @SerializedName("createdOn")
    public String createdOn;

}
