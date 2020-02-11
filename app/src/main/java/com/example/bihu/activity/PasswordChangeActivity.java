package com.example.bihu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.bihu.R;
import com.example.bihu.utils.Http;
import com.example.bihu.utils.HttpCallbackListener;
import com.example.bihu.utils.MySQLiteOpenHelper;
import com.example.bihu.utils.MyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PasswordChangeActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout changePasswordBack;
    private EditText changePasswordOld;
    private EditText changePasswordNew;
    private EditText changePasswordNewConfirm;
    private Button changePasswordBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initView();
        setOnClickListener();
    }

    /**
     * 设置点击事件
     */
    private void setOnClickListener() {
        changePasswordBack.setOnClickListener(this);
        changePasswordBtn.setOnClickListener(this);
    }

    /**
     * 加载视图
     */
    private void initView() {
        changePasswordBack = findViewById(R.id.change_password_back);
        changePasswordOld = findViewById(R.id.change_password_old);
        changePasswordNew = findViewById(R.id.change_password_new);
        changePasswordNewConfirm = findViewById(R.id.change_password_new);
        changePasswordBtn = findViewById(R.id.change_password_btn);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回按钮
            case R.id.change_password_back:
                onBackPressed();
                break;
            //请求修改密码
            case R.id.change_password_btn:
                if (MainActivity.person != null) {
                    if (MainActivity.person.getPassword() == changePasswordOld.getText().toString()) {
                        if (changePasswordNew.getText().toString().equals(changePasswordNewConfirm.getText().toString())) {
                            Map<String, String> query = new HashMap<>();
                            query.put("password", changePasswordNew.getText().toString());
                            query.put("token", MainActivity.person.getToken());
                            Http.sendHttpRequest(Http.URL_CHANGE_PASSWORD, query, new HttpCallbackListener() {
                                @Override
                                public void onFinish(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        if (jsonObject.getInt("status") != 200) {
                                            Looper.prepare();
                                            MyToast.showToast(jsonObject.getInt("status") + " : " + jsonObject.getString("info"));
                                            Looper.loop();
                                        }
                                        if (jsonObject.getString("info").equals("success")) {
                                            JSONObject object = jsonObject.getJSONObject("data");
                                            MySQLiteOpenHelper.changePassword(object.getString("password"), object.getString("token"));
                                            MainActivity.person.setPassword(object.getString("password"));
                                            MainActivity.person.setToken(object.getString("token"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(Exception e) {

                                }

                                @Override
                                public void onNetworkError() {

                                }
                            });
                            MainActivity.person.setId(-1);
                            MyToast.showToast("请重新登录");
                            Intent intent1 = new Intent(PasswordChangeActivity.this, MainActivity.class);
                            startActivity(intent1);
                        } else
                            MyToast.showToast("新密码不一致");
                    } else
                        MyToast.showToast("旧密码错误");
                } else
                    MyToast.showToast("账号未登录");
        }
    }
}
