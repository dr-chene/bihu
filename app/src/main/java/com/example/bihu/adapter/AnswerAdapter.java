package com.example.bihu.adapter;

import android.content.Context;
import android.content.Intent;
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

import com.bumptech.glide.Glide;
import com.example.bihu.R;
import com.example.bihu.activity.MainActivity;
import com.example.bihu.utils.Answer;
import com.example.bihu.utils.Http;
import com.example.bihu.utils.MyHelper;
import com.example.bihu.utils.Question;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnswerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Answer> answerList = new ArrayList<>();
    private Question realQuestion = new Question();
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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == MainActivity.TYPE_ANSWER) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_answer, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question_head, parent, false);
            return new QuestionViewHolder(itemView);
        }
        return new AnswerAdapter.AnswerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == MainActivity.TYPE_ANSWER) {
            final AnswerViewHolder answerViewHolder = (AnswerViewHolder) holder;
            final Answer answer = answerList.get(position - 1);
            if (answer.getAuthorAvatar().length() >= 10) {
                Glide.with(context)
                        .load(answer.getAuthorAvatar())
                        .into(answerViewHolder.answerItemAuthorImg);
            }
            if (answer.getImages().length() >= 10) {
                Glide.with(context)
                        .load(answer.getImages())
                        .fitCenter()
                        .into(answerViewHolder.answerItemContentImg);
            } else {
                answerViewHolder.answerItemContentImg.setVisibility(View.GONE);
            }
            answerViewHolder.answerItemAuthorName.setText(answer.getAuthorName());
            answerViewHolder.answerItemContent.setText(answer.getContent());
            if (answer.getContent().length() < 1) {
                answerViewHolder.answerItemContent.setVisibility(View.GONE);
            }
            answerViewHolder.answerItemDate.setText(answer.getDate());
            answerViewHolder.answerItemExcitingCount.setText(answer.getExciting() + "");
            answerViewHolder.answerItemNaiveCount.setText(answer.getNaive() + "");
            aid = answer.getId();
            final Boolean[] isExciting = {answer.getIsExciting()};
            final Boolean[] isNaive = {answer.getIsNaive()};
            if (isExciting[0]) {
                answerViewHolder.answerItemExcitingImg.setImageResource(R.drawable.hand_thumbsup_fill);
            } else {
                answerViewHolder.answerItemExcitingImg.setImageResource(R.drawable.hand_thumbsup);
            }
            if (isNaive[0]) {
                answerViewHolder.answerItemNaiveImg.setImageResource(R.drawable.hand_thumbsdown_fill);
            } else {
                answerViewHolder.answerItemNaiveImg.setImageResource(R.drawable.hand_thumbsdown);
            }
            if (!(question.getAuthorId() == MainActivity.person.getId())) {
                answerViewHolder.answerItemBestBtn.setVisibility(View.GONE);
            }
            if (answer.getBest() == 1) {
                best = false;
                answerViewHolder.answerItemBestBtn.setText("已采纳");
                answerViewHolder.answerItemBestBtn.setTextColor(Color.parseColor("#FFFF00"));
                Drawable drawable = context.getResources().getDrawable(R.drawable.accept_bg, null);
                answerViewHolder.answerItemBestBtn.setBackground(drawable);
            }
            answerViewHolder.answerItemBestBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (best) {
                        Map<String, String> queryBest = new HashMap<>();
                        queryBest.put("qid", qid + "");
                        queryBest.put("aid", aid + "");
                        queryBest.put("token", MainActivity.person.getToken());
                        Http http5 = new Http(context);
                        http5.post(Http.URL_ACCEPT, queryBest, Http.TYPE_ACCEPT);
                        answerViewHolder.answerItemBestBtn.setText("已采纳");
                        answerViewHolder.answerItemBestBtn.setTextColor(Color.parseColor("#FFFF00"));
                        Drawable drawable = context.getResources().getDrawable(R.drawable.accept_bg, null);
                        answerViewHolder.answerItemBestBtn.setBackground(drawable);
                    } else {
                        Toast.makeText(context, "best只能有一位", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            answerViewHolder.answerItemExcitingImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, String> queryExciting = new HashMap<>();
                    queryExciting.put("id", aid + "");
                    queryExciting.put("type", MainActivity.TYPE_ANSWER + "");
                    queryExciting.put("token", MainActivity.person.getToken());
                    Http http = new Http(context);
                    if (isExciting[0]) {
                        Log.d("debug", "isExciting = " + isExciting[0]);
                        http.post(Http.URL_CANCEL_EXCITING, queryExciting, Http.TYPE_CANCEL_EXCITING);
                        answerViewHolder.answerItemExcitingImg.setImageResource(R.drawable.hand_thumbsup);
                        String s = answerViewHolder.answerItemExcitingCount.getText().toString();
                        answerViewHolder.answerItemExcitingCount.setText(Integer.parseInt(s) - 1 + "");
                        isExciting[0] = false;
                    } else {
                        http.post(Http.URL_EXCITING, queryExciting, Http.TYPE_EXCITING);
                        answerViewHolder.answerItemExcitingImg.setImageResource(R.drawable.hand_thumbsup_fill);
                        String s = answerViewHolder.answerItemExcitingCount.getText().toString();
                        answerViewHolder.answerItemExcitingCount.setText((Integer.parseInt(s) + 1) + "");
                        isExciting[0] = true;
                    }
                }
            });
            answerViewHolder.answerItemNaiveImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, String> queryNaive = new HashMap<>();
                    queryNaive.put("id", aid + "");
                    queryNaive.put("type", MainActivity.TYPE_ANSWER + "");
                    queryNaive.put("token", MainActivity.person.getToken());
                    Http http1 = new Http(context);
                    if (isNaive[0]) {
                        http1.post(Http.URL_CANCEL_NAIVE, queryNaive, Http.TYPE_CANCEL_NAIVE);
                        answerViewHolder.answerItemNaiveImg.setImageResource(R.drawable.hand_thumbsdown);
                        String s = answerViewHolder.answerItemNaiveCount.getText().toString();
                        answerViewHolder.answerItemNaiveCount.setText(Integer.parseInt(s) - 1 + "");
                        isNaive[0] = false;
                    } else {
                        http1.post(Http.URL_NAIVE, queryNaive, Http.TYPE_NAIVE);
                        answerViewHolder.answerItemNaiveImg.setImageResource(R.drawable.hand_thumbsdown_fill);
                        String s = answerViewHolder.answerItemNaiveCount.getText().toString();
                        answerViewHolder.answerItemNaiveCount.setText((Integer.parseInt(s) + 1) + "");
                        isNaive[0] = true;
                    }
                }
            });
        } else {
            final QuestionViewHolder questionViewHolder = (QuestionViewHolder) holder;
            MyHelper.searchQuestion(context, qid, realQuestion);
            //加载作者头像
            if (realQuestion.getAuthorAvatar().length() >= 10) {
                Glide.with(context)
                        .load(realQuestion.getAuthorAvatar())
                        .into(questionViewHolder.realQuestionUserImg);
            }
            questionViewHolder.realQuestionAuthorName.setText(realQuestion.getAuthorName());
            questionViewHolder.realQuestionRecent.setText(realQuestion.getRecent());
            questionViewHolder.realQuestionTitle.setText(realQuestion.getTitle());
            questionViewHolder.realQuestionContent.setText(realQuestion.getContent());
            //加载问题图片
            if (realQuestion.getImages().length() >= 10) {
                Glide.with(context)
                        .load(realQuestion.getImages())
                        .fitCenter()
                        .into(questionViewHolder.realQuestionContentImg);
            } else {
                questionViewHolder.realQuestionContentImg.setVisibility(View.GONE);
            }
            questionViewHolder.realQuestionExcitingCount.setText(realQuestion.getExciting() + "");
            questionViewHolder.realQuestionAnswerCount.setText(realQuestion.getAnswerCount() + "");
            //
            questionViewHolder.realQuestionNaiveCount.setText(realQuestion.getNaive() + "");
            //
            questionViewHolder.isExciting = realQuestion.getIsExciting();
            if (questionViewHolder.isExciting) {
                questionViewHolder.realQuestionExcitingImg.setImageResource(R.drawable.hand_thumbsup_fill);
            } else {
                questionViewHolder.realQuestionNaiveImg.setImageResource(R.drawable.hand_thumbsup);
            }
            questionViewHolder.isNaive = realQuestion.getIsNaive();
            if (questionViewHolder.isNaive) {
                questionViewHolder.realQuestionNaiveImg.setImageResource(R.drawable.hand_thumbsdown_fill);
            } else {
                questionViewHolder.realQuestionNaiveImg.setImageResource(R.drawable.hand_thumbsdown);
            }
            questionViewHolder.isFavorite = realQuestion.getFavorite();
            if (questionViewHolder.isFavorite) {
                questionViewHolder.realQuestionFavoriteImg.setImageResource(R.drawable.star_fill);
            } else {
                questionViewHolder.realQuestionFavoriteImg.setImageResource(R.drawable.star);
            }
            questionViewHolder.realQuestionExciting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, String> queryExciting = new HashMap<>();
                    queryExciting.put("id", qid + "");
                    queryExciting.put("type", MainActivity.TYPE_QUESTION + "");
                    queryExciting.put("token", MainActivity.person.getToken());
                    Http http = new Http(context);
                    if (questionViewHolder.isExciting) {
                        http.post(Http.URL_CANCEL_EXCITING, queryExciting, Http.TYPE_CANCEL_EXCITING);
                        questionViewHolder.realQuestionExcitingImg.setImageResource(R.drawable.hand_thumbsup);
                        String s = questionViewHolder.realQuestionExcitingCount.getText().toString();
                        questionViewHolder.realQuestionExcitingCount.setText(Integer.parseInt(s) - 1 + "");
                        questionViewHolder.isExciting = false;
                    } else {
                        http.post(Http.URL_EXCITING, queryExciting, Http.TYPE_EXCITING);
                        questionViewHolder.realQuestionExcitingImg.setImageResource(R.drawable.hand_thumbsup_fill);
                        String s = questionViewHolder.realQuestionExcitingCount.getText().toString();
                        questionViewHolder.realQuestionExcitingCount.setText((Integer.parseInt(s) + 1) + "");
                        questionViewHolder.isExciting = true;
                    }
                }
            });
            questionViewHolder.realQuestionNaive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, String> queryNaive = new HashMap<>();
                    queryNaive.put("id", qid + "");
                    queryNaive.put("type", MainActivity.TYPE_QUESTION + "");
                    queryNaive.put("token", MainActivity.person.getToken());
                    Http http1 = new Http(context);
                    if (questionViewHolder.isNaive) {
                        http1.post(Http.URL_CANCEL_NAIVE, queryNaive, Http.TYPE_CANCEL_NAIVE);
                        questionViewHolder.realQuestionNaiveImg.setImageResource(R.drawable.hand_thumbsdown);
                        String s = questionViewHolder.realQuestionNaiveCount.getText().toString();
                        questionViewHolder.realQuestionNaiveCount.setText(Integer.parseInt(s) - 1 + "");
                        questionViewHolder.isNaive = false;
                    } else {
                        http1.post(Http.URL_NAIVE, queryNaive, Http.TYPE_NAIVE);
                        questionViewHolder.realQuestionNaiveImg.setImageResource(R.drawable.hand_thumbsdown_fill);
                        String s = questionViewHolder.realQuestionNaiveCount.getText().toString();
                        questionViewHolder.realQuestionNaiveCount.setText((Integer.parseInt(s) + 1) + "");
                        questionViewHolder.isNaive = true;
                    }
                }
            });
            questionViewHolder.realQuestionFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, String> queryFavorite = new HashMap<>();
                    queryFavorite.put("id", qid + "");
                    queryFavorite.put("token", MainActivity.person.getToken());
                    Http http2 = new Http(context);
                    if (questionViewHolder.isFavorite) {
                        http2.post(Http.URL_CANCEL_FAVORITE, queryFavorite, Http.TYPE_CANCEL_FAVORITE);
                        questionViewHolder.realQuestionFavoriteImg.setImageResource(R.drawable.star);
                        questionViewHolder.isFavorite = false;
                    } else {
                        http2.post(Http.URL_FAVORITE, queryFavorite, Http.TYPE_FAVORITE);
                        questionViewHolder.realQuestionFavoriteImg.setImageResource(R.drawable.star_fill);
                        questionViewHolder.isFavorite = true;
                    }
                }
            });
            questionViewHolder.questionBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return MainActivity.TYPE_QUESTION;
        }
        return MainActivity.TYPE_ANSWER;
    }

    @Override
    public int getItemCount() {
        return answerList.size() + 1;
    }

    public void dataChange(List<Answer> answerList) {
        this.answerList = answerList;
    }

    public class AnswerViewHolder extends RecyclerView.ViewHolder {
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
        private ImageView answerItemContentImg;

        public AnswerViewHolder(@NonNull View itemView) {
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
            answerItemContentImg = itemView.findViewById(R.id.answer_item_img);
        }
    }

    public class QuestionViewHolder extends RecyclerView.ViewHolder {
        private ImageView realQuestionUserImg;
        private TextView realQuestionAuthorName;
        private TextView realQuestionRecent;
        private TextView realQuestionTitle;
        private TextView realQuestionContent;
        private ImageView realQuestionContentImg;
        private ImageView realQuestionExcitingImg;
        private TextView realQuestionExcitingCount;
        private TextView realQuestionAnswerCount;
        private ImageView realQuestionNaiveImg;
        private TextView realQuestionNaiveCount;
        private ImageView realQuestionFavoriteImg;
        private LinearLayout realQuestionExciting;
        private LinearLayout realQuestionNaive;
        private LinearLayout realQuestionFavorite;
        private Boolean isExciting;
        private Boolean isNaive;
        private Boolean isFavorite;
        private LinearLayout questionBack;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            realQuestionUserImg = itemView.findViewById(R.id.real_question_user_img);
            realQuestionAuthorName = itemView.findViewById(R.id.real_question_username);
            realQuestionRecent = itemView.findViewById(R.id.real_question_recent);
            realQuestionTitle = itemView.findViewById(R.id.real_question_title);
            realQuestionContent = itemView.findViewById(R.id.real_question_content);
            realQuestionContentImg = itemView.findViewById(R.id.rv_item_content_img);
            realQuestionExcitingImg = itemView.findViewById(R.id.real_question_exciting_img);
            realQuestionExcitingCount = itemView.findViewById(R.id.real_question_exciting_count);
            realQuestionAnswerCount = itemView.findViewById(R.id.real_question_answer_count);
            realQuestionNaiveImg = itemView.findViewById(R.id.real_question_naive_img);
            realQuestionNaiveCount = itemView.findViewById(R.id.real_question_naive_count);
            realQuestionFavoriteImg = itemView.findViewById(R.id.real_question_favorite_img);
            realQuestionExciting = itemView.findViewById(R.id.real_question_exciting);
            realQuestionNaive = itemView.findViewById(R.id.real_question_naive);
            realQuestionFavorite = itemView.findViewById(R.id.real_question_favorite);
            questionBack = itemView.findViewById(R.id.question_back);
        }
    }
}