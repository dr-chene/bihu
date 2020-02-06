package com.example.bihu.adapter;

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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.bihu.R;
import com.example.bihu.activity.MainActivity;
import com.example.bihu.activity.QuestionContentActivity;
import com.example.bihu.utils.MySQLiteOpenHelper;
import com.example.bihu.utils.Question;

import java.util.ArrayList;
import java.util.List;


public class QuestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Question> questionList = new ArrayList<>();
    private List<Question> favoriteList = new ArrayList<>();
    private Question question;
    private int curSize = 20;
    private int type;
    private Context context;

    public QuestionAdapter(Context context, int type) {
        this.type = type;
        this.context = context;
        if (type == MainActivity.TYPE_QUESTION) {
            MySQLiteOpenHelper.readQuestion(questionList, curSize, MainActivity.TYPE_LOAD_MORE);
        } else if (type == MainActivity.TYPE_FAVORITE) {
            MySQLiteOpenHelper.readFavorite(favoriteList, curSize);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new MyInnerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        switch (type) {
            case MainActivity.TYPE_QUESTION:
                question = questionList.get(position);
                break;
            case MainActivity.TYPE_FAVORITE:
                question = favoriteList.get(position);
        }
        MyInnerViewHolder itemHolder = (MyInnerViewHolder) holder;
        //加载question数据
        itemHolder.questionItemAuthorName.setText(question.getAuthorName());
        itemHolder.questionItemRecent.setText(question.getRecent());
        itemHolder.questionItemTitle.setText(question.getTitle());
        itemHolder.questionItemContent.setText(question.getContent());
        itemHolder.questionItemExcitingCount.setText(question.getExciting() + "");
        itemHolder.questionItemAnswerCount.setText(question.getAnswerCount() + "");
        itemHolder.questionItemNaiveCount.setText(question.getNaive() + "");
        //加载question作者头像
        if (question.getAuthorAvatar().length() >= 10) {
            Glide.with(context)
                    .load(question.getAuthorAvatar())
                    .error(R.drawable.error_avatar)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(itemHolder.questionItemUserImg);
        }
        //加载question图片
        if (question.getImages().length() >= 10) {
            Glide.with(context)
                    .load(question.getImages())
                    .error(R.drawable.error)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter()
                    .into(itemHolder.questionItemContentImg);
        } else {
            itemHolder.questionItemContentImg.setVisibility(View.GONE);
        }
        final int id = question.getId();
        //设置item点击事件（进入详情页面）
        itemHolder.questionItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, QuestionContentActivity.class);
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

    /**
     * 刷新问题
     *
     * @param type
     */
    public void refresh(int type) {
        switch (type) {
            case MainActivity.TYPE_QUESTION:
                MySQLiteOpenHelper.readQuestion(questionList, getItemCount(), MainActivity.TYPE_REFRESH);
                break;
            case MainActivity.TYPE_FAVORITE:
                MySQLiteOpenHelper.readFavorite(favoriteList, getItemCount());
                break;
        }
    }

    /**
     * 加载更多
     */
    public void loadMoreData() {
        curSize += 20;
        if (type == MainActivity.TYPE_QUESTION) {
            MySQLiteOpenHelper.readQuestion(questionList, curSize, MainActivity.TYPE_LOAD_MORE);
        } else if (type == MainActivity.TYPE_FAVORITE) {
            MySQLiteOpenHelper.readFavorite(favoriteList, curSize);
        }
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
