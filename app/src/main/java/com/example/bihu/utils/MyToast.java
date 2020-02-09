package com.example.bihu.utils;

import android.widget.Toast;

public class MyToast {
    private static Toast toast;

    public static void showToast(String msg) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(MyApplication.getContext(), msg, Toast.LENGTH_SHORT);
        toast.show();
    }
}
