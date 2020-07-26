package com.greystudio.maththinkers;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_splash_screen);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Thread splash_screen = new Thread() {
            public void run() {

                try {
                    sleep(1500);

                } catch(Exception e) {
                    e.printStackTrace();

                } finally {
                    Intent main_menu = new Intent(SplashScreen.this,MainMenu.class);
                    startActivity(main_menu);
                }
            }
        };
        splash_screen.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
