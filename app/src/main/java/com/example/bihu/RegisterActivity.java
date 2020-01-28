package com.example.bihu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout registerBack;
    private EditText registerUsername;
    private EditText registerPassword;
    private EditText registerPasswordConfirm;
    private Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        setOnClickListener();
    }

    private void setOnClickListener() {
        registerBack.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
    }

    private void initView() {
        registerBack = findViewById(R.id.register_back);
        registerUsername = findViewById(R.id.register_username);
        registerPassword = findViewById(R.id.register_password);
        registerPasswordConfirm = findViewById(R.id.register_password_confirm);
        registerBtn = findViewById(R.id.register_btn);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_back:
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.register_btn:
                if (registerPassword.getText().toString().equals(registerPasswordConfirm.getText().toString())) {
                    Map<String, String> query = new HashMap<>();
                    query.put("username", registerUsername.getText().toString());
                    query.put("password", registerPassword.getText().toString());
                    URLPostUtils urlPostUtils = new URLPostUtils(this);
                    urlPostUtils.post(URLPostUtils.URL_REGISTER, query, URLPostUtils.TYPE_REGISTER);
                } else Toast.makeText(RegisterActivity.this, "密码不一致，请重新输入", Toast.LENGTH_SHORT);
        }
    }

}
