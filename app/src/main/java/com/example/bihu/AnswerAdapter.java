package com.example.bihu;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bihu.tool.Answer;
import com.example.bihu.tool.MyHelper;
import com.example.bihu.tool.Question;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.MyInnerViewHolder> {

    private Context context;
    private List<Answer> answerList = new ArrayList<>();
    private int qid;
    private int aid;
    private Question question;
    private Boolean best = true;

    public AnswerAdapter(Context context, int qid) {
        this.qid = qid;
        this.context = context;
        question = new Question();
        MyHelper.searchQuestion(context, qid, question);
        MyHelper.readAnswer(context, answerList, qid);
    }


    @NonNull
    @Override
    public AnswerAdapter.MyInnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_item, parent, false);
        return new AnswerAdapter.MyInnerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final AnswerAdapter.MyInnerViewHolder holder, int position) {
        final Answer answer = answerList.get(position);
        holder.answerItemAuthorName.setText(answer.getAuthorName());
        holder.answerItemContent.setText(answer.getContent());
        holder.answerItemDate.setText(answer.getDate());
        holder.answerItemExcitingCount.setText(answer.getExciting() + "");
        holder.answerItemNaiveCount.setText(answer.getNaive() + "");
        aid = answer.getId();
        final Boolean[] isExciting = {answer.getIsExciting()};
        final Boolean[] isNaive = {answer.getIsNaive()};
        if (isExciting[0]) {
            holder.answerItemExcitingImg.setImageResource(R.drawable.hand_thumbsup_fill);
        } else {
            holder.answerItemExcitingImg.setImageResource(R.drawable.hand_thumbsup);
        }
        if (isNaive[0]) {
            holder.answerItemNaiveImg.setImageResource(R.drawable.hand_thumbsdown_fill);
        } else {
            holder.answerItemNaiveImg.setImageResource(R.drawable.hand_thumbsdown);
        }
        if (!(question.getAuthorId() == MainActivity.person.getId())) {
            holder.answerItemBestBtn.setVisibility(View.GONE);
        }
        if (answer.getBest() == 1) {
            best = false;
            holder.answerItemBestBtn.setText("已采纳");
            holder.answerItemBestBtn.setTextColor(Color.parseColor("#FFFF00"));
            Drawable drawable = context.getResources().getDrawable(R.drawable.accept_bg, null);
            holder.answerItemBestBtn.setBackground(drawable);
        }
        holder.answerItemBestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (best) {
                    Map<String, String> queryBest = new HashMap<>();
                    queryBest.put("qid", qid + "");
                    queryBest.put("aid", aid + "");
                    queryBest.put("token", MainActivity.person.getToken());
                    URLPost urlPost5 = new URLPost(context);
                    urlPost5.post(URLPost.URL_ACCEPT, queryBest, URLPost.TYPE_ACCEPT);
                    holder.answerItemBestBtn.setText("已采纳");
                    holder.answerItemBestBtn.setTextColor(Color.parseColor("#FFFF00"));
                    Drawable drawable = context.getResources().getDrawable(R.drawable.accept_bg, null);
                    holder.answerItemBestBtn.setBackground(drawable);
                }else {
                    Toast.makeText(context,"best只能有一位",Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.answerItemExcitingImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> queryExciting = new HashMap<>();
                queryExciting.put("id", aid + "");
                queryExciting.put("type", MainActivity.TYPE_ANSWER + "");
                queryExciting.put("token", MainActivity.person.getToken());
                URLPost urlPost = new URLPost(context);
                if (isExciting[0]) {
                    Log.d("debug", "isExciting = " + isExciting[0]);
                    urlPost.post(URLPost.URL_CANCEL_EXCITING, queryExciting, URLPost.TYPE_CANCEL_EXCITING);
                    holder.answerItemExcitingImg.setImageResource(R.drawable.hand_thumbsup);
                    String s = holder.answerItemExcitingCount.getText().toString();
                    holder.answerItemExcitingCount.setText(Integer.parseInt(s) - 1 + "");
                    isExciting[0] = false;
                } else {
                    urlPost.post(URLPost.URL_EXCITING, queryExciting, URLPost.TYPE_EXCITING);
                    holder.answerItemExcitingImg.setImageResource(R.drawable.hand_thumbsup_fill);
                    String s = holder.answerItemExcitingCount.getText().toString();
                    holder.answerItemExcitingCount.setText((Integer.parseInt(s) + 1) + "");
                    isExciting[0] = true;
                }
            }
        });
        holder.answerItemNaiveImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> queryNaive = new HashMap<>();
                queryNaive.put("id", aid + "");
                queryNaive.put("type", MainActivity.TYPE_ANSWER + "");
                queryNaive.put("token", MainActivity.person.getToken());
                URLPost urlPost1 = new URLPost(context);
                if (isNaive[0]) {
                    urlPost1.post(URLPost.URL_CANCEL_NAIVE, queryNaive, URLPost.TYPE_CANCEL_NAIVE);
                    holder.answerItemNaiveImg.setImageResource(R.drawable.hand_thumbsdown);
                    String s = holder.answerItemNaiveCount.getText().toString();
                    holder.answerItemNaiveCount.setText(Integer.parseInt(s) - 1 + "");
                    isNaive[0] = false;
                } else {
                    urlPost1.post(URLPost.URL_NAIVE, queryNaive, URLPost.TYPE_NAIVE);
                    holder.answerItemNaiveImg.setImageResource(R.drawable.hand_thumbsdown_fill);
                    String s = holder.answerItemNaiveCount.getText().toString();
                    holder.answerItemNaiveCount.setText((Integer.parseInt(s) + 1) + "");
                    isNaive[0] = true;
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return answerList.size();
    }

    public void dataChange(List<Answer> answerList) {
        this.answerList = answerList;
    }

    public class MyInnerViewHolder extends RecyclerView.ViewHolder {
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