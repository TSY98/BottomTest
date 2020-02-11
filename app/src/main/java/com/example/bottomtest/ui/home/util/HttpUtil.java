package com.example.bottomtest.ui.home.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

//使用OKhttp，需要使用回调机制来处理服务器响应
public class HttpUtil {
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
