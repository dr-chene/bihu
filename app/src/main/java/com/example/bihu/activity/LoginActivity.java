package com.example.bihu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.transition.ChangeBounds;
import android.transition.ChangeTransform;
import android.transition.Slide;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bihu.R;
import com.example.bihu.utils.ActivityCollector;
import com.example.bihu.utils.Http;
import com.example.bihu.utils.HttpCallbackListener;
import com.example.bihu.utils.MySQLiteOpenHelper;
import com.example.bihu.utils.MyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity {

    private Button loginButton;
    private EditText loginUsernameET;
    private EditText loginPasswordET;
    private String username;
    private String password;
    private ImageView img;
    private Button toRegister;
    private Toast toast;
    //处理登录结果
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        setOnClickListener();
        getWindow().setEnterTransition(new Slide(Gravity.BOTTOM));
        getWindow().setExitTransition(new Slide(Gravity.BOTTOM));
        TransitionSet transitionSet = new TransitionSet();
        transitionSet.addTransition(new ChangeBounds());
        transitionSet.addTransition(new ChangeTransform());
        transitionSet.addTarget(img);
        getWindow().setSharedElementEnterTransition(transitionSet);
        getWindow().setSharedElementExitTransition(transitionSet);
    }

    /**
     * 设置点击事件
     */
    private void setOnClickListener() {

        //登录按钮，发送登录请求
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = loginUsernameET.getText().toString();
                password = loginPasswordET.getText().toString();
                Map<String, String> query = new HashMap<>();
                query.put("username", username);
                query.put("password", password);
                Http.sendHttpRequest(Http.URL_LOGIN, query, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            switch (jsonObject.getInt("status")) {
                                case 401:
                                case 500:
                                case 400:
                                    Looper.prepare();
                                    MyToast.showToast(jsonObject.getInt("status") + " : " + jsonObject.getString("info"));
                                    Looper.loop();
                                    break;
                                case 200:
                                    JSONObject object = jsonObject.getJSONObject("data");
                                    MainActivity.person.setId(object.getInt("id"));
                                    MainActivity.person.setUsername(object.getString("username"));
                                    MainActivity.person.setToken(object.getString("token"));
                                    MainActivity.person.setAvatar(object.getString("avatar"));
                                    MySQLiteOpenHelper.addPerson(object.getInt("id"), object.getString("username"), 0 + "", object.getString("avatar"), object.getString("token"));
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            MyToast.showToast("登录成功，即将跳转");
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
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
            }
        });
        toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    /**
     * 加载视图
     */
    private void initView() {
        img = findViewById(R.id.login_logo);
        toRegister = findViewById(R.id.to_register_btn);
        loginButton = findViewById(R.id.login_btn);
        loginUsernameET = findViewById(R.id.register_username);
        loginPasswordET = findViewById(R.id.register_password);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (toast == null) {
            toast = Toast.makeText(LoginActivity.this, "再按一次退出", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            ActivityCollector.finishAll();
            return super.onKeyDown(keyCode, event);
        }
        return false;
    }
}