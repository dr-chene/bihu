package com.example.bihu.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bihu.R;
import com.example.bihu.utils.Http;
import com.example.bihu.utils.QiNiu;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.Map;

import static com.example.bihu.utils.Methods.getFileByUri;

public class QuestionCommitActivity extends AppCompatActivity {

    public static String images = "";
    private EditText titleEd;
    private EditText contentEd;
    private FloatingActionButton questionEnterBtn;
    private LinearLayout enterQuestionBack;
    private ImageView enterQuestionImg;
    private String title;
    private String content;
    private TextView titleCount;
    private TextView contentCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_question);
        initView();
    }

    private void initView() {
        enterQuestionImg = findViewById(R.id.enter_question_img);
        enterQuestionBack = findViewById(R.id.enter_question_back);
        titleCount = findViewById(R.id.title_count);
        contentCount = findViewById(R.id.content_count);
        questionEnterBtn = findViewById(R.id.enter_question_btn);
        titleEd = findViewById(R.id.title_ed);
        contentEd = findViewById(R.id.content_ed);
        enterQuestionBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionCommitActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        titleEd.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            private int editStart;
            private int editEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                temp = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                titleEd.setText(s);//将输入的内容实时显示
            }

            @Override
            public void afterTextChanged(Editable s) {
                editStart = titleEd.getSelectionStart();
                editEnd = titleEd.getSelectionEnd();
                titleCount.setText(20 - temp.length() + "");
                if (temp.length() > 20) {
                    Toast.makeText(QuestionCommitActivity.this,
                            "你输入的字数已经超过了限制！", Toast.LENGTH_SHORT)
                            .show();
                    s.delete(editStart - 1, editEnd);
                    int tempSelection = editStart;
                    titleEd.setText(s);
                    titleEd.setSelection(tempSelection);
                }
            }
        });
        contentEd.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            private int editStart;
            private int editEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                temp = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                contentEd.setText(s);//将输入的内容实时显示
            }

            @Override
            public void afterTextChanged(Editable s) {
                editStart = contentEd.getSelectionStart();
                editEnd = contentEd.getSelectionEnd();
                contentCount.setText(200 - temp.length() + "");
                if (temp.length() > 200) {
                    Toast.makeText(QuestionCommitActivity.this,
                            "你输入的字数已经超过了限制！", Toast.LENGTH_SHORT)
                            .show();
                    s.delete(editStart - 1, editEnd);
                    int tempSelection = editStart;
                    contentEd.setText(s);
                    contentEd.setSelection(tempSelection);
                }
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
                    Http http = new Http(QuestionCommitActivity.this);
                    http.post(Http.URL_QUESTION, query, Http.TYPE_QUESTION);
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
                    new QiNiu(this).upload(getFileByUri(this, uri), MainActivity.TYPE_QUESTION);
                    enterQuestionImg.setImageURI(uri);
                }
        }
    }
}
