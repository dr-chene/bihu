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

public class FavoriteActivity extends BaseActivity {

    private QuestionAdapter favoriteAdapter;
    private List<Question> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        initView();
    }

    /*
    加载视图，绑定数据，设置点击事件
     */
    private void initView() {
        RecyclerView recyclerView;
        TextView favoriteBack;
        favoriteBack = findViewById(R.id.favorite_back);
        Drawable drawable = getResources().getDrawable(R.drawable.fanhui);
        drawable.setBounds(0, 0, 40, 40);
        favoriteBack.setCompoundDrawables(drawable, null, null, null);
        recyclerView = findViewById(R.id.favorite_rv);
        questions = new ArrayList<>();
        MySQLiteOpenHelper.readFavorite(questions);
        favoriteAdapter = new QuestionAdapter(FavoriteActivity.this, MainActivity.TYPE_FAVORITE, questions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(favoriteAdapter);
        favoriteBack.setOnClickListener(new View.OnClickListener() {
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
                    favoriteAdapter.setQuestions(questions);
                    favoriteAdapter.notifyItemChanged(position, -1);
                }
            }
        }
    }

}
