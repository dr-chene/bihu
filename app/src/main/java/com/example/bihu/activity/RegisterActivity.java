package com.example.bihu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bihu.R;
import com.example.bihu.utils.Http;
import com.example.bihu.utils.HttpCallbackListener;
import com.example.bihu.utils.MySQLiteOpenHelper;

import org.json.JSONException;
import org.json.JSONObject;

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

    /**
     * 设置点击事件
     */
    private void setOnClickListener() {
        registerBack.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
    }

    /**
     * 加载视图
     */
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
            //返回按钮
            case R.id.register_back:
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                break;
                //注册按钮
            case R.id.register_btn:
                if (registerPassword.getText().toString().equals(registerPasswordConfirm.getText().toString())) {
                    Map<String, String> query = new HashMap<>();
                    query.put("username", registerUsername.getText().toString());
                    query.put("password", registerPassword.getText().toString());
                    Http.sendHttpRequest(Http.URL_REGISTER, query, new HttpCallbackListener() {
                        @Override
                        public void onFinish(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                switch (jsonObject.getInt("status")) {
                                    case 401:
                                    case 500:
                                    case 400:
                                        Looper.prepare();
                                        Toast.makeText(RegisterActivity.this, jsonObject.getString("info"), Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                        break;
                                    case 200:
                                        JSONObject object = jsonObject.getJSONObject("data");
                                        MainActivity.person.setId(object.getInt("id"));
                                        MainActivity.person.setUsername(object.getString("username"));
                                        MainActivity.person.setToken(object.getString("token"));
                                        MainActivity.person.setAvatar(object.getString("avatar"));
                                        MySQLiteOpenHelper.addPerson(RegisterActivity.this, object.getInt("id"), object.getString("username"), 0 + "", object.getString("avatar"), object.getString("token"));
                                        Toast.makeText(RegisterActivity.this, "登录成功，即将跳转", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        startActivity(intent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                } else Toast.makeText(RegisterActivity.this, "密码不一致，请重新输入", Toast.LENGTH_SHORT);
        }
    }

}
