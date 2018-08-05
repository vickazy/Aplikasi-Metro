package com.jojo.metroapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import static com.jojo.metroapp.config.config.DELAY_SPLASHSCREEN;

public class SplashscreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Thread.sleep(DELAY_SPLASHSCREEN);
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        // block user interruption
    }
}
