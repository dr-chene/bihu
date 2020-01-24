package com.example.bihu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bihu.tool.MyHelper;
import com.example.bihu.tool.Question;

import java.util.HashMap;
import java.util.Map;

public class QuestionActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView realQuestionUserImg;
    private TextView realQuestionAuthorName;
    private TextView realQuestionRecent;
    private TextView realQuestionTitle;
    private TextView realQuestionContent;
    private ImageView realQuestionContentImg;
    private ImageView realQuestionExcitingImg;
    private TextView realQuestionExcitingCount;
    private TextView realQuestionAnswerCount;
    private ImageView realQuestionNaiveImg;
    private TextView realQuestionNaiveCount;
    private ImageView realQuestionFavoriteImg;
    private RecyclerView realQuestionRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AnswerAdapter answerAdapter;
    private LinearLayout realQuestionExciting;
    private LinearLayout realQuestionNaive;
    private LinearLayout realQuestionFavorite;
    private EditText enterAnswerED;
    private Button enterAnswerBtn;
    private Question realQuestion = new Question();
    private int qid = -1;
    private int page = 0;
    private int count = 10;
    private Boolean isExciting;
    private Boolean isNaive;
    private Boolean isFavorite;
    private LinearLayout questionBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        initView();
        setOnclickListener();
    }

    private void initView() {
        questionBack = findViewById(R.id.question_back);
        realQuestionUserImg = findViewById(R.id.real_question_user_img);
        realQuestionAuthorName = findViewById(R.id.real_question_username);
        realQuestionRecent = findViewById(R.id.real_question_recent);
        realQuestionTitle = findViewById(R.id.real_question_title);
        realQuestionContent = findViewById(R.id.real_question_content);
//        realQuestionContentImg= findViewById(R.id.real_question_???);
        realQuestionExcitingImg = findViewById(R.id.real_question_exciting_img);
        realQuestionExcitingCount = findViewById(R.id.real_question_exciting_count);
        realQuestionAnswerCount = findViewById(R.id.real_question_answer_count);
        realQuestionNaiveImg = findViewById(R.id.real_question_naive_img);
        realQuestionNaiveCount = findViewById(R.id.real_question_naive_count);
        realQuestionFavoriteImg = findViewById(R.id.real_question_favorite_img);
        realQuestionRecyclerView = findViewById(R.id.real_question_answer_rv);
        realQuestionExciting = findViewById(R.id.real_question_exciting);
        realQuestionNaive = findViewById(R.id.real_question_naive);
        realQuestionFavorite = findViewById(R.id.real_question_favorite);
        enterAnswerED = findViewById(R.id.enter_answer_ed);
        enterAnswerBtn = findViewById(R.id.enter_answer_btn);
        swipeRefreshLayout = findViewById(R.id.real_question_refresh);
        Intent intent = getIntent();
        qid = intent.getIntExtra("question_id", 0);
//        Toast.makeText(QuestionActivity.this,""+questionId,Toast.LENGTH_SHORT).show();
        if (qid == -1) {
            Toast.makeText(QuestionActivity.this, "发生了未知的错误，请及时反馈（虽然不一定能修好）", Toast.LENGTH_LONG).show();
        } else {
            loadQuestion();
        }
        realQuestionRecyclerView = findViewById(R.id.real_question_answer_rv);
        answerAdapter = new AnswerAdapter(QuestionActivity.this, qid);
        realQuestionRecyclerView.setAdapter(answerAdapter);
        realQuestionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        realQuestionRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void loadQuestion() {
        MyHelper.searchQuestion(this, qid, realQuestion);
        //加载作者头像
        realQuestionAuthorName.setText(realQuestion.getAuthorName());
        realQuestionRecent.setText(realQuestion.getRecent());
        realQuestionTitle.setText(realQuestion.getTitle());
        realQuestionContent.setText(realQuestion.getContent());
        //加载问题图片
        //
        realQuestionExcitingCount.setText(realQuestion.getExciting() + "");
        realQuestionAnswerCount.setText(realQuestion.getAnswerCount() + "");
        //
        realQuestionNaiveCount.setText(realQuestion.getNaive() + "");
        //
        isExciting = realQuestion.getIsExciting();
        if (isExciting) {
            realQuestionExcitingImg.setImageResource(R.drawable.hand_thumbsup);
        } else {
            realQuestionNaiveImg.setImageResource(R.drawable.hand_thumbsup_fill);
        }
        isNaive = realQuestion.getIsNaive();
        if (isNaive) {
            realQuestionNaiveImg.setImageResource(R.drawable.hand_thumbsdown);
        } else {
            realQuestionNaiveImg.setImageResource(R.drawable.hand_thumbsdown_fill);
        }
        isFavorite = realQuestion.getFavorite();
        if (isFavorite) {
            realQuestionFavoriteImg.setImageResource(R.drawable.star);
        } else {
            realQuestionFavoriteImg.setImageResource(R.drawable.star_fill);
        }
    }

    private void setOnclickListener() {
        realQuestionExciting.setOnClickListener(this);
        realQuestionNaive.setOnClickListener(this);
        realQuestionFavorite.setOnClickListener(this);
        enterAnswerBtn.setOnClickListener(this);
        questionBack.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Map<String, String> query = new HashMap<>();
                query.put("page", page + "");
                query.put("count", count + "");
                query.put("qid", qid + "");
                query.put("token", MainActivity.person.getToken());
                URLPost urlPost = new URLPost(QuestionActivity.this, qid);
                urlPost.post(URLPost.URL_GET_ANSWER_LIST, query, URLPost.TYPE_GET_ANSWER_LIST);
                answerAdapter.refresh();
                answerAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.real_question_exciting:
                Map<String, String> queryExciting = new HashMap<>();
                queryExciting.put("id", qid + "");
                queryExciting.put("type", MainActivity.TYPE_QUESTION + "");
                queryExciting.put("token", MainActivity.person.getToken());
                URLPost urlPost = new URLPost(QuestionActivity.this);
                if (isExciting) {
                    urlPost.post(URLPost.URL_CANCEL_EXCITING, queryExciting, URLPost.TYPE_CANCEL_EXCITING);
                    realQuestionExcitingImg.setImageResource(R.drawable.hand_thumbsup);
                    String s = realQuestionExcitingCount.getText().toString();
                    realQuestionExcitingCount.setText(Integer.parseInt(s) - 1 + "");
                    isExciting = false;
                } else {
                    urlPost.post(URLPost.URL_EXCITING, queryExciting, URLPost.TYPE_EXCITING);
                    realQuestionNaiveImg.setImageResource(R.drawable.hand_thumbsup_fill);
                    String s = realQuestionNaiveCount.getText().toString();
                    realQuestionNaiveCount.setText((Integer.parseInt(s) + 1) + "");
                    isExciting = true;
                }
                break;
            case R.id.real_question_naive:
                Map<String, String> queryNaive = new HashMap<>();
                queryNaive.put("id", qid + "");
                queryNaive.put("type", MainActivity.TYPE_QUESTION + "");
                queryNaive.put("token", MainActivity.person.getToken());
                URLPost urlPost1 = new URLPost(QuestionActivity.this);
                if (isNaive) {
                    urlPost1.post(URLPost.URL_CANCEL_NAIVE, queryNaive, URLPost.TYPE_CANCEL_NAIVE);
                    realQuestionNaiveImg.setImageResource(R.drawable.hand_thumbsdown);
                    String s = realQuestionNaiveCount.getText().toString();
                    realQuestionNaiveCount.setText(Integer.parseInt(s) - 1 + "");
                    isNaive = false;
                } else {
                    urlPost1.post(URLPost.URL_NAIVE, queryNaive, URLPost.TYPE_NAIVE);
                    realQuestionNaiveImg.setImageResource(R.drawable.hand_thumbsdown_fill);
                    String s = realQuestionNaiveCount.getText().toString();
                    realQuestionNaiveCount.setText((Integer.parseInt(s) + 1) + "");
                    isNaive = true;
                }
                break;
            case R.id.real_question_favorite:
                Map<String, String> queryFavorite = new HashMap<>();
                queryFavorite.put("id", qid + "");
                queryFavorite.put("token", MainActivity.person.getToken());
                URLPost urlPost2 = new URLPost(QuestionActivity.this);
                if (isFavorite) {
                    urlPost2.post(URLPost.URL_CANCEL_FAVORITE, queryFavorite, URLPost.TYPE_CANCEL_FAVORITE);
                    realQuestionFavoriteImg.setImageResource(R.drawable.star);
                    isFavorite = false;
                } else {
                    urlPost2.post(URLPost.URL_FAVORITE, queryFavorite, URLPost.TYPE_FAVORITE);
                    realQuestionFavoriteImg.setImageResource(R.drawable.star_fill);
                    isFavorite = true;
                }
                break;
            case R.id.enter_answer_btn:
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
                break;
            case R.id.question_back:
                Intent intent = new Intent(QuestionActivity.this, MainActivity.class);
                startActivity(intent);
                break;
        }
    }
}
