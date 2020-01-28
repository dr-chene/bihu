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

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout changePasswordBack;
    private EditText changePasswordOld;
    private EditText changePasswordNew;
    private EditText changePasswordNewConfirm;
    private Button changePasswordBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initView();
        setOnClickListener();
    }

    private void setOnClickListener() {
        changePasswordBack.setOnClickListener(this);
        changePasswordBtn.setOnClickListener(this);
    }

    private void initView() {
        changePasswordBack = findViewById(R.id.change_password_back);
        changePasswordOld = findViewById(R.id.change_password_old);
        changePasswordNew = findViewById(R.id.change_password_new);
        changePasswordNewConfirm = findViewById(R.id.change_password_new);
        changePasswordBtn = findViewById(R.id.change_password_btn);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_password_back:
                Intent intent = new Intent(ChangePasswordActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.change_password_btn:
                if (MainActivity.person != null) {
                    if (MainActivity.person.getPassword() == changePasswordOld.getText().toString()) {
                        if (changePasswordNew.getText().toString().equals(changePasswordNewConfirm.getText().toString())) {
                            Map<String, String> query = new HashMap<>();
                            query.put("password", changePasswordNew.getText().toString());
                            query.put("token", MainActivity.person.getToken());
                            URLPostUtils urlPostUtils = new URLPostUtils(ChangePasswordActivity.this);
                            urlPostUtils.post(URLPostUtils.URL_CHANGE_PASSWORD, query, URLPostUtils.TYPE_CHANGE_PASSWORD);
                            MainActivity.person.setId(-1);
                            Toast.makeText(ChangePasswordActivity.this, "请重新登录", Toast.LENGTH_SHORT).show();
                            Intent intent1 = new Intent(ChangePasswordActivity.this, MainActivity.class);
                            startActivity(intent1);
                        } else
                            Toast.makeText(ChangePasswordActivity.this, "新密码不一致", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(ChangePasswordActivity.this, "旧密码错误", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(ChangePasswordActivity.this, "账号未登录", Toast.LENGTH_SHORT).show();
        }
    }
}
