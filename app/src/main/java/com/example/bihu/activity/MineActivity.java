package com.example.bihu.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bihu.R;
import com.example.bihu.adapter.QuestionAdapter;
import com.example.bihu.utils.Question;

public class MineActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private QuestionAdapter mineAdapter;
    private LinearLayout mineBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);
        mineBack = findViewById(R.id.mine_back);
        recyclerView = findViewById(R.id.mine_rv);
        mineAdapter = new QuestionAdapter(MineActivity.this, MainActivity.TYPE_MINE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mineAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
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
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    int position = data.getIntExtra("position", -1);
                    if (position != -1) {
                        Question question = mineAdapter.getQuestion(position, MainActivity.TYPE_MINE);
                        question.setExciting(data.getBooleanExtra("isExciting", false));
                        question.setNaive(data.getBooleanExtra("isNaive", false));
                        question.setFavorite(data.getBooleanExtra("isFavorite", false));
                        question.setAnswerCount(data.getIntExtra("answerCount", 0));
                        question.setExciting(data.getIntExtra("excitingCount", 0));
                        question.setNaive(data.getIntExtra("naiveCount", 0));
                        mineAdapter.notifyItemChanged(position, -1);
                    }
                }
                break;
            default:
        }
    }

}
