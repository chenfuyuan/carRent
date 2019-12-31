package com.example.carrent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.example.carrent.utils.EditCheckUtils;
import com.example.carrent.utils.OkHttpUtils;
import com.example.carrent.utils.ToastUtil;
import com.example.carrent.vo.EditTextException;
import com.example.carrent.vo.SignInMessage;
import com.example.carrent.vo.SignInVo;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = "SignInActivity";
    private EditText edit_phone;
    private EditText edit_password;

    private Button btn_signIn;
    private Button btn_signUp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        //找到所有控件
        findAllView();
        setListener();
    }

    /**
     * 添加监听器
     */
    private void setListener() {
        setSignUpListener();
        setSignInListener();
    }

    private void setSignInListener() {
        btn_signIn.setOnClickListener(view->{
            SignInVo signInVo = getEditToSignInVo();
            if (signInVo != null) {
                //向后台发送登录请求
                signIn(signInVo);
            }
        });
    }

    /**
     * 将输入框数据封装到SignInVo对象中
     */
    private SignInVo getEditToSignInVo() {
        SignInVo signInVo = new SignInVo();
        String phone = edit_phone.getText().toString();
        String password = edit_password.getText().toString();

        //判断是否为空
        if (EditCheckUtils.checkNull(phone, password)) {
            ToastUtil.showToast(SignInActivity.this, "有信息未填写");
            return null;
        }

        //判断手机号码是否符合格式
        if (!EditCheckUtils.checkPhone(phone)) {
            ToastUtil.showToast(SignInActivity.this,"手机号码格式错误");
            return null;
        }

        signInVo.setPhone(phone);
        signInVo.setPassword(password);
        signInVo.setRememberPassword(false);
        return signInVo;
    }

    /**
     * 向后台发送登录请求
     * @param signInVo
     */
    private void signIn(SignInVo signInVo) {
        String path = "checkSignIn";    //请求路径
        Call call = OkHttpUtils.postRequestBuild(path, signInVo);    //获取请求
        //异步处理请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                ToastUtil.showToast(SignInActivity.this, "登录失败");
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                SignInMessage signInMessage = JSON.parseObject(response.body().string(), SignInMessage.class);
                System.out.println(signInMessage);

                //成功则跳转到主界面
                if (signInMessage.isSuccess()) {
                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    //失败显示失败结果
                    ToastUtil.showToast(SignInActivity.this,signInMessage.getMessage());
                }
            }
        });
    }

    /**
     * 设置注册按钮监听器
     */
    private void setSignUpListener() {
        btn_signUp.setOnClickListener(view->{
            Log.d(TAG, "setSignUpListener: " + "进行注册");
            Intent intent = new Intent(SignInActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    /**
     * 找到所有控件
     */
    private void findAllView() {
        edit_phone = findViewById(R.id.edit_phone);
        edit_password = findViewById(R.id.edit_password);
        btn_signIn = findViewById(R.id.btn_signin);
        btn_signUp = findViewById(R.id.btn_signup);
    }
}
