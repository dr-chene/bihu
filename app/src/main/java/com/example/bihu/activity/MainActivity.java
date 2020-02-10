package com.example.bihu.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.bihu.utils.MyToast;
import com.example.bihu.utils.Person;
import com.example.bihu.utils.Question;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.bihu.activity.SplashActivity.person;
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
    public static int totalQuestionPage = 0;
    public static int questionPage = 0;
    public int totalCount = 0;
    private Thread thread;
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
    private NavigationView navView;
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
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Fade());
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
                    Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
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
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fabUp = findViewById(R.id.fab_up);
        fab = findViewById(R.id.fab);
        noLogin = findViewById(R.id.no_login);
        swipeRefreshLayout = findViewById(R.id.srl);
        recyclerView = findViewById(R.id.main_rv);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        navView = findViewById(R.id.nav_view);
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
                    case R.id.nav_favorite:
                            Intent intent2 = new Intent(MainActivity.this, FavoriteActivity.class);
                            startActivity(intent2);
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    @Override
    protected void onRestart() {
        navView.setCheckedItem(R.id.nav_home);
        super.onRestart();
    }

    /**
     * 设置点击事件
     */
    private void setOnClickListener() {
        //发布问题
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, QuestionCommitActivity.class);
                    startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
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
                refreshSome();
            }
        });
        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshSome();
            }
        });
        //上拉加载
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            //判断是不是往上拖动
            public boolean isLastRefresh;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && isLastRefresh) {
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
                isLastRefresh = dy > 0;
            }
        });
    }

    /**
     * 刷新
     */
    private void refreshSome() {
        if (totalQuestionPage == 0 || questionPage < totalQuestionPage - 1) {
            Map<String, String> query = new HashMap<>();
            query.put("page", questionPage + "");
            query.put("count", count + "");
            query.put("token", SplashActivity.person.getToken());
            final int questionCount = MySQLiteOpenHelper.getQuestionCount();
            Http.sendHttpRequest(Http.URL_GET_QUESTION_LIST, query, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        switch (jsonObject.getInt("status")) {
                            case 401:
                                Looper.prepare();
                                MyToast.showToast(jsonObject.getInt("status") + " : " + "登录失效，请重新登录");
                                swipeRefreshLayout.setRefreshing(false);
                                Looper.loop();
                                break;
                            case 400:
                            case 500:
                                Looper.prepare();
                                MyToast.showToast(jsonObject.getInt("status") + " : " + jsonObject.getString("info"));
                                swipeRefreshLayout.setRefreshing(false);
                                Looper.loop();
                                break;
                            case 200:
                                JSONObject object = jsonObject.getJSONObject("data");
                                totalCount = object.getInt("totalCount");
                                if (questionCount != totalCount) {
                                    totalQuestionPage = object.getInt("totalPage");
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
                                } else {
                                    Looper.prepare();
                                    MyToast.showToast("暂无最新问题");
                                    swipeRefreshLayout.setRefreshing(false);
                                    Looper.loop();
                                }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Exception e) {

                }

                @Override
                public void onNetworkError() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
            refreshAll();
        }
    }

    private void refreshAll() {
        if (thread != null) {
            thread.interrupt();
        }
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> query = new HashMap<>();
                query.put("page", "0");
                query.put("count", MySQLiteOpenHelper.getQuestionCount() + "");
                query.put("token", SplashActivity.person.getToken());
                Http.sendHttpRequest(Http.URL_GET_QUESTION_LIST, query, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getInt("status") == 200) {
                                JSONObject object = jsonObject.getJSONObject("data");
                                JSONArray jsonArray = object.getJSONArray("questions");
                                JSONObject questionData;
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    questionData = jsonArray.getJSONObject(i);
                                    MySQLiteOpenHelper.addQuestion(questionData.getInt("id"), questionData.getString("title"), questionData.getString("content"), questionData.getString("images"), questionData.getString("date"), questionData.getInt("exciting")
                                            , questionData.getInt("naive"), questionData.getString("recent"), questionData.getInt("answerCount"), questionData.getInt("authorId"), questionData.getString("authorName"), questionData.getString("authorAvatar"),
                                            questionData.getBoolean("is_exciting") == true ? 1 : 0, questionData.getBoolean("is_naive") == true ? 1 : 0,
                                            questionData.getBoolean("is_favorite") == true ? 1 : 0);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {

                    }

                    @Override
                    public void onNetworkError() {

                    }
                });
            }
        });
        thread.start();
    }

    /**
     * 根据是否登录加载主页面
     */
    private void loadPerson() {
        View headView = navView.getHeaderView(0);
        avatar = headView.findViewById(R.id.avatar);
        name = headView.findViewById(R.id.nav_header_main_username);
            noLogin.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            fabUp.setVisibility(View.VISIBLE);
            //加载头像
            if (SplashActivity.person.getAvatar().length() >= 5) {
                Glide.with(this)
                        .load(SplashActivity.person.getAvatar())
                        .error(R.drawable.error_avatar)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(avatar);
            }
            name.setText(SplashActivity.person.getUsername());
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    int position = data.getIntExtra("position", -1);
                    if (position != -1) {
                        Question question = questionAdapter.getQuestion(position);
                        question.setExciting(data.getBooleanExtra("isExciting", false));
                        question.setNaive(data.getBooleanExtra("isNaive", false));
                        question.setFavorite(data.getBooleanExtra("isFavorite", false));
                        question.setAnswerCount(data.getIntExtra("answerCount", 0));
                        question.setExciting(data.getIntExtra("excitingCount", 0));
                        question.setNaive(data.getIntExtra("naiveCount", 0));
                        questionAdapter.notifyItemChanged(position,-1);
                    }
                }
                break;
            default:
        }
    }
}
