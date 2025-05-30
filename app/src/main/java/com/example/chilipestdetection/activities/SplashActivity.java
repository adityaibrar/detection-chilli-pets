package com.example.chilipestdetection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chilipestdetection.R;
import com.example.chilipestdetection.utils.SharedPreferencesManager;

public class SplashActivity extends AppCompatActivity {
    private SharedPreferencesManager prefManager;
    private static final int SPLASH_DELAY = 2000; // 2 detik

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        prefManager = new SharedPreferencesManager(this);

        new Handler().postDelayed(this::checkLoginStatus, SPLASH_DELAY);
    }

    private void checkLoginStatus() {
        if (prefManager.isLoggedIn()) {
            String typeUser = prefManager.getTypeUser();
            Intent intent;
            if ("admin".equals(typeUser)) {
                intent = new Intent(SplashActivity.this, SecondaryActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            }
            startActivity(intent);
        } else {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        finish();
    }
}