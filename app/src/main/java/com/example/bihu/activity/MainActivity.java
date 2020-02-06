package com.example.bihu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.bihu.R;
import com.example.bihu.adapter.QuestionAdapter;
import com.example.bihu.utils.Http;
import com.example.bihu.utils.HttpCallbackListener;
import com.example.bihu.utils.MySQLiteOpenHelper;
import com.example.bihu.utils.Person;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.bihu.utils.Methods.getQuestionPage;

public class MainActivity extends AppCompatActivity {

    public static final int TYPE_QUESTION = 1;
    public static final int TYPE_ANSWER = 2;
    public static final int TYPE_LOAD_MORE = 3;
    public static final int TYPE_REFRESH = 4;

    public static final int TYPE_FAVORITE = 6;

    public static final int TYPE_TAKE_PHOTO = 8;
    public static final int TYPE_CHOOSE_PHOTO = 9;
    public static final int count = 20;
    public static int vision = 1;
    public static Person person;
    public static int totalQuestionPage = 0;
    public static int questionPage = 0;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private QuestionAdapter questionAdapter;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private ConstraintLayout noLogin;
    private LinearLayoutManager linearLayoutManager;
    private DrawerLayout drawerLayout;
    private ImageView avatar;
    private TextView name;
    private FloatingActionButton fabUp;
    //处理刷新事件结果
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MainActivity.TYPE_REFRESH:
                    questionAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setOnClickListener();
    }

    /**
     * 处理登录成功跳转之后的主界面加载事件
     */
    @Override
    protected void onRestart() {
        //读取用户数据
        loadPerson();
        if (MainActivity.person.getId() != -1) {
            //加载头像
            if (MainActivity.person.getAvatar().length() >= 10) {
                Glide.with(this)
                        .load(MainActivity.person.getAvatar())
                        .error(R.drawable.error_avatar)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(avatar);
            }
            name.setText(MainActivity.person.getUsername());
        }
        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * 设置menu点击事件
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_settings:
                if (person.getId() != -1) {
                    Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(intent);
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

    /**
     * 加载视图，绑定数据
     */
    private void initView() {
        person = new Person();
        person.setId(-1);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fabUp = findViewById(R.id.fab_up);
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
//读取用户数据
        loadPerson();

        questionAdapter = new QuestionAdapter(MainActivity.this, MainActivity.TYPE_QUESTION);
        recyclerView.setAdapter(questionAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        navView.setCheckedItem(R.id.nav_home);
        //设置nav点击事件
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
        avatar = headView.findViewById(R.id.avatar);
        name = headView.findViewById(R.id.nav_header_main_username);
        if (MainActivity.person.getId() != -1) {
            //加载头像
            if (MainActivity.person.getAvatar().length() >= 10) {
                Glide.with(this)
                        .load(MainActivity.person.getAvatar())
                        .error(R.drawable.error_avatar)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(avatar);
            }
            name.setText(MainActivity.person.getUsername());
        }
    }

    /**
     * 设置点击事件
     */
    private void setOnClickListener() {
        //发布问题
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
        //滑到顶部并刷新
        fabUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.scrollToPosition(0);
                LinearLayoutManager mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                mLayoutManager.scrollToPositionWithOffset(0, 0);
                swipeRefreshLayout.setRefreshing(true);
                refresh();
            }
        });
        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        //上拉加载
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            //判断是不是往上拖动
            public boolean isLastReflash;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && isLastReflash) {
                    if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange()) {
                        questionAdapter.loadMoreData();
                        questionAdapter.notifyDataSetChanged();
                    }
                }
            }

            //根据dy，dx可以判断是往哪个方向滑动
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                isLastReflash = dy > 0;
            }
        });
    }

    /**
     * 刷新
     */
    private void refresh() {
        if (totalQuestionPage == 0 || questionPage < totalQuestionPage - 1) {
            Map<String, String> query = new HashMap<>();
            query.put("page", "" + questionPage);
            query.put("count", "" + count);
            query.put("token", MainActivity.person.getToken());
            Http.sendHttpRequest(Http.URL_GET_QUESTION_LIST, query, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        switch (jsonObject.getInt("status")) {
                            case 401:
                                Looper.prepare();
                                Toast.makeText(MainActivity.this, jsonObject.getInt("status") + " : " + "登录失效，请重新登录", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                                break;
                            case 400:
                            case 500:
                                Looper.prepare();
                                Toast.makeText(MainActivity.this, jsonObject.getInt("status") + " : " + jsonObject.getString("info"), Toast.LENGTH_SHORT).show();
                                Looper.loop();
                                break;
                            case 200:
                                Log.d("first", "Http");
                                JSONObject object = jsonObject.getJSONObject("data");
                                MainActivity.totalQuestionPage = object.getInt("totalPage");
                                JSONArray jsonArray = object.getJSONArray("questions");
                                JSONObject questionData;
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    questionData = jsonArray.getJSONObject(i);
                                    MySQLiteOpenHelper.addQuestion(questionData.getInt("id"), questionData.getString("title"), questionData.getString("content"), questionData.getString("images"), questionData.getString("date"), questionData.getInt("exciting")
                                            , questionData.getInt("naive"), questionData.getString("recent"), questionData.getInt("answerCount"), questionData.getInt("authorId"), questionData.getString("authorName"), questionData.getString("authorAvatar"),
                                            questionData.getBoolean("is_exciting") == true ? 1 : 0, questionData.getBoolean("is_naive") == true ? 1 : 0,
                                            questionData.getBoolean("is_favorite") == true ? 1 : 0);
                                }
                                questionPage = getQuestionPage();
                                if (questionPage < totalQuestionPage - 1) {
                                    questionPage++;
                                }
                                questionAdapter.refresh(MainActivity.TYPE_QUESTION);
                                Message msg = new Message();
                                msg.what = TYPE_REFRESH;
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
            Toast.makeText(MainActivity.this, "暂无最新问题", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * 根据是否登录加载主页面
     */
    private void loadPerson() {
        MySQLiteOpenHelper.readPerson(person);
        if (person.getId() == -1) {
            swipeRefreshLayout.setVisibility(View.GONE);
            noLogin.setVisibility(View.VISIBLE);
            fabUp.setVisibility(View.GONE);
        } else {
            noLogin.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            fabUp.setVisibility(View.VISIBLE);
        }
    }

}
