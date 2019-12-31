package com.example.carrent.utils;

import android.widget.Toast;

import com.example.carrent.vo.EditTextException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditCheckUtils {

    /**
     * 判断非空
     * @param values
     * @throws EditTextException
     */
    public static boolean checkNull(String... values){
        for (String value : values) {
            if (value == null || value.equals("") ) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkPhone(String phone) {
        Pattern p = null;
        Matcher m = null;

        //验证手机号格式
        p = Pattern.compile("^[1](([3][0-9])|([4][5-9])|([5][0-3,5-9])|([6][5,6])|([7][0-8])|([8][0-9])|([9][1,8,9]))[0-9]{8}$"); // 验证手机号
        m = p.matcher(phone);
        if (!m.matches()) {
            return false;
        }
        return true;
    }
}
