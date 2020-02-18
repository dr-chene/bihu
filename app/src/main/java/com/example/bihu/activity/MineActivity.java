package com.example.bihu.activity;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bihu.R;
import com.example.bihu.adapter.QuestionAdapter;
import com.example.bihu.utils.MySQLiteOpenHelper;
import com.example.bihu.utils.Question;

import java.util.ArrayList;
import java.util.List;

public class MineActivity extends BaseActivity {

    private QuestionAdapter mineAdapter;
    private List<Question> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);
        RecyclerView recyclerView;
        TextView mineBack;
        mineBack = findViewById(R.id.mine_back);
        Drawable drawable = getResources().getDrawable(R.drawable.fanhui);
        drawable.setBounds(0, 0, 40, 40);
        mineBack.setCompoundDrawables(drawable, null, null, null);
        recyclerView = findViewById(R.id.mine_rv);
        questions = new ArrayList<>();
        MySQLiteOpenHelper.readMine(questions);
        mineAdapter = new QuestionAdapter(MineActivity.this, MainActivity.TYPE_MINE, questions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mineAdapter);
        mineBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                int position = data.getIntExtra("position", -1);
                if (position != -1) {
                    Question question = questions.get(position);
                    question.setExciting(data.getBooleanExtra("isExciting", false));
                    question.setNaive(data.getBooleanExtra("isNaive", false));
                    question.setFavorite(data.getBooleanExtra("isFavorite", false));
                    question.setAnswerCount(data.getIntExtra("answerCount", 0));
                    question.setExciting(data.getIntExtra("excitingCount", 0));
                    question.setNaive(data.getIntExtra("naiveCount", 0));
                    mineAdapter.setQuestions(questions);
                    mineAdapter.notifyItemChanged(position, -1);
                }
            }
        }
    }

}
