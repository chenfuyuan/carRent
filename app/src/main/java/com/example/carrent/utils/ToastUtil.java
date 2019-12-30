package com.example.carrent.utils;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

public class ToastUtil {
    public static void showToast(final Activity activity, final String message) {
        //判断是否在主线程
        //在主线程直接调用Toast输出
        if ("main".equals(Thread.currentThread().getName())) {
            Log.e("ToastUtil", "在主线程");
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        } else {
            //不在主线程
            //使用runOnUiThread输出Toast
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("ToastUtil", "不在主线程");
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
