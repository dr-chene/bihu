package com.example.bihu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class EnterQuestionActivity extends AppCompatActivity {

    private EditText titleEd;
    private EditText contentEd;
    private Button questionEnterBtn;
    private LinearLayout enterQuestionBack;
    private String title;
    private String content;
    private String images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_question);
        initView();
    }

    private void initView() {
        enterQuestionBack = findViewById(R.id.enter_question_back);
        questionEnterBtn = findViewById(R.id.enter_question_btn);
        titleEd = findViewById(R.id.title_ed);
        contentEd = findViewById(R.id.content_ed);
        enterQuestionBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnterQuestionActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        questionEnterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = titleEd.getText().toString();
                content = contentEd.getText().toString();
                if ((!title.equals("")) && (!content.equals(""))) {
                    Map<String, String> query = new HashMap<>();
                    query.put("title", title);
                    query.put("content", content);
                    query.put("images", "");
                    query.put("token", MainActivity.person.getToken());
                    sendPost(UrlPost.URL_QUESTION, query);
                }
            }
        });
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
//                   Toast.makeText(QuestionActivity.this, jsonObject.toString(), Toast.LENGTH_SHORT).show();
                    Log.d("cheney",jsonObject+"");
                    switch (jsonObject.getInt("status")) {
                        case 400:
                            Toast.makeText(EnterQuestionActivity.this, "参数错误", Toast.LENGTH_SHORT).show();
                            break;
                        case 401:
                            Toast.makeText(EnterQuestionActivity.this, "用户认证错误", Toast.LENGTH_SHORT).show();
                            break;
                        case 500:
                            Toast.makeText(EnterQuestionActivity.this, "奇怪的错误", Toast.LENGTH_SHORT).show();
                            break;
                        case 200:
                            if (!jsonObject.getString("info").equals("success"))
                                Toast.makeText(EnterQuestionActivity.this, "登录失效，请重新登录", Toast.LENGTH_LONG).show();
                            else {
                                Toast.makeText(EnterQuestionActivity.this, "发布成功", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(EnterQuestionActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
