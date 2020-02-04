package com.example.bihu.utils;

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

    public static void sendHttpRequest(final String urlParam, Map<String, String> params, final HttpCallbackListener listener) {
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
                    if (listener != null) {
                        //回调onFinish()方法
                        listener.onFinish(response.toString());
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    if (listener != null) {
                        //回调onError()方法
                        listener.onError(e);
                    }
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
    }
}
