package com.example.bihu.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bihu.activity.MainActivity;
import com.example.bihu.activity.QuestionCommitActivity;
import com.example.bihu.activity.QuestionContentActivity;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class QiNiu {
    private static final String ACCESS_KEY = "N0KOJh4LVjqbjQtQsCvF3lR9zBRHXLmZs6GC41RX";
    private static final String SECRET_KEY = "VRhZ1ZUmDDoNy4j-zZQ-yJBdA15ucvb5WwCHoWLn";
    private static final String BUCKET_NAME = "mybihu";
    private static final Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
    private static Configuration configuration = new Configuration(Region.autoRegion());
    private static UploadManager uploadManager = new UploadManager(configuration);
    private Context context;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull final Message msg) {
            switch (msg.what) {
                case MainActivity.TYPE_MODIFY_AVATAR:
                    Map<String, String> query = new HashMap<>();
                    query.put("token", MainActivity.person.getToken());
                    query.put("avatar", msg.obj.toString());
                   Http.sendHttpRequest(Http.URL_MODIFY_AVATAR, query, new HttpCallbackListener() {
                       @Override
                       public void onFinish(String response) {
                           try {
                               JSONObject jsonObject = new JSONObject(response);
                               if (jsonObject.getInt("status") != 200) {
                                   Looper.prepare();
                                   Toast.makeText(context,jsonObject.getString("info"),Toast.LENGTH_SHORT).show();
                                   Looper.loop();
                               } else {
                                   MySQLiteOpenHelper.modifyAvatar(context, msg.obj.toString());
                               }
                           } catch (JSONException e) {
                               e.printStackTrace();
                           }
                       }

                       @Override
                       public void onError(Exception e) {

                       }
                   });
                    break;
                case MainActivity.TYPE_ANSWER:
                    QuestionContentActivity.image = msg.obj.toString();
                    break;
                case MainActivity.TYPE_QUESTION:
                    QuestionCommitActivity.images = msg.obj.toString();
                    break;
            }
        }
    };

    public QiNiu(Context context) {
        this.context = context;
    }

    public void upload(final File img, final int type) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        final String key = "icon_" + sdf.format(new Date());
        final String upToken = auth.uploadToken(BUCKET_NAME);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = uploadManager.put(img, key, upToken);
                    //解析上传成功的结果
                    DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                    Message msg = new Message();
                    msg.what = type;
                    msg.obj = "http://q4pta80dw.bkt.clouddn.com/" + key;
                    handler.sendMessage(msg);
                } catch (QiniuException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
