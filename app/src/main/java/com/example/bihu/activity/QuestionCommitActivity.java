package com.example.bihu.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.bihu.R;
import com.example.bihu.utils.Http;
import com.example.bihu.utils.HttpCallbackListener;
import com.example.bihu.utils.QiNiu;
import com.example.bihu.utils.QiNiuCallbackListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;
import static com.example.bihu.utils.Methods.getFileByUri;

public class QuestionCommitActivity extends AppCompatActivity {

    private String images = "";
    private EditText titleEd;
    private EditText contentEd;
    private FloatingActionButton questionEnterBtn;
    private LinearLayout enterQuestionBack;
    private ImageView enterQuestionImg;
    private TextView titleCount;
    private TextView contentCount;
    private Boolean isCommitting = false;
    private Uri uri;
    private Boolean imgChanged = false;
    //处理问题发布成功后的事件
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    images = "";
                    titleEd.setText("");
                    contentEd.setText("");
                    enterQuestionImg.setImageResource(R.drawable.jia_question);
                    imgChanged = false;
                    isCommitting = false;
                    Toast.makeText(QuestionCommitActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                    break;
                default:
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_question);
        initView();
    }

    /**
     * 加载视图，设置点击事件
     */
    private void initView() {
        enterQuestionImg = findViewById(R.id.enter_question_img);
        enterQuestionBack = findViewById(R.id.enter_question_back);
        titleCount = findViewById(R.id.title_count);
        contentCount = findViewById(R.id.content_count);
        questionEnterBtn = findViewById(R.id.enter_question_btn);
        titleEd = findViewById(R.id.title_ed);
        contentEd = findViewById(R.id.content_ed);
        //返回按钮
        enterQuestionBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionCommitActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        //实时监听title框字数
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
        //实时监听content框字数
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
        //问题发布按钮，点击请求发布问题
        questionEnterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCommitting) {
                    isCommitting = true;
                    if (imgChanged) {
                        new QiNiu().upload(getFileByUri(uri), new QiNiuCallbackListener() {
                            @Override
                            public void onSuccess(String image) {
                                images = image;
                                postQuestion();
                            }
                        });
                    } else {
                        postQuestion();
                    }
                } else {
                    Toast.makeText(QuestionCommitActivity.this, "正在发布问题", LENGTH_SHORT).show();
                }
            }
        });
        //选择上传图片（限一张）
        enterQuestionImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(QuestionCommitActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(QuestionCommitActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlum();
                }
            }
        });
    }

    /**
     * 请求发布问题
     */
    private void postQuestion() {
        String title = titleEd.getText().toString();
        String content = contentEd.getText().toString();
        if ((!title.equals("")) && (!content.equals(""))) {
            Map<String, String> query = new HashMap<>();
            query.put("title", title);
            query.put("content", content);
            query.put("images", images);
            query.put("token", MainActivity.person.getToken());
            Http.sendHttpRequest(Http.URL_QUESTION, query, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getInt("status") != 200) {
                            Looper.prepare();
                            Toast.makeText(QuestionCommitActivity.this, jsonObject.getInt("status") + " : " + jsonObject.getString("info"), Toast.LENGTH_SHORT).show();
                            isCommitting = false;
                            Looper.loop();
                        } else {
                            Message msg = new Message();
                            msg.what = 1;
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
        }else {
            Looper.prepare();
            Toast.makeText(QuestionCommitActivity.this,"请补全问题描述", LENGTH_SHORT).show();
            isCommitting = false;
            Looper.loop();
        }
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
     * 获取访问sd卡权限回调
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
     * 处理相册返回的事件
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
                    enterQuestionImg.setImageURI(uri);
                    imgChanged = true;
                }
        }
    }
}
