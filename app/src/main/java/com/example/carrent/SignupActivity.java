package com.example.carrent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;
import com.example.carrent.utils.OkHttpUtils;
import com.example.carrent.utils.ToastUtil;
import com.example.carrent.vo.EditTextException;
import com.example.carrent.vo.ImageUploadMessage;
import com.example.carrent.vo.SendSmsMessage;
import com.example.carrent.vo.SignUpMessage;
import com.example.carrent.vo.SignUpVo;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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
    private Button btn_return;
    private ImageView select_img;

    public static final String BASE_URL = "http://10.0.2.2:8080/";
    public static final MediaType JSONTYPE
            = MediaType.get("application/json; charset=utf-8");
    private SignUpVo signUpVo;
    private static final int SELECT_PHOTO = 1;
    private String img_src;
    private Uri img_uri;
    private String imagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        findAllView();

        setLisenter();
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
        btn_return = findViewById(R.id.btn_return);

        select_img = findViewById(R.id.select_img);
    }

    private void setLisenter() {
        //验证码发送
        btn_authCode.setOnClickListener(view -> {
            String phone = edit_phone.getText().toString();
            //检测手机号格式
            if (!checkPhone(phone)) {
                return;
            }
            //发送验证码
            sendSms(phone);
        });

        //注册
        btn_signup.setOnClickListener(view ->{
            //判断输入框信息是否输入正确
            if (!editHasError()) {
                if (uploadImage()) {
                } else {
                    ToastUtil.showToast(SignupActivity.this,"注册失败");
                }

            }
        });

        //返回
        btn_return.setOnClickListener(view -> {
            finish();
        });

        //进行相册页跳转
        select_img.setOnClickListener(view->{
            Intent intent=new Intent();
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            if(Build.VERSION.SDK_INT<19){
                intent.setAction(Intent.ACTION_GET_CONTENT);
            }else{
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            }
            startActivityForResult(intent,SELECT_PHOTO);

        });
    }

    /**
     * 上传图片
     *
     * @return
     */
    private boolean uploadImage() {
        ContentResolver cr = this.getContentResolver();
        if (img_uri == null) {
            ToastUtil.showToast(SignupActivity.this,"未选择头像");
            return false;
        }
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(img_uri));
            Log.d(TAG, "uploadImage: bitMap no image = " + bitmap );
            saveMyBitmap(getBaseContext(),bitmap);
            final  String path=Environment.getExternalStorageDirectory() + "/pic/head.jpg";

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
                    ToastUtil.showToast(SignupActivity.this,"照片上传失败");

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    ImageUploadMessage message = JSON.parseObject(response.body().string(), ImageUploadMessage.class);
                    Log.d(TAG, "onResponse: message = " + message);
                    if (message.isSuccess()) {
                        imagePath = message.getPath();
                        Log.d(TAG, "onResponse:" + "照片上传成功");
                        Log.d(TAG, "onResponse: 路径为:" + imagePath);
                        signUp();
                    } else {
                        ToastUtil.showToast(SignupActivity.this, message.getMessage());
                    }
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return true;
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
        String fileName =  "head.jpg";
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
        SignupActivity.this.sendBroadcast(intent);
        Toast.makeText(SignupActivity.this,"图片保存成功",Toast.LENGTH_SHORT).show();
    }


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
                        select_img.setImageBitmap(bitmap);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


            }

        }
    }
    /**
     * 向服务器发送注册请求
     */
    private void signUp() {
        String path = "checkSignUp";
        signUpVo.setImagePath(imagePath);
        Call call = OkHttpUtils.postRequestBuild(path, signUpVo);

        //异步处理请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                ToastUtil.showToast(SignupActivity.this,"注册失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //获取响应
                SignUpMessage message = JSON.parseObject(response.body().string(),SignUpMessage.class);
                if (message.isSuccess()) {
                    Log.d(TAG, "onResponse: 注册成功");
                    Intent intent = new Intent(SignupActivity.this, SignInActivity.class);
                    startActivity(intent);

                } else {
                    ToastUtil.showToast(SignupActivity.this,message.getMessage());
                }

            }
        });
    }

    /**
     *  获取文本框数据，并判断是否输入有误，无误时封装入signUpVo对象进行传输
     * @return
     */
    private boolean editHasError() {
        //获取文本框数据
        try {
            signUpVo = getEditMessage();

        } catch (EditTextException e) {
            ToastUtil.showToast(SignupActivity.this, e.getMessage());
            return true;
        }
        return false;
    }

    /**
     * 获取输入框的数据，并封装进SignVo中进行传输
     * @return
     * @throws EditTextException
     */
    private SignUpVo getEditMessage() throws EditTextException {
        SignUpVo signUpVo = new SignUpVo();

        //获取输入框的输入
        String name = edit_username.getText().toString();
        String phone = edit_phone.getText().toString();
        String password = edit_password.getText().toString();
        String repassword = edit_rpassword.getText().toString();
        String authCode = edit_authcode.getText().toString();

        //判断输入框值是否为空
        if (name.equals("")) {
            throw new EditTextException("用户名不能为空");
        }
        if (password.equals("")) {
            throw new EditTextException("密码不能为空");
        }
        if (repassword.equals("")) {
            throw new EditTextException("确认密码不能为空");
        }
        if (authCode.equals("")) {
            throw new EditTextException("验证码不能为空");
        }

        if (!checkPhone(phone)) {
            throw new EditTextException("电话号码错误");
        }

        if (password.length() < 6) {
            throw new EditTextException("密码长度过短");
        }
        //判断密码和确认密码是否一致
        if (!password.equals(repassword)) {
            throw new EditTextException("两次输入密码不一致");
        }

        //填充数据
        signUpVo.setName(name);
        signUpVo.setPhone(phone);
        signUpVo.setAuthCode(authCode);
        signUpVo.setPassword(password);
        return signUpVo;
    }


    /**
     * 向服务端发送GET请求，请求服务端发送短信验证码
     * @param phone
     */
    private void sendSms(String phone) {

        Map<String, String> map = new HashMap<>();
        map.put("phone", phone);
        Call call = OkHttpUtils.getRequestBuild("authCode", map);

        //异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure: error:"+e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                int code = response.code();
                //反序列化

                SendSmsMessage message = JSON.parseObject( response.body().string(),SendSmsMessage.class);
                Log.d(TAG, "onResponse: message = " + message);
                if (!message.isSuccess()) {
                    System.out.println("短信发送失败");
                    ToastUtil.showToast(SignupActivity.this, message.getMessage());
                    return;
                }else{
                    ToastUtil.showToast(SignupActivity.this,"短信发送成功");
                    return;
                }
            }
        });
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



}
