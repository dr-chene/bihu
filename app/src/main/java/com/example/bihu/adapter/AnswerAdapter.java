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
import com.example.bihu.utils.HttpCallbackListener;
import com.example.bihu.utils.MySQLiteOpenHelper;
import com.example.bihu.utils.Question;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnswerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
        MySQLiteOpenHelper.searchQuestion(context, qid, question);
        MySQLiteOpenHelper.readAnswer(context, answerList, qid);
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
            if (isExciting[0]) {
                answerViewHolder.answerItemExcitingImg.setImageResource(R.drawable.hand_thumbsup_fill);
            } else {
                answerViewHolder.answerItemExcitingImg.setImageResource(R.drawable.hand_thumbsup);
            }
            final Boolean[] isNaive = {answer.getIsNaive()};
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
                        Http http5 = new Http(context, new HttpCallbackListener() {
                            @Override
                            public void postSuccess() {
                                MySQLiteOpenHelper.answerAccept(context,qid);
                                answerViewHolder.answerItemBestBtn.setText("已采纳");
                                answerViewHolder.answerItemBestBtn.setTextColor(Color.parseColor("#FFFF00"));
                                Drawable drawable = context.getResources().getDrawable(R.drawable.accept_bg, null);
                                answerViewHolder.answerItemBestBtn.setBackground(drawable);
                            }

                            @Override
                            public void postFailed(String response) {
                                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                            }
                        });
                        http5.post(Http.URL_ACCEPT, queryBest, Http.TYPE_ACCEPT);

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
                    if (isExciting[0]) {
                        Log.d("debug", "isExciting = " + isExciting[0]);
                        Http http = new Http(context, new HttpCallbackListener() {
                            @Override
                            public void postSuccess() {
                                MySQLiteOpenHelper.answerChange(context,aid,"isExciting",0);
                                answerViewHolder.answerItemExcitingImg.setImageResource(R.drawable.hand_thumbsup);
                                String s = answerViewHolder.answerItemExcitingCount.getText().toString();
                                answerViewHolder.answerItemExcitingCount.setText(Integer.parseInt(s) - 1 + "");
                                isExciting[0] = false;
                            }

                            @Override
                            public void postFailed(String response) {
                                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                            }
                        });
                        http.post(Http.URL_CANCEL_EXCITING, queryExciting, Http.TYPE_CANCEL_EXCITING);

                    } else {
                        Http http = new Http(context, new HttpCallbackListener() {
                            @Override
                            public void postSuccess() {
                                MySQLiteOpenHelper.answerChange(context,aid,"isExciting",1);
                                answerViewHolder.answerItemExcitingImg.setImageResource(R.drawable.hand_thumbsup_fill);
                                String s = answerViewHolder.answerItemExcitingCount.getText().toString();
                                answerViewHolder.answerItemExcitingCount.setText((Integer.parseInt(s) + 1) + "");
                                isExciting[0] = true;
                            }

                            @Override
                            public void postFailed(String response) {
                                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                            }
                        });
                        http.post(Http.URL_EXCITING, queryExciting, Http.TYPE_EXCITING);

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

                    if (isNaive[0]) {
                        Http http1 = new Http(context, new HttpCallbackListener() {
                            @Override
                            public void postSuccess() {
                                MySQLiteOpenHelper.answerChange(context,aid,"isNaive",0);
                                answerViewHolder.answerItemNaiveImg.setImageResource(R.drawable.hand_thumbsdown);
                                String s = answerViewHolder.answerItemNaiveCount.getText().toString();
                                answerViewHolder.answerItemNaiveCount.setText(Integer.parseInt(s) - 1 + "");
                                isNaive[0] = false;
                            }

                            @Override
                            public void postFailed(String response) {
                                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                            }
                        });
                        http1.post(Http.URL_CANCEL_NAIVE, queryNaive, Http.TYPE_CANCEL_NAIVE);

                    } else {
                        Http http1 = new Http(context, new HttpCallbackListener() {
                            @Override
                            public void postSuccess() {
                                MySQLiteOpenHelper.answerChange(context,aid,"isNaive",1);
                                answerViewHolder.answerItemNaiveImg.setImageResource(R.drawable.hand_thumbsdown_fill);
                                String s = answerViewHolder.answerItemNaiveCount.getText().toString();
                                answerViewHolder.answerItemNaiveCount.setText((Integer.parseInt(s) + 1) + "");
                                isNaive[0] = true;
                            }

                            @Override
                            public void postFailed(String response) {
                                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                            }
                        });
                        http1.post(Http.URL_NAIVE, queryNaive, Http.TYPE_NAIVE);

                    }
                }
            });
        } else {
            final QuestionViewHolder questionViewHolder = (QuestionViewHolder) holder;
            //加载作者头像
            if (question.getAuthorAvatar().length() >= 10) {
                Glide.with(context)
                        .load(question.getAuthorAvatar())
                        .into(questionViewHolder.realQuestionUserImg);
            }
            questionViewHolder.realQuestionAuthorName.setText(question.getAuthorName());
            questionViewHolder.realQuestionRecent.setText(question.getRecent());
            questionViewHolder.realQuestionTitle.setText(question.getTitle());
            questionViewHolder.realQuestionContent.setText(question.getContent());
            //加载问题图片
            if (question.getImages().length() >= 10) {
                Glide.with(context)
                        .load(question.getImages())
                        .fitCenter()
                        .into(questionViewHolder.realQuestionContentImg);
            } else {
                questionViewHolder.realQuestionContentImg.setVisibility(View.GONE);
            }
            questionViewHolder.realQuestionExcitingCount.setText(question.getExciting() + "");
            questionViewHolder.realQuestionAnswerCount.setText(question.getAnswerCount() + "");
            //
            questionViewHolder.realQuestionNaiveCount.setText(question.getNaive() + "");
            //
            final Boolean[] isExciting = {question.getIsExciting()};
            if(isExciting[0]) {
                questionViewHolder.realQuestionExcitingImg.setImageResource(R.drawable.hand_thumbsup_fill);
            } else {
                questionViewHolder.realQuestionNaiveImg.setImageResource(R.drawable.hand_thumbsup);
            }
            final Boolean[] isNaive = {question.getIsNaive()};
            if (isNaive[0]) {
                questionViewHolder.realQuestionNaiveImg.setImageResource(R.drawable.hand_thumbsdown_fill);
            } else {
                questionViewHolder.realQuestionNaiveImg.setImageResource(R.drawable.hand_thumbsdown);
            }
            final Boolean[] isFavorite = {question.getFavorite()};
            if (isFavorite[0]) {
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
                    if (isExciting[0]) {
                        Http http = new Http(context, new HttpCallbackListener() {
                            @Override
                            public void postSuccess() {
                                MySQLiteOpenHelper.questionChange(context,qid,"isExciting",0);
                                questionViewHolder.realQuestionExcitingImg.setImageResource(R.drawable.hand_thumbsup);
                                String s = questionViewHolder.realQuestionExcitingCount.getText().toString();
                                questionViewHolder.realQuestionExcitingCount.setText(Integer.parseInt(s) - 1 + "");
                                isExciting[0] = false;
                            }

                            @Override
                            public void postFailed(String response) {
                                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                            }
                        });
                        http.post(Http.URL_CANCEL_EXCITING, queryExciting, Http.TYPE_CANCEL_EXCITING);
                    } else {
                        Http http = new Http(context, new HttpCallbackListener() {
                            @Override
                            public void postSuccess() {
                                MySQLiteOpenHelper.questionChange(context,qid,"isExciting",1);
                                questionViewHolder.realQuestionExcitingImg.setImageResource(R.drawable.hand_thumbsup_fill);
                                String s = questionViewHolder.realQuestionExcitingCount.getText().toString();
                                questionViewHolder.realQuestionExcitingCount.setText((Integer.parseInt(s) + 1) + "");
                                isExciting[0] = true;
                            }

                            @Override
                            public void postFailed(String response) {
                                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                            }
                        });
                        http.post(Http.URL_EXCITING, queryExciting, Http.TYPE_EXCITING);

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

                    if (isNaive[0]) {
                        Http http1 = new Http(context, new HttpCallbackListener() {
                            @Override
                            public void postSuccess() {
                                MySQLiteOpenHelper.questionChange(context,qid,"isNaive",0);
                                questionViewHolder.realQuestionNaiveImg.setImageResource(R.drawable.hand_thumbsdown);
                                String s = questionViewHolder.realQuestionNaiveCount.getText().toString();
                                questionViewHolder.realQuestionNaiveCount.setText(Integer.parseInt(s) - 1 + "");
                                isNaive[0] = false;
                            }

                            @Override
                            public void postFailed(String response) {
                                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                            }
                        });
                        http1.post(Http.URL_CANCEL_NAIVE, queryNaive, Http.TYPE_CANCEL_NAIVE);

                    } else {
                        Http http1 = new Http(context, new HttpCallbackListener() {
                            @Override
                            public void postSuccess() {
                                MySQLiteOpenHelper.questionChange(context,qid,"isNaive",1);
                                questionViewHolder.realQuestionNaiveImg.setImageResource(R.drawable.hand_thumbsdown_fill);
                                String s = questionViewHolder.realQuestionNaiveCount.getText().toString();
                                questionViewHolder.realQuestionNaiveCount.setText((Integer.parseInt(s) + 1) + "");
                                isNaive[0] = true;
                            }

                            @Override
                            public void postFailed(String response) {
                                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                            }
                        });
                        http1.post(Http.URL_NAIVE, queryNaive, Http.TYPE_NAIVE);

                    }
                }
            });
            questionViewHolder.realQuestionFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, String> queryFavorite = new HashMap<>();
                    queryFavorite.put("id", qid + "");
                    queryFavorite.put("token", MainActivity.person.getToken());

                    if (isFavorite[0]) {
                        Http http2 = new Http(context, new HttpCallbackListener() {
                            @Override
                            public void postSuccess() {
                                MySQLiteOpenHelper.questionChange(context,qid,"isFavorite",0);
                                questionViewHolder.realQuestionFavoriteImg.setImageResource(R.drawable.star);
                                isFavorite[0] = false;
                            }

                            @Override
                            public void postFailed(String response) {
                                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                            }
                        });
                        http2.post(Http.URL_CANCEL_FAVORITE, queryFavorite, Http.TYPE_CANCEL_FAVORITE);

                    } else {
                        Http http2 = new Http(context, new HttpCallbackListener() {
                            @Override
                            public void postSuccess() {
                                MySQLiteOpenHelper.questionChange(context,qid,"isFavorite",1);
                                questionViewHolder.realQuestionFavoriteImg.setImageResource(R.drawable.star_fill);
                                isFavorite[0] = true;
                            }

                            @Override
                            public void postFailed(String response) {
                                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                            }
                        });
                        http2.post(Http.URL_FAVORITE, queryFavorite, Http.TYPE_FAVORITE);

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

    public void refresh(){
        MySQLiteOpenHelper.searchQuestion(context,qid,question);
        MySQLiteOpenHelper.readAnswer(context,answerList,qid);
    };
}