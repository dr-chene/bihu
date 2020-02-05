package com.example.bihu.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bihu.R;
import com.example.bihu.adapter.AnswerAdapter;
import com.example.bihu.utils.Http;
import com.example.bihu.utils.HttpCallbackListener;
import com.example.bihu.utils.MySQLiteOpenHelper;
import com.example.bihu.utils.QiNiu;
import com.example.bihu.utils.RecyclerViewNoBugLinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.bihu.utils.Methods.getFileByUri;

public class QuestionContentActivity extends AppCompatActivity {

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
    private PopupWindow popupWindow;
    private ConstraintLayout hf;
    private Boolean isAnswering = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MainActivity.TYPE_REFRESH:
                    answerAdapter.notifyItemInserted(answerAdapter.getItemCount() - 1);
                    swipeRefreshLayout.setRefreshing(false);
                    break;
                case MainActivity.TYPE_ANSWER:
                    enterAnswerED.setText("");
                    popupWindow.dismiss();
                    image = "";
                    isAnswering = false;
                    Toast.makeText(QuestionContentActivity.this, "回答成功", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_content);
        initView();
        setOnclickListener();
    }

    private void initView() {
        hf = findViewById(R.id.hf);
        enterPic = findViewById(R.id.enter_pic_btn);
        realQuestionRecyclerView = findViewById(R.id.real_question_answer_rv);
        enterAnswerED = findViewById(R.id.enter_answer_ed);
        enterAnswerBtn = findViewById(R.id.enter_answer_btn);
        swipeRefreshLayout = findViewById(R.id.real_question_refresh);
        Intent intent = getIntent();
        qid = intent.getIntExtra("question_id", 0);

        realQuestionRecyclerView = findViewById(R.id.real_question_answer_rv);
        answerAdapter = new AnswerAdapter(QuestionContentActivity.this, qid);
        realQuestionRecyclerView.setAdapter(answerAdapter);
        RecyclerViewNoBugLinearLayoutManager manager = new RecyclerViewNoBugLinearLayoutManager(this);
        realQuestionRecyclerView.setLayoutManager(manager);
        realQuestionRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }


    private void setOnclickListener() {
        enterAnswerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAnswering) {
                    isAnswering = true;
                    String content = enterAnswerED.getText().toString();
                    if ((!content.equals("")) || image != "") {
                        Map<String, String> queryAnswer = new HashMap<>();
                        queryAnswer.put("qid", qid + "");
                        queryAnswer.put("content", content);
                        queryAnswer.put("images", image);
                        queryAnswer.put("token", MainActivity.person.getToken());
                        Http.sendHttpRequest(Http.URL_ANSWER, queryAnswer, new HttpCallbackListener() {
                            @Override
                            public void onFinish(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getInt("status") != 200) {
                                        Looper.prepare();
                                        Toast.makeText(QuestionContentActivity.this, jsonObject.getString("info"), Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    } else {
                                        Message msg = new Message();
                                        msg.what = MainActivity.TYPE_ANSWER;
                                        handler.sendMessage(msg);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                    }
                }
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Map<String, String> query = new HashMap<>();
                query.put("page", page + "");
                query.put("count", count + "");
                query.put("qid", qid + "");
                query.put("token", MainActivity.person.getToken());
                Http.sendHttpRequest(Http.URL_GET_ANSWER_LIST, query, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.d("first", jsonObject + "");
                            switch (jsonObject.getInt("status")) {
                                case 401:
                                    Looper.prepare();
                                    Toast.makeText(QuestionContentActivity.this, "登录失效，请重新登录", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                    break;
                                case 400:
                                case 500:
                                    Looper.prepare();
                                    Toast.makeText(QuestionContentActivity.this, jsonObject.getString("info"), Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                    break;
                                case 200:
                                    JSONObject object = jsonObject.getJSONObject("data");
                                    int totalCount = object.getInt("totalCount");
                                    JSONArray jsonArray = object.getJSONArray("answers");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        Log.d("first", "" + i);
                                        JSONObject answerData = jsonArray.getJSONObject(i);
                                        MySQLiteOpenHelper.addAnswer(QuestionContentActivity.this, answerData.getInt("id"), qid, answerData.getString("content"), answerData.getString("images"), answerData.getString("date"), answerData.getInt("best"), answerData.getInt("exciting")
                                                , answerData.getInt("naive"), answerData.getInt("authorId"), answerData.getString("authorName"), answerData.getString("authorAvatar"),
                                                answerData.getBoolean("is_exciting") == true ? 1 : 0, answerData.getBoolean("is_naive") == true ? 1 : 0);

                                    }
                                    Log.d("three", "refresh");
                                    answerAdapter.refresh();
                                    Message msg = new Message();
                                    msg.what = MainActivity.TYPE_REFRESH;
                                    handler.sendMessage(msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }
        });
        enterPic.setOnClickListener(new View.OnClickListener() {
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
                    new QiNiu(this).upload(getFileByUri(this, uri), MainActivity.TYPE_ANSWER);
                    View contentView = LayoutInflater.from(QuestionContentActivity.this).inflate(R.layout.pop_img, null);
                    popupWindow = new PopupWindow(contentView, 300, 300, true);
                    popupWindow.setContentView(contentView);
                    popupWindow.setFocusable(false);
                    ImageView popImg = contentView.findViewById(R.id.pop_img);
                    popImg.setImageURI(uri);
                    popupWindow.showAsDropDown(hf, 750, -20);
                }
        }
    }

    @Override
    protected void onDestroy() {
        answerAdapter.post();
        super.onDestroy();
    }
}
