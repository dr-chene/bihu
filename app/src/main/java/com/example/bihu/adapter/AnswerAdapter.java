package com.example.bihu.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnswerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int ACCEPT = 1;
    private final int CANCEL_FAVORITE = 2;
    private final int FAVORITE = 3;
    private final String TAG = "Adapter";
    private Context context;
    private List<Answer> answerList = new ArrayList<>();
    private int qid;
    private int aid;
    private Question question;
    private Boolean best = true;
    private Boolean[] isExciting;
    private Boolean[] isNaive;
    private Boolean isFavorite = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case ACCEPT:
                    AnswerViewHolder answerViewHolder = (AnswerViewHolder) msg.obj;
                    answerViewHolder.answerItemBestBtn.setText("已采纳");
                    answerViewHolder.answerItemBestBtn.setTextColor(Color.parseColor("#FFFF00"));
                    Drawable drawable = context.getResources().getDrawable(R.drawable.accept_bg, null);
                    answerViewHolder.answerItemBestBtn.setBackground(drawable);
                    break;
                case CANCEL_FAVORITE:
                    QuestionViewHolder questionViewHolder = (QuestionViewHolder) msg.obj;
                    questionViewHolder.realQuestionFavoriteImg.setImageResource(R.drawable.star);
                    isFavorite = (msg.arg1 == 1);
                    break;
                case FAVORITE:
                    QuestionViewHolder questionViewHolder1 = (QuestionViewHolder) msg.obj;
                    questionViewHolder1.realQuestionFavoriteImg.setImageResource(R.drawable.star_fill);
                    isFavorite = (msg.arg1 == 1);
                    break;
            }
        }
    };

    public AnswerAdapter(Context context, int qid) {
        this.qid = qid;
        this.context = context;
        question = new Question();
        refresh();
        Log.d("first", "" + getItemCount());
        Log.d(TAG, "AnswerAdapter: 构造器");

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: ");
        final int curPosition = position;
        if (getItemViewType(curPosition) == MainActivity.TYPE_ANSWER) {
            final AnswerViewHolder answerViewHolder = (AnswerViewHolder) holder;
            final Answer answer = answerList.get(curPosition - 1);
            if (answer.getAuthorAvatar().length() >= 10) {
                Glide.with(context)
                        .load(answer.getAuthorAvatar())
                        .error(R.drawable.error_avatar)
                        .into(answerViewHolder.answerItemAuthorImg);
            }
            if (answer.getImages().length() >= 10) {
                Glide.with(context)
                        .load(answer.getImages())
                        .error(R.drawable.error)
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
            if (isExciting[curPosition]) {
                answerViewHolder.answerItemExcitingImg.setImageResource(R.drawable.hand_thumbsup_fill);
            } else {
                answerViewHolder.answerItemExcitingImg.setImageResource(R.drawable.hand_thumbsup);
            }
            if (isNaive[curPosition]) {
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
                        Http.sendHttpRequest(Http.URL_ACCEPT, queryBest, new HttpCallbackListener() {
                            @Override
                            public void onFinish(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getInt("status") != 200) {
                                        Looper.prepare();
                                        Toast.makeText(context, jsonObject.getString("info"), Toast.LENGTH_SHORT).show();
                                        Looper.prepare();
                                    } else {
                                        MySQLiteOpenHelper.answerAccept(context, qid);
                                        Message msg = new Message();
                                        msg.what = ACCEPT;
                                        msg.obj = answerViewHolder;
                                        handler.sendMessage(msg);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                    } else {
                        Toast.makeText(context, "best只能有一位", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            answerViewHolder.answerItemExcitingImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isExciting[curPosition]) {
                        answerViewHolder.answerItemExcitingImg.setImageResource(R.drawable.hand_thumbsup);
                        String s = answerViewHolder.answerItemExcitingCount.getText().toString();
                        answerViewHolder.answerItemExcitingCount.setText(Integer.parseInt(s) - 1 + "");
                        isExciting[curPosition] = false;
                    } else {
                        answerViewHolder.answerItemExcitingImg.setImageResource(R.drawable.hand_thumbsup_fill);
                        String s = answerViewHolder.answerItemExcitingCount.getText().toString();
                        answerViewHolder.answerItemExcitingCount.setText((Integer.parseInt(s) + 1) + "");
                        isExciting[curPosition] = true;
                    }
                }
            });
            answerViewHolder.answerItemNaiveImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isNaive[curPosition]) {
                        answerViewHolder.answerItemNaiveImg.setImageResource(R.drawable.hand_thumbsdown);
                        String s = answerViewHolder.answerItemNaiveCount.getText().toString();
                        answerViewHolder.answerItemNaiveCount.setText(Integer.parseInt(s) - 1 + "");
                        isNaive[curPosition] = false;
                    } else {
                        answerViewHolder.answerItemNaiveImg.setImageResource(R.drawable.hand_thumbsdown_fill);
                        String s = answerViewHolder.answerItemNaiveCount.getText().toString();
                        answerViewHolder.answerItemNaiveCount.setText((Integer.parseInt(s) + 1) + "");
                        isNaive[curPosition] = true;
                    }
                }
            });
        } else {
            final QuestionViewHolder questionViewHolder = (QuestionViewHolder) holder;
            //加载作者头像
            if (question.getAuthorAvatar().length() >= 10) {
                Glide.with(context)
                        .load(question.getAuthorAvatar())
                        .error(R.drawable.error_avatar)
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
                        .error(R.drawable.error)
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
            if (isExciting[curPosition]) {
                questionViewHolder.realQuestionExcitingImg.setImageResource(R.drawable.hand_thumbsup_fill);
            } else {
                questionViewHolder.realQuestionNaiveImg.setImageResource(R.drawable.hand_thumbsup);
            }
            isNaive[curPosition] = question.getIsNaive();
            if (isNaive[curPosition]) {
                questionViewHolder.realQuestionNaiveImg.setImageResource(R.drawable.hand_thumbsdown_fill);
            } else {
                questionViewHolder.realQuestionNaiveImg.setImageResource(R.drawable.hand_thumbsdown);
            }
            if (isFavorite) {
                questionViewHolder.realQuestionFavoriteImg.setImageResource(R.drawable.star_fill);
            } else {
                questionViewHolder.realQuestionFavoriteImg.setImageResource(R.drawable.star);
            }
            questionViewHolder.realQuestionExciting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isExciting[curPosition]) {
                        questionViewHolder.realQuestionExcitingImg.setImageResource(R.drawable.hand_thumbsup);
                        String s = questionViewHolder.realQuestionExcitingCount.getText().toString();
                        questionViewHolder.realQuestionExcitingCount.setText(Integer.parseInt(s) - 1 + "");
                        isExciting[curPosition] = false;
                    } else {
                        questionViewHolder.realQuestionExcitingImg.setImageResource(R.drawable.hand_thumbsup_fill);
                        String s = questionViewHolder.realQuestionExcitingCount.getText().toString();
                        questionViewHolder.realQuestionExcitingCount.setText(Integer.parseInt(s) + 1 + "");
                        isExciting[curPosition] = true;
                    }
                }
            });
            questionViewHolder.realQuestionNaive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isNaive[curPosition]) {
                        questionViewHolder.realQuestionNaiveImg.setImageResource(R.drawable.hand_thumbsdown);
                        String s = questionViewHolder.realQuestionNaiveCount.getText().toString();
                        questionViewHolder.realQuestionNaiveCount.setText(Integer.parseInt(s) - 1 + "");
                        isNaive[curPosition] = false;
                    } else {
                        questionViewHolder.realQuestionNaiveImg.setImageResource(R.drawable.hand_thumbsdown_fill);
                        String s = questionViewHolder.realQuestionNaiveCount.getText().toString();
                        questionViewHolder.realQuestionNaiveCount.setText(Integer.parseInt(s) + 1 + "");
                        isNaive[curPosition] = true;
                    }
                }
            });
            questionViewHolder.realQuestionFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, String> queryFavorite = new HashMap<>();
                    queryFavorite.put("id", qid + "");
                    queryFavorite.put("token", MainActivity.person.getToken());
                    if (isFavorite) {
                        Http.sendHttpRequest(Http.URL_CANCEL_FAVORITE, queryFavorite, new HttpCallbackListener() {
                            @Override
                            public void onFinish(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getInt("status") != 200) {
                                        Looper.prepare();
                                        Toast.makeText(context, jsonObject.getString("info"), Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    } else {
                                        MySQLiteOpenHelper.questionChange(context, qid, "isFavorite", 0);
                                        Message msg = new Message();
                                        msg.what = CANCEL_FAVORITE;
                                        msg.arg1 = 0;
                                        msg.obj = questionViewHolder;
                                        handler.sendMessage(msg);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Exception e) {

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
                                        Toast.makeText(context, jsonObject.getString("info"), Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    } else {
                                        MySQLiteOpenHelper.questionChange(context, qid, "isFavorite", 1);
                                        Message msg = new Message();
                                        msg.what = FAVORITE;
                                        msg.arg1 = 1;
                                        msg.obj = questionViewHolder;
                                        handler.sendMessage(msg);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
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
        Log.d(TAG, "getItemCount: ");
        return answerList.size() + 1;
    }

    public void refresh() {
        MySQLiteOpenHelper.searchQuestion(context, qid, question);
        MySQLiteOpenHelper.readAnswer(context, answerList, qid);
        isExciting = new Boolean[answerList.size() + 1];
        isNaive = new Boolean[answerList.size() + 1];
        for (int i = 0; i < isExciting.length; i++) {
            if (i == 0) {
                isExciting[i] = question.getIsExciting();
                isNaive[i] = question.getIsNaive();
            } else {
                isExciting[i] = answerList.get(i - 1).getIsExciting();
                isNaive[i] = answerList.get(i - 1).getIsNaive();
            }
        }
    }

    public void post() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < answerList.size() + 1; i++) {
                    if (i == 0) {
                        Map<String, String> query = new HashMap<>();
                        query.put("id", qid + "");
                        query.put("type", MainActivity.TYPE_QUESTION + "");
                        query.put("token", MainActivity.person.getToken());
                        if (!isExciting[i].equals(question.getIsExciting())) {
                            if (isExciting[i]) {
                                Http.sendHttpRequest(Http.URL_EXCITING, query, null);
                                MySQLiteOpenHelper.answerChange(context, aid, "isExciting", 1);
                            } else {
                                Http.sendHttpRequest(Http.URL_CANCEL_EXCITING, query, null);
                                MySQLiteOpenHelper.answerChange(context, aid, "isExciting", 0);
                            }
                        }
                        if (!isNaive[i].equals(question.getIsNaive())) {
                            if (isNaive[i]) {
                                Http.sendHttpRequest(Http.URL_NAIVE, query, null);
                                MySQLiteOpenHelper.answerChange(context, aid, "isNaive", 1);
                            } else {
                                Http.sendHttpRequest(Http.URL_CANCEL_NAIVE, query, null);
                                MySQLiteOpenHelper.answerChange(context, aid, "isNaive", 0);
                            }
                        }
                    } else {
                        Map<String, String> query = new HashMap<>();
                        query.put("id", aid + "");
                        query.put("type", MainActivity.TYPE_ANSWER + "");
                        query.put("token", MainActivity.person.getToken());
                        if (!isExciting[i].equals(answerList.get(i - 1).getIsExciting())) {
                            if (isExciting[i]) {
                                Http.sendHttpRequest(Http.URL_EXCITING, query, null);
                                MySQLiteOpenHelper.questionChange(context, qid, "isExciting", 1);
                            } else {
                                Http.sendHttpRequest(Http.URL_CANCEL_EXCITING, query, null);
                                MySQLiteOpenHelper.questionChange(context, qid, "isExciting", 0);
                            }
                        }
                        if (!isNaive[i].equals(answerList.get(i - 1).getIsNaive())) {
                            if (isNaive[i]) {
                                Http.sendHttpRequest(Http.URL_NAIVE, query, null);
                                MySQLiteOpenHelper.questionChange(context, qid, "isNaive", 1);
                            } else {
                                Http.sendHttpRequest(Http.URL_CANCEL_NAIVE, query, null);
                                MySQLiteOpenHelper.questionChange(context, qid, "isNaive", 0);
                            }
                        }
                    }
                }
            }
        }).start();
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
            Log.d(TAG, "AnswerViewHolder: ");
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

}