package com.r17dame.connecttool;


public class ConnectBasic {

    public String client_id;
    public String X_Developer_Id;
    public String client_secret;
    public String Game_id;

    public ConnectBasic(
            String _client_id,
            String _X_Developer_Id,
            String _client_secret,
            String _Game_id ) {
        client_id = _client_id;
        X_Developer_Id = _X_Developer_Id;
        client_secret = _client_secret;
        Game_id = _Game_id;

    }
}
