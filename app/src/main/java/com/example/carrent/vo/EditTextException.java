package com.example.carrent.vo;

import android.widget.EditText;

public class EditTextException extends Exception {
    // 提供无参数的构造方法
    public EditTextException() {
    }

    // 提供一个有参数的构造方法，可自动生成
    public EditTextException(String message) {
        super(message);// 把参数传递给Throwable的带String参数的构造方法
    }
}
