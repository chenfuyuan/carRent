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

import com.alibaba.fastjson.JSON;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeListAdapter extends BaseAdapter {

    private List<Car> carList;
    private LayoutInflater inflater;

    private static final String TAG = "HomeListAdapter";
    private Activity activity;

    //Dialog的控件
    private TextView text_time;
    private DatePicker sub_date_picker;
    private TimePicker sub_time_picker;
    private EditText edit_mobile;
    private Button btn_confirm;
    private Button btn_cancel;
    private Dialog dialog;
    private ListView homeList;
    private HomeListAdapter homeListAdapter;
    public HomeListAdapter() {
    }

    public HomeListAdapter(List<Car> carList, Context context, Activity activity) {
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
        homeList = activity.findViewById(R.id.homeList);
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
        setListener(btn_subScript,car);
        return view;
    }

    private void setListener(Button btn_subScript, Car car) {
        btn_subScript.setOnClickListener(view->{

            //生成弹窗
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = LayoutInflater.from(activity);
            View dialogView = inflater.inflate(R.layout.activity_sub, null);
            builder.setView(dialogView);
            dialog = builder.create();
            dialog.show();

            FindAllByDiaLogView(dialogView);

            setListenerToDiaLogView(car);
            //获取控件

        });
    }

    private void setListenerToDiaLogView(Car car) {
        //设置确认预约按钮
        setBtnConfirmListener(car);

        setBtnCancelListener();
    }

    private void setBtnCancelListener() {
        btn_cancel.setOnClickListener(view->{
            dialog.hide();
        });
    }


    private void setBtnConfirmListener(Car car) {
        btn_confirm.setOnClickListener(view -> {
            String subDateTime = GetTimeUtils.getTime(sub_date_picker, sub_time_picker);
            String phone = edit_mobile.getText().toString();
            if (phone.equals("")) {
                ToastUtil.showToast(activity,"电话号码不能为空");
                return;
            }
            if(!EditCheckUtils.checkPhone(phone)){
                ToastUtil.showToast(activity, "电话号码格式错误");
                return;
            }

            SharedPreferences sharedPreferences = activity.getSharedPreferences("user", Context.MODE_PRIVATE);
            String token = sharedPreferences.getString("token", null);
            SubCarVo subCarVo = new SubCarVo();
            subCarVo.setPhone(phone);
            subCarVo.setSubDateTime(subDateTime);
            subCarVo.setToken(token);
            subCarVo.setCar(car);
            String path = "subCar";
            Log.d(TAG, "setBtnConfirmListener: 要传输的数据为"+subCarVo);
            Call call = OkHttpUtils.postRequestBuild(path, subCarVo);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.d(TAG, "onFailure: e " + e.getMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    UpdateUiUtils.updateUi(activity,()->{
                        dialog.hide();
                        carList.remove(car);
                        HomeListAdapter homeListAdapter = new HomeListAdapter(carList, activity, activity);
                        homeList.setAdapter(homeListAdapter);
                    });

                }
            });
        });

    }

    private void FindAllByDiaLogView(View dialogView) {
        text_time = dialogView.findViewById(R.id.text_time);
        sub_date_picker = dialogView.findViewById(R.id.sub_date_picker);
        sub_time_picker = dialogView.findViewById(R.id.sub_time_picker);
        btn_confirm = dialogView.findViewById(R.id.btn_confirm);
        btn_cancel = dialogView.findViewById(R.id.btn_cancel);
        edit_mobile = dialogView.findViewById(R.id.edit_mobile);
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
