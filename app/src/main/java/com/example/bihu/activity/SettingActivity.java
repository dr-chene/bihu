package com.example.bihu.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.bihu.R;
import com.example.bihu.utils.MyHelper;
import com.example.bihu.utils.QiNiu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static android.widget.Toast.LENGTH_SHORT;
import static com.example.bihu.utils.Methods.getFileByUri;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private ConstraintLayout settingAccount;
    private ConstraintLayout settingPerson;
    private ConstraintLayout settingModifyAvatar;
    private ConstraintLayout settingChangePassword;
    private TextView loginOut;
    private ImageView settingBack;
    private ImageView settingAvatar;
    private TextView settingUsername;
    private PopupWindow popupWindow;
    private Uri imageUri;
    private File outputImage;

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
        settingAccount = findViewById(R.id.setting_account);
        settingPerson = findViewById(R.id.setting_person);
        settingModifyAvatar = findViewById(R.id.setting_modify_avatar);
        settingChangePassword = findViewById(R.id.setting_change_password);
        loginOut = findViewById(R.id.login_out);
        settingBack = findViewById(R.id.setting_back);
        settingAvatar = findViewById(R.id.setting_avatar);
        settingUsername = findViewById(R.id.setting_username);
        if (MainActivity.person.getId() != -1) {
            //加载头像settingAvatar
            if (MainActivity.person.getAvatar().length() >= 10) {
                Glide.with(this)
                        .load(MainActivity.person.getAvatar())
                        .into(settingAvatar);
            }
            settingUsername.setText(MainActivity.person.getUsername());

        }
    }

    private void modifyAvatar() {
        View contentView = LayoutInflater.from(SettingActivity.this).inflate(R.layout.pop_up_window, null);
        popupWindow = new PopupWindow(contentView, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT, true);
        popupWindow.setContentView(contentView);
        TextView cameraPop = contentView.findViewById(R.id.pop_camera);
        TextView photoPop = contentView.findViewById(R.id.pop_photo);
        TextView backPop = contentView.findViewById(R.id.pop_back);
        cameraPop.setOnClickListener(this);
        photoPop.setOnClickListener(this);
        backPop.setOnClickListener(this);
        View rootView = LayoutInflater.from(SettingActivity.this).inflate(R.layout.activity_setting, null);
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
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
                modifyAvatar();
                break;
            case R.id.setting_change_password:
                Intent intent = new Intent(SettingActivity.this, PasswordChangeActivity.class);
                startActivity(intent);
                break;
            case R.id.login_out:
                dialog(v);
                if (MainActivity.person.getId() == -1) {
                    Toast.makeText(SettingActivity.this, "正在返回主页", LENGTH_SHORT).show();
                    Intent intent1 = new Intent(SettingActivity.this, MainActivity.class);
                    startActivity(intent1);
                }
                break;
            case R.id.setting_back:
                Intent intent1 = new Intent(SettingActivity.this, MainActivity.class);
                startActivity(intent1);
                break;
            case R.id.pop_camera:
                camera();
                popupWindow.dismiss();
                break;
            case R.id.pop_photo:
                photo();
                popupWindow.dismiss();
                break;
            case R.id.pop_back:
                popupWindow.dismiss();
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
                MyHelper.deletePerson(SettingActivity.this);
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                startActivity(intent);
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

    private void camera() {
        outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(SettingActivity.this, "com.example.bihu.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        //隐式启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, MainActivity.TYPE_TAKE_PHOTO);
    }

    public void photo() {
        if (ContextCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SettingActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            openAlum();
        }
    }

    private void openAlum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, MainActivity.TYPE_CHOOSE_PHOTO);
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MainActivity.TYPE_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        settingAvatar.setImageBitmap(bitmap);
                        new QiNiu(this).upload(outputImage, MainActivity.TYPE_MODIFY_AVATAR);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case MainActivity.TYPE_CHOOSE_PHOTO:
                Uri uri = data.getData();
                settingAvatar.setImageURI(uri);
                File file = getFileByUri(this, uri);
                new QiNiu(this).upload(file, MainActivity.TYPE_MODIFY_AVATAR);
                break;
        }
    }
}