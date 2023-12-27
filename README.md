# 17dame Connect Tool for Android 
17dame connect tool: ConnectTool provides registration, login, Authorize,get access token, refresh token and user information. 
## Table of Contents  
- [Prerequisites](#prerequisites)
    - [Minimum requirements](#minimum-requirements)
- [Installation](#installation) 
- [Setting](#setting)
- [Authorize Flow](#authorize-flow)
- [ConnectTool function](#connecttool-function)  
    - [OpenRegisterURL, OpenLoginURL ](#openregisterurl-openloginurl)
    - [App-side event response (Register, Login, Logout)　](#app-side-event-response-register-login-logout)
    - [OpenAuthorizeURL](#openauthorizeurl)
    - [Authorize subsequent events ](#)
    - [GetConnectToken_Coroutine](#getconnecttoken_coroutine)
    - [GetRefreshToken_Coroutine](#getrefreshtoken_coroutine)
    - [GetMe_Coroutine](#getme_coroutine) 
- [Recharge function](#recharge-function)
    - [Recharge flow](#recharge-flow)  
    - [Open Recharge page](#open-recharge-page)
    - [Recharge subsequent events](#recharge-subsequent-events)
    - [GetPurchaseOrderList](#getpurchaseorderlist)
    - [GetPurchaseOrderOne](#getpurchaseorderone)
- [ConsumeSP function](#consumesp-function)
    - [ConsumeSP flow](#consumesp-flow)  
    - [Open ConsumeSP page](#open-consumesp-page)
    - [ConsumeSP subsequent events ](#consumesp-subsequent-events)
    	-[AppLinkData ConsumeSP Response](#applinkdata-consumesp-response) 
    - [Query ConsumeSP By transactionId](#query-consumesp-by-transactionid)
- [NotifyUrl & State](#notifyurl--state)
    - [Recharge NotifyUrl](#recharge-notifyurl)
    - [ConsumeSP NotifyUrl](#consumesp-notifyurl)
- [Notify Verify](#notify-verify)
    - [C# verify "Sign" sample](#c-verify-sign-sample)
    - [PHP verify "Sign" sample](#php-verify-sign-sample) 
- [Recommended testing process](#recommended-testing-process) 
    - [Generate new account](#generate-new-account)
    - [Switch account](#switch-account)
- [Model](#model) 

## Prerequisites
### Minimum requirements  
Your application needs to support :
- Minimum SDK version 26
- Android Gradle Plugin Version: 8.1.3
- Gradle Version : 8.0


## Installation
- Downliad libary:[connecttool-v1.3.1.aar](https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/Tutorial/connecttool-v1.3.1.aar)
- Connect Tool AAR Tutorial-v1.3.1.pdf (for Payment): [View](https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/Tutorial/Connect%20Tool%20AAR%20Tutorial-v1.3.1.pdf)

- Downliad libary:[connecttool-v1.0.0.aar](https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/Tutorial/connecttool-v1.0.0.aar)
- Connect Tool AAR Tutorial-v1.0.0.pdf (for Authorize): [View](https://github.com/jianweiCiou/com.17dame.connecttool_android/blob/main/Tutorial/Connect%20Tool%20AAR%20Tutorial-v1.0.0.pdf)
  
## Setting
- Open \app\src\main\AndroidManifest.xml to add:
```xml
<uses-permission android:name="android.permission.INTERNET" />
``` 
```xml
<intent-filter>
  <action android:name="android.intent.action.VIEW" />
  <category android:name="android.intent.category.DEFAULT" />
  <category android:name="android.intent.category.BROWSABLE" />
  <data android:scheme="{{ Get from redirect_uri's scheme }}" android:host="connectlink" />
</intent-filter>
```  
- redirect_uri : Set the name of the scene to be opened, for example `{{ Get from redirect_uri's scheme }}://connectlink?connectscene`
 
- Add implementation to build.gradle : 
```txt
dependencies { 
   ...
    // post request
    implementation 'com.squareup.retrofit2:retrofit:2.1.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.1.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:3.4.1'
    implementation 'com.squareup.okhttp3:okhttp:3.4.1'

    implementation(project(":connecttool"))
}
```

## Authorize Flow
Here is a simple flow chart:
```mermaid 
graph TD;
    Register-->Login;
    A(OpenAuthorizeURL) -->|request login| B1(onDeepLinkActivated);
    B1(onDeepLinkActivated)-->|get Code | D(GetConnectToken_Coroutine ); 
    D(GetConnectToken_Coroutine ) -->|get Access token |s(GetMe_Coroutine);
     D(GetConnectToken_Coroutine )--> C{Token expired?}
    C -->|yet| s(GetMe_Coroutine)
    C -->|expired| Rf[GetRefreshToken_Coroutine]
    Rf[GetRefreshToken_Coroutine]-->|get Access token |s(GetMe_Coroutine) 
```
 
Send Authorize to get access_token and get code:
```java 
Intent appLinkIntent = getIntent();
String appLinkAction = appLinkIntent.getAction();
Uri appLinkData = appLinkIntent.getData(); 
if (appLinkData != null && appLinkData.isHierarchical()) {
 String uri = this.getIntent().getDataString();
 _connectTool.code = appLinkData.getQueryParameter("code");
}
```


## ConnectTool function
- Create `ConnectTool` and `ConnectTool.ConnectBasic`, parameters must be filled in:
- the new ConnectTool() constructor, change the parameters to 6, Please keep the parameters properly and safely.
    - Context context,
    - _redirect_uri,
    - _RSAstr,
    - _X_Developer_Id,
    - _client_secret,
    - _Game_id
```csharp
_connectTool = new ConnectTool(
       this,
       ".......://connectlink",
       "-----BEGIN RSA PRIVATE KEY-----\n" + "MIIEowIBAAKCAQEAudt2mFGvE.......",
       "ebe4ae.......", 
       "AQAAAA.......",
       "07d5c2......."); 
``` 

### OpenRegisterURL, OpenLoginURL　
- Open the host page, perform registration and login.
- Will sign out first.
```java
// Register
Register_pageButton.setOnClickListener(view -> {
	_connectTool.OpenRegisterURL();
});
// Login
Login_pageButton.setOnClickListener(view -> {
	_connectTool.OpenLoginURL();
}); 
``` 
### App-side event response (Register, Login, Logout)　
```java
if (appLinkData.getQueryParameterNames().contains("accountBackType")) {
	String accountBackType = appLinkData.getQueryParameter("accountBackType");
	if(accountBackType.equals("Register")){
		/*
		* App-side add functions.
		*/ 
	}
	if(accountBackType.equals("Login")){
		/*
		* App-side add functions.
		*/ 
	}
	String state = "App-side-State";
        _connectTool.AccountPageEvent(accountBackType);
}
```
`state` : Please fill in what you want to verify,`state` can be query through redirect_uri.

#### Register event response
```mermaid
sequenceDiagram
    autonumber
    participant C as Game App
    participant S as host 
 
    C->>S: OpenRegisterURL()
    activate C
        note over S: Complete registration
        S-->>C: DeepLink accountBackType( Register )
        note over C: App-side add functions 
        C-->>S: OpenAuthorizeURL()

        activate S
            S-->>C: DeepLink ( code ) 
        deactivate S  
        note over C: Get Access_token & me data
    deactivate C 
  
  
```
#### Login event response
```mermaid
sequenceDiagram
    autonumber
    participant C as Game App
    participant S as host 
 
    C->>S: OpenLoginURL()
    activate C
        note over S: Complete login
        S-->>C: DeepLink accountBackType( Login )
        note over C: App-side add functions 
        C-->>S: OpenAuthorizeURL() 
        activate S
            S-->>C: DeepLink ( code ) 
        deactivate S  
        note over C: Get Access_token & me data
    deactivate C  
```

#### Logout event response
```mermaid 
sequenceDiagram
    autonumber
    participant C as Game App
    participant S as host 
 
    C->>S: OpenLogoutURL()
    activate C
        note over S: Complete the logout process
        S-->>C: DeepLink accountBackType( Logout )
        note over C: App-side add functions   
        note over C: remove access_token
        
    deactivate C 
```
  
 
### OpenAuthorizeURL　  
- `state` : Please fill in what you want to verify,`state` can be query through redirect_uri. 
- Open host page to log in.
- You will get `code` and `state` from redirect_uri's parameter after log in. 
Send OpenAuthorizeURL:
```java  
String state = "App-side-State";
_connectTool.OpenAuthorizeURL(state);
```
DeepLink will get "getQueryParameter("code")" back :
```java  
// deepLink
Intent appLinkIntent = getIntent();
String appLinkAction = appLinkIntent.getAction();
Uri appLinkData = appLinkIntent.getData();
if (appLinkData != null && appLinkData.isHierarchical()) {
	String uri = this.getIntent().getDataString();
	_connectTool.code = appLinkData.getQueryParameter("code");
}
```
Step 
1. Execute Authorize through ConnectTool.
2. Open Login page.
3. Retrieve code through onDeepLinkActivated.
4. Execute GetConnectToken_Coroutine to obtain access_token.

#### Authorize subsequent events 
The App will automatically obtain Me information.
```java
// get Access token
                if (appLinkData.getQueryParameterNames().contains("code") ) {
                    _connectTool.code = appLinkData.getQueryParameter("code");

                    _connectTool.GetConnectToken_Coroutine(new ConnectTokenCall() {
                        @Override
                        public void callbackConnectToken(ConnectToken value) throws NoSuchAlgorithmException {
                            _connectCallbackText.setText("ConnectToken callback : " + value.access_token);

                            UUID GetMe_RequestNumber = UUID.fromString("73da5d8e-9fd6-11ee-8c90-0242ac120002"); // App-side-RequestNumber(UUID)
                            _connectTool.GetMe_Coroutine(GetMe_RequestNumber,new MeCallback() {
                                @Override
                                public void callbackMeInfo(MeInfo value) {
                                    Log.v(TAG, "MeInfo callback : " + value.status);
                                }
                            });
                        }
                    });
                }
```

### GetConnectToken_Coroutine 
- `connectTool.code` is required. 
- `connectTool.code` can be obtained through ConnectTool set or onDeepLinkActivated function.
- Return ConnectTokenModel

### GetRefreshToken_Coroutine  
- `connectTool.refresh_token` is required.  
- Return ConnectTokenModel.

### GetMe_Coroutine 
- `connectTool.access_token` is required.
- `GetMe_RequestNumber` is required UUID, and used for app-side verification, cannot be empty string. 
- Return MeInfo.

```java
UUID GetMe_RequestNumber = UUID.fromString("73da5d8e-9fd6-11ee-8c90-0242ac120002"); // App-side-RequestNumber(UUID)                           
_connectTool.GetMe_Coroutine(GetMe_RequestNumber,new MeCallback() {
	@Override
	public void callbackMeInfo(MeInfo value) {
		/*
		* App-side add functions.
		*/
		Log.v(TAG, "MeInfo email : " + value.data.email);
		Log.v(TAG, "MeInfo userId : " + value.data.userId);
		Toast.makeText(getApplicationContext(), value.data.email, Toast.LENGTH_SHORT).show();
	}
}); 
```
#### MeInfo response.body : 
- Will get requestNumber back.
- userId : UUID format.
``` JSON
{
  "data": {
    "userId": "db89ce....",
    "email": "...user mail",
    "nickName": null,
    "avatarUrl": null,
    "spCoin": 0,
    "rebate": 0
  },
  "status": 1000,
  "message": null,
  "detailMessage": null,
  "requestNumber": "73da5d8e-9fd6-11ee-8c90-0242ac120002"
}
```


## Recharge function 

### Recharge flow
```mermaid 
sequenceDiagram
    autonumber
    participant GS as Game Server
    participant C as Game App
    participant S as host
    participant payment as third-party payment

    

    C->>S: OpenRechargeURL()
    activate S
    
        activate S
            alt Set NotifyUrl & state 
            S-->>C: App deeplink get state
            S-->>GS: After Payment complete, call NotifyUrl
            else No NotifyUrl and state
                S-->>S: Only results page
            end
            note over S: Create order, Select payment 
        deactivate S

        S->>payment:Send getPrime()
        
        activate payment
            note over payment: Verification request
            payment-->>S: Send prime back
        deactivate payment 
        S->>S: CreatePurchaseOrder(spCoinItemPriceId)
 
        activate S
            note over S: Get tradeNo
        deactivate S
  
        S->>payment:PayWithBindPrime(tradeNo) 
        payment-->>S: Complete purchase of SP Coin
    deactivate S 
 
    S->>C: Return to App  
``` 
1.  After selecting CurrencyCode, open the Recharge page.
2.  Call connectTool.set_purchase_notifyData: Set data to be brought back to App and Server. 
3.  After Payment complete, host will call NotifyUrl automatically.
4.  If NotifyUrl & state are not set, only the results page.
5.  Confirming the purchase item, obtain authorization prime from the third-party payment.
6.  After verifying request, host will receive the prime code.
7.  Bring spCoinItemPriceId into backend to generate tradeNo.
8.  PayWithBindPrime brings prime and tradeNo to the backend and third-party payment, and opens the transaction page.
9.  Bring back transaction results.
10.  Return to App.
    
### Open Recharge page 
Open SP Coin Recharge page. 
```java
String notifyUrl = "";// NotifyUrl is a URL customized by the game developer
String state = "Custom state";// Custom state ,
// Step1. Set notifyUrl and state,
_connectTool.set_purchase_notifyData(notifyUrl, state);

// Step2. Set currencyCode
String currencyCode = "2";

// Step3. Open Recharge Page
_connectTool.OpenRechargeURL(currencyCode, notifyUrl, state);
```
- `notifyUrl` & `state` : Please refer to [NotifyUrl & State](#notifyurl--state)
- `currencyCode` : Please refer to [Currency Code](#currency-code)

#### Recharge subsequent events 
The App will automatically obtain Recharge information.
```java
// Complete purchase of SP Coin
                if (appLinkData.getQueryParameterNames().contains("purchase_state")) {
                    _connectTool.appLinkDataCallBack_CompletePurchase(appLinkData,new PurchaseOrderCallback() {
                        @Override
                        public PurchaseOrderOneResponse callback(PurchaseOrderOneResponse value) {
                            Log.v(TAG, "appLinkData PurchaseOrderOneResponse callback : " + value);
                            /*
                             * App-side add functions.
                             */
                            return value;
                        }
                    });
}
```
#### AppLinkData Recharge Response:

#### Currency Code
| Code  | USD |TWD |CNY |JPY |KRW |VND |THB |MYR |SGD |  
| --- | --- |--- |--- |--- |--- |--- |--- |--- |--- |
| key  | 1 |2 |4 |8 |16 |32 |64 |128 |256 |   

#### PayMethods
| Method  | Credit Card |Credit Card(Bind) |Apple Pay |Google Pay | 
| --- | --- |--- |--- |--- | 
| key  | 0 |1 |2 |3 | 

### GetPurchaseOrderList
Get the order list for purchasing SP Coin.
```java
GetPurchaseOrderListButton.setOnClickListener(view -> {
	try {
		_connectTool.GetPurchaseOrderList(new GetPurchaseOrderListCallback() {
			@Override
			public void callback(PurchaseOrderListResponse value) {
				Log.v(TAG, "PurchaseOrderListResponse callback : " + value);
			}
		});
	} catch (NoSuchAlgorithmException e) {
		throw new RuntimeException(e);
	}
});
```
Response :
```json
{
  "data": [
    {
      "payMethod": 0,
      "tradeNo": "PAC2023121400000261",
      "spCoin": 120,
      "rebate": 0,
      "state": "Custom state",
      "notifyUrl": "",
      "sign": null,
      "status": 0,
      "currencyCode": "TWD",
      "totalAmt": 56,
      "createdOn": "2023-12-14T19:17:32.3789489+00:00"
    },
    ...
  ],
  "status": 1000,
  "message": null,
  "detailMessage": null,
  "requestNumber": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
}
```
> [!NOTE]  
> The Response of PurchaseOrderList will not have NotifyUrl and Sign.


### GetPurchaseOrderOne
Get a single SP Coin order via tradeNo.
```java
try {
	String tradeNo = "PAC2023121400000261";
	_connectTool.GetPurchaseOrderOne(new PurchaseOrderCallback() {
		@Override
		public void callback(PurchaseOrderOneResponse value) {
			Log.v(TAG, "PurchaseOrderOneResponse callback : " + value);
		}
	}, tradeNo);
} catch (NoSuchAlgorithmException e) {
	throw new RuntimeException(e);
}
```
Response :
```json
{
  "data": {
    "payMethod": 0,
    "tradeNo": "PAC2023121400000261",
    "spCoin": 120,
    "rebate": 0,
    "state": "Custom state",
    "notifyUrl": "",
    "sign": null,
    "status": 0,
    "currencyCode": "TWD",
    "totalAmt": 56,
    "createdOn": "2023-12-14T19:17:32.3789489+00:00"
  },
  "status": 1000,
  "message": null,
  "detailMessage": null,
  "requestNumber": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
}
```
> [!NOTE]  
> The Response of GetPurchaseOrderOne will not have NotifyUrl and Sign.

## ConsumeSP function
### ConsumeSP flow
```mermaid
sequenceDiagram
    autonumber
    participant GS as Game Server
    participant C as Game App
    participant S as host
    participant hs as host Sever

    

    C->>S: OpenConsumeSPURL()
    
    activate S
            alt Set NotifyUrl & state 
                S->>C: App deeplink get consumespresult
                hs-->>GS: After Consume complete, call NotifyUrl

             note over GS:   Game-side distribution product (optional)

            else No NotifyUrl and state
                C->>S: Open results page
            end
    deactivate S
    note over S: Client confirms SPCoin info to be consumed


    alt Clinet’s SPCoin is affordable 
        S->>S: Client’s check consume SP intention
        activate S
            S-->>hs: Send CreateSPCoinOrder() request 
            note over hs: Verify client consumption request 
            hs-->>S: HostPage get CreateSPCoinResponse
        deactivate S
    else Insufficient SPCoin 
        S-->>S: Open Recharge page
    end
 
 
    S->>C: Consume_transactionId return to App 

    note over C: Use OrderNo or TransactionId to check ConsumeSP  
    note over C:   Game-side distribution product (optional)
```
1. Prepare the SPcoin value from  App and bring the consumption info to the ConsumeSP page. There is no need to check whether the user's SPCoin is affordable. 
2. If the developer has filled in state, consumespresult will be brought back from the ConsumeSP page after the consumption is completed. 
3. If the developer has prepared NotifyUrl, the user's transaction info will be sent from the host server to the Game Server after the consumption is completed.
4. If NotifyUrl and state are not filled in, only the results page will be displayed.
5. Client’s check consume SP intention.
6. Call the CreateSPCoinOrder() to complete the consumption, and the SPCoin will be deducted after the host server verification.
7. The Host page obtains CreateSPCoinResponse to confirm the consumption.
8. If the user cannot afford the SPCoin value, the user can open the Recharge page.
9. When consumption is completed, App can query tx by bringing OrderNo or TransactionId into [_connectTool.Get_SPCoin_tx](#query-consumesp-by-transactionid) function.
 

### Open ConsumeSP page  
- To use the SP Coin held by user, please use the createPayment function.
- `consume_spCoin`,`consume_rebate`,`orderNo`,`GameName`,`productName` are required.
- `orderNo` must be unique.
-  Game developers can customize the rules of `orderNo` 
- `GameName` 
- Usage : 
```java  
    String notifyUrl = "";// NotifyUrl is a URL customized by the game developer
    String state = "Custom state";// Custom state ,
    // Step1. Set notifyUrl and state,
    _connectTool.set_purchase_notifyData(notifyUrl, state);

    int consume_spCoin = 50;
    int consume_rebate = 20;
    String orderNo = UUID.randomUUID().toString();
    String GameName = "Good 18 Game";
    String productName = "10 of the best diamonds";
    _connectTool.OpenConsumeSPURL(consume_spCoin, consume_rebate, orderNo, GameName, productName);
```

ConsumeSP Response : [body](#consumesp-response-body)

#### ConsumeSP subsequent events 
The App will automatically obtain ConsumeSP information.
```java
// Complete consumption of SP Coin
if (appLinkData.getQueryParameterNames().contains("consume_transactionId")) {
	_connectTool.appLinkDataCallBack_CompleteConsumeSP(appLinkData,new GetSPCoinTxCallback(){
		@Override
		public void callback(CreateSPCoinResponse value) {
			/*
			* App-side add functions.
			*/
			Log.v(TAG, "appLinkData SPCoinTxResponse callback : " + value.data.orderStatus);
		}
	});
}
```

#### AppLinkData ConsumeSP Response:
- After the user's ConsumeSP is completed, return to the App to obtain the response of the user's ConsumeSP Response. 
- If the orderNo(customized on the game side) is correct and the orderStatus is Completed, the product can be sent.
- Response body:
``` JSON
{
  "data": {
    "transactionId": "T2023122500000188",
    "orderNo": "13f2ed19-3d18-419f-9148-b171a03665a9",
    "spCoin": 50,
    "rebate": 0,
    "orderStatus": "Completed",
    "state": "Custom state",
    "notifyUrl": null,
    "sign": null
  },
  "status": 1000,
  "message": null,
  "detailMessage": null,
  "requestNumber": "73da5d8e-9fd6-11ee-8c90-0242ac120002"
}
```

### Query ConsumeSP By transactionId 
- Obtain transaction data after consuming SPCoin.
- Retrieve the consumption information through `OrderNo` or `TransactionId`
- `queryConsumeSP_requestNumber` is required UUID, and used for app-side verification, cannot be empty string. 
```java
try {
	UUID queryConsumeSP_requestNumber = UUID.fromString( "73da5d8e-9fd6-11ee-8c90-0242ac120002"); // App-side-RequestNumber(UUID)
	String transactionId = "T2023121500000030";

	_connectTool.Get_SPCoin_tx(queryConsumeSP_requestNumber,transactionId,new GetSPCoinTxCallback() {
		@Override
		public void callback(SPCoinTxResponse value) {
			Log.v(TAG, "SPCoinTxResponse callback : " + value.status);
		}
	});
} catch (NoSuchAlgorithmException e) {
	throw new RuntimeException(e);
}
```
	
#### ConsumeSP Response body:
``` JSON
{
  "data": {
    "transactionId": "T2023121500000030",
    "orderNo": "b427a826-4101-4172-8694-9e0ee868b9ab",
    "spCoin": 50,
    "rebate": 3,
    "orderStatus": "Completed",
    "state": "Custom state",
    "notifyUrl": null,
    "sign": null
  },
  "status": 1000,
  "message": null,
  "detailMessage": null,
  "requestNumber": "ebe4ae28-dda1-499d-bdbc-1066ce080a6f"
}
```
- transactionId : Consumption SP Coin record ID.
- orderStatus(Completed) : Complete SP coin deduction.
- status(0) : Complete SP coin deduction.

 

#### OrderStatuses
| Failed  | WaitingForPayment |Processing |Completed |Refund |Cancel |PaymentExpired | 
| --- | --- |--- |--- |--- |  --- | --- | 
 
 
## NotifyUrl & State
> [!NOTE]  
> - notifyUrl :NotifyUrl is a URL customized by the game developer. We will post NotifyUrl automatically when the purchase is completed.
> - state : State is customized by game developer, which will be returned to game app after purchase complete. (Deeplink QueryParameter => `purchase_state`)

### Recharge NotifyUrl
#### Recharge NotifyUrl response.body : 
``` JSON 
{
  "payMethod": 3,
  "tradeNo": "PAC2023121400000245",
  "spCoin": 1160,
  "rebate": 40,
  "state": "M1 State_GooglePay",
  "notifyUrl": "https://localhost:7109/ACPayNotify/TradeNotify",
  "sign": "KxWFrnWPGquIAC/Pt1WPvX5operr5uHaPWG2YP8X28e6nLalfLCTZlq+liXijWrcJo1Ha9JzMC+9VbcZeG3pcin63xoBkKfEtdV9QbnT6pnxXH+pS8pEWNmQIQKKkDrxjkMZ3OjcY/CC9TW+mDURCYj8vu8EHB9zDJ1sGOP7y4o2aNRa+ZK/SxC9eZKV5l6P7Y/iv88DH7wiTbQ5qVw5FhwJLuqfi3gCOn4aVsmjc270jU9mP6TgdTUo5y2FHtYXAbsQP/07h2gJeTwQf/nO6gHVs3Ur8/t3hHtIwqCGBNQl6/TYwf6rSXRdMoBUjdLGm5GpBA5Pq7mzBqYI3UDheg==",
  "status": 2,
  "currencyCode": "TWD",
  "totalAmt": 545.0000,
  "createdOn": "2023-12-14T07:13:19.0375746+00:00"
} 
```

#### Encrypted Recharge content (Without "sign" string): 
- Create "Sign" to verify : Please refer to [Notify Verify](#notify-verify)
``` JSON
{
  "payMethod": 3,
  "tradeNo": "PAC2023121400000245",
  "spCoin": 1160,
  "rebate": 40,
  "state": "M1 State_GooglePay",
  "notifyUrl": "https://localhost:7109/ACPayNotify/TradeNotify",
  "status": 2,
  "currencyCode": "TWD",
  "totalAmt": 545.0000,
  "createdOn": "2023-12-14T07:13:19.0375746+00:00"
} 
```
 
### ConsumeSP NotifyUrl
#### ConsumeSP NotifyUrl response.body : 
``` JSON
{
  "transactionId": "T2023122700000222",
  "orderNo": "stringsss1111",
  "spCoin": 20,
  "rebate": 0,
  "orderStatus": "Completed",
  "state": "string",
  "notifyUrl": "http://localhost:8080/ConsumeSP/gameserver/TradeNotify/",
  "sign": "F+opHk3IzetbJBpZGPuzxR2UoA18Md6ZV6Yas047RMPsrGjAevlYwQ/Suse5b/Io1dohccf+T5U/B7lAFta9P73/VdD1x+poffsH238Xgyo2JDQQ1VnpFvvrRqi8hJXJWqFjvPjBFS6h/LT2BePXulXT0071RFJZsDb83jTiWlaKkUyl+lYn/g1o8KcEON8TIQtOL3HcCnDXfLZnkvbx8gjcK54kk7dTld3dMynRkErpev9tzcSsaYrjPCltJ4XIhkK5OL7acjPXVPh/JvbHOj7WdWcbd1yQIDoospkEbh2McNOAaEdgq985hTz1tuycyRAtfEJLG4n9+J7y6HkXpA=="
}
```
#### Encrypted ConsumeSP content (Without "sign" string): 
- Create "Sign" to verify : Please refer to [Notify Verify](#notify-verify)
``` JSON
{
  "transactionId": "T2023122700000222",
  "orderNo": "stringsss1111",
  "spCoin": 20,
  "rebate": 0,
  "orderStatus": "Completed",
  "state": "string",
  "notifyUrl": "http://localhost:8080/ConsumeSP/gameserver/TradeNotify/" 
}
```
### Notify Verify 
#### C# verify "Sign" sample: 
- privateKey : Use RSAstr as privateKey.
- data: JSON string.
- C# sample:
```csharp
  var rsa = RSA.Create();
  rsa.ImportFromPem(privateKey);
  var bytes = Encoding.UTF8.GetBytes(data);
  // sign
  var signature = rsa.SignData(bytes, 0, bytes.Length, HashAlgorithmName.SHA256,RSASignaturePadding.Pkcs1);
  var xSignature = Convert.ToBase64String(signature);
```
#### PHP verify "Sign" sample: 
- key.pem : Use RSAstr as privateKey.
- jsonData: response.body.
- PHP sample:
```php
<?php
// Get Notify response.body
$jsonData = file_get_contents('php://input');

// Parse ConsumeSP JSON
$data = json_decode($jsonData, true);
if ($data != null) {

  // Create sign data
  $dataString = "{\"transactionId\":\"" . $data['transactionId'] . "\",\"orderNo\":\"" . $data['orderNo'] . "\",\"spCoin\":" . $data['spCoin'] . ",\"rebate\":" . $data['rebate'] . ",\"orderStatus\":\"" . $data['orderStatus'] . "\",\"state\":\"" . $data['state'] . "\",\"notifyUrl\":\"" . $data['notifyUrl'] . "\"}";
  $signatureFinal =  $data['sign'];

  // Get RSAstr 
  $privateKeyId = openssl_pkey_get_private(file_get_contents('./key.pem'));
  openssl_sign($dataString, $signature, $privateKeyId, 'RSA-SHA256');
  // echo "signature: \n" . base64_encode($signature) . "\n";

  if ($signatureFinal == base64_encode($signature)) {
    echo 'Verification successful';
  } else {
    echo 'Verification failed';
  }
}
```


## Recommended testing process
### Generate new account:   
1. Press the <kbd> <br> Register Link <br> </kbd> button
2. Enter new account information 
3. Press <kbd> <br> Recharge Link<br> </kbd>, Open the Recharge page and confirm that SP Coin is 0

### Switch account:
1. Press the <kbd> <br> Login Link <br> </kbd> button 
2. Enter the account you want to use 
3. Press <kbd> <br> Recharge Link<br> </kbd>, and complete the recharge
4. Press <kbd> <br> Consume SP Link<br> </kbd>,Open the consumption page and complete the consumption

## Model 
```mermaid 
classDiagram 
    class ConnectBasic{
      +client_id 
      +X_Developer_Id  
      +client_secret  
      +Game_id  
      +referralCode  
    }

    class AccountInitRequest{
      +email 
      +password  
    }

    class ConnectToken{
      +access_token 測試 
      +token_type 固定 Bearer
      +expires_in 有效時間 (秒) 
      +scope 
      +refresh_token  refresh時帶入
    }
    
    class MeData{
      +email
      +nickName
      +avatarUrl
      +eCoin
      +rebate
    }

    MeInfo<|-- MeData
    class MeInfo{
      +MeData data
      +StatusCode status
      +message
      +requestNumber
    }

    class PaymentResponse{
      +transactionId
      +spCoin
      +rebate
      +orderStatus 
    }


```

## License
Android Utlity SDK is licensed with the MIT License. For more details, see LICENSE.







