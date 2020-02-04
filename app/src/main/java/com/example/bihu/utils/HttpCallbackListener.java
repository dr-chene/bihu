package com.example.bihu.utils;

public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
