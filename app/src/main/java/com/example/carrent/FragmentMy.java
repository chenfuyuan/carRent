package com.example.carrent;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.example.carrent.model.User;
import com.example.carrent.utils.OkHttpUtils;
import com.example.carrent.utils.ToastUtil;
import com.example.carrent.utils.UpdateUiUtils;
import com.example.carrent.vo.ImageUploadMessage;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FragmentMy extends Fragment {
    private static final String TAG = "FragmentMy";
    private TextView text_name;
    private User user;
    private ImageView user_image;
    private Button publish_car;
    private Button carCheck;
    private Button btn_mySub;
    private String img_src;
    private Uri img_uri;
    private String imagePath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_my, container, false);
        findAllView(view);
        registerForContextMenu(text_name);
        initUser();
        setListener();
        return view;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        Log.d(TAG, "onCreateContextMenu: 加载菜单");
        getActivity().getMenuInflater().inflate(R.menu.user_menu, menu);

    }

    private void initUser() {
        //从Intent中获取user
        user = (User) getActivity().getIntent().getSerializableExtra("user");
        Log.d(TAG, "initUser: 从Intent中获取的user = " + user);
        if (user != null) {
            text_name.setText(user.getName());
            initUserImage();
        } else {
            //如果Intent获取不到，从SP中获取
            getUserBySp();
        }


    }

    private void initUserImage() {
        String path = "image/" + user.getImagePath();
        Call call = OkHttpUtils.getRequestBuild(path, null);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                InputStream imageStream = response.body().byteStream();
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                UpdateUiUtils.updateUi(getActivity(), () -> {
                    user_image.setImageBitmap(bitmap);
                    if (user.getType() == 1) {
                        carCheck.setVisibility(View.VISIBLE);
                    }
                });

            }
        });
    }

    private void setListener() {
        setTextNameListener();
        setPublishCarListener();
        setMySubListener();
        setCarCheckListener();
    }

    private void setCarCheckListener() {
        carCheck.setOnClickListener(view->{
            Intent intent = new Intent(getActivity(), AdminMangerActivity.class);
            startActivity(intent);
        });
    }

    private void setMySubListener() {
        btn_mySub.setOnClickListener(view->{
            Intent intent = new Intent(getActivity(),MySubActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });
    }

    private void setPublishCarListener() {
        publish_car.setOnClickListener(view -> {
            if (user == null) {
                ToastUtil.showToast(getActivity(), "未登录,请登录");
                return;
            }
            Intent intent = new Intent(getContext(), PublishActivity.class);
            Log.d(TAG, "setPublishCarListener: user + " + user);
            intent.putExtra("user", user);

            startActivity(intent);
        });
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_img:
                selectImageIntent();
                break;
            case R.id.logOut:
                logOut();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 退出登录
     */
    private void logOut() {
        text_name.setText("未登录");
        unregisterForContextMenu(text_name);
        user_image.setImageResource(R.drawable.user);
        carCheck.setVisibility(View.GONE);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();    //清空
        editor.commit();    //提交
        Log.d(TAG, "logOut: sp.getToken" + sharedPreferences.getString("token", null));

    }

    private void selectImageIntent() {
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT < 19) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        }
        startActivityForResult(intent, 1);
    }

    private void setTextNameListener() {
        text_name.setOnClickListener(view -> {
            String name = text_name.getText().toString();
            if (name.equals("未登录")) {
                Intent intent = new Intent(getContext(), SignInActivity.class);
                startActivity(intent);
            }
        });

    }

    private void getUserBySp() {
        SharedPreferences sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        //从sp中获取token
        String token = sp.getString("token", null);
        if (token != null) {
            String path = "checkToken?token=" + token;
            Call call = OkHttpUtils.getRequestBuild(path, null);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.d(TAG, "onFailure: 校验token时发生的错误信息:" + e.getMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    //将响应返回的body解析成User对象
                    user = JSON.parseObject(response.body().string(), User.class);
                    Log.d(TAG, "onResponse: 检验token后返回的user = " + user);
                    if (user != null) {
                        Runnable runnable = () -> {
                            text_name.setText(user.getName());
                            initUserImage();
                        };
                        UpdateUiUtils.updateUi(getActivity(), runnable);
                    }
                }
            });
        }
    }

    private void findAllView(View view) {
        Log.d(TAG, "findAllView: 开始找寻FragmentMy的所有控件");
        text_name = view.findViewById(R.id.text_name);
        user_image = view.findViewById(R.id.user_Image);
        publish_car = view.findViewById(R.id.publish_car);
        carCheck = view.findViewById(R.id.carCheck);
        btn_mySub = view.findViewById(R.id.btn_mySub);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            switch (resultCode) {
                case -1:
                    img_uri = data.getData();
                    img_src = img_uri.getPath();    //本机图片路径
                    ContentResolver cr = getActivity().getContentResolver();
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(img_uri));
                        user_image.setImageBitmap(bitmap);
                        changeImageToServer();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


            }

        }
    }

    private boolean changeImageToServer() {
        ContentResolver cr = getActivity().getContentResolver();
        if (img_uri == null) {
            ToastUtil.showToast(getActivity(), "未选择头像");
            return false;
        }
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(img_uri));
            Log.d(TAG, "uploadImage: bitMap no image = " + bitmap);
            saveMyBitmap(getActivity().getBaseContext(), bitmap);
            final String path = Environment.getExternalStorageDirectory() + "/pic/head.jpg";

            File file = new File(path);
            String servicePath = "upLoadImage";
            Call call = OkHttpUtils.ImageRequestBuild(servicePath, file.toString());
            Log.d(TAG, "uploadImage: file " + file.toString());
            Log.d(TAG, "uploadImage: file_path " + file.getPath());
            Log.d(TAG, "uploadImage: call = " + call);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.d(TAG, "onFailure: " + e.getMessage().toString());
                    ToastUtil.showToast(getActivity(), "照片上传失败");

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    ImageUploadMessage message = JSON.parseObject(response.body().string(), ImageUploadMessage.class);
                    Log.d(TAG, "onResponse: message = " + message);
                    if (message.isSuccess()) {
                        imagePath = message.getPath();
                        Log.d(TAG, "onResponse:" + "照片上传成功");
                        Log.d(TAG, "onResponse: 路径为:" + imagePath);
                        changeUserImage();
                    } else {
                        ToastUtil.showToast(getActivity(), message.getMessage());
                    }
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return true;
    }

    private void changeUserImage() {
        String path = "changeImagePath";
        Map<String, String> map = new HashMap<>();
        map.put("id", user.getId()+"");
        map.put("imagePath", imagePath);
        Call call = OkHttpUtils.getRequestBuild(path, map);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure: "+ e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d(TAG, "onResponse: 更改头像，数据库操作成功");

            }
        });
    }


    /**
     * 保存到指定路径
     *
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
        Log.d(TAG, "saveMyBitmap: appDir 再次" + appDir.exists());
        String fileName = "head.jpg";
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
        getActivity().sendBroadcast(intent);
        Toast.makeText(getActivity(), "图片保存成功", Toast.LENGTH_SHORT).show();
    }
}

