package com.example.sunnyweather.util;

/**
 * Created by Administrator on 2016/11/3.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
