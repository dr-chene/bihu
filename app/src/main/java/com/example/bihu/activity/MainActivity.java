package com.example.bihu.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.transition.Fade;
import android.view.KeyEvent;
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
import com.example.bihu.utils.ActivityCollector;
import com.example.bihu.utils.Http;
import com.example.bihu.utils.HttpCallbackListener;
import com.example.bihu.utils.MySQLiteOpenHelper;
import com.example.bihu.utils.MyToast;
import com.example.bihu.utils.Person;
import com.example.bihu.utils.Question;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.bihu.utils.Methods.getQuestionPage;

public class MainActivity extends BaseActivity {
    public static final int TYPE_QUESTION = 1;
    public static final int TYPE_ANSWER = 2;
    public static final int TYPE_LOAD_MORE = 3;
    public static final int TYPE_REFRESH = 4;
    public static final int TYPE_MINE = 5;
    public static final int TYPE_FAVORITE = 6;
    public static final int TYPE_TAKE_PHOTO = 8;
    public static final int TYPE_CHOOSE_PHOTO = 9;
    public static final int count = 20;
    public static Person person;
    public static int vision = 1;
    private int totalQuestionPage = 0;
    private int questionPage = 0;
    private int totalCount = 0;
    private Thread thread;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private QuestionAdapter questionAdapter;
    private FloatingActionButton fab;
    private ConstraintLayout noLogin;
    private LinearLayoutManager linearLayoutManager;
    private DrawerLayout drawerLayout;
    private FloatingActionButton fabUp;
    private NavigationView navView;
    private Toast toast;
    private List<Question> questions;
    //处理刷新事件结果
    private Handler handler = new Handler();

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
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
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
        Toolbar toolbar;
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
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadMoreData();
                    }
                }, 1000);
            }
        });
        questionAdapter = new QuestionAdapter(MainActivity.this, MainActivity.TYPE_QUESTION);
        questions = questionAdapter.getQuestionList();
        recyclerView.setAdapter(questionAdapter);
        linearLayoutManager = new LinearLayoutManager(MainActivity.this);
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
                    case R.id.nav_mine:
                        Intent intent = new Intent(MainActivity.this, MineActivity.class);
                        startActivity(intent);
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
        loadPerson();
        refreshAvatar();
        super.onRestart();
    }

    private void refreshAvatar() {
        String name = person.getUsername();
        String avatar = person.getAvatar();
        for (Question question : questions
        ) {
            if (question.getAuthorName().equals(name)) {
                question.setAuthorAvatar(avatar);
            }
        }
        questionAdapter.notifyDataSetChanged();
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
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
            }
        });
        //滑到顶部
        fabUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.scrollToPosition(0);
                LinearLayoutManager mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                assert mLayoutManager != null;
                mLayoutManager.scrollToPositionWithOffset(0, 0);
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
            boolean isLoading;

            @Override
            public void onScrollStateChanged(@NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            //根据dy，dx可以判断是往哪个方向滑动
            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                    if (lastVisibleItemPosition + 1 == questionAdapter.getItemCount()) {
                        if (!isLoading) {
                            isLoading = true;
                            View view = linearLayoutManager.findViewByPosition(questionAdapter.getItemCount() - 1);
                            assert view != null;
                            view.findViewById(R.id.load_bar).setVisibility(View.VISIBLE);
                            ((TextView) view.findViewById(R.id.load_text)).setText("正在加载");
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    loadMoreData();
                                    isLoading = false;
                                }
                            }, 1000);
                        }
                    }
                }
            }
        });
    }

    /**
     * 刷新
     */
    private void refreshSome() {
        if (totalQuestionPage == 0 || questionPage < totalQuestionPage) {
            Map<String, String> query = new HashMap<>();
            query.put("page", questionPage + "");
            query.put("count", count + "");
            query.put("token", MainActivity.person.getToken());
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
                                                questionData.getBoolean("is_exciting") ? 1 : 0, questionData.getBoolean("is_naive") ? 1 : 0,
                                                questionData.getBoolean("is_favorite") ? 1 : 0);
                                    }
                                    questionPage = getQuestionPage();
                                    if (questionPage < totalQuestionPage - 1) {
                                        questionPage++;
                                    }
                                    questionAdapter.refresh(MainActivity.TYPE_QUESTION);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            questionAdapter.notifyDataSetChanged();
                                            swipeRefreshLayout.setRefreshing(false);
                                        }
                                    });
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
                query.put("token", MainActivity.person.getToken());
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
                                            questionData.getBoolean("is_exciting") ? 1 : 0, questionData.getBoolean("is_naive") ? 1 : 0,
                                            questionData.getBoolean("is_favorite") ? 1 : 0);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
        ImageView avatar;
        TextView name;
        MySQLiteOpenHelper.readPerson(person);
        View headView = navView.getHeaderView(0);
        avatar = headView.findViewById(R.id.avatar);
        name = headView.findViewById(R.id.nav_header_main_username);
        noLogin.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        fabUp.setVisibility(View.VISIBLE);
        //加载头像
        if (MainActivity.person.getAvatar().length() >= 5) {
            Glide.with(this)
                    .load(MainActivity.person.getAvatar())
                    .error(R.drawable.error_avatar)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(avatar);
        }
        name.setText(MainActivity.person.getUsername());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                int position = data.getIntExtra("position", -1);
                if (position != -1) {
                    Question question = questionAdapter.getQuestion(position, TYPE_QUESTION);
                    question.setExciting(data.getBooleanExtra("isExciting", false));
                    question.setNaive(data.getBooleanExtra("isNaive", false));
                    question.setFavorite(data.getBooleanExtra("isFavorite", false));
                    question.setAnswerCount(data.getIntExtra("answerCount", 0));
                    question.setExciting(data.getIntExtra("excitingCount", 0));
                    question.setNaive(data.getIntExtra("naiveCount", 0));
                    questionAdapter.notifyItemChanged(position, -1);
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (toast == null) {
            toast = Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            ActivityCollector.finishAll();
            return super.onKeyDown(keyCode, event);
        }
        return false;
    }

    private void loadMoreData() {
        questionAdapter.curSize += 20;
        int preSize = questions.size();
        MySQLiteOpenHelper.readQuestion(questions, questionAdapter.curSize, MainActivity.TYPE_LOAD_MORE);
        if (preSize != questions.size()) {
            questionAdapter.notifyItemInserted(questionAdapter.getItemCount());
        } else {
            View view = linearLayoutManager.findViewByPosition(questionAdapter.getItemCount() - 1);
            assert view != null;
            view.findViewById(R.id.load_bar).setVisibility(View.GONE);
            ((TextView) view.findViewById(R.id.load_text)).setText(".....没有更多数据.....");
        }
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
