package com.example.carrent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.alibaba.fastjson.JSON;
import com.example.carrent.adapter.SubCarListAdapter;
import com.example.carrent.model.SubCar;
import com.example.carrent.model.User;
import com.example.carrent.utils.OkHttpUtils;
import com.example.carrent.utils.UpdateUiUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MySubActivity extends AppCompatActivity {
    private Button btn_select_all;
    private Button btn_select_sub;
    private Button btn_select_end;
    private ListView l_subcar;
    private List<SubCar> subCarList;
    private static final String TAG = "MySubActivity";
    private User user;
    private SubCarListAdapter subCarListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mysub);
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        Log.d(TAG, "onCreate: 从intent获取的user = " + user);
        findAllView();
        String path = "getAllSubCarList?uid=" + user.getId();
        initSubCarList(path,0);
        setListener();
    }

    private void setListener() {
        setBtnAll();
        setBtnSub();
        setBtnEnd();
    }

    private void setBtnEnd() {
        btn_select_end.setOnClickListener(view->{
            String path = "getEndSubCarList?uid=" + user.getId();
            initSubCarList(path,2);
        });
    }

    private void setBtnSub() {
        btn_select_sub.setOnClickListener(view -> {
            String path = "getSubCarList?uid=" + user.getId();
            initSubCarList(path, 1);
        });
    }

    private void setBtnAll() {
        btn_select_all.setOnClickListener(view -> {
            String path = "getAllSubCarList?uid=" + user.getId();
            initSubCarList(path,0);
        });
    }

    /**
     * 初始化租车信息列表
     */
    private void initSubCarList(String path,int btnSelectState) {
        Log.d(TAG, "initSubCarList: 初始化SubCarList");
        Call call = OkHttpUtils.getRequestBuild(path, null);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure: e" + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                subCarList = JSON.parseArray(response.body().string(), SubCar.class);
                Log.d(TAG, "onResponse: subCarList.size = " + subCarList.size());
                Log.d(TAG, "onResponse: subCarList = " + subCarList);
                UpdateUiUtils.updateUi(MySubActivity.this,()->{
                    subCarListAdapter = new SubCarListAdapter(subCarList, MySubActivity.this, MySubActivity.this,btnSelectState);
                    l_subcar.setAdapter(subCarListAdapter);
                });
            }
        });
    }

    private void findAllView() {
        btn_select_all = findViewById(R.id.btn_select_all);
        btn_select_sub = findViewById(R.id.btn_select_sub);
        btn_select_end = findViewById(R.id.btn_select_end);
        l_subcar = findViewById(R.id.l_subcar);

    }



}
