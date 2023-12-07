package com.r17dame.connecttool;
import com.r17dame.connecttool.datamodel.ACPAYPayWithPrimeRequest;
import com.r17dame.connecttool.datamodel.ConnectToken;
import com.r17dame.connecttool.datamodel.MeInfo;
import com.r17dame.connecttool.datamodel.PayWithPrimeRespone;
import com.r17dame.connecttool.datamodel.PaymentRequest;
import com.r17dame.connecttool.datamodel.PaymentResponse;
import com.r17dame.connecttool.datamodel.PurchaseOrder;
import com.r17dame.connecttool.datamodel.PurchaseOrderListRequest;
import com.r17dame.connecttool.datamodel.PurchaseOrderListResponse;
import com.r17dame.connecttool.datamodel.PurchaseOrderOneRequest;
import com.r17dame.connecttool.datamodel.PurchaseOrderOneResponse;
import com.r17dame.connecttool.datamodel.PurchaseOrderRequest;
import com.r17dame.connecttool.datamodel.UserCard;
import com.r17dame.connecttool.datamodel.UserCardRequest;

import retrofit2.Call;
import retrofit2.http.Body;
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
            ,@Field("gameId")String gameId
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


//    {
//        "requestNumber": "ebe4ae28-dda1-499d-bdbc-1066ce080a6f",
//            "timestamp": "2023-12-01T13:30:44.518Z",
//            "currencyCode": "2",
//            "eCoin": 60,
//            "rebate": 0,
//            "totalAmt": 28,
//            "gameId": "07d5c223-c8ba-44f5-b9db-86001886da8d",
//            "payGateway": 1,
//            "payMethod": 0
//    }

//    @Headers("Content-Type: application/json" )
//    @Headers("Content-Type: application/

    @POST("/api/Payment")
    Call<PaymentResponse> createPayment(
            @Header("Authorization") String authorization,
            @Header("X-Developer-Id") String X_Developer_Id,
            @Header("X-Signature") String X_Signature,
            @Body PaymentRequest request );

    @POST("/api/Payment/CreatePurchaseOrder")
    Call<PurchaseOrder> createPurchaseOrder(
            @Header("Authorization") String authorization,
            @Header("X-Developer-Id") String X_Developer_Id,
            @Header("X-Signature") String X_Signature,
            @Body PurchaseOrderRequest request );


    @POST("/api/Payment/ACPAY/PayWithPrime")
    Call<PayWithPrimeRespone> ACPAYPayWithPrime(
            @Header("Authorization") String authorization,
            @Header("X-Developer-Id") String X_Developer_Id,
            @Header("X-Signature") String X_Signature,
            @Body ACPAYPayWithPrimeRequest request );

    @POST("/api/Payment/GetPurchaseOrderList")
    Call<PurchaseOrderListResponse> GetPurchaseOrderList(
            @Header("Authorization") String authorization,
            @Header("X-Developer-Id") String X_Developer_Id,
            @Header("X-Signature") String X_Signature,
            @Body PurchaseOrderListRequest request );

    @POST("/api/Payment/GetPurchaseOrder")
    Call<PurchaseOrderOneResponse> GetPurchaseOrderOne(
            @Header("Authorization") String authorization,
            @Header("X-Developer-Id") String X_Developer_Id,
            @Header("X-Signature") String X_Signature,
            @Body PurchaseOrderOneRequest request );

    @POST("/api/Payment/GetPurchaseOrderList")
    Call<UserCard[]> GetUserCards(
            @Header("Authorization") String authorization,
            @Header("X-Developer-Id") String X_Developer_Id,
            @Header("X-Signature") String X_Signature,
            @Body UserCardRequest request );


}