package com.example.bihu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

    private RecyclerView realQuestionRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AnswerAdapter answerAdapter;
    private EditText enterAnswerED;
    private Button enterAnswerBtn;
    private int qid = -1;
    private int page = 0;
    private int count = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        initView();
        setOnclickListener();
    }

    private void initView() {
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
                String images = "";
                if ((!content.equals("")) && (images.equals(""))) {
                    Map<String, String> queryAnswer = new HashMap<>();
                    queryAnswer.put("qid", qid + "");
                    queryAnswer.put("content", content);
                    queryAnswer.put("images", images);
                    queryAnswer.put("token", MainActivity.person.getToken());
                    URLPost urlPost3 = new URLPost(QuestionActivity.this);
                    urlPost3.post(URLPost.URL_ANSWER, queryAnswer, URLPost.TYPE_ANSWER);
                    enterAnswerED.setText("");
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
    }

    public void refresh() {
        Data.refreshAnswer(QuestionActivity.this, page, count, qid);
        List<Answer> answerList = new ArrayList<>();
        MyHelper.readAnswer(QuestionActivity.this, answerList, qid);
        answerAdapter.dataChange(answerList);
        answerAdapter.notifyDataSetChanged();
    }
}
