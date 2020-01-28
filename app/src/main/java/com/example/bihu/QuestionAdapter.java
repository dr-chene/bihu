package com.example.bihu;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bihu.tool.MyHelper;
import com.example.bihu.tool.Question;

import java.util.ArrayList;
import java.util.List;


public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.MyInnerViewHolder> {

    private List<Question> questionList = new ArrayList<>();
    private List<Question> favoriteList = new ArrayList<>();
    private Context context;
    private Question question;
    private int type;

    public QuestionAdapter(Context context, int type) {
        this.context = context;
        this.type = type;
        switch (type) {
            case MainActivity.TYPE_QUESTION:
                getQuestionData();
                break;
            case MainActivity.TYPE_FAVORITE:
                getFavoriteData();
                break;
        }
    }

    @NonNull
    @Override
    public MyInnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item, parent, false);
        return new MyInnerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyInnerViewHolder holder, final int position) {
        switch (type) {
            case MainActivity.TYPE_QUESTION:
                question = questionList.get(position);
                break;
            case MainActivity.TYPE_FAVORITE:
                question = favoriteList.get(position);
        }
        holder.questionItemAuthorName.setText(question.getAuthorName());
        holder.questionItemRecent.setText(question.getRecent());
        holder.questionItemTitle.setText(question.getTitle());
        holder.questionItemContent.setText(question.getContent());
        holder.questionItemExcitingCount.setText(question.getExciting() + "");
        holder.questionItemAnswerCount.setText(question.getAnswerCount() + "");
        holder.questionItemNaiveCount.setText(question.getNaive() + "");
        if (question.getAuthorAvatar().length() >= 10) {
            Glide.with(context)
                    .load(question.getAuthorAvatar())
                    .into(holder.questionItemUserImg);
        }
        if (question.getImages().length() >= 10) {
            Log.d("debug", "question" + position + " " + question.getImages());
            Glide.with(context)
                    .load(question.getImages())
                    .fitCenter()
                    .into(holder.questionItemContentImg);
        } else {
            holder.questionItemContentImg.setVisibility(View.GONE);
        }
        final int id = question.getId();
        holder.questionItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, QuestionActivity.class);
                intent.putExtra("question_id", id);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        switch (type) {
            case MainActivity.TYPE_QUESTION:
                return questionList.size();
            case MainActivity.TYPE_FAVORITE:
                return favoriteList.size();
        }
        return 0;
    }

    public void refresh(int type) {
        switch (type) {
            case MainActivity.TYPE_QUESTION:
                getQuestionData();
                break;
            case MainActivity.TYPE_FAVORITE:
                getFavoriteData();
                break;
        }

    }

    public void getQuestionData() {
        MyHelper.readQuestion(context, questionList);
    }

    public void getFavoriteData() {
        MyHelper.readFavorite(context, favoriteList);
    }

    public class MyInnerViewHolder extends RecyclerView.ViewHolder {

        private ImageView questionItemUserImg;
        private TextView questionItemAuthorName;
        private TextView questionItemRecent;
        private TextView questionItemTitle;
        private TextView questionItemContent;
        private ImageView questionItemContentImg;
        private TextView questionItemExcitingCount;
        private TextView questionItemAnswerCount;
        private TextView questionItemNaiveCount;
        private LinearLayout questionItem;

        public MyInnerViewHolder(@NonNull View itemView) {
            super(itemView);
            questionItemContentImg = itemView.findViewById(R.id.question_item_content_img);
            questionItemAuthorName = itemView.findViewById(R.id.question_item_username);
            questionItemRecent = itemView.findViewById(R.id.question_item_recent);
            questionItemTitle = itemView.findViewById(R.id.question_item_title);
            questionItemContent = itemView.findViewById(R.id.question_item_content);
            questionItemExcitingCount = itemView.findViewById(R.id.question_item_exciting_count);
            questionItemAnswerCount = itemView.findViewById(R.id.question_item_answer_count);
            questionItemNaiveCount = itemView.findViewById(R.id.question_item_naive_count);
            questionItem = itemView.findViewById(R.id.question_item);
            questionItemUserImg = itemView.findViewById(R.id.question_item_user_img);
        }
    }
}
