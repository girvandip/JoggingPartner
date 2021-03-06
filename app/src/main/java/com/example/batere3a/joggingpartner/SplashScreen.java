package com.example.batere3a.joggingpartner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class SplashScreen extends AppCompatActivity {

    // Splash Screen Time Setter
    private static final int SPLASH_TIME_OUT = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);
        */

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        //ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();

        setContentView(R.layout.activity_splash_screen);

        // Calling Splash Screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent splashScreenIntent = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(splashScreenIntent);
                finish();
            }
        },SPLASH_TIME_OUT);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("SplashScreenTag", "ASDFASDF");
    }
}
