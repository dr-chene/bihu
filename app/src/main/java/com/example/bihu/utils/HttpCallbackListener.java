package com.example.bihu.utils;

public interface HttpCallbackListener {
    void postSuccess();
    void postFailed(String response);
}
