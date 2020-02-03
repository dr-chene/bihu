package com.example.bihu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bihu.R;
import com.example.bihu.utils.Http;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private LinearLayout loginBack;
    private Button loginButton;
    private EditText loginUsernameET;
    private EditText loginPasswordET;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        setOnClickListener();
    }

    private void setOnClickListener() {
        loginBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = loginUsernameET.getText().toString();
                password = loginPasswordET.getText().toString();
                Map<String, String> query = new HashMap<>();
                query.put("username", username);
                query.put("password", password);
                Http http = new Http(LoginActivity.this);
                http.post(Http.URL_LOGIN, query, Http.TYPE_LOGIN);
            }
        });
    }

    private void initView() {
        loginBack = findViewById(R.id.login_back);
        loginButton = findViewById(R.id.register_btn);
        loginUsernameET = findViewById(R.id.register_username);
        loginPasswordET = findViewById(R.id.register_password);
    }

}