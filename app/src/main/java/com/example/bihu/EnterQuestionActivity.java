package com.example.bihu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class EnterQuestionActivity extends AppCompatActivity {

    public static String images = "";
    private EditText titleEd;
    private EditText contentEd;
    private Button questionEnterBtn;
    private LinearLayout enterQuestionBack;
    private ImageView enterQuestionImg;
    private String title;
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_question);
        initView();
    }

    private void initView() {
        enterQuestionImg = findViewById(R.id.enter_question_img);
        enterQuestionBack = findViewById(R.id.enter_question_back);
        questionEnterBtn = findViewById(R.id.enter_question_btn);
        titleEd = findViewById(R.id.title_ed);
        contentEd = findViewById(R.id.content_ed);
        enterQuestionBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnterQuestionActivity.this, MainActivity.class);
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
                    query.put("images", images);
                    query.put("token", MainActivity.person.getToken());
                    Log.d("debug", "title = " + title);
                    Log.d("debug", "content = " + content);
                    Log.d("debug", "token = " + MainActivity.person.getToken());
                    URLPostUtils urlPostUtils = new URLPostUtils(EnterQuestionActivity.this);
                    urlPostUtils.post(URLPostUtils.URL_QUESTION, query, URLPostUtils.TYPE_QUESTION);
                    titleEd.setText("");
                    contentEd.setText("");
                    images = "";
                }
            }
        });
        enterQuestionImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAlum();
            }
        });
    }

    private void openAlum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, MainActivity.TYPE_CHOOSE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MainActivity.TYPE_CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    new QiNiuUtils(this).upload(SettingActivity.getFileByUri(this, uri), MainActivity.TYPE_QUESTION);
                    enterQuestionImg.setImageURI(uri);
                }
        }
    }
}
