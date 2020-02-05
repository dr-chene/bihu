package com.example.bihu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bihu.R;
import com.example.bihu.adapter.QuestionAdapter;

public class FavoriteActivity extends AppCompatActivity {

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
                Intent intent = new Intent(FavoriteActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


}
