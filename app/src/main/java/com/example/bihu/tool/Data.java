package com.example.bihu.tool;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.widget.Toast;

import com.example.bihu.MainActivity;
import com.example.bihu.URLPostUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Data {

    public static void refreshQuestion(Context context, int page, int count) {
        Map<String, String> query = new HashMap<>();
        query.put("page", "" + page);
        query.put("count", "" + count);
        query.put("token", MainActivity.person.getToken());
        URLPostUtils urlPostUtils = new URLPostUtils(context);
        urlPostUtils.post(URLPostUtils.URL_GET_QUESTION_LIST, query, URLPostUtils.TYPE_GET_QUESTION_LIST);
    }

    public static void refreshAnswer(Context context, int page, int count, int qid) {
        Map<String, String> query = new HashMap<>();
        query.put("page", page + "");
        query.put("count", count + "");
        query.put("qid", qid + "");
        query.put("token", MainActivity.person.getToken());
        URLPostUtils urlPostUtils = new URLPostUtils(context, qid);
        urlPostUtils.post(URLPostUtils.URL_GET_ANSWER_LIST, query, URLPostUtils.TYPE_GET_ANSWER_LIST);
    }

    public static void register(Context context, Message msg) {
        try {
            JSONObject jsonObject = new JSONObject(msg.obj.toString());
            switch (jsonObject.getInt("status")) {
                case 400:
                case 401:
                case 500:
                    Toast.makeText(context, jsonObject.getString("info"), Toast.LENGTH_SHORT).show();
                    break;
                case 200:
                    JSONObject object = jsonObject.getJSONObject("data");
                    MainActivity.person.setId(object.getInt("id"));
                    MainActivity.person.setUsername(object.getString("username"));
                    MainActivity.person.setPassword(object.getString("password"));
                    MainActivity.person.setToken(object.getString("token"));
                    MainActivity.person.setAvatar(object.getString("avatar"));
                    Toast.makeText(context, "注册成功，正在跳转", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void login(Context context, Message msg) {
        try {
            JSONObject jsonObject = new JSONObject(msg.obj.toString());
            switch (jsonObject.getInt("status")) {
                case 400:
                case 401:
                case 500:
                    Toast.makeText(context, jsonObject.getString("info"), Toast.LENGTH_SHORT).show();
                    break;
                case 200:
                    JSONObject object = jsonObject.getJSONObject("data");
                    MainActivity.person.setId(object.getInt("id"));
                    MainActivity.person.setUsername(object.getString("username"));
                    MainActivity.person.setToken(object.getString("token"));
                    MainActivity.person.setAvatar(object.getString("avatar"));
                    MyHelper.addPerson(context, object.getInt("id"), object.getString("username"), 0 + "", object.getString("avatar"), object.getString("token"));
                    Toast.makeText(context, "登录成功，即将跳转", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void getQuestionList(Context context, Message msg) {
        try {
            JSONObject jsonObject = new JSONObject(msg.obj.toString());
            switch (jsonObject.getInt("status")) {
                case 401:
                    Toast.makeText(context, "登录失效，请重新登录", Toast.LENGTH_SHORT).show();
                    break;
                case 400:
                case 500:
                    Toast.makeText(context, jsonObject.getString("info"), Toast.LENGTH_SHORT).show();
                    break;
                case 200:
                    JSONObject object = jsonObject.getJSONObject("data");
                    int totalCount = object.getInt("totalCount");
                    int totalPage = object.getInt("totalPage");
                    int curPage = object.getInt("curPage");
                    JSONArray jsonArray = object.getJSONArray("questions");
                    JSONObject questionData;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        questionData = jsonArray.getJSONObject(i);
                        MyHelper.addQuestion(context, questionData.getInt("id"), questionData.getString("title"), questionData.getString("content"), questionData.getString("images"), questionData.getString("date"), questionData.getInt("exciting")
                                , questionData.getInt("naive"), questionData.getString("recent"), questionData.getInt("answerCount"), questionData.getInt("authorId"), questionData.getString("authorName"), questionData.getString("authorAvatar"),
                                questionData.getBoolean("is_exciting") == true ? 1 : 0, questionData.getBoolean("is_naive") == true ? 1 : 0,
                                questionData.getBoolean("is_favorite") == true ? 1 : 0);
                    }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void getAnswerList(Context context, Message msg, int qid) {
        try {
            JSONObject jsonObject = new JSONObject(msg.obj.toString());
            switch (jsonObject.getInt("status")) {
                case 400:
                case 401:
                case 500:
                    Toast.makeText(context, jsonObject.getString("info"), Toast.LENGTH_SHORT).show();
                    break;
                case 200:
                    if (!jsonObject.getString("info").equals("success"))
                        Toast.makeText(context, "登录失效，请重新登录", Toast.LENGTH_SHORT).show();
                    else {
                        JSONObject object = jsonObject.getJSONObject("data");
                        int totalCount = object.getInt("totalCount");
                        int totalPage = object.getInt("totalPage");
                        int curPage = object.getInt("curPage");
                        JSONArray jsonArray = object.getJSONArray("answers");
                        JSONObject answerData = null;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            answerData = jsonArray.getJSONObject(i);
                            MyHelper.addAnswer(context, answerData.getInt("id"), qid, answerData.getString("content"), answerData.getString("images"), answerData.getString("date"), answerData.getInt("best"), answerData.getInt("exciting")
                                    , answerData.getInt("naive"), answerData.getInt("authorId"), answerData.getString("authorName"), answerData.getString("authorAvatar"),
                                    answerData.getBoolean("is_exciting") == true ? 1 : 0, answerData.getBoolean("is_naive") == true ? 1 : 0);
                        }

                    }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void modifyAvatar(Context context, String avatar) {
        MyHelper.modifyAvatar(context, avatar);
    }
}
