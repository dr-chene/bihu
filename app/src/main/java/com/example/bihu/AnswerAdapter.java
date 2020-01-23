package com.example.bihu;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bihu.tool.Answer;
import com.example.bihu.tool.MyHelper;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.MyInnerViewHolder>
//        implements View.OnClickListener
{

    private Context context;
    private ImageView answerItemAuthorImg;
    private TextView answerItemAuthorName;
    private Button answerItemBestBtn;
    private TextView answerItemContent;
    private TextView answerItemDate;
    private LinearLayout answerItemExciting;
    private ImageView answerItemExcitingImg;
    private TextView answerItemExcitingCount;
    private LinearLayout answerItemNaive;
    private ImageView answerItemNaiveImg;
    private TextView answerItemNaiveCount;
    private MyHelper myHelper;
    private SQLiteDatabase db;
    private List<Answer> answerList = new ArrayList<>();
    private int qid;
    private int aid;
    private Boolean isExciting;
    private Boolean isNaive;

    public AnswerAdapter(Context context, int qid) {
        this.qid = qid;
        this.context = context;
        myHelper = new MyHelper(this.context, MainActivity.vision);
        db = myHelper.getReadableDatabase();
        getAnswerData();
    }

    private void getAnswerData() {
        myHelper.readAnswer(db, answerList, qid);
        Log.d("debug", answerList.size() + "");
        for (int i = 0; i < answerList.size(); i++) {
            Log.d("question", "content = " + answerList.get(i).getContent());
        }
    }

    @NonNull
    @Override
    public AnswerAdapter.MyInnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_item, parent, false);
        return new AnswerAdapter.MyInnerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerAdapter.MyInnerViewHolder holder, int position) {
        Answer answer = answerList.get(position);
        answerItemAuthorName.setText(answer.getAuthorName());
        answerItemContent.setText(answer.getContent());
        answerItemDate.setText(answer.getDate());
        answerItemExcitingCount.setText(answer.getExciting() + "");
        answerItemNaiveCount.setText(answer.getNaive() + "");
        Log.d("debug", "position = " + position);
        Log.d("debug", "content = " + answer.getContent());
        aid = answer.getId();
        isExciting = answer.getIsExciting();
        isNaive = answer.getIsNaive();
        answerItemExciting = holder.itemView.findViewById(R.id.answer_item_exciting);
        if (isExciting) {
            answerItemExcitingImg.setImageResource(R.drawable.hand_thumbsup_fill);
        } else {
            answerItemExcitingImg.setImageResource(R.drawable.hand_thumbsup);
        }
        if (isNaive) {
            answerItemNaiveImg.setImageResource(R.drawable.hand_thumbsdown_fill);
        } else {
            answerItemNaiveImg.setImageResource(R.drawable.hand_thumbsdown);
        }
        if (!answer.getAuthorName().equals(MainActivity.person.getUsername())) {
            answerItemBestBtn.setVisibility(View.GONE);
        }
        if (answer.getBest() == 1) {
            answerItemBestBtn.setVisibility(View.VISIBLE);
            answerItemBestBtn.setText("已采纳");
            answerItemBestBtn.setTextColor(Integer.parseInt("#FFFF00"));
            Drawable drawable = ResourcesCompat.getDrawable(Resources.getSystem(), R.drawable.accept_bg, null);
            answerItemBestBtn.setBackground(drawable);
        }
//        answerItemExciting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("debug2","点击了");
//                answerItemContent.setText("debug");
//                Map<String,String>  queryExciting=new HashMap<>();
//                queryExciting.put("id",aid+"");
//                queryExciting.put("type",MainActivity.TYPE_ANSWER+"");
//                queryExciting.put("token",MainActivity.person.getToken());
//                    if(isExciting){
//                        sendPost(UrlPost.URL_CANCELEXCITING,queryExciting);
//                        answerItemExcitingImg.setImageResource(R.drawable.hand_thumbsup);
//                        String s = answerItemExcitingCount.getText().toString();
//                        answerItemExcitingCount.setText(Integer.parseInt(s)-1+"");
//                        isExciting =false;
//                    }else {
//                        sendPost(UrlPost.URL_EXCITING,queryExciting);
//                        answerItemExcitingImg.setImageResource(R.drawable.hand_thumbsup_fill);
//                        String s = answerItemExcitingCount.getText().toString();
//                        answerItemExcitingCount.setText((Integer.parseInt(s)+1)+"");
//                        isExciting = true;
//                    }
//            }
//        });
////        setOnClickListener();
    }


    @Override
    public int getItemCount() {
        return answerList.size();
    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.answer_item_best:
//                    Map<String,String> queryBest = new HashMap<>();
//                    queryBest.put("qid",qid+"");
//                    queryBest.put("aid",aid+"");
//                    queryBest.put("token",MainActivity.person.getToken());
//                    sendPost(UrlPost.URL_ACCEPT,queryBest);
//                    answerItemBestBtn.setText("已采纳");
//                    answerItemBestBtn.setTextColor(Integer.parseInt("#FFFF00"));
//                    Drawable drawable = ResourcesCompat.getDrawable(Resources.getSystem(),R.drawable.accept_bg,null);
//                    answerItemBestBtn.setBackground(drawable);
//
//                    break;
//            case R.id.answer_item_exciting:
//                Map<String,String>  queryExciting=new HashMap<>();
//                queryExciting.put("id",aid+"");
//                queryExciting.put("type",MainActivity.TYPE_ANSWER+"");
//                queryExciting.put("token",MainActivity.person.getToken());
//                    if(isExciting){
//                        sendPost(UrlPost.URL_CANCELEXCITING,queryExciting);
//                        answerItemExcitingImg.setImageResource(R.drawable.hand_thumbsup);
//                        String s = answerItemExcitingCount.getText().toString();
//                        answerItemExcitingCount.setText(Integer.parseInt(s)-1+"");
//                        isExciting =false;
//                    }else {
//                        sendPost(UrlPost.URL_EXCITING,queryExciting);
//                        answerItemExcitingImg.setImageResource(R.drawable.hand_thumbsup_fill);
//                        String s = answerItemExcitingCount.getText().toString();
//                        answerItemExcitingCount.setText((Integer.parseInt(s)+1)+"");
//                        isExciting = true;
//                    }
//                    break;
//            case R.id.answer_item_naive:
//                Map<String,String>  queryNaive=new HashMap<>();
//                queryNaive.put("id",aid+"");
//                queryNaive.put("type",MainActivity.TYPE_ANSWER+"");
//                queryNaive.put("token",MainActivity.person.getToken());
//                if (isNaive){
//                    sendPost(UrlPost.URL_CANCELNAIVE,queryNaive);
//                    answerItemNaiveImg.setImageResource(R.drawable.hand_thumbsdown);
//                    answerItemNaiveCount.setText(Integer.getInteger(String.valueOf(answerItemNaiveCount.getText()))-1+"");
//                    isNaive = false;
//                }else {
//                    sendPost(UrlPost.URL_NAIVE,queryNaive);
//                    answerItemNaiveImg.setImageResource(R.drawable.hand_thumbsdown_fill);
//                    answerItemNaiveCount.setText(Integer.getInteger(String.valueOf(answerItemNaiveCount.getText()))+1+"");
//                    isNaive = true;
//                }
//        }
//    }

//    private void setOnClickListener() {
//        answerItemBestBtn.setOnClickListener(this);
//        answerItemExciting.setOnClickListener(this);
//        answerItemNaive.setOnClickListener(this);
//    }

    public void refresh() {
        Log.d("question", "开始刷新数据");
        myHelper.readAnswer(db, answerList, qid);
        Log.d("question", "数据刷新成功");
    }

    private void sendPost(final String urlParam, Map<String, String> params) {
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
                    StringBuffer response = new StringBuffer();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    json(response.toString());
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
    }

    private void json(final String data) {
        try {
            JSONObject jsonObject = null;
            jsonObject = new JSONObject(data);
            switch (jsonObject.getInt("status")) {
                case 400:
//                    Toast.makeText(context, "参数错误", Toast.LENGTH_SHORT).show();
                    break;
                case 401:
//                    Toast.makeText(context, "用户认证错误", Toast.LENGTH_SHORT).show();
                    break;
                case 500:
//                    Toast.makeText(context, "奇怪的错误", Toast.LENGTH_SHORT).show();
                    break;
                case 200:
                    if (!jsonObject.getString("info").equals("success")) {
//                        Toast.makeText(context,"登录失效，请重新登录",Toast.LENGTH_LONG).show();
                    }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class MyInnerViewHolder extends RecyclerView.ViewHolder {

        public MyInnerViewHolder(@NonNull View itemView) {
            super(itemView);
            answerItemAuthorImg = itemView.findViewById(R.id.answer_item_user_img);
            answerItemAuthorName = itemView.findViewById(R.id.answer_item_username);
            answerItemBestBtn = itemView.findViewById(R.id.answer_item_best);
            answerItemContent = itemView.findViewById(R.id.answer_item_content);
            answerItemDate = itemView.findViewById(R.id.answer_item_date);
            answerItemExciting = itemView.findViewById(R.id.answer_item_exciting);
            answerItemExcitingImg = itemView.findViewById(R.id.answer_item_exciting_img);
            answerItemExcitingCount = itemView.findViewById(R.id.answer_item_exciting_count);
            answerItemNaive = itemView.findViewById(R.id.answer_item_naive);
            answerItemNaiveImg = itemView.findViewById(R.id.answer_item_naive_img);
            answerItemNaiveCount = itemView.findViewById(R.id.answer_item_naive_count);
        }
    }
}