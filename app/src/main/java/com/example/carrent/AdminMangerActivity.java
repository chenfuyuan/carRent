package com.example.carrent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.example.carrent.adapter.AdminCheckAdapter;
import com.example.carrent.adapter.HomeListAdapter;
import com.example.carrent.model.Car;
import com.example.carrent.utils.OkHttpUtils;
import com.example.carrent.utils.UpdateUiUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AdminMangerActivity extends AppCompatActivity {
    private ImageView flush;
    private ListView l_check;
    private static final String TAG = "AdminMangerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manger);
        findAllView();
        initCarList();
        setListener();
    }

    private void setListener() {
        flush.setOnClickListener(view->{
            initCarList();
        });
    }

    private void initCarList() {
        String path = "getCheckCarList";
        Call call = OkHttpUtils.getRequestBuild(path, null);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                List<Car> carList = JSON.parseArray(response.body().string(), Car.class);
                Log.d(TAG, "onResponse: carList = " + carList );

                UpdateUiUtils.updateUi(AdminMangerActivity.this,()->{
                    AdminCheckAdapter adminCheckAdapter = new AdminCheckAdapter(carList,AdminMangerActivity.this,AdminMangerActivity.this);

                    l_check.setAdapter(adminCheckAdapter);

                });
            }
        });
    }

    private void findAllView() {
        flush = findViewById(R.id.flush);
        l_check = findViewById(R.id.l_check);
    }
}
