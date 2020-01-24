package com.example.bihu;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import com.example.bihu.tool.Question;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.MyInnerViewHolder> implements View.OnClickListener {

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
    private List<Answer> answerList = new ArrayList<>();
    private int qid;
    private int aid;
    private Boolean isExciting;
    private Boolean isNaive;
    private Question question;

    public AnswerAdapter(Context context, int qid) {
        this.qid = qid;
        this.context = context;
        question = new Question();
        MyHelper.searchQuestion(context, qid, question);
        getAnswerData();
    }

    private void getAnswerData() {
        MyHelper.readAnswer(context, answerList, qid);
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
        aid = answer.getId();
        isExciting = answer.getIsExciting();
        isNaive = answer.getIsNaive();
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
        if (!(question.getAuthorId() == MainActivity.person.getId())) {
            answerItemBestBtn.setVisibility(View.GONE);
        }
        if (answer.getBest() == 1) {
            answerItemBestBtn.setText("已采纳");
            answerItemBestBtn.setTextColor(Integer.parseInt("#FFFF00"));
            Drawable drawable = ResourcesCompat.getDrawable(Resources.getSystem(), R.drawable.accept_bg, null);
            answerItemBestBtn.setBackground(drawable);
        }
        setOnClickListener();
    }


    @Override
    public int getItemCount() {
        return answerList.size();
    }

    //
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.answer_item_best:
                Map<String, String> queryBest = new HashMap<>();
                queryBest.put("qid", qid + "");
                queryBest.put("aid", aid + "");
                queryBest.put("token", MainActivity.person.getToken());
                URLPost urlPost5 = new URLPost(context);
                urlPost5.post(URLPost.URL_ACCEPT, queryBest, URLPost.TYPE_ACCEPT);
                answerItemBestBtn.setText("已采纳");
                answerItemBestBtn.setTextColor(Color.parseColor("#FFFF00"));
                Drawable drawable = context.getResources().getDrawable(R.drawable.accept_bg, null);
                answerItemBestBtn.setBackground(drawable);
                break;
            case R.id.answer_item_exciting:
                Map<String, String> queryExciting = new HashMap<>();
                queryExciting.put("id", aid + "");
                queryExciting.put("type", MainActivity.TYPE_ANSWER + "");
                queryExciting.put("token", MainActivity.person.getToken());
                URLPost urlPost = new URLPost(context);
                if (isExciting) {
                    urlPost.post(URLPost.URL_CANCEL_EXCITING, queryExciting, URLPost.TYPE_CANCEL_EXCITING);
                    answerItemExcitingImg.setImageResource(R.drawable.hand_thumbsup);
                    String s = answerItemExcitingCount.getText().toString();
                    answerItemExcitingCount.setText(Integer.parseInt(s) - 1 + "");
                    isExciting = false;
                } else {
                    urlPost.post(URLPost.URL_EXCITING, queryExciting, URLPost.TYPE_EXCITING);
                    answerItemExcitingImg.setImageResource(R.drawable.hand_thumbsup_fill);
                    String s = answerItemExcitingCount.getText().toString();
                    answerItemExcitingCount.setText((Integer.parseInt(s) + 1) + "");
                    isExciting = true;
                }
                break;
            case R.id.answer_item_naive:
                Map<String, String> queryNaive = new HashMap<>();
                queryNaive.put("id", aid + "");
                queryNaive.put("type", MainActivity.TYPE_ANSWER + "");
                queryNaive.put("token", MainActivity.person.getToken());
                URLPost urlPost1 = new URLPost(context);
                if (isNaive) {
                    urlPost1.post(URLPost.URL_CANCEL_NAIVE, queryNaive, URLPost.TYPE_CANCEL_NAIVE);
                    answerItemNaiveImg.setImageResource(R.drawable.hand_thumbsdown);
                    String s = answerItemNaiveCount.getText().toString();
                    answerItemNaiveCount.setText(Integer.parseInt(s) - 1 + "");
                    isNaive = false;
                } else {
                    urlPost1.post(URLPost.URL_NAIVE, queryNaive, URLPost.TYPE_NAIVE);
                    answerItemExcitingImg.setImageResource(R.drawable.hand_thumbsdown_fill);
                    String s = answerItemNaiveCount.getText().toString();
                    answerItemNaiveCount.setText((Integer.parseInt(s) + 1) + "");
                    isNaive = true;
                }
        }
    }

    private void setOnClickListener() {
        answerItemBestBtn.setOnClickListener(this);
        answerItemExciting.setOnClickListener(this);
        answerItemNaive.setOnClickListener(this);
    }

    public void refresh() {
        MyHelper.readAnswer(context, answerList, qid);
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