package com.example.carrent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class SignupActivity extends AppCompatActivity {

    private EditText edit_username;
    private EditText edit_phone;
    private EditText edit_authcode;
    private EditText edit_password;
    private EditText edit_rpassword;

    private Button btn_signup;
    private Button btn_authCode;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        findAllView();

        setLisenter();
    }

    private void setLisenter() {
        btn_authCode.setOnClickListener(view->{
            
        });

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
