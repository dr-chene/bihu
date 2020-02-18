package com.example.bihu.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Fade;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.bihu.R;
import com.example.bihu.utils.MySQLiteOpenHelper;
import com.example.bihu.utils.Person;

import static com.example.bihu.activity.MainActivity.person;

public class SplashActivity extends BaseActivity {


    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setExitTransition(new Fade());
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);
        person = new Person();
        person.setId(-1);
        MySQLiteOpenHelper.readPerson(person);
        img = findViewById(R.id.logo);
        Animation animation = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.alpha);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (person.getId() == -1) {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, img, "share_logo");
                    startActivity(intent, options.toBundle());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);
                } else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class), ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this).toBundle());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        img.startAnimation(animation);
    }
}
