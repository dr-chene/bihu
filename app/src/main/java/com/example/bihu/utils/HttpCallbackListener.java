package com.example.bihu.utils;

/**
 * 网络请求回调接口
 */
public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);

    void onNetworkError();
}
