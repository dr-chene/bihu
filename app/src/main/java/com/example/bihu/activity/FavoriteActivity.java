package com.example.bihu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bihu.R;
import com.example.bihu.adapter.QuestionAdapter;
import com.example.bihu.utils.Question;

public class FavoriteActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private QuestionAdapter favoriteAdapter;
    private LinearLayout favoriteBack;

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
        favoriteBack = findViewById(R.id.favorite_back);
        recyclerView = findViewById(R.id.favorite_rv);
        favoriteAdapter = new QuestionAdapter(FavoriteActivity.this, MainActivity.TYPE_FAVORITE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(favoriteAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
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
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    int position = data.getIntExtra("position", -1);
                    if (position != -1) {
                        Question question = favoriteAdapter.getQuestion(position,MainActivity.TYPE_FAVORITE);
                        question.setExciting(data.getBooleanExtra("isExciting", false));
                        question.setNaive(data.getBooleanExtra("isNaive", false));
                        question.setFavorite(data.getBooleanExtra("isFavorite", false));
                        question.setAnswerCount(data.getIntExtra("answerCount", 0));
                        question.setExciting(data.getIntExtra("excitingCount", 0));
                        question.setNaive(data.getIntExtra("naiveCount", 0));
                        favoriteAdapter.notifyItemChanged(position,-1);
                    }
                }
                break;
            default:
        }
    }


}
