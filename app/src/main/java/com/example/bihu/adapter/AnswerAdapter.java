package com.example.bihu.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.bihu.R;
import com.example.bihu.activity.MainActivity;
import com.example.bihu.utils.Answer;
import com.example.bihu.utils.Http;
import com.example.bihu.utils.HttpCallbackListener;
import com.example.bihu.utils.MySQLiteOpenHelper;
import com.example.bihu.utils.MyToast;
import com.example.bihu.utils.Question;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnswerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public Question question;
    private Context context;
    private List<Answer> answers;
    private int qid;
    private Handler handler = new Handler();

    public AnswerAdapter(Context context, List<Answer> answers, Question question) {
        this.question = question;
        this.qid = question.getId();
        this.context = context;
        this.answers = answers;
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
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (getItemViewType(position) == MainActivity.TYPE_ANSWER) {
            final AnswerViewHolder answerViewHolder = (AnswerViewHolder) holder;
            Answer answer = answers.get(position - 1);
            //加载answer作者头像
            if (answer.getAuthorAvatar().length() >= 5) {
                Glide.with(context)
                        .load(answer.getAuthorAvatar())
                        .error(R.drawable.error_avatar)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(answerViewHolder.answerItemAuthorImg);
            }
            //加载answer图片
            if (answer.getImages().length() >= 5) {
                Glide.with(context)
                        .load(answer.getImages())
                        .error(R.drawable.error)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .fitCenter()
                        .into(answerViewHolder.answerItemContentImg);
            } else {
                answerViewHolder.answerItemContentImg.setVisibility(View.GONE);
            }
            //加载answer数据
            answerViewHolder.answerItemAuthorName.setText(answer.getAuthorName());
            answerViewHolder.answerItemContent.setText(answer.getContent());
            if (answer.getContent().length() < 1) {
                answerViewHolder.answerItemContent.setVisibility(View.GONE);
            }
            answerViewHolder.answerItemDate.setText(answer.getDate());
            answerViewHolder.answerItemExcitingCount.setText(answer.getExciting() + "");
            answerViewHolder.answerItemNaiveCount.setText(answer.getNaive() + "");
            if (answer.getIsExciting()) {
                answerViewHolder.answerItemExcitingImg.setImageResource(R.drawable.hand_thumbsup_fill);
            } else {
                answerViewHolder.answerItemExcitingImg.setImageResource(R.drawable.hand_thumbsup);
            }
            if (answer.getIsNaive()) {
                answerViewHolder.answerItemNaiveImg.setImageResource(R.drawable.hand_thumbsdown_fill);
            } else {
                answerViewHolder.answerItemNaiveImg.setImageResource(R.drawable.hand_thumbsdown);
            }
            if (!(question.getAuthorId() == MainActivity.person.getId())) {
                answerViewHolder.answerItemBestBtn.setVisibility(View.GONE);
            }
            if (answer.getBest() == 1) {
                answerViewHolder.answerItemBestBtn.setText("已采纳");
                answerViewHolder.answerItemBestBtn.setTextColor(Color.parseColor("#FFFF00"));
                Drawable drawable = context.getResources().getDrawable(R.drawable.accept_bg, null);
                answerViewHolder.answerItemBestBtn.setBackground(drawable);
            }
            //best点击事件
            answerViewHolder.answerItemBestBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, String> queryBest = new HashMap<>();
                    queryBest.put("qid", qid + "");
                    queryBest.put("aid", answers.get(position - 1).getId() + "");
                    queryBest.put("token", MainActivity.person.getToken());
                    Http.sendHttpRequest(Http.URL_ACCEPT, queryBest, new HttpCallbackListener() {
                        @Override
                        public void onFinish(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getInt("status") != 200) {
                                    Looper.prepare();
                                    MyToast.showToast(jsonObject.getInt("status") + " : " + jsonObject.getString("info"));
                                    Looper.loop();
                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            MySQLiteOpenHelper.answerAccept(qid);
                                            answerViewHolder.answerItemBestBtn.setText("已采纳");
                                            answerViewHolder.answerItemBestBtn.setTextColor(Color.parseColor("#FFFF00"));
                                            Drawable drawable = context.getResources().getDrawable(R.drawable.accept_bg, null);
                                            answerViewHolder.answerItemBestBtn.setBackground(drawable);
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onNetworkError() {

                        }
                    });
                }
            });
            //exciting点击事件
            answerViewHolder.answerItemExciting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, String> query = new HashMap<>();
                    query.put("id", answers.get(position - 1).getId() + "");
                    query.put("type", MainActivity.TYPE_ANSWER + "");
                    query.put("token", MainActivity.person.getToken());
                    if (answers.get(position - 1).getIsExciting()) {
                        Http.sendHttpRequest(Http.URL_CANCEL_EXCITING, query, new HttpCallbackListener() {
                            @Override
                            public void onFinish(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getInt("status") != 200) {
                                        Looper.prepare();
                                        MyToast.showToast(jsonObject.getInt("status") + " : " + jsonObject.getString("info"));
                                        Looper.loop();
                                    } else {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                answerViewHolder.answerItemExcitingImg.setImageResource(R.drawable.hand_thumbsup);
                                                String s = answerViewHolder.answerItemExcitingCount.getText().toString();
                                                answerViewHolder.answerItemExcitingCount.setText(Integer.parseInt(s) - 1 + "");
                                                answers.get(position - 1).setExciting(false);
                                                MySQLiteOpenHelper.answerChange(answers.get(position - 1).getId(), "isExciting", 0);
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onNetworkError() {

                            }
                        });
                    } else {
                        Http.sendHttpRequest(Http.URL_EXCITING, query, new HttpCallbackListener() {
                            @Override
                            public void onFinish(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getInt("status") != 200) {
                                        Looper.prepare();
                                        MyToast.showToast(jsonObject.getInt("status") + " : " + jsonObject.getString("info"));
                                        Looper.loop();
                                    } else {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                answerViewHolder.answerItemExcitingImg.setImageResource(R.drawable.hand_thumbsup_fill);
                                                String s = answerViewHolder.answerItemExcitingCount.getText().toString();
                                                answerViewHolder.answerItemExcitingCount.setText((Integer.parseInt(s) + 1) + "");
                                                answers.get(position - 1).setExciting(true);
                                                MySQLiteOpenHelper.answerChange(answers.get(position - 1).getId(), "isExciting", 1);
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onNetworkError() {

                            }
                        });
                    }
                }
            });
            //naive点击事件
            answerViewHolder.answerItemNaive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, String> query = new HashMap<>();
                    query.put("id", answers.get(position - 1).getId() + "");
                    query.put("type", MainActivity.TYPE_ANSWER + "");
                    query.put("token", MainActivity.person.getToken());
                    if (answers.get(position - 1).getIsNaive()) {
                        Http.sendHttpRequest(Http.URL_CANCEL_NAIVE, query, new HttpCallbackListener() {
                            @Override
                            public void onFinish(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getInt("status") != 200) {
                                        Looper.prepare();
                                        MyToast.showToast(jsonObject.getInt("status") + " : " + jsonObject.getString("info"));
                                        Looper.loop();
                                    } else {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                answerViewHolder.answerItemNaiveImg.setImageResource(R.drawable.hand_thumbsdown);
                                                String s = answerViewHolder.answerItemNaiveCount.getText().toString();
                                                answerViewHolder.answerItemNaiveCount.setText(Integer.parseInt(s) - 1 + "");
                                                answers.get(position - 1).setNaive(false);
                                                MySQLiteOpenHelper.answerChange(answers.get(position - 1).getId(), "isNaive", 0);
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onNetworkError() {

                            }
                        });
                    } else {
                        Http.sendHttpRequest(Http.URL_NAIVE, query, new HttpCallbackListener() {
                            @Override
                            public void onFinish(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getInt("status") != 200) {
                                        Looper.prepare();
                                        MyToast.showToast(jsonObject.getInt("status") + " : " + jsonObject.getString("info"));
                                        Looper.loop();
                                    } else {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                answerViewHolder.answerItemNaiveImg.setImageResource(R.drawable.hand_thumbsdown_fill);
                                                String s = answerViewHolder.answerItemNaiveCount.getText().toString();
                                                answerViewHolder.answerItemNaiveCount.setText((Integer.parseInt(s) + 1) + "");
                                                answers.get(position - 1).setNaive(true);
                                                MySQLiteOpenHelper.answerChange(answers.get(position - 1).getId(), "isNaive", 1);
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onNetworkError() {

                            }
                        });

                    }
                }
            });
        } else {
            final QuestionViewHolder questionViewHolder = (QuestionViewHolder) holder;
            //加载作者头像
            if (question.getAuthorAvatar().length() >= 5) {
                Glide.with(context)
                        .load(question.getAuthorAvatar())
                        .error(R.drawable.error_avatar)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(questionViewHolder.realQuestionUserImg);
            }
            questionViewHolder.realQuestionAuthorName.setText(question.getAuthorName());
            questionViewHolder.realQuestionRecent.setText(question.getRecent());
            questionViewHolder.realQuestionTitle.setText(question.getTitle());
            questionViewHolder.realQuestionContent.setText(question.getContent());
            //加载问题图片
            if (question.getImages().length() >= 5) {
                Glide.with(context)
                        .load(question.getImages())
                        .error(R.drawable.error)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .fitCenter()
                        .into(questionViewHolder.realQuestionContentImg);
            } else {
                questionViewHolder.realQuestionContentImg.setVisibility(View.GONE);
            }
            //加载问题数据
            questionViewHolder.realQuestionExcitingCount.setText(question.getExciting() + "");
            questionViewHolder.realQuestionAnswerCount.setText(question.getAnswerCount() + "");
            questionViewHolder.realQuestionNaiveCount.setText(question.getNaive() + "");
            if (question.getIsExciting()) {
                questionViewHolder.realQuestionExcitingImg.setImageResource(R.drawable.hand_thumbsup_fill);
            } else {
                questionViewHolder.realQuestionNaiveImg.setImageResource(R.drawable.hand_thumbsup);
            }
            if (question.getIsNaive()) {
                questionViewHolder.realQuestionNaiveImg.setImageResource(R.drawable.hand_thumbsdown_fill);
            } else {
                questionViewHolder.realQuestionNaiveImg.setImageResource(R.drawable.hand_thumbsdown);
            }
            if (question.getFavorite()) {
                questionViewHolder.realQuestionFavoriteImg.setImageResource(R.drawable.star_fill);
            } else {
                questionViewHolder.realQuestionFavoriteImg.setImageResource(R.drawable.star);
            }
            //设置exciting点击事件
            questionViewHolder.realQuestionExciting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, String> query = new HashMap<>();
                    query.put("id", qid + "");
                    query.put("type", MainActivity.TYPE_QUESTION + "");
                    query.put("token", MainActivity.person.getToken());
                    if (question.getIsExciting()) {
                        Http.sendHttpRequest(Http.URL_CANCEL_EXCITING, query, new HttpCallbackListener() {
                            @Override
                            public void onFinish(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getInt("status") != 200) {
                                        Looper.prepare();
                                        MyToast.showToast(jsonObject.getInt("status") + " : " + jsonObject.getString("info"));
                                        Looper.loop();
                                    } else {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                questionViewHolder.realQuestionExcitingImg.setImageResource(R.drawable.hand_thumbsup);
                                                String s = questionViewHolder.realQuestionExcitingCount.getText().toString();
                                                questionViewHolder.realQuestionExcitingCount.setText(Integer.parseInt(s) - 1 + "");
                                                question.setExciting(false);
                                                question.setExciting(question.getExciting() - 1);
                                                MySQLiteOpenHelper.questionChange(qid, "isExciting", 0);
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onNetworkError() {

                            }
                        });

                    } else {
                        Http.sendHttpRequest(Http.URL_EXCITING, query, new HttpCallbackListener() {
                            @Override
                            public void onFinish(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getInt("status") != 200) {
                                        Looper.prepare();
                                        MyToast.showToast(jsonObject.getInt("status") + " : " + jsonObject.getString("info"));
                                        Looper.loop();
                                    } else {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                questionViewHolder.realQuestionExcitingImg.setImageResource(R.drawable.hand_thumbsup_fill);
                                                String s = questionViewHolder.realQuestionExcitingCount.getText().toString();
                                                questionViewHolder.realQuestionExcitingCount.setText(Integer.parseInt(s) + 1 + "");
                                                question.setExciting(true);
                                                question.setExciting(question.getExciting() + 1);
                                                MySQLiteOpenHelper.questionChange(qid, "isExciting", 1);
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onNetworkError() {

                            }
                        });

                    }
                }
            });
            //设置naive点击事件
            questionViewHolder.realQuestionNaive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, String> query = new HashMap<>();
                    query.put("id", qid + "");
                    query.put("type", MainActivity.TYPE_QUESTION + "");
                    query.put("token", MainActivity.person.getToken());
                    if (question.getIsNaive()) {
                        Http.sendHttpRequest(Http.URL_CANCEL_NAIVE, query, new HttpCallbackListener() {
                            @Override
                            public void onFinish(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getInt("status") != 200) {
                                        Looper.prepare();
                                        MyToast.showToast(jsonObject.getInt("status") + " : " + jsonObject.getString("info"));
                                        Looper.loop();
                                    } else {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                questionViewHolder.realQuestionNaiveImg.setImageResource(R.drawable.hand_thumbsdown);
                                                String s = questionViewHolder.realQuestionNaiveCount.getText().toString();
                                                questionViewHolder.realQuestionNaiveCount.setText(Integer.parseInt(s) - 1 + "");
                                                question.setNaive(false);
                                                question.setNaive(question.getNaive() - 1);
                                                MySQLiteOpenHelper.questionChange(qid, "isNaive", 0);
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onNetworkError() {

                            }
                        });

                    } else {
                        Http.sendHttpRequest(Http.URL_NAIVE, query, new HttpCallbackListener() {
                            @Override
                            public void onFinish(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getInt("status") != 200) {
                                        Looper.prepare();
                                        MyToast.showToast(jsonObject.getInt("status") + " : " + jsonObject.getString("info"));
                                        Looper.loop();
                                    } else {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                questionViewHolder.realQuestionNaiveImg.setImageResource(R.drawable.hand_thumbsdown_fill);
                                                String s = questionViewHolder.realQuestionNaiveCount.getText().toString();
                                                questionViewHolder.realQuestionNaiveCount.setText(Integer.parseInt(s) + 1 + "");
                                                question.setNaive(true);
                                                question.setNaive(question.getNaive() + 1);
                                                MySQLiteOpenHelper.questionChange(qid, "isNaive", 1);
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onNetworkError() {

                            }
                        });

                    }
                }
            });
            //设置favorite点击事件
            questionViewHolder.realQuestionFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, String> queryFavorite = new HashMap<>();
                    queryFavorite.put("id", qid + "");
                    queryFavorite.put("token", MainActivity.person.getToken());
                    if (question.getFavorite()) {
                        Http.sendHttpRequest(Http.URL_CANCEL_FAVORITE, queryFavorite, new HttpCallbackListener() {
                            @Override
                            public void onFinish(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getInt("status") != 200) {
                                        Looper.prepare();
                                        MyToast.showToast(jsonObject.getInt("status") + " : " + jsonObject.getString("info"));
                                        Looper.loop();
                                    } else {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                MySQLiteOpenHelper.questionChange(qid, "isFavorite", 0);
                                                questionViewHolder.realQuestionFavoriteImg.setImageResource(R.drawable.star);
                                                question.setFavorite(false);
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onNetworkError() {

                            }
                        });
                    } else {
                        Http.sendHttpRequest(Http.URL_FAVORITE, queryFavorite, new HttpCallbackListener() {
                            @Override
                            public void onFinish(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getInt("status") != 200) {
                                        Looper.prepare();
                                        MyToast.showToast(jsonObject.getInt("status") + " : " + jsonObject.getString("info"));
                                        Looper.loop();
                                    } else {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                MySQLiteOpenHelper.questionChange(qid, "isFavorite", 1);
                                                questionViewHolder.realQuestionFavoriteImg.setImageResource(R.drawable.star_fill);
                                                question.setFavorite(true);
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onNetworkError() {

                            }
                        });
                    }
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
        return answers.size() + 1;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
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
        private int curPosition;

        AnswerViewHolder(@NonNull View itemView) {
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

        QuestionViewHolder(@NonNull View itemView) {
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
        }
    }

}