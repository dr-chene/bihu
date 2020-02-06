package com.example.bihu.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bihu.R;
import com.example.bihu.adapter.AnswerAdapter;
import com.example.bihu.utils.Http;
import com.example.bihu.utils.HttpCallbackListener;
import com.example.bihu.utils.MySQLiteOpenHelper;
import com.example.bihu.utils.QiNiu;
import com.example.bihu.utils.QiNiuCallbackListener;
import com.example.bihu.utils.RecyclerViewNoBugLinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;
import static com.example.bihu.utils.Methods.getFileByUri;

public class QuestionContentActivity extends AppCompatActivity {

    private String images = "";
    private RecyclerView realQuestionRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AnswerAdapter answerAdapter;
    private EditText enterAnswerED;
    private Button enterAnswerBtn;
    private int qid = -1;
    private int page = 0;
    private int count = 10;
    private int totalCount = -1;
    private ImageView enterPic;
    private PopupWindow popupWindow;
    private ConstraintLayout hf;
    private Boolean isAnswering = false;
    private Toolbar toolbar;
    private Uri uri;
    //处理刷新和发布回答成功的事件
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
                    swipeRefreshLayout.setRefreshing(true);
                    refreshAnswer();
                    if (popupWindow != null) {
                        popupWindow.dismiss();
                    }
                    images = "";
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

    /**
     * 处理返回按钮
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(QuestionContentActivity.this, MainActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    /**
     * 加载视图，得到intent传输数据
     */
    private void initView() {
        toolbar = findViewById(R.id.toolbar_answer);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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

    /**
     * 获取权限事件回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlum();
                } else {
                    Toast.makeText(this, "获取权限失败", LENGTH_SHORT).show();
                }
                break;
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
                if (!isAnswering) {
                    isAnswering = true;
                    if (popupWindow != null) {
                        Log.d("test", "popupWindow!=null");
                        new QiNiu().upload(getFileByUri(uri), new QiNiuCallbackListener() {
                            @Override
                            public void onSuccess(String image) {
                                if (image == "") {
                                    isAnswering = false;
                                    Toast.makeText(QuestionContentActivity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
                                } else {
                                    images = image;
                                    Log.d("test", 1 + images);
                                    postAnswer();
                                }
                            }
                        });
                    } else {
                        Log.d("test", 2 + images);
                        postAnswer();
                    }
                } else {
                    Toast.makeText(QuestionContentActivity.this, "正在发布回答", Toast.LENGTH_SHORT).show();
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
        switch (requestCode) {
            case MainActivity.TYPE_CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    uri = data.getData();
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

    /**
     * 请求exciting，naive事件
     */
    @Override
    protected void onDestroy() {
        answerAdapter.post();
        super.onDestroy();
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
                            Toast.makeText(QuestionContentActivity.this, jsonObject.getInt("status") + " : " + jsonObject.getString("info"), Toast.LENGTH_SHORT).show();
                            isAnswering = false;
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
        } else {
            isAnswering = false;
        }
    }
    private void refreshAnswer(){
        if (totalCount!=-1){
            count = totalCount;
        }
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
                    switch (jsonObject.getInt("status")) {
                        case 401:
                            Looper.prepare();
                            Toast.makeText(QuestionContentActivity.this, jsonObject.getInt("status") + " : " + "登录失效，请重新登录", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            break;
                        case 400:
                        case 500:
                            Looper.prepare();
                            Toast.makeText(QuestionContentActivity.this, jsonObject.getInt("status") + " : " + jsonObject.getString("info"), Toast.LENGTH_SHORT).show();
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
                                        answerData.getBoolean("is_exciting") == true ? 1 : 0, answerData.getBoolean("is_naive") == true ? 1 : 0);

                            }
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
}
