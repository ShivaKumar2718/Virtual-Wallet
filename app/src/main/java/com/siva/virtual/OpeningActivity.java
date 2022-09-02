package com.siva.virtual;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.siva.virtual.util.Utils;

public class OpeningActivity extends AppCompatActivity {

    private Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        AppCompatImageView iv = findViewById(R.id.iv);

        SharedPreferences sp = getSharedPreferences(Utils.FIRST_LOGIN_DETAILS,MODE_PRIVATE);
        //checking whether user logging in for the first time or not
        if(sp.getBoolean(Utils.IS_FIRST_LOGIN,true)){
            i = new Intent(OpeningActivity.this,IntroActivity.class);
        }
        else
            i= new Intent(OpeningActivity.this,LoginScreen.class);

        Animation anim = AnimationUtils.loadAnimation(this,R.anim.scale_out);
        iv.startAnimation(anim);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(i);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}