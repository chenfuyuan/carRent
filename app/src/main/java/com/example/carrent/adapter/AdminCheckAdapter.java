package com.example.carrent.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.carrent.R;
import com.example.carrent.model.Car;
import com.example.carrent.model.User;
import com.example.carrent.utils.EditCheckUtils;
import com.example.carrent.utils.GetTimeUtils;
import com.example.carrent.utils.OkHttpUtils;
import com.example.carrent.utils.ToastUtil;
import com.example.carrent.utils.UpdateUiUtils;
import com.example.carrent.vo.SubCarVo;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AdminCheckAdapter extends BaseAdapter {

    private List<Car> carList;
    private LayoutInflater inflater;

    private static final String TAG = "AdminCheckAdapter";
    private Activity activity;

    //Dialog的控件
    private TextView text_time;
    private DatePicker sub_date_picker;
    private TimePicker sub_time_picker;
    private EditText edit_mobile;
    private Button btn_confirm;
    private Button btn_cancel;
    private Dialog dialog;
    public AdminCheckAdapter() {
    }

    public AdminCheckAdapter(List<Car> carList, Context context, Activity activity) {
        this.carList = carList;
        this.inflater = LayoutInflater.from(context);
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return carList == null ? 0 : carList.size();
    }


    @Override
    public Object getItem(int i) {
        return carList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertview, ViewGroup viewGroup) {
        //加载布局为一个视图
        View view = inflater.inflate(R.layout.home_list, null);
        Car car = carList.get(i);
        User user = car.getUser();
        Log.d(TAG, "getView: car == " + car);
        //将car的数据填充入home_list布局中
        //找寻控件
        ImageView car_image = view.findViewById(R.id.car_image);
        TextView carNumber = view.findViewById(R.id.carNumber);
        TextView carType = view.findViewById(R.id.carType);
        TextView freeTime = view.findViewById(R.id.freeTime);

        TextView name = view.findViewById(R.id.name);
        //获取图片
        name.setText(car.getUser().getName());
        initImage(car_image,car);
        carNumber.setText(car.getCarNumber());
        carType.setText(car.getCarType());
        name.setText(user.getName());
        //构建空闲时间文本
        StringBuilder freeTimeStr = new StringBuilder();
        freeTimeStr.append(car.getStartTime());
        freeTimeStr.append("到");
        freeTimeStr.append(car.getEndTime());
        freeTime.setText(freeTimeStr.toString());

        Button btn_subScript = view.findViewById(R.id.btn_subscribe);
        btn_subScript.setText("审核");
        setListener(btn_subScript,car);
        return view;
    }

    private void setListener(Button btn_subScript, Car car) {
        btn_subScript.setOnClickListener(view->{
            String path = "pass?id=" + car.getId();
            Call call = OkHttpUtils.getRequestBuild(path, null);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.d(TAG, "onFailure: "+e.getMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Log.d(TAG, "onResponse: "+response.body().string());
                    carList.remove(car);
                    UpdateUiUtils.updateUi(activity,()->{
                        AdminCheckAdapter adminCheckAdapter = new AdminCheckAdapter(carList, activity, activity);
                        ListView l_check = activity.findViewById(R.id.l_check);
                        l_check.setAdapter(adminCheckAdapter);
                    });

                }
            });

        });
    }


    private void initImage(ImageView car_image, Car car) {
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
