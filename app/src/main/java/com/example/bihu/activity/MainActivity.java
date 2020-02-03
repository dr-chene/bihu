package com.example.bihu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.bihu.R;
import com.example.bihu.adapter.QuestionAdapter;
import com.example.bihu.utils.Data;
import com.example.bihu.utils.MyHelper;
import com.example.bihu.utils.Person;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    public static final int TYPE_QUESTION = 1;
    public static final int TYPE_ANSWER = 2;
    public static final int TYPE_LOAD_MORE = 3;
    public static final int TYPE_REFRESH = 4;
    public static final int TYPE_NAIVE = 5;
    public static final int TYPE_FAVORITE = 6;
    public static final int TYPE_MODIFY_AVATAR = 7;
    public static final int TYPE_TAKE_PHOTO = 8;
    public static final int TYPE_CHOOSE_PHOTO = 9;
    public static int questionCount = 20;
    public static int questionLeft;
    public static int vision = 1;
    public static Person person = new Person();
    public static int totalQuestionPage;
    public static int totalQuestionCount;
    public static int curQuestionPage;
    public static int questionPage = 0;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private QuestionAdapter questionAdapter;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private ConstraintLayout noLogin;
    private Boolean isLoading = false;
    private LinearLayoutManager linearLayoutManager;
    private DrawerLayout drawerLayout;

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
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
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

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        NavigationView navView = findViewById(R.id.nav_view);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        loadPerson();

        questionAdapter = new QuestionAdapter(this, MainActivity.TYPE_QUESTION);
        recyclerView.setAdapter(questionAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        navView.setCheckedItem(R.id.nav_home);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        Intent intent3 = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent3);
                        break;
                    case R.id.nav_login:
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_register:
                        Intent intent1 = new Intent(MainActivity.this, RegisterActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.nav_favorite:
                        if (person.getId() != -1) {
                            Intent intent2 = new Intent(MainActivity.this, FavoriteActivity.class);
                            startActivity(intent2);
                        } else {
                            Toast.makeText(MainActivity.this, "请先登录或注册", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
        View headView = navView.getHeaderView(0);
        ImageView avatar = headView.findViewById(R.id.avatar);
        TextView name = headView.findViewById(R.id.nav_header_main_username);
        if (MainActivity.person.getId() != -1) {
            //加载头像
            if (MainActivity.person.getAvatar().length() >= 10) {
                Glide.with(this)
                        .load(MainActivity.person.getAvatar())
                        .into(avatar);
            }
            name.setText(MainActivity.person.getUsername());
        }
    }


    private void setOnClickListener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (person.getId() != -1) {
                    Intent intent = new Intent(MainActivity.this, QuestionCommitActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "请先登录或注册", Toast.LENGTH_LONG).show();
                }
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Data.refreshQuestion(MainActivity.this);
                questionAdapter.refresh(MainActivity.TYPE_QUESTION);
                questionAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    private void loadPerson() {
        MyHelper.readPerson(this, person);
        if (person.getId() == -1) {
            swipeRefreshLayout.setVisibility(View.GONE);
            noLogin.setVisibility(View.VISIBLE);
        } else {
            noLogin.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }

}
