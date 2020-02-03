package com.example.bihu.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bihu.activity.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

import static com.example.bihu.utils.Methods.isNetworkAvailable;

public class Http {
    public static final String OLD_BASE_URL = "http://bihu.blogofyb.com/";
    public static final String NEW_BASE_URL = "http://bihu.jay86.com/";
    public static final String URL_LOGIN = NEW_BASE_URL + "login.php";
    public static final String URL_REGISTER = NEW_BASE_URL + "register.php";
    public static final String URL_MODIFY_AVATAR = NEW_BASE_URL + "modifyAvatar.php";
    public static final String URL_CHANGE_PASSWORD = NEW_BASE_URL + "changePassword.php";
    public static final String URL_QUESTION = NEW_BASE_URL + "question.php";
    public static final String URL_GET_QUESTION_LIST = NEW_BASE_URL + "getQuestionList.php";
    public static final String URL_ANSWER = NEW_BASE_URL + "answer.php";
    public static final String URL_GET_ANSWER_LIST = NEW_BASE_URL + "getAnswerList.php";
    public static final String URL_FAVORITE = NEW_BASE_URL + "favorite.php";
    public static final String URL_CANCEL_FAVORITE = NEW_BASE_URL + "cancelFavorite.php";
    public static final String URL_GET_FAVORITE_LIST = NEW_BASE_URL + "getFavoriteList.php";
    public static final String URL_ACCEPT = NEW_BASE_URL + "accept.php";
    public static final String URL_EXCITING = NEW_BASE_URL + "exciting.php";
    public static final String URL_CANCEL_EXCITING = NEW_BASE_URL + "cancelExciting.php";
    public static final String URL_NAIVE = NEW_BASE_URL + "naive.php";
    public static final String URL_CANCEL_NAIVE = NEW_BASE_URL + "cancelNaive.php";
    public static final int TYPE_LOGIN = 1;
    public static final int TYPE_REGISTER = 2;
    public static final int TYPE_MODIFY_AVATAR = 3;
    public static final int TYPE_CHANGE_PASSWORD = 4;
    public static final int TYPE_QUESTION = 5;
    public static final int TYPE_GET_QUESTION_LIST = 6;
    public static final int TYPE_ANSWER = 7;
    public static final int TYPE_GET_ANSWER_LIST = 8;
    public static final int TYPE_FAVORITE = 9;
    public static final int TYPE_CANCEL_FAVORITE = 10;
    public static final int TYPE_GET_FAVORITE_LIST = 10;
    public static final int TYPE_ACCEPT = 12;
    public static final int TYPE_EXCITING = 13;
    public static final int TYPE_CANCEL_EXCITING = 14;
    public static final int TYPE_NAIVE = 15;
    public static final int TYPE_CANCEL_NAIVE = 16;
    private Context context;
    private int qid;
    private int totalCount;
    private String avatar;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case TYPE_REGISTER:
                    Data.register(context, msg);
                    break;
                case TYPE_LOGIN:
                    Data.login(context, msg);
                    break;
                case TYPE_GET_QUESTION_LIST:
                    Data.getQuestionList(context, msg);
                    break;
                case TYPE_GET_ANSWER_LIST:
                    Data.getAnswerList(context, msg, qid);
                    break;
                case TYPE_EXCITING:
                case TYPE_NAIVE:
                case TYPE_FAVORITE:
                case TYPE_CANCEL_EXCITING:
                case TYPE_CANCEL_NAIVE:
                case TYPE_CANCEL_FAVORITE:
                case TYPE_ANSWER:
                case TYPE_ACCEPT:
                case TYPE_QUESTION:
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        if (jsonObject.getInt("status") != 200) {
                            Toast.makeText(context, jsonObject.getString("info"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case TYPE_MODIFY_AVATAR:
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        if (jsonObject.getInt("status") != 200) {
                            Toast.makeText(context, jsonObject.getString("info"), Toast.LENGTH_SHORT).show();
                        } else {
                            Data.modifyAvatar(context, avatar);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case TYPE_CHANGE_PASSWORD:
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        if (jsonObject.getInt("status") != 200) {
                            Toast.makeText(context, jsonObject.getString("info"), Toast.LENGTH_SHORT).show();
                        }
                        if (jsonObject.getString("info").equals("success")) {
                            JSONObject object = jsonObject.getJSONObject("data");
                            MainActivity.person.setToken(object.getString("token"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        }
    };

    public Http(Context context) {
        this.context = context;
    }


    public Http(Context context, int qid) {
        this.context = context;
        this.qid = qid;
    }

    public Http(Context context, String avatar) {
        this.context = context;
        this.avatar = avatar;
    }

    public void post(final String urlParam, Map<String, String> params, final int type) {
        if (isNetworkAvailable(context)) {
            final StringBuffer sbParams = new StringBuffer();
            if (params != null && params.size() > 0) {
                for (Map.Entry<String, String> e : params.entrySet()) {
                    sbParams.append(e.getKey());
                    sbParams.append("=");
                    sbParams.append(e.getValue());
                    sbParams.append("&");
                }
            }
            sbParams.deleteCharAt(sbParams.length() - 1);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpURLConnection connection = null;
                    BufferedReader reader = null;
                    URL url = null;
                    try {
                        url = new URL(urlParam);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                        out.writeBytes(sbParams.toString());
                        connection.setReadTimeout(8000);
                        connection.setConnectTimeout(8000);
                        InputStream in = connection.getInputStream();
                        reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        Message msg = new Message();
                        msg.what = type;
                        msg.obj = response.toString();
                        handler.sendMessage(msg);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (connection != null) {
                            connection.disconnect();
                        }
                    }
                }
            }).start();
        } else {
            Toast.makeText(context, "当前网络不可用", Toast.LENGTH_SHORT).show();
        }
    }
}
