package com.example.bihu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

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
                    query.put("images", "");
                    query.put("token", MainActivity.person.getToken());
                    URLPost urlPost = new URLPost(EnterQuestionActivity.this);
                    urlPost.post(URLPost.URL_QUESTION, query, URLPost.TYPE_QUESTION);
                    titleEd.setText("");
                    contentEd.setText("");
                }
            }
        });
    }
}
