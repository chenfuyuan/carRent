package com.example.carrent;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.carrent.model.Car;
import com.example.carrent.model.User;
import com.example.carrent.utils.GetTimeUtils;
import com.example.carrent.utils.OkHttpUtils;
import com.example.carrent.utils.ToastUtil;
import com.example.carrent.utils.UpdateUiUtils;
import com.example.carrent.vo.ImageUploadMessage;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PublishActivity extends AppCompatActivity {
    private static final String TAG = "PublishActivity";
    private EditText edit_car;
    private EditText edit_carNumber;
    private Button btn_selectImg;
    private DatePicker start_date_picker;
    private DatePicker end_date_picker;
    private TimePicker start_time_picker;
    private TimePicker end_time_picker;
    private Button btn_publish;
    private Car car;
    private User user;
    private final static int SELECT_PHOTO = 2;
    private ImageView car_image;
    private String img_src;
    private Uri img_uri;
    private String imagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        Log.d(TAG, "onCreate: " + user);
        //找寻所有控件
        findAllView();

        setListener();
    }


    private void setListener() {
        setBtnPublishListener();
        setUpCarImageListener();
    }

    private void setUpCarImageListener() {
        btn_selectImg.setOnClickListener(view -> {
            Log.d(TAG, "setUpCarImageListener: ");
            Intent intent=new Intent();
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            if(Build.VERSION.SDK_INT<19){
                intent.setAction(Intent.ACTION_GET_CONTENT);
            }else{
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            }
            startActivityForResult(intent,SELECT_PHOTO);});

    }

    /**
     * 设置确认发布按钮监听器
     * 将数据存储在Car，并把图片上传后，保存预约记录
     */
    private void setBtnPublishListener() {
        btn_publish.setOnClickListener(view->{
            String carNumber = edit_carNumber.getText().toString();
            String carType = edit_car.getText().toString();
            String start = GetTimeUtils.getTime(start_date_picker, start_time_picker);
            String end = GetTimeUtils.getTime(end_date_picker, end_time_picker);
            car = new Car();
            car.setUser(user);
            car.setStartTime(start);
            car.setEndTime(end);
            car.setCarNumber(carNumber);
            car.setCarType(carType);
            Log.d(TAG, "setBtnPublishListener: car = " + car);
            uploadCarImage();
            Log.d(TAG, "setListener: startTime" + start);
            Log.d(TAG, "setListener: end" + end);
        });
    }

    private boolean uploadCarImage() {
        ContentResolver cr = this.getContentResolver();
        if (img_uri == null) {
            ToastUtil.showToast(this,"未选择头像");
            return false;
        }
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(img_uri));
            Log.d(TAG, "uploadImage: bitMap no image = " + bitmap );
            saveMyBitmap(getBaseContext(),bitmap);
            final  String path= Environment.getExternalStorageDirectory() + "/pic/head.jpg";

            File file = new File(path);
            String servicePath = "upLoadImage";
            Call call = OkHttpUtils.ImageRequestBuild(servicePath,file.toString());
            Log.d(TAG, "uploadImage: file " + file.toString());
            Log.d(TAG, "uploadImage: file_path " + file.getPath());
            Log.d(TAG, "uploadImage: call = " + call);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.d(TAG, "onFailure: "+e.getMessage().toString());
                    ToastUtil.showToast(PublishActivity.this,"照片上传失败");

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    ImageUploadMessage message = JSON.parseObject(response.body().string(), ImageUploadMessage.class);
                    Log.d(TAG, "onResponse: message = " + message);
                    if (message.isSuccess()) {
                        imagePath = message.getPath();
                        Log.d(TAG, "onResponse:" + "照片上传成功");
                        Log.d(TAG, "onResponse: 路径为:" + imagePath);
                        car.setImagePath(imagePath);
                        saveCarMessage();
                    } else {
                        ToastUtil.showToast(PublishActivity.this, message.getMessage());
                    }
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return true;
    }

    private void saveCarMessage() {
        String path = "saveCarMessage";
        Call call = OkHttpUtils.postRequestBuild(path, car);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d(TAG, "onResponse: reponse = "  +  response.body().string());
                ToastUtil.showToast(PublishActivity.this,"发布成功");
                finish();
            }
        });
    }

    /**
     * 图片选择后返回
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == SELECT_PHOTO) {
            switch (resultCode) {
                case RESULT_OK:
                    img_uri = data.getData();
                    img_src =img_uri.getPath();    //本机图片路径
                    ContentResolver cr=this.getContentResolver();
                    try {
                        Bitmap bitmap= BitmapFactory.decodeStream(cr.openInputStream(img_uri));
                        car_image.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


            }

        }
    }
    private String getTime(DatePicker start_date_picker, TimePicker start_time_picker) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(start_date_picker.getYear() + "-");
        stringBuilder.append(start_date_picker.getMonth()+1+ "-");
        stringBuilder.append(start_date_picker.getDayOfMonth()+" ");
        stringBuilder.append(start_time_picker.getCurrentHour() + ":");
        stringBuilder.append(start_time_picker.getCurrentMinute() + ":00");
        return stringBuilder.toString();
    }


    /**
     * 保存到指定路径
     * @param baseContext
     * @param bitmap
     */
    private void saveMyBitmap(Context baseContext, Bitmap bitmap) {
        String sdCardDir = Environment.getExternalStorageDirectory() + "/pic/";
        Log.d(TAG, "saveMyBitmap: sdCardDir" + sdCardDir);
        File appDir = new File(sdCardDir);
        Log.d(TAG, "saveMyBitmap: appDir " + appDir.exists());
        if (!appDir.exists()) {//不存在
            appDir.mkdir();
        }
        Log.d(TAG, "saveMyBitmap: appDir 再次" +appDir.exists());
        String fileName =  "car.jpg";
        File file = new File(appDir, fileName);
        try {
            Log.d(TAG, "saveMyBitmap: file = " + file);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        PublishActivity.this.sendBroadcast(intent);
        Toast.makeText(PublishActivity.this,"图片保存成功",Toast.LENGTH_SHORT).show();
    }


    private void findAllView() {
        Log.d(TAG, "findAllView: 开始找寻所有控件");
        edit_car = findViewById(R.id.edit_car);
        edit_carNumber = findViewById(R.id.edit_carNumber);
        btn_selectImg = findViewById(R.id.btn_selectImg);
        start_date_picker = findViewById(R.id.start_date_picker);
        end_date_picker = findViewById(R.id.end_date_picker);
        start_time_picker = findViewById(R.id.start_time_picker);
        end_time_picker = findViewById(R.id.end_time_picker);
        btn_publish = findViewById(R.id.btn_publish);
        car_image = findViewById(R.id.car_image);
    }
}
