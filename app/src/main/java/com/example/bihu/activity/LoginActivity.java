package com.example.bihu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bihu.R;
import com.example.bihu.utils.Http;
import com.example.bihu.utils.HttpCallbackListener;
import com.example.bihu.utils.MySQLiteOpenHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private LinearLayout loginBack;
    private Button loginButton;
    private EditText loginUsernameET;
    private EditText loginPasswordET;
    private String username;
    private String password;
    //处理登录结果
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(LoginActivity.this, "登录成功，即将跳转", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        setOnClickListener();
    }

    /**
     * 设置点击事件
     */
    private void setOnClickListener() {
        //返回按钮
        loginBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //登录按钮，发送登录请求
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = loginUsernameET.getText().toString();
                password = loginPasswordET.getText().toString();
                Map<String, String> query = new HashMap<>();
                query.put("username", username);
                query.put("password", password);
                Http.sendHttpRequest(Http.URL_LOGIN, query, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            switch (jsonObject.getInt("status")) {
                                case 401:
                                case 500:
                                case 400:
                                    Looper.prepare();
                                    Toast.makeText(LoginActivity.this, jsonObject.getInt("status") + " : " + jsonObject.getString("info"), Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                    break;
                                case 200:
                                    JSONObject object = jsonObject.getJSONObject("data");
                                    MainActivity.person.setId(object.getInt("id"));
                                    MainActivity.person.setUsername(object.getString("username"));
                                    MainActivity.person.setToken(object.getString("token"));
                                    MainActivity.person.setAvatar(object.getString("avatar"));
                                    MySQLiteOpenHelper.addPerson(object.getInt("id"), object.getString("username"), 0 + "", object.getString("avatar"), object.getString("token"));
                                    Message msg = new Message();
                                    msg.what = 1;
                                    handler.sendMessage(msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }
        });
    }

    /**
     * 加载视图
     */
    private void initView() {
        loginBack = findViewById(R.id.login_back);
        loginButton = findViewById(R.id.register_btn);
        loginUsernameET = findViewById(R.id.register_username);
        loginPasswordET = findViewById(R.id.register_password);
    }

}