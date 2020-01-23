package com.example.bihu;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

import com.example.bihu.tool.MyHelper;
import com.example.bihu.tool.Person;
import com.example.bihu.tool.Question;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final int TYPE_QUESTION =1;
    public static final int TYPE_ANSWER = 2;
    public static final int TYPE_ACCEPT = 3;
    public static final int TYPE_EXCITING = 4;
    public static final int TYPE_NAIVE = 5;
    public static final int TYPE_FAVORITE = 6;
    public static int vision = 1;
    public static Person person = new Person();
    private int page = 0;
    private int count = 10;
    private Question question = new Question();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private QuestionAdapter questionAdapter;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private ConstraintLayout noLogin;
    private MyHelper myHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setOnClickListener();
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
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
                if (person != null) {
                    Intent intent2 = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(intent2);
                } else Toast.makeText(MainActivity.this, "请先登录", Toast.LENGTH_LONG).show();
                break;
            case R.id.action_login:
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.action_register:
                Intent intent1 = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent1);
                break;
        }
        return true;
    }

    private void initView() {
        person.setId(-1);
        myHelper = new MyHelper(MainActivity.this, vision);
        db = myHelper.getWritableDatabase();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.fab);
        noLogin = findViewById(R.id.no_login);

        swipeRefreshLayout = findViewById(R.id.srl);
        recyclerView = findViewById(R.id.main_rv);
        loadData();
        questionAdapter = new QuestionAdapter(this);
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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Map<String, String> query = new HashMap<>();
                query.put("page", "" + page);
                query.put("count", "" + count);
                query.put("token", person.getToken());
                sendPost(UrlPost.URL_GETQUESTIONLIST, query);
                questionAdapter.refresh();
                questionAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void sendPost(final String urlParam, Map<String, String> params) {
        final StringBuffer sbParams = new StringBuffer();
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, String> e : params.entrySet()) {
                sbParams.append(e.getKey());
                sbParams.append("=");
                sbParams.append(e.getValue());
                sbParams.append("&");
            }
        }
        sbParams.deleteCharAt(sbParams.length() - 1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                URL url = null;
                try {
                    url = new URL(urlParam);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes(sbParams.toString());
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuffer response = new StringBuffer();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    json(response.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    private void json(final String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    switch (jsonObject.getInt("status")) {
                        case 400:
                            Toast.makeText(MainActivity.this, "参数错误", Toast.LENGTH_SHORT).show();
                            break;
                        case 401:
                            Toast.makeText(MainActivity.this, "用户认证错误", Toast.LENGTH_SHORT).show();
                            break;
                        case 500:
                            Toast.makeText(MainActivity.this, "奇怪的错误", Toast.LENGTH_SHORT).show();
                            break;
                        case 200:
                            JSONObject object = jsonObject.getJSONObject("data");
                            int totalCount = object.getInt("totalCount");
                            int totalPage = object.getInt("totalPage");
                            int curPage = object.getInt("curPage");
                            if (!jsonObject.getString("info").equals("success"))
                                Toast.makeText(MainActivity.this, "登录失效，请重新登录", Toast.LENGTH_LONG).show();
                            else {
                                Cursor cursor = db.rawQuery("select * from question", null);
                                if (totalCount != cursor.getCount()) {
                                    JSONArray jsonArray = object.getJSONArray("questions");
                                    JSONObject questionData = null;
//                                Log.d("MainActivity",jsonArray.toString());
                                    for (int i = 0; i < jsonArray.length(); i++) {
//                                    Log.d("MainActivity","jsonArray "+ i);
                                        questionData = jsonArray.getJSONObject(i);
//                                    Log.d("MainActivity",questionData.toString());
                                        myHelper.addQuestion(db, questionData.getInt("id"), questionData.getString("title"), questionData.getString("content"), questionData.getString("images"), questionData.getString("date"), questionData.getInt("exciting")
                                                , questionData.getInt("naive"), questionData.getString("recent"), questionData.getInt("answerCount"), questionData.getInt("authorId"), questionData.getString("authorName"), questionData.getString("authorAvatar"),
                                                questionData.getBoolean("is_exciting") == true ? 1 : 0, questionData.getBoolean("is_naive") == true ? 1 : 0,
                                                questionData.getBoolean("is_favorite") == true ? 1 : 0);
                                    }
                                }
                            }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadData() {
        personLoad();
        if (person.getId() == -1) {
            swipeRefreshLayout.setVisibility(View.GONE);
            noLogin.setVisibility(View.VISIBLE);
        } else {
//            Toast.makeText(this,"person is not null",Toast.LENGTH_LONG).show();
//            Log.d("MainActivity",person.getToken());
            noLogin.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }

    private void personLoad() {
        myHelper.readPerson(db, person);
    }

}
