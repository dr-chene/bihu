package com.example.bihu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import static android.widget.Toast.LENGTH_SHORT;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    public static SettingActivity settingActivity;
    private ConstraintLayout settingAccount;
    private ConstraintLayout settingPerson;
    private ConstraintLayout settingModifyAvatar;
    private ConstraintLayout settingChangePassword;
    private TextView loginOut;
    private ImageView settingBack;
    private ImageView settingAvatar;
    private TextView settingUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        setOnClickListener();
    }

    private void setOnClickListener() {
        settingAccount.setOnClickListener(this);
        settingPerson.setOnClickListener(this);
        settingModifyAvatar.setOnClickListener(this);
        settingChangePassword.setOnClickListener(this);
        loginOut.setOnClickListener(this);
        settingBack.setOnClickListener(this);
    }

    private void initView() {
        settingActivity = this;
        settingAccount = findViewById(R.id.setting_account);
        settingPerson = findViewById(R.id.setting_person);
        settingModifyAvatar = findViewById(R.id.setting_modify_avatar);
        settingChangePassword = findViewById(R.id.setting_change_password);
        loginOut = findViewById(R.id.login_out);
        settingBack = findViewById(R.id.setting_back);
        settingAvatar = findViewById(R.id.setting_avatar);
        settingUsername = findViewById(R.id.setting_username);
        if (MainActivity.person != null) {
            //设置头像settingAvatar
            settingUsername.setText(MainActivity.person.getUsername());

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_account:
                Toast.makeText(SettingActivity.this, "功能暂未开放", LENGTH_SHORT).show();
                break;
            case R.id.setting_person:
                Toast.makeText(SettingActivity.this, "相信我，这没什么好看的", LENGTH_SHORT).show();
                break;
            case R.id.setting_modify_avatar:
                //更改头像的逻辑
                break;
            case R.id.setting_change_password:
                Intent intent = new Intent(SettingActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.login_out:
                dialog(v);
                if (MainActivity.person == null) {
                    Toast.makeText(SettingActivity.this, "正在返回主页", LENGTH_SHORT).show();
                    Intent intent1 = new Intent(SettingActivity.this, MainActivity.class);
                    startActivity(intent1);
                }
                break;
            case R.id.setting_back:
                Intent intent1 = new Intent(SettingActivity.this, MainActivity.class);
                startActivity(intent1);
                break;
        }
    }

    public void dialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("警示");
        builder.setMessage("是否确定退出登录");
        builder.setIcon(R.drawable.warnning);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.person = null;
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(SettingActivity.this, "操作取消", LENGTH_SHORT).show();
            }
        });
        builder.show();
    }
}
