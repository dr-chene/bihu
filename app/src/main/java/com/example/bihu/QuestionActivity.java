package com.example.bihu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bihu.tool.Answer;
import com.example.bihu.tool.Data;
import com.example.bihu.tool.MyHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionActivity extends AppCompatActivity {

    public static String image = "";
    private RecyclerView realQuestionRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AnswerAdapter answerAdapter;
    private EditText enterAnswerED;
    private Button enterAnswerBtn;
    private int qid = -1;
    private int page = 0;
    private int count = 10;
    private ImageView enterPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        initView();
        setOnclickListener();
    }

    private void initView() {
        enterPic = findViewById(R.id.enter_pic_btn);
        realQuestionRecyclerView = findViewById(R.id.real_question_answer_rv);
        enterAnswerED = findViewById(R.id.enter_answer_ed);
        enterAnswerBtn = findViewById(R.id.enter_answer_btn);
        swipeRefreshLayout = findViewById(R.id.real_question_refresh);
        Intent intent = getIntent();
        qid = intent.getIntExtra("question_id", 0);
        //        Toast.makeText(QuestionActivity.this,""+questionId,Toast.LENGTH_SHORT).show();
        realQuestionRecyclerView = findViewById(R.id.real_question_answer_rv);
        answerAdapter = new AnswerAdapter(QuestionActivity.this, qid);
        realQuestionRecyclerView.setAdapter(answerAdapter);
        realQuestionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        realQuestionRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }


    private void setOnclickListener() {
        enterAnswerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = enterAnswerED.getText().toString();
                if ((!content.equals("")) || image != "") {
                    Map<String, String> queryAnswer = new HashMap<>();
                    queryAnswer.put("qid", qid + "");
                    queryAnswer.put("content", content);
                    queryAnswer.put("images", image);
                    queryAnswer.put("token", MainActivity.person.getToken());
                    URLPostUtils urlPostUtils3 = new URLPostUtils(QuestionActivity.this);
                    urlPostUtils3.post(URLPostUtils.URL_ANSWER, queryAnswer, URLPostUtils.TYPE_ANSWER);
                    enterAnswerED.setText("");
                    image = "";
                }
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        enterPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAlum();
            }
        });
    }

    public void refresh() {
        Data.refreshAnswer(QuestionActivity.this, page, count, qid);
        List<Answer> answerList = new ArrayList<>();
        MyHelper.readAnswer(QuestionActivity.this, answerList, qid);
        answerAdapter.dataChange(answerList);
        answerAdapter.notifyDataSetChanged();
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
                    new QiNiuUtils(this).upload(SettingActivity.getFileByUri(this, uri), MainActivity.TYPE_ANSWER);
                }
        }
    }
}
