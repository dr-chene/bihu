package com.example.bihu;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bihu.tool.Answer;
import com.example.bihu.tool.MyHelper;
import com.example.bihu.tool.Question;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private MyHelper myHelper;
    private SQLiteDatabase db;
    private AnswerAdapter answerAdapter;
    private LinearLayout realQuestionExciting;
    private LinearLayout realQuestionNaive;
    private LinearLayout realQuestionFavorite;
    private EditText enterAnswerED;
    private Button enterAnswerBtn;
    private Question realQuestion = new Question();
    private int questionId = -1;
    private int page = 0;
    private int count = 10;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        initView();
        setOnclickListener();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Map<String, String> query = new HashMap<>();
                query.put("page", page + "");
                query.put("count", count + "");
                query.put("qid", questionId + "");
                query.put("token", MainActivity.person.getToken());
                Log.d("debug","开始刷新");
                sendPost(UrlPost.URL_GETANSWERLIST, query);
                handler = new Handler() {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);
                        switch (msg.what){
                            case 0:
                            List<Answer> answerList = new ArrayList<>();
                            myHelper.readAnswer(db,answerList,questionId);
                            answerAdapter.refresh(answerList);
                            answerAdapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);

                            break;
                            default:
                        }
                    }
                };

            }
        });
    }

    private void initView() {
        myHelper = new MyHelper(QuestionActivity.this, MainActivity.vision);
        db = myHelper.getReadableDatabase();
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
        questionId = intent.getIntExtra("question_id", 0);
//        Toast.makeText(QuestionActivity.this,""+questionId,Toast.LENGTH_SHORT).show();
        if (questionId == -1) {
            Toast.makeText(QuestionActivity.this, "发生了未知的错误，请及时反馈（虽然不一定能修好）", Toast.LENGTH_LONG).show();
        } else {
            loadQuestion();
        }
        realQuestionRecyclerView = findViewById(R.id.real_question_answer_rv);
        answerAdapter = new AnswerAdapter(QuestionActivity.this, questionId);
        realQuestionRecyclerView.setAdapter(answerAdapter);
        realQuestionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        realQuestionRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void loadQuestion() {
        myHelper = new MyHelper(QuestionActivity.this, MainActivity.vision);
        db = myHelper.getReadableDatabase();
        myHelper.searchQuestion(db, questionId, realQuestion);
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
                    switch (jsonObject.getInt("status")) {
                        case 400:
                            Toast.makeText(QuestionActivity.this, "参数错误", Toast.LENGTH_SHORT).show();
                            break;
                        case 401:
                            Toast.makeText(QuestionActivity.this, "用户认证错误", Toast.LENGTH_SHORT).show();
                            break;
                        case 500:
                            Toast.makeText(QuestionActivity.this, "奇怪的错误", Toast.LENGTH_SHORT).show();
                            break;
                        case 200:
                            if (!jsonObject.getString("info").equals("success"))
                                Toast.makeText(QuestionActivity.this, "登录失效，请重新登录", Toast.LENGTH_LONG).show();
                            else {
                                JSONObject object = jsonObject.getJSONObject("data");
                                int totalCount = object.getInt("totalCount");
                                int totalPage = object.getInt("totalPage");
                                int curPage = object.getInt("curPage");
                                    JSONArray jsonArray = object.getJSONArray("answers");
                                    JSONObject answerData = null;
//                                Log.d("debug",jsonArray.toString());
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                   Log.d("debug","jsonArray "+ i);
                                        answerData = jsonArray.getJSONObject(i);
                                  Log.d("debug",answerData.toString());
                                        Log.d("debug","jsonData = "+answerData.getString("content"));
                                        myHelper.addAnswer(db, answerData.getInt("id"), questionId, answerData.getString("content"), answerData.getString("images"), answerData.getString("date"), answerData.getInt("best"), answerData.getInt("exciting")
                                                , answerData.getInt("naive"), answerData.getInt("authorId"), answerData.getString("authorName"), answerData.getString("authorAvatar"),
                                                answerData.getBoolean("is_exciting") == true ? 1 : 0, answerData.getBoolean("is_naive") == true ? 1 : 0);
                                }
                                    handler.sendEmptyMessage(0);

                            }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void setOnclickListener() {
        realQuestionExciting.setOnClickListener(this);
        realQuestionNaive.setOnClickListener(this);
        realQuestionFavorite.setOnClickListener(this);
        enterAnswerBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.real_question_exciting:

                break;
            case R.id.real_question_naive:

                break;
            case R.id.real_question_favorite:

                break;
            case R.id.enter_answer_btn:


        }
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
