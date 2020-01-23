package com.example.bihu;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bihu.tool.MyHelper;
import com.example.bihu.tool.Question;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.MyInnerViewHolder> {

    private MyHelper myHelper;
    private SQLiteDatabase db;
    private List<Question> questionList =new ArrayList<>();
    private Context context;
    private Question question;
    private ImageView questionItemUserImg;
    private TextView questionItemAuthorName;
    private TextView questionItemRecent;
    private TextView questionItemTitle;
    private TextView questionItemContent;
    //       private ImageView questionItemContentImg;
    private TextView questionItemExcitingCount;
    private TextView questionItemAnswerCount;
    private TextView questionItemNaiveCount;
    private LinearLayout questionItem;

    public QuestionAdapter(Context context) {
        this.context = context;
        myHelper = new MyHelper(this.context, MainActivity.vision);
        db = myHelper.getReadableDatabase();
        getQuestionData();
    }

    @NonNull
    @Override
    public MyInnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item, parent, false);
        return new MyInnerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyInnerViewHolder holder, final int position) {
        question = questionList.get(position);
        questionItemAuthorName.setText(question.getAuthorName());
        questionItemRecent.setText(question.getRecent());
        questionItemTitle.setText(question.getTitle());
        questionItemContent.setText(question.getContent());
        questionItemExcitingCount.setText(question.getExciting() + "");
        questionItemAnswerCount.setText(question.getAnswerCount() + "");
        questionItemNaiveCount.setText(question.getNaive() + "");
        final int id = question.getId();
        questionItem.setOnClickListener(new View.OnClickListener() {
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
            return questionList.size();

    }

    public void refresh() {
       getQuestionData();
    }

    public void getQuestionData() {
        myHelper.readQuestion(db, questionList);
    }

    public class MyInnerViewHolder extends RecyclerView.ViewHolder {


        public MyInnerViewHolder(@NonNull View itemView) {
            super(itemView);
            questionItemAuthorName = itemView.findViewById(R.id.question_item_username);
            questionItemRecent = itemView.findViewById(R.id.question_item_recent);
            questionItemTitle = itemView.findViewById(R.id.question_item_title);
            questionItemContent = itemView.findViewById(R.id.question_item_content);
            questionItemExcitingCount = itemView.findViewById(R.id.question_item_exciting_count);
            questionItemAnswerCount = itemView.findViewById(R.id.question_item_answer_count);
            questionItemNaiveCount = itemView.findViewById(R.id.question_item_naive_count);
            questionItem = itemView.findViewById(R.id.question_item);
        }
    }
}
