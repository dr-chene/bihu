package com.example.bihu.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bihu.activity.MainActivity;

import org.json.JSONArray;
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

import static com.example.bihu.utils.Methods.getAnswerPage;
import static com.example.bihu.utils.Methods.getQuestionPage;
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
    private HttpCallbackListener listener;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case TYPE_REGISTER:
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        switch (jsonObject.getInt("status")) {
                            case 401:
                                listener.postFailed("登录失效，请重新登录");
                                break;
                            case 500:
                            case 400:
                                listener.postFailed(jsonObject.getString("info"));
                                break;
                            case 200:
                                JSONObject object = jsonObject.getJSONObject("data");
                                MainActivity.person.setId(object.getInt("id"));
                                MainActivity.person.setUsername(object.getString("username"));
                                MainActivity.person.setPassword(object.getString("password"));
                                MainActivity.person.setToken(object.getString("token"));
                                MainActivity.person.setAvatar(object.getString("avatar"));
                                MySQLiteOpenHelper.addPerson(context, object.getInt("id"), object.getString("username"), object.getString("password"), object.getString("avatar"), object.getString("token"));
                                Toast.makeText(context, "注册成功，正在跳转", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, MainActivity.class);
                                context.startActivity(intent);
                                listener.postSuccess();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case TYPE_LOGIN:
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        switch (jsonObject.getInt("status")) {
                            case 401:
                                listener.postFailed("登录失效，请重新登录");
                                break;
                            case 500:
                            case 400:
                                listener.postFailed(jsonObject.getString("info"));
                                break;
                            case 200:
                                JSONObject object = jsonObject.getJSONObject("data");
                                MainActivity.person.setId(object.getInt("id"));
                                MainActivity.person.setUsername(object.getString("username"));
                                MainActivity.person.setToken(object.getString("token"));
                                MainActivity.person.setAvatar(object.getString("avatar"));
                                MySQLiteOpenHelper.addPerson(context, object.getInt("id"), object.getString("username"), 0 + "", object.getString("avatar"), object.getString("token"));
                                Toast.makeText(context, "登录成功，即将跳转", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, MainActivity.class);
                                context.startActivity(intent);
                                listener.postSuccess();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case TYPE_GET_QUESTION_LIST:
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        switch (jsonObject.getInt("status")) {
                            case 401:
                                listener.postFailed("登录失效，请重新登录");
                                break;
                            case 400:
                            case 500:
                                listener.postFailed(jsonObject.getString("info"));
                                break;
                            case 200:
                                Log.d("first","Http");
                                JSONObject object = jsonObject.getJSONObject("data");
                                MainActivity.totalQuestionPage = object.getInt("totalPage");
                                JSONArray jsonArray = object.getJSONArray("questions");
                                JSONObject questionData;
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    questionData = jsonArray.getJSONObject(i);
                                    MySQLiteOpenHelper.addQuestion(context, questionData.getInt("id"), questionData.getString("title"), questionData.getString("content"), questionData.getString("images"), questionData.getString("date"), questionData.getInt("exciting")
                                            , questionData.getInt("naive"), questionData.getString("recent"), questionData.getInt("answerCount"), questionData.getInt("authorId"), questionData.getString("authorName"), questionData.getString("authorAvatar"),
                                            questionData.getBoolean("is_exciting") == true ? 1 : 0, questionData.getBoolean("is_naive") == true ? 1 : 0,
                                            questionData.getBoolean("is_favorite") == true ? 1 : 0);
                                }
                                listener.postSuccess();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case TYPE_GET_ANSWER_LIST:
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        switch (jsonObject.getInt("status")) {
                            case 401:
                                listener.postFailed("登录失效，请重新登录");
                                break;
                            case 400:
                            case 500:
                                listener.postFailed(jsonObject.getString("info"));
                                break;
                            case 200:
                                if (!jsonObject.getString("info").equals("success"))
                                    Toast.makeText(context, "登录失效，请重新登录", Toast.LENGTH_SHORT).show();
                                else {
                                    JSONObject object = jsonObject.getJSONObject("data");
                                    int totalPage = object.getInt("totalPage");
                                    MainActivity.answerPage = getAnswerPage(context, qid);
                                    if (MainActivity.answerPage < totalPage - 1) {
                                        MainActivity.answerPage++;
                                    }
                                    JSONArray jsonArray = object.getJSONArray("answers");
                                    JSONObject answerData = null;
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        answerData = jsonArray.getJSONObject(i);
                                        MySQLiteOpenHelper.addAnswer(context, answerData.getInt("id"), qid, answerData.getString("content"), answerData.getString("images"), answerData.getString("date"), answerData.getInt("best"), answerData.getInt("exciting")
                                                , answerData.getInt("naive"), answerData.getInt("authorId"), answerData.getString("authorName"), answerData.getString("authorAvatar"),
                                                answerData.getBoolean("is_exciting") == true ? 1 : 0, answerData.getBoolean("is_naive") == true ? 1 : 0);
                                    }
                                }
                                listener.postSuccess();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
                            listener.postFailed(jsonObject.getString("info"));
                        } else {
                            listener.postSuccess();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case TYPE_MODIFY_AVATAR:
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        if (jsonObject.getInt("status") != 200) {
                            listener.postFailed(jsonObject.getString("info"));
                        } else {
                            listener.postSuccess();
                            MySQLiteOpenHelper.modifyAvatar(context, avatar);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case TYPE_CHANGE_PASSWORD:
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        if (jsonObject.getInt("status") != 200) {
                            listener.postFailed(jsonObject.getString("info"));
                        }
                        if (jsonObject.getString("info").equals("success")) {
                            JSONObject object = jsonObject.getJSONObject("data");
                            MainActivity.person.setToken(object.getString("token"));
                            listener.postSuccess();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        }
    };

    public Http(Context context,HttpCallbackListener listener) {
        this.listener = listener;
        this.context = context;
    }


    public Http(Context context, int qid,HttpCallbackListener listener) {
        this.listener = listener;
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
