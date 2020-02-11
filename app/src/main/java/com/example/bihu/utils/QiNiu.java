package com.example.bihu.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class QiNiu {
    private static final String ACCESS_KEY = "N0KOJh4LVjqbjQtQsCvF3lR9zBRHXLmZs6GC41RX";
    private static final String SECRET_KEY = "VRhZ1ZUmDDoNy4j-zZQ-yJBdA15ucvb5WwCHoWLn";
    private static final String BUCKET_NAME = "mybihu";
    private static final Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
    private static Configuration configuration = new Configuration(Region.autoRegion());
    private static UploadManager uploadManager = new UploadManager(configuration);

    /**
     * 上传图片到七牛云
     *
     * @param img
     * @param qiNiuCallbackListener
     */
    public void upload(final File img, final QiNiuCallbackListener qiNiuCallbackListener) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmSS");
        final String key = "icon_" + sdf.format(new Date());
        final String upToken = auth.uploadToken(BUCKET_NAME);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("test", "Thread");
                    Response response = uploadManager.put(img, key, upToken);
                    Log.d("test", response.toString());
                    //解析上传成功的结果
                    DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                    qiNiuCallbackListener.onSuccess("http://q4pta80dw.bkt.clouddn.com/" + key);
                } catch (QiniuException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
