package com.example.bihu;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bihu.tool.MyHelper;
import com.example.bihu.tool.Person;

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

public class LoginActivity extends AppCompatActivity {

    private LinearLayout loginBack;
    private Button loginButton;
    private EditText loginUsernameET;
    private EditText loginPasswordET;
    private String username;
    private String password;
    private MyHelper myHelper;
    private SQLiteDatabase db;

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
                sendPost(UrlPost.URL_LOGIN, query);
            }
        });
    }

    private void initView() {

        myHelper = new MyHelper(LoginActivity.this, MainActivity.vision);
        db = myHelper.getReadableDatabase();
        loginBack = findViewById(R.id.login_back);
        loginButton = findViewById(R.id.register_btn);
        loginUsernameET = findViewById(R.id.register_username);
        loginPasswordET = findViewById(R.id.register_password);
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
                    switch (jsonObject.getInt("status")) {
                        case 400:
                            Toast.makeText(LoginActivity.this, "参数错误", Toast.LENGTH_SHORT).show();
                            break;
                        case 401:
                            Toast.makeText(LoginActivity.this, "用户认证错误", Toast.LENGTH_SHORT).show();
                            break;
                        case 500:
                            Toast.makeText(LoginActivity.this, "奇怪的错误", Toast.LENGTH_SHORT).show();
                            break;
                        case 200:
                            JSONObject object = jsonObject.getJSONObject("data");
                            MainActivity.person.setId(object.getInt("id"));
                            MainActivity.person.setUsername(object.getString("username"));
                            MainActivity.person.setToken(object.getString("token"));
                            MainActivity.person.setAvatar(object.getString("avatar"));
                            myHelper.addPerson(db,object.getString("username"),0+"",object.getString("avatar"),object.getString("token"),object.getInt("id"));
                            Toast.makeText(LoginActivity.this, "登录成功，即将跳转", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}