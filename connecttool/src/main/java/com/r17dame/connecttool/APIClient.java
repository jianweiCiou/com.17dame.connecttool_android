package com.r17dame.connecttool;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


//    const currencyOptions = ref([
//            { key: 1, value: 'USD' },
//            { key: 2, value: 'TWD' },
//            { key: 4, value: 'CNY' },
//            { key: 8, value: 'JPY' },
//            { key: 16, value: 'KRW' },
//            { key: 32, value: 'VND' },
//            { key: 64, value: 'THB' },
//            { key: 128, value: 'MYR' },
//            { key: 256, value: 'SGD' }
//            ])


class APIClient {

    private static Retrofit retrofit = null;
    public static String host = "https://r18stage-portal.azurewebsites.net";
    public static String game_api_host = "https://r18stage-gameapi.azurewebsites.net";

//  public static String host = "https://www.17dame.com";
//  public static String game_api_host = "https://gameapi.17dame.com";

    static Retrofit getHostClient(ConnectTool.TOOL_VERSION _toolVS) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        String _host = _toolVS.equals(ConnectTool.TOOL_VERSION.testVS) ? "https://r18stage-portal.azurewebsites.net" : "https://www.17dame.com";

        retrofit = new Retrofit.Builder()
                .baseUrl(_host)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }

    static Retrofit getGame_api_hostClient(ConnectTool.TOOL_VERSION _toolVS) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        String _game_api_host = _toolVS.equals(ConnectTool.TOOL_VERSION.testVS) ? "https://r18stage-gameapi.azurewebsites.net" : "https://gameapi.17dame.com";

        retrofit = new Retrofit.Builder()
                .baseUrl(_game_api_host)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }

}