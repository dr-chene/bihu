package com.example.bihu.activity;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.bihu.R;
import com.example.bihu.utils.Http;
import com.example.bihu.utils.HttpCallbackListener;
import com.example.bihu.utils.MySQLiteOpenHelper;
import com.example.bihu.utils.MyToast;
import com.example.bihu.utils.QiNiu;
import com.example.bihu.utils.QiNiuCallbackListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;
import static com.example.bihu.utils.Methods.getFileByUri;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private PopupWindow popModifying;
    private ConstraintLayout settingAccount;
    private ConstraintLayout settingPerson;
    private ConstraintLayout settingModifyAvatar;
    private ConstraintLayout settingChangePassword;
    private TextView loginOut;
    private ImageView settingBack;
    private ImageView settingAvatar;
    private PopupWindow popupWindow;
    private Uri imageUri;
    private File outputImage;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        setOnClickListener();
    }

    /**
     * 设置点击事件
     */
    private void setOnClickListener() {
        settingAccount.setOnClickListener(this);
        settingPerson.setOnClickListener(this);
        settingModifyAvatar.setOnClickListener(this);
        settingChangePassword.setOnClickListener(this);
        loginOut.setOnClickListener(this);
        settingBack.setOnClickListener(this);
    }

    /**
     * 加载视图
     */
    private void initView() {
        TextView settingUsername;
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
            if (MainActivity.person.getAvatar().length() >= 5) {
                Glide.with(this)
                        .load(MainActivity.person.getAvatar())
                        .error(R.drawable.error_avatar)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(settingAvatar);
            }
            settingUsername.setText(MainActivity.person.getUsername());

        }
    }

    /**
     * 弹出修改头像选择窗口
     */
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
                MyToast.showToast("功能暂未开放");
                break;
            case R.id.setting_person:
                MyToast.showToast("相信我，这没什么好看的");
                break;
            //更改头像的逻辑
            case R.id.setting_modify_avatar:
                modifyAvatar();
                break;
            //修改密码
            case R.id.setting_change_password:
                Intent intent = new Intent(SettingActivity.this, PasswordChangeActivity.class);
                startActivity(intent);
                break;
            //退出登录
            case R.id.login_out:
                dialog(v);
                break;
            //返回按钮
            case R.id.setting_back:
                onBackPressed();
                break;
            //拍照
            case R.id.pop_camera:
                camera();
                popupWindow.dismiss();
                break;
            //选择图片
            case R.id.pop_photo:
                photo();
                popupWindow.dismiss();
                break;
            //取消修改头像
            case R.id.pop_back:
                popupWindow.dismiss();
                break;
        }
    }

    /**
     * 退出登录
     *
     * @param v
     */
    private void dialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("警示");
        builder.setMessage("是否确定退出登录");
        builder.setIcon(R.drawable.warnning);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MySQLiteOpenHelper.deletePerson();
                MainActivity.person.setId(-1);
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(SettingActivity.this).toBundle());

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

    /**
     * 拍照
     */
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

    /**
     * 选择图片
     */
    private void photo() {
        if (ContextCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SettingActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            openAlum();
        }
    }

    /**
     * 打开图片
     */
    private void openAlum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, MainActivity.TYPE_CHOOSE_PHOTO);
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        popModifying();
        switch (requestCode) {
            case MainActivity.TYPE_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        settingAvatar.setImageBitmap(bitmap);
                        new QiNiu().upload(outputImage, new QiNiuCallbackListener() {
                            @Override
                            public void onSuccess(String image) {
                                postSuccess(image);
                            }
                        });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    MyToast.showToast("未获取到图片");
                    popModifying.dismiss();
                }
                break;
            case MainActivity.TYPE_CHOOSE_PHOTO:
                if (data != null) {
                    Uri uri = data.getData();
                    settingAvatar.setImageURI(uri);
                    assert uri != null;
                    File file = getFileByUri(uri);
                    new QiNiu().upload(file, new QiNiuCallbackListener() {
                        @Override
                        public void onSuccess(String image) {
                            postSuccess(image);
                        }
                    });
                    break;
                } else {
                    MyToast.showToast("未获取到图片");
                    popModifying.dismiss();
                }
        }
    }

    /**
     * 图片成功上传到七牛云后，请求修改头像
     *
     * @param image
     */
    private void postSuccess(final String image) {
        Map<String, String> query = new HashMap<>();
        query.put("token", MainActivity.person.getToken());
        query.put("avatar", image);
        Http.sendHttpRequest(Http.URL_MODIFY_AVATAR, query, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") != 200) {
                        Looper.prepare();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                popModifying.dismiss();
                            }
                        });
                        MyToast.showToast(jsonObject.getInt("status") + " : " + jsonObject.getString("info"));
                        Looper.loop();
                    } else {
                        MySQLiteOpenHelper.modifyAvatar(image);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                popModifying.dismiss();
                                MyToast.showToast("头像更改成功");
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

    private void popModifying() {
        View contentView = LayoutInflater.from(SettingActivity.this).inflate(R.layout.pop_modifying, null);
        ((TextView) contentView.findViewById(R.id.pop_loading_text)).setText("正在上传头像");
        popModifying = new PopupWindow(contentView, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT, true);
        popModifying.setContentView(contentView);
        View rootView = LayoutInflater.from(SettingActivity.this).inflate(R.layout.activity_setting, null);
        popModifying.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }
}