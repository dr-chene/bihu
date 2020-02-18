package com.example.bihu.utils;

import android.os.Handler;
import android.widget.Toast;

public class MyToast {
    private static Toast toast;
    private static Handler handler = new Handler();

    public static void showToast(final String msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(MyApplication.getContext(), msg, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}
