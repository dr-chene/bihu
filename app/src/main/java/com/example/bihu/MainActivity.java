package com.example.bihu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bihu.tool.Data;
import com.example.bihu.tool.MyHelper;
import com.example.bihu.tool.Person;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    public static final int TYPE_QUESTION = 1;
    public static final int TYPE_ANSWER = 2;
    public static final int TYPE_ACCEPT = 3;
    public static final int TYPE_EXCITING = 4;
    public static final int TYPE_NAIVE = 5;
    public static final int TYPE_FAVORITE = 6;
    public static final int TYPE_MODIFY_AVATAR = 7;
    public static final int TYPE_TAKE_PHOTO = 8;
    public static final int TYPE_CHOOSE_PHOTO = 9;
    public static int vision = 1;
    public static Person person = new Person();
    private int page = 0;
    private int count = 46;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private QuestionAdapter questionAdapter;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private ConstraintLayout noLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setOnClickListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_settings:
                if (person.getId() != -1) {
                    Intent intent2 = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(intent2);
                } else {
                    Toast.makeText(MainActivity.this, "请先登录或注册", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_login:
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.action_register:
                Intent intent1 = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent1);
                break;
            case R.id.action_favoriteList:
                if (person.getId() != -1) {
                    Intent intent2 = new Intent(MainActivity.this, FavoriteActivity.class);
                    startActivity(intent2);
                } else {
                    Toast.makeText(MainActivity.this, "请先登录或注册", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return true;
    }

    private void initView() {
        person.setId(-1);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.fab);
        noLogin = findViewById(R.id.no_login);
        swipeRefreshLayout = findViewById(R.id.srl);
        recyclerView = findViewById(R.id.main_rv);
        loadData();
        questionAdapter = new QuestionAdapter(this, MainActivity.TYPE_QUESTION);
        recyclerView.setAdapter(questionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, new LinearLayoutManager(this).getOrientation());
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divide_style));
        recyclerView.addItemDecoration(dividerItemDecoration);

    }


    private void setOnClickListener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (person.getId() != -1) {
                    Intent intent = new Intent(MainActivity.this, EnterQuestionActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "请先登录或注册", Toast.LENGTH_LONG).show();
                }
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Data.refreshQuestion(MainActivity.this, page, count);
                questionAdapter.refresh(MainActivity.TYPE_QUESTION);
                questionAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadData() {
        personLoad();
        if (person.getId() == -1) {
            swipeRefreshLayout.setVisibility(View.GONE);
            noLogin.setVisibility(View.VISIBLE);
        } else {
            noLogin.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }

    private void personLoad() {
        MyHelper.readPerson(this, person);
        Log.d("test", "avatar == null is " + (person.getAvatar() == null));
    }
}
