package com.example.carrent;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.example.carrent.adapter.HomeListAdapter;
import com.example.carrent.model.Car;
import com.example.carrent.model.User;
import com.example.carrent.utils.OkHttpUtils;
import com.example.carrent.utils.UpdateUiUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FragmentHome extends Fragment {

    private ListView homeList;
    private HomeListAdapter homeListAdapter;
    private ImageView flush;
    private static final String TAG = "FragmentHome";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);
        findAllView(view);
        initCarList();
        setListener();
        return view;

    }

    private void setListener() {
        flush.setOnClickListener(view->{
            initCarList();
        });
    }

    /**
     * 从服务端拿到车辆列表并初始化
     */
    private void initCarList() {
        String path = "getCarList";
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

                    UpdateUiUtils.updateUi(getActivity(),()->{
                        homeListAdapter = new HomeListAdapter(carList, getContext(),getActivity());

                        homeList.setAdapter(homeListAdapter);

                    });
            }
        });
    }



    private void findAllView(View view) {
        homeList = view.findViewById(R.id.homeList);
        flush = view.findViewById(R.id.flush);
    }
}
