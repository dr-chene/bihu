package com.example.bihu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
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

    private void sendPost(final String urlParam, Map<String, String> params) {
        final StringBuffer sbParams = new StringBuffer();
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, String> e : params.entrySet()) {
                sbParams.append(e.getKey());
                sbParams.append("=");
                sbParams.append(e.getValue());
                sbParams.append("&");
            }
        }
        sbParams.deleteCharAt(sbParams.length() - 1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                URL url = null;
                try {
                    url = new URL(urlParam);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes(sbParams.toString());
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuffer response = new StringBuffer();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    json(response.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    private void json(final String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    JSONObject object = jsonObject.getJSONObject("data");
                    switch (jsonObject.getInt("status")) {
                        case 400:
                            Toast.makeText(ChangePasswordActivity.this, "参数错误", Toast.LENGTH_SHORT).show();
                            break;
                        case 401:
                            Toast.makeText(ChangePasswordActivity.this, "用户认证错误", Toast.LENGTH_SHORT).show();
                            break;
                        case 500:
                            Toast.makeText(ChangePasswordActivity.this, "奇怪的错误", Toast.LENGTH_SHORT).show();
                            break;
                        case 200:
                            if (!jsonObject.getString("info").equals("success"))
                                Toast.makeText(ChangePasswordActivity.this, "登录失效，请重新登录", Toast.LENGTH_LONG).show();
                            else {
                                MainActivity.person.setToken(object.getString("token"));
                                Toast.makeText(ChangePasswordActivity.this, "密码修改成功", Toast.LENGTH_LONG).show();
                            }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
                            sendPost(UrlPost.URL_CHANGEPASSWORD, query);
                        } else
                            Toast.makeText(ChangePasswordActivity.this, "新密码不一致", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(ChangePasswordActivity.this, "旧密码错误", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(ChangePasswordActivity.this, "账号未登录", Toast.LENGTH_SHORT).show();
        }
    }
}
