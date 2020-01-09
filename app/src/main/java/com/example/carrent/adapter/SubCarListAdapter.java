package com.example.carrent.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.carrent.MySubActivity;
import com.example.carrent.R;
import com.example.carrent.model.Car;
import com.example.carrent.model.SubCar;
import com.example.carrent.model.User;
import com.example.carrent.utils.OkHttpUtils;
import com.example.carrent.utils.UpdateUiUtils;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SubCarListAdapter extends BaseAdapter {
    private List<SubCar> subCarList;
    private LayoutInflater inflater;
    private static final String TAG = "SubCarListAdapter";
    private Activity activity;
    private User user;
    private int btnSelectState;
    public SubCarListAdapter() {
    }

    public SubCarListAdapter(List<SubCar> subCarList, Context context, Activity activity,int btnSelectState) {
        this.subCarList = subCarList;
        this.inflater = LayoutInflater.from(context);
        this.activity = activity;
        this.btnSelectState = btnSelectState;
    }

    @Override
    public int getCount() {
        return subCarList == null ? 0 : subCarList.size();
    }


    @Override
    public Object getItem(int i) {
        return subCarList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertview, ViewGroup viewGroup) {
        //加载布局为一个视图
        View view = inflater.inflate(R.layout.subcar_list, null);
        SubCar subcar = subCarList.get(i);
        user = subcar.getUser();
        Car car = subcar.getCar();
        Log.d(TAG, "getView:");
        //将car的数据填充入home_list布局中
        //找寻控件
        ImageView car_image = view.findViewById(R.id.car_image);
        TextView carNumber = view.findViewById(R.id.carNumber);
        TextView carType = view.findViewById(R.id.carType);
        TextView freeTime = view.findViewById(R.id.freeTime);
        TextView name = view.findViewById(R.id.name);
        TextView text_subtime_data = view.findViewById(R.id.text_subtime_data);
        TextView text_phone_data = view.findViewById(R.id.text_phone_data);
        Button btn_end = view.findViewById(R.id.btn_end);
        TextView text_endSubCar = view.findViewById(R.id.text_endSubCar);
        if (subcar.getState()==1) {
            btn_end.setVisibility(View.GONE);
            text_endSubCar.setVisibility(View.VISIBLE);
        }
        //获取图片
        initImage(car_image,car);
        name.setText(user.getName());
        carNumber.setText(car.getCarNumber());
        carType.setText(car.getCarType());
        //构建空闲时间文本
        StringBuilder freeTimeStr = new StringBuilder();
        freeTimeStr.append(car.getStartTime());
        freeTimeStr.append("到");
        freeTimeStr.append(car.getEndTime());
        freeTime.setText(freeTimeStr.toString());
        text_subtime_data.setText(subcar.getSubDateTime());
        text_phone_data.setText(subcar.getPhone());

        setListener(btn_end,text_endSubCar,subcar);
        return view;
    }



    private void setListener(Button btn_end, TextView text_endSubCar, SubCar subcar) {
        btn_end.setOnClickListener(view->{
            Log.d(TAG, "setListener: subcar = " + subcar);
            String path = "endSubCar?id=" + subcar.getId();
            Call call = OkHttpUtils.getRequestBuild(path, null);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.d(TAG, "onFailure: e" + e.getMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Log.d(TAG, "onResponse: 使用成功");
                    Log.d(TAG, "onResponse: body" +response.body().string());
                    UpdateUiUtils.updateUi(activity,()->{
                        if(btnSelectState == 0) {
                            btn_end.setVisibility(View.GONE);
                            text_endSubCar.setVisibility(View.VISIBLE);
                        }else{
                            subCarList.remove(subcar);
                            SubCarListAdapter subCarListAdapter = new SubCarListAdapter(subCarList,activity,activity,1);
                            ListView l_subcar = activity.findViewById(R.id.l_subcar);
                            l_subcar.setAdapter(subCarListAdapter);
                        }
                    });

                }
            });
        });

    }

    private void initImage(ImageView car_image,Car car) {
        String path = "image/" + car.getImagePath();
        Log.d(TAG, "initImage: imageurlPath = " + path);
        Call call = OkHttpUtils.getRequestBuild(path, null);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                InputStream inputStream = new BufferedInputStream(response.body().byteStream());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Log.d(TAG, "onResponse: bitmap = " + bitmap);
                UpdateUiUtils.updateUi(activity,()->{
                    car_image.setImageBitmap(bitmap);
//                    Log.d(TAG, "onResponse: 更换了"+car.getUser().getName()+"的图片");
                });


            }
        });
    }
}
