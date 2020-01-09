package com.example.carrent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.alibaba.fastjson.JSON;
import com.example.carrent.adapter.MyFragmentAdapter;
import com.example.carrent.model.User;
import com.example.carrent.utils.OkHttpUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends FragmentActivity {
    private static final String TAG = "MainActivity";
    private GridView gv_buttom_menu;
    private ViewPager mViewPager;
    private ArrayList fragments;
    private SharedPreferences sharedPreferences;
    private User user;
    private FragmentMy fragmentMy;
    private FragmentHome fragmentHome;
    private FragmentTransaction transaction;
    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUser();
        initFragment();
        loadButtonMenu();
        initViewPager();




    }

    private void initFragment() {

    }

    private void initUser() {
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        Call call = OkHttpUtils.getRequestBuild("checkToken", map);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                user = JSON.parseObject(response.body().string(), User.class);
                if (user == null) {
                    Log.d(TAG, "onResponse: user = null");
                    return;
                }else{
                    Log.d(TAG, "onResponse: user = " + user);
                }

            }
        });
    }

    private void initViewPager() {
        mViewPager = findViewById(R.id.myViewPager);
        fragments = new ArrayList<Fragment>();
        fragmentHome = new FragmentHome();
        fragmentMy = new FragmentMy();
        fragments.add(fragmentHome);
        fragments.add(fragmentMy);
        mViewPager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager(), fragments));
        mViewPager.setCurrentItem(0);
    }

    /**
     * 加载按钮控件
     */
    private void loadButtonMenu() {
        gv_buttom_menu = this.findViewById(R.id.gv_button_menu);
        gv_buttom_menu.setNumColumns(2);    //设置列数
        gv_buttom_menu.setGravity(Gravity.CENTER);
        gv_buttom_menu.setVerticalSpacing(10);
        gv_buttom_menu.setHorizontalSpacing(10);
        ArrayList data = new ArrayList();
        HashMap map = new HashMap();

        initButtomMenuMap(data, map);


        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.item_menu, new String[]{"itemImage", "itemText"}, new int[]{R.id.item_image, R.id.item_text});

        gv_buttom_menu.setAdapter(adapter);
        gv_buttom_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mViewPager.setCurrentItem(i);
            }
        });


    }

    private void initButtomMenuMap(ArrayList data, HashMap map) {
        map.put("itemImage", R.drawable.home);
        map.put("itemText", "首页");
        data.add(map);


        map = new HashMap();
        map.put("itemImage", R.drawable.my);
        map.put("itemText", "我的");
        data.add(map);

    }
}
