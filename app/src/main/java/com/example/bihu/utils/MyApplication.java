package com.example.bihu.utils;

import android.app.Application;
import android.content.Context;

/**
 * 全局获得context
 */
public class MyApplication extends Application {
    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
