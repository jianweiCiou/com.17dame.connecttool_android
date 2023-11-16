package com.r17dame.connecttool;
import com.r17dame.connecttool.datamodel.ConnectToken;
import com.r17dame.connecttool.datamodel.MeInfo;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.Query;

interface APIInterface {

    @FormUrlEncoded
    @POST("/api/account/Register")
    Call<Void> sendRegister(
            @Header("ts") String ts
            ,@Header("A-Signature") String A_Signature
            ,@Field("email")String email
            ,@Field("password")String password
            ,@Field("referralCode")String referralCode
            ,@Field("gameId")String gameId);

    @FormUrlEncoded
    @POST("/api/account/Login")
    Call<Void> sendLoginData(
            @Header("ts") String ts
            ,@Header("A-Signature") String A_Signature
            ,@Field("email")String email
            ,@Field("password")String password
            ,@Field("referralCode")String referralCode );

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("/connect/token")
    Call<ConnectToken> getConnectToken(
            @Field("code")String code
            ,@Field("client_id")String client_id
            ,@Field("client_secret")String client_secret
            ,@Field("redirect_uri")String redirect_uri
            ,@Field("grant_type")String grant_type);

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("/connect/token")
    Call<ConnectToken> getRefreshTokenData(
            @Field("refresh_token")String refresh_token
            ,@Field("client_id")String client_id
            ,@Field("client_secret")String client_secret
            ,@Field("redirect_uri")String redirect_uri
            ,@Field("grant_type")String grant_type);

    @Headers("Content-Type: application/json; charset=utf-8" )
    @GET("/api/Me")
    Call<MeInfo> getMeData(
            @Header("Authorization") String authorization,
            @Header("X-Developer-Id") String X_Developer_Id,
            @Header("X-Signature") String X_Signature,
            @Query("RequestNumber") String RequestNumber,
            @Query("Timestamp") String Timestamp);

}