package com.example.carrent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.carrent.utils.ToastUtil;
import com.example.carrent.vo.SignUpVo;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private EditText edit_username;
    private EditText edit_phone;
    private EditText edit_authcode;
    private EditText edit_password;
    private EditText edit_rpassword;

    private Button btn_signup;
    private Button btn_authCode;

    private static final String BASE_URL = "http://10.0.2.2:8080/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        findAllView();

        setLisenter();
    }

    private void setLisenter() {
        btn_authCode.setOnClickListener(view -> {
            String phone = edit_phone.getText().toString();
            //检测手机号格式
            if (!checkPhone(phone)) {
                return;
            }

            //判断该手机号是否在数据库中
            if (isExistDataBase(phone)) {
                Toast.makeText(SignupActivity.this, "该手机号码已存在", Toast.LENGTH_LONG).show();
                return;
            }

        });

    }

    private boolean isExistDataBase(String phone) {
        //建立OkHttp客户端
        OkHttpClient client = new OkHttpClient();
        String url = BASE_URL + "authCode?phone=" + phone;
        //构建Request对象
        //采用建造者模式，链式调用指明进行get请求，传入Get的请求地址
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();

        //调用请求
        Call call = client.newCall(request);

        //异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure: error:"+e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                int code = response.code();
                Log.d(TAG, "onResponse: response.code == " + code);
                Log.d(TAG, "onResponse: response.body == "+ response.body().string());
            }
        });
        return false;
    }

    /**
     * 判断手机号是否输入正确
     *
     * @param phone
     * @return
     */
    private boolean checkPhone(String phone) {
        //非空判断
        if (phone.equals("")) {
            Toast.makeText(SignupActivity.this, "手机号码不能为空", Toast.LENGTH_LONG).show();
            return false;
        }

        Pattern p = null;
        Matcher m = null;

        //验证手机号格式
        p = Pattern.compile("^[1](([3][0-9])|([4][5-9])|([5][0-3,5-9])|([6][5,6])|([7][0-8])|([8][0-9])|([9][1,8,9]))[0-9]{8}$"); // 验证手机号
        m = p.matcher(phone);
        if (!m.matches()) {
            Toast.makeText(this, "电话号码格式错误", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;


    }


    /**
     * 找到所有控件
     */
    private void findAllView() {
        edit_username = findViewById(R.id.edit_username);
        edit_phone = findViewById(R.id.edit_phone);
        edit_password = findViewById(R.id.edit_password);
        edit_rpassword = findViewById(R.id.edit_rpassword);
        edit_authcode = findViewById(R.id.edit_authCode);

        btn_signup = findViewById(R.id.btn_signup);
        btn_authCode = findViewById(R.id.btn_authCode);
    }
}
