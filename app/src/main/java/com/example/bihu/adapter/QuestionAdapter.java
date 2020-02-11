package com.example.bihu.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    public int curSize = 0;
    private List<Question> questionList = new ArrayList<>();
    private List<Question> favoriteList = new ArrayList<>();
    private List<Question> mineList = new ArrayList<>();
    private int type;
    private Context context;

    public QuestionAdapter(Context context, int type) {
        this.type = type;
        this.context = context;
        if (type == MainActivity.TYPE_FAVORITE) {
            MySQLiteOpenHelper.readFavorite(favoriteList);
        } else if (type == MainActivity.TYPE_MINE) {
            MySQLiteOpenHelper.readMine(mineList);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (type == MainActivity.TYPE_QUESTION){
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_question, parent,
                    false);
            return new MyInnerViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_foot, parent,
                    false);
            return new FootViewHolder(view);
        }}else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_question, parent,
                    false);
            return new MyInnerViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof MyInnerViewHolder) {
                Question question = new Question();
                switch (type) {
                    case MainActivity.TYPE_QUESTION:
                        question = questionList.get(position);
                        break;
                    case MainActivity.TYPE_FAVORITE:
                        question = favoriteList.get(position);
                        break;
                    case MainActivity.TYPE_MINE:
                        question = mineList.get(position);
                        break;
                }
                final MyInnerViewHolder itemHolder = (MyInnerViewHolder) holder;
                //加载question数据
                itemHolder.questionItemAuthorName.setText(question.getAuthorName());
                itemHolder.questionItemRecent.setText(question.getRecent());
                itemHolder.questionItemTitle.setText(question.getTitle());
                itemHolder.questionItemContent.setText(question.getContent());
                itemHolder.questionItemExcitingCount.setText(question.getExciting() + "");
                itemHolder.questionItemAnswerCount.setText(question.getAnswerCount() + "");
                itemHolder.questionItemNaiveCount.setText(question.getNaive() + "");
                //加载question作者头像
                if (question.getAuthorAvatar().length() >= 5) {
                    Glide.with(context)
                            .load(question.getAuthorAvatar())
                            .error(R.drawable.error_avatar)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(itemHolder.questionItemUserImg);
                }
                //加载question图片
                if (question.getImages().length() >= 5) {
                    Glide.with(context)
                            .load(question.getImages())
                            .error(R.drawable.error)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .fitCenter()
                            .into(itemHolder.questionItemContentImg);
                } else {
                    itemHolder.questionItemContentImg.setVisibility(View.GONE);
                }
                //加载是否点赞
                if (question.getIsExciting()) {
                    itemHolder.questionItemExcitingImg.setImageResource(R.drawable.hand_thumbsup_fill);
                } else {
                    itemHolder.questionItemExcitingImg.setImageResource(R.drawable.hand_thumbsup);
                }
                //加载是否点踩
                if (question.getIsNaive()) {
                    itemHolder.questionItemNaiveImg.setImageResource(R.drawable.hand_thumbsdown_fill);
                } else {
                    itemHolder.questionItemNaiveImg.setImageResource(R.drawable.hand_thumbsdown);
                }
                //加载是否收藏
                if (question.getFavorite()) {
                    itemHolder.questionItemFavoriteImg.setImageResource(R.drawable.star_fill);
                } else {
                    itemHolder.questionItemFavoriteImg.setImageResource(R.drawable.star);
                }
                final int id = question.getId();
                //设置item点击事件（进入详情页面）
                itemHolder.questionItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, QuestionContentActivity.class);
                        intent.putExtra("question_id", id);
                        intent.putExtra("position", position);
                        ((Activity) context).startActivityForResult(intent, 1);
                    }
                });
            }
    }

    @Override
    public int getItemCount() {
        switch (type) {
            case MainActivity.TYPE_QUESTION:
                return questionList.size() == 0 ? 0 : questionList.size() + 1;
            case MainActivity.TYPE_FAVORITE:
                return favoriteList.size();
            case MainActivity.TYPE_MINE:
                return mineList.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (type == MainActivity.TYPE_QUESTION) {
            if (position + 1 == getItemCount()) {
                return TYPE_FOOTER;
            } else {
                return TYPE_ITEM;
            }
        }else {
            return TYPE_ITEM;
        }
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
//            case MainActivity.TYPE_FAVORITE:
//                MySQLiteOpenHelper.readFavorite(favoriteList, getItemCount());
//                break;
        }
    }


    public Question getQuestion(int position, int type) {
        switch (type) {
            case MainActivity.TYPE_QUESTION:
                return questionList.get(position);
            case MainActivity.TYPE_FAVORITE:
                return favoriteList.get(position);
            case MainActivity.TYPE_MINE:
                return mineList.get(position);
        }
        return null;
    }

    public List<Question> getQuestionList() {
        return questionList;
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
        private ImageView questionItemExcitingImg;
        private ImageView questionItemNaiveImg;
        private ImageView questionItemFavoriteImg;

        MyInnerViewHolder(@NonNull View itemView) {
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
            questionItemExcitingImg = itemView.findViewById(R.id.question_item_exciting_img);
            questionItemNaiveImg = itemView.findViewById(R.id.question_item_naive_img);
            questionItemFavoriteImg = itemView.findViewById(R.id.question_item_favorite_img);
        }
    }

    public class FootViewHolder extends RecyclerView.ViewHolder {

        FootViewHolder(View view) {
            super(view);
        }
    }
}
