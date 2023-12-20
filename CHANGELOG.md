# CHANGELOG.md
## 1.3.3 (unreleased)
Features: 
  - Add  the App-side's  response.
## 1.3.2 (unreleased)
Fix:
  - PaymentResponse null problem.
Features:
  - Add SendLogout
  - Add OpenRechargeURL
  - Add OpenConsumeSPURL
  - Add OpenRegisterURL
  - Add OpenLoginURL
  - Add OpenLogoutURL
  - Add OpenConsumeSPResultURL
  - Add Get_SPCoin_tx
  - Add set_purchase_notifyData
  - Add set_purchase_notifyData
  - Add CreateSPCoinOrder
 
## 1.3.1 (unreleased)
Features:
  - Change minSdk 22 to minSdk 26.
  - AndroidManifest.xml remove scheme="r17dame".
  - MainActivity.java add Intent to startActivity.
  - build.gradle add buildFeatures
  - connecttool add scheme="r17dame" and CallBackActivity
  - APIInterface.java add createPayment,createPurchaseOrder,ACPAYPayWithPrime, GetPurchaseOrderList, GetPurchaseOrderOne, GetUserCards
  - Add ACPAYPayWithPrimeCallback.java, CreatePaymentCallback.java, CreatePurchaseOrderCallback .java, GetPurchaseOrderListCallback.java, GetPurchaseOrderOneCallback.java, GetUserCardsCallback.java, MeCallback.java, PurchaseOrderCallback.java
  - ConnectTool.java add OpenRechargeURL,OpenConsumeSPURL, OpenConsumeSPResultURL, createPayment, GetPurchaseOrderList , GetPurchaseOrderOne, GetUserCards, ACPAYPayWithPrime, CreatePurchaseOrder
  - Add ConnectToolSampleActivity 
