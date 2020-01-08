package com.example.carrent.utils;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

public class UpdateUiUtils {
    public static void updateUi(Activity activity,Runnable runnable) {
        //判断是否在主线程
        //在主线程直接调用Toast输出
        if ("main".equals(Thread.currentThread().getName())) {
            Log.e("UpdateUiUtils", "在主线程");
            runnable.run();
        } else {
            //不在主线程
            //使用runOnUiThread输出Toast
            Log.e("UpdateUiUtils", "不在主线程");
            activity.runOnUiThread(runnable);
        }
    }
}
