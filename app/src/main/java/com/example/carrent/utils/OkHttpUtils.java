package com.example.carrent.utils;

import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OkHttpUtils {

    public static final String BASE_URL = "http://10.0.2.2:8080/";
    public static final MediaType JSONTYPE
            = MediaType.get("application/json; charset=utf-8");

    public static Call getRequestBuild(String path, Map<String,String>parmMap) {//建立OkHttp客户端
        OkHttpClient client = new OkHttpClient();
        StringBuilder url = new StringBuilder(BASE_URL);
        url.append(path);
        //添加url后的参数
        if (parmMap != null) {
            url.append("?");
            Iterator<Map.Entry<String, String>> entries = parmMap.entrySet().iterator();
            //遍历Entry
            while (entries.hasNext()) {
                //获取entry
                Map.Entry<String, String> entry = entries.next();

                url.append(entry.getKey() + "=");
                url.append(entry.getValue());
                if (entries.hasNext()) {
                    url.append("&");
                }
            }
            System.out.println(url.toString());


        }

        //构建Request对象
        //采用建造者模式，链式调用指明进行get请求，传入Get的请求地址
        Request request = new Request.Builder()
                .get()
                .url(url.toString())
                .build();

        //调用请求
        return client.newCall(request);
    }

    public static Call postRequestBuild(String path, Object object) {
        //客户端
        OkHttpClient client = new OkHttpClient();
        //封装请求体
        String json = JSON.toJSONString(object);
        RequestBody requestBody = RequestBody.create(JSONTYPE, json);

        String url = BASE_URL + path;
        //生成post请求
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        System.out.println(json);
        System.out.println(request.body());
        System.out.println(request.url());
        //发出请求
        return client.newCall(request);

    }

}
