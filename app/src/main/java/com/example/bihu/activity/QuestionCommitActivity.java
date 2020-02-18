package com.example.bihu.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Explode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.bihu.R;
import com.example.bihu.utils.Http;
import com.example.bihu.utils.HttpCallbackListener;
import com.example.bihu.utils.Methods;
import com.example.bihu.utils.MyToast;
import com.example.bihu.utils.QiNiu;
import com.example.bihu.utils.QiNiuCallbackListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.bihu.utils.Methods.getFileByUri;

public class QuestionCommitActivity extends BaseActivity {

    private String images = "";
    private EditText titleEd;
    private EditText contentEd;
    private ImageView enterQuestionImg;
    private TextView titleCount;
    private TextView contentCount;
    private Boolean isCommitting = false;
    private Uri uri;
    private Boolean imgChanged = false;
    private ImageView imgCancel;
    //处理问题发布成功后的事件
    private Handler handler = new Handler();
    private PopupWindow popQuestioning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Explode());
        setContentView(R.layout.activity_enter_question);
        initView();
    }

    /**
     * 加载视图，设置点击事件
     */
    private void initView() {
        FloatingActionButton questionEnterBtn;
        TextView enterQuestionBack;
        enterQuestionImg = findViewById(R.id.enter_question_img);
        enterQuestionBack = findViewById(R.id.enter_question_back);
        Drawable drawable = getResources().getDrawable(R.drawable.fanhui);
        drawable.setBounds(0, 0, 40, 40);
        enterQuestionBack.setCompoundDrawables(drawable, null, null, null);
        titleCount = findViewById(R.id.title_count);
        contentCount = findViewById(R.id.content_count);
        questionEnterBtn = findViewById(R.id.enter_question_btn);
        titleEd = findViewById(R.id.title_ed);
        contentEd = findViewById(R.id.content_ed);
        imgCancel = findViewById(R.id.question_commit_img_cancel);
        imgCancel.setVisibility(View.GONE);
        //返回按钮
        enterQuestionBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgCancel.setVisibility(View.GONE);
                images = "";
                enterQuestionImg.setImageResource(R.drawable.jia_question);
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
                    MyToast.showToast("你输入的字数已经超过了限制！");
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
                    MyToast.showToast("你输入的字数已经超过了限制！");
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
                if (!Methods.isNetworkAvailable()) {
                    MyToast.showToast("当前网络不可用");
                } else {
                    if (!isCommitting) {
                        popQuestioning();
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
                        MyToast.showToast("正在发布问题");
                    }
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
                            MyToast.showToast(jsonObject.getInt("status") + " : " + jsonObject.getString("info"));
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    popQuestioning.dismiss();
                                    isCommitting = false;
                                }
                            });
                            Looper.loop();
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    images = "";
                                    titleEd.setText("");
                                    contentEd.setText("");
                                    enterQuestionImg.setImageResource(R.drawable.jia_question);
                                    imgChanged = false;
                                    popQuestioning.dismiss();
                                    isCommitting = false;
                                    MyToast.showToast("发布成功");
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
            MyToast.showToast("请补全问题描述");
            isCommitting = false;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    popQuestioning.dismiss();
                }
            });
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
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openAlum();
            } else {
                MyToast.showToast("获取权限失败");
            }
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
        if (requestCode == MainActivity.TYPE_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                uri = data.getData();
                enterQuestionImg.setImageURI(uri);
                imgCancel.setVisibility(View.VISIBLE);
                imgChanged = true;
            }
        }
    }

    private void popQuestioning() {
        View contentView = LayoutInflater.from(QuestionCommitActivity.this).inflate(R.layout.pop_modifying, null);
        ((TextView) contentView.findViewById(R.id.pop_loading_text)).setText("正在发布问题");
        popQuestioning = new PopupWindow(contentView, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT, true);
        popQuestioning.setContentView(contentView);
        View rootView = LayoutInflater.from(QuestionCommitActivity.this).inflate(R.layout.activity_setting, null);
        popQuestioning.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }
}
