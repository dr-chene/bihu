package com.example.bihu.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bihu.R;
import com.example.bihu.adapter.AnswerAdapter;
import com.example.bihu.utils.Answer;
import com.example.bihu.utils.Http;
import com.example.bihu.utils.HttpCallbackListener;
import com.example.bihu.utils.Methods;
import com.example.bihu.utils.MySQLiteOpenHelper;
import com.example.bihu.utils.MyToast;
import com.example.bihu.utils.QiNiu;
import com.example.bihu.utils.QiNiuCallbackListener;
import com.example.bihu.utils.WrapContentLinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.bihu.utils.Methods.getFileByUri;

public class QuestionContentActivity extends BaseActivity {

    private String images = "";
    private SwipeRefreshLayout swipeRefreshLayout;
    private AnswerAdapter answerAdapter;
    private EditText enterAnswerED;
    private Button enterAnswerBtn;
    private int qid = -1;
    private int totalCount = -1;
    private ImageView enterPic;
    private PopupWindow popupWindow;
    private ConstraintLayout hf;
    private Boolean isAnswering = false;
    private Uri uri;
    private int position;
    private PopupWindow popAnswering;
    private List<Answer> answers;
    //处理刷新和发布回答成功的事件
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_content);
        initView();
        setOnclickListener();
    }

    /**
     * 处理返回按钮
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent();
            intent.putExtra("isExciting", answerAdapter.question.getIsExciting());
            intent.putExtra("isNaive", answerAdapter.question.getIsNaive());
            intent.putExtra("isFavorite", answerAdapter.question.getFavorite());
            intent.putExtra("answerCount", answerAdapter.getItemCount() - 1);
            intent.putExtra("position", position);
            intent.putExtra("excitingCount", answerAdapter.question.getExciting());
            intent.putExtra("naiveCount", answerAdapter.question.getNaive());
            setResult(RESULT_OK, intent);
            finish();
        }
        return true;
    }

    /**
     * 加载视图，得到intent传输数据
     */
    private void initView() {
        Toolbar toolbar;
        RecyclerView realQuestionRecyclerView;
        toolbar = findViewById(R.id.toolbar_answer);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        hf = findViewById(R.id.hf);
        enterPic = findViewById(R.id.enter_pic_btn);
        enterAnswerED = findViewById(R.id.enter_answer_ed);
        enterAnswerBtn = findViewById(R.id.enter_answer_btn);
        swipeRefreshLayout = findViewById(R.id.real_question_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        Intent intent = getIntent();
        qid = intent.getIntExtra("question_id", 0);
        position = intent.getIntExtra("position", -1);

        realQuestionRecyclerView = findViewById(R.id.real_question_answer_rv);
        answers = new ArrayList<>();
        MySQLiteOpenHelper.readAnswer(answers, qid);
        answerAdapter = new AnswerAdapter(QuestionContentActivity.this, answers, MySQLiteOpenHelper.searchQuestion(qid));
        realQuestionRecyclerView.setAdapter(answerAdapter);
        LinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        realQuestionRecyclerView.setLayoutManager(layoutManager);
    }

    /**
     * 获取权限事件回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openAlum();
            } else {
                MyToast.showToast("获取权限失败");
            }
        }
    }

    /**
     * 设置点击事件
     */
    private void setOnclickListener() {
        //发布回答按钮
        enterAnswerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Methods.isNetworkAvailable()) {
                    MyToast.showToast("当前网络不可用");
                } else {
                    if (!isAnswering) {
                        popAnswering();
                        isAnswering = true;
                        if (popupWindow != null) {
                            new QiNiu().upload(getFileByUri(uri), new QiNiuCallbackListener() {
                                @Override
                                public void onSuccess(String image) {
                                    if (image == "") {
                                        isAnswering = false;
                                        popAnswering.dismiss();
                                        MyToast.showToast("图片上传失败");
                                    } else {
                                        images = image;
                                        postAnswer();
                                    }
                                }
                            });
                        } else {
                            postAnswer();
                        }
                    } else {
                        MyToast.showToast("正在发布回答");
                    }
                }
            }
        });
        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshAnswer();
            }
        });
        //回答图片（仅一张）
        enterPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(QuestionContentActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(QuestionContentActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    if (popupWindow != null) {
                        popupWindow.dismiss();
                    }
                    openAlum();
                }
            }
        });
    }

    /**
     * 打开相册
     */
    private void openAlum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, MainActivity.TYPE_CHOOSE_PHOTO);
    }

    /**
     * 处理相册返回事件
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.TYPE_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                uri = data.getData();
                View contentView = LayoutInflater.from(QuestionContentActivity.this).inflate(R.layout.pop_img, null);
                popupWindow = new PopupWindow(contentView, 300, 300, true);
                popupWindow.setContentView(contentView);
                popupWindow.setFocusable(false);
                ImageView popImg = contentView.findViewById(R.id.pop_img);
                popImg.setImageURI(uri);
                popupWindow.showAsDropDown(hf, 750, -20);
                contentView.findViewById(R.id.answer_commit_img_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        images = "";
                        popupWindow.dismiss();
                    }
                });
            }
        }
    }

    /**
     * 请求发布回答
     */
    private void postAnswer() {
        String content = enterAnswerED.getText().toString();
        if ((!content.equals("")) || (!images.equals(""))) {
            Map<String, String> queryAnswer = new HashMap<>();
            queryAnswer.put("qid", qid + "");
            queryAnswer.put("content", content);
            queryAnswer.put("images", images);
            queryAnswer.put("token", MainActivity.person.getToken());
            Http.sendHttpRequest(Http.URL_ANSWER, queryAnswer, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getInt("status") != 200) {
                            Looper.prepare();
                            MyToast.showToast(jsonObject.getInt("status") + " : " + jsonObject.getString("info"));
                            Looper.loop();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    popAnswering.dismiss();
                                    isAnswering = false;
                                }
                            });
                            Looper.loop();
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    enterAnswerED.setText("");
                                    refreshAnswer();
                                    if (popupWindow != null) {
                                        popupWindow.dismiss();
                                    }
                                    images = "";
                                    popAnswering.dismiss();
                                    isAnswering = false;
                                    MyToast.showToast("回答成功");
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNetworkError() {

                }
            });
        } else {
            popAnswering.dismiss();
            isAnswering = false;
        }
    }

    private void refreshAnswer() {
        if (totalCount == -1) {
            Map<String, String> query = new HashMap<>();
            query.put("page", 0 + "");
            query.put("count", 10 + "");
            query.put("qid", qid + "");
            query.put("token", MainActivity.person.getToken());
            Http.sendHttpRequest(Http.URL_GET_ANSWER_LIST, query, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getInt("status") == 200) {
                            JSONObject object = jsonObject.getJSONObject("data");
                            totalCount = object.getInt("totalCount");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNetworkError() {

                }
            });
        }
        Map<String, String> query = new HashMap<>();
        query.put("page", 0 + "");
        query.put("count", totalCount + "");
        query.put("qid", qid + "");
        query.put("token", MainActivity.person.getToken());
        Http.sendHttpRequest(Http.URL_GET_ANSWER_LIST, query, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    switch (jsonObject.getInt("status")) {
                        case 401:
                            Looper.prepare();
                            MyToast.showToast(jsonObject.getInt("status") + " : " + "登录失效，请重新登录");
                            swipeRefreshLayout.setRefreshing(false);
                            Looper.loop();
                            break;
                        case 400:
                        case 500:
                            Looper.prepare();
                            MyToast.showToast(jsonObject.getInt("status") + " : " + jsonObject.getString("info"));
                            swipeRefreshLayout.setRefreshing(false);
                            Looper.loop();
                            break;
                        case 200:
                            JSONObject object = jsonObject.getJSONObject("data");
                            totalCount = object.getInt("totalCount");
                            JSONArray jsonArray = object.getJSONArray("answers");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject answerData = jsonArray.getJSONObject(i);
                                MySQLiteOpenHelper.addAnswer(answerData.getInt("id"), qid, answerData.getString("content"), answerData.getString("images"), answerData.getString("date"), answerData.getInt("best"), answerData.getInt("exciting")
                                        , answerData.getInt("naive"), answerData.getInt("authorId"), answerData.getString("authorName"), answerData.getString("authorAvatar"),
                                        answerData.getBoolean("is_exciting") ? 1 : 0, answerData.getBoolean("is_naive") ? 1 : 0);

                            }
                            refresh();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNetworkError() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent = new Intent();
        intent.putExtra("isExciting", answerAdapter.question.getIsExciting());
        intent.putExtra("isNaive", answerAdapter.question.getIsNaive());
        intent.putExtra("isFavorite", answerAdapter.question.getFavorite());
        intent.putExtra("answerCount", answerAdapter.getItemCount() - 1);
        intent.putExtra("position", position);
        intent.putExtra("excitingCount", answerAdapter.question.getExciting());
        intent.putExtra("naiveCount", answerAdapter.question.getNaive());
        setResult(RESULT_OK, intent);
        return super.onKeyDown(keyCode, event);
    }

    private void popAnswering() {
        View contentView = LayoutInflater.from(QuestionContentActivity.this).inflate(R.layout.pop_modifying, null);
        ((TextView) contentView.findViewById(R.id.pop_loading_text)).setText("正在发布回答");
        popAnswering = new PopupWindow(contentView, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT, true);
        popAnswering.setContentView(contentView);
        View rootView = LayoutInflater.from(QuestionContentActivity.this).inflate(R.layout.activity_setting, null);
        popAnswering.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }

    private void refresh() {
        MySQLiteOpenHelper.readAnswer(answers, qid);
        answerAdapter.setAnswers(answers);
        handler.post(new Runnable() {
            @Override
            public void run() {
                answerAdapter.notifyItemChanged(answerAdapter.getItemCount());
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
