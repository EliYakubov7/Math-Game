package com.greystudio.maththinkers;

import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.ActivityInfo;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import java.util.Random;

import es.dmoral.toasty.Toasty;


public class LoadingScreen_QuickMath extends AppCompatActivity {
    private static final int loadingScreenTime = 4000;
    private TextView timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        TextView hintText = findViewById(R.id.tvHint);
        Random rd = new Random();
        switch (rd.nextInt(4)){
            case 0:
                hintText.setText(getString(R.string.hint_background_alert));
                break;
            case 1:
                hintText.setText(getString(R.string.hint_panic));
                break;
            case 2:
                hintText.setText(getString(R.string.start_over));
                break;
            case 3:
                hintText.setText(getString(R.string.check_with_friends));
                break;
        }
        timer = findViewById(R.id.tvTime);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), Game_QuickMath.class));
                finish();
            }
        },loadingScreenTime);
        new CountDownTimer(loadingScreenTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText(getString(R.string.loading_screen_timer, (int) millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {

            }
        }.start();

        boolean addition = sharedPref.getBoolean(SettingsActivity.KEY_ADDITION_QUICK_MATH_GAME, false);
        boolean subtraction = sharedPref.getBoolean(SettingsActivity.KEY_SUBTRACTION_QUICK_MATH_GAME, false);
        boolean multiplication = sharedPref.getBoolean(SettingsActivity.KEY_MULTIPLICATION_QUICK_MATH_GAME, false);
        boolean division = sharedPref.getBoolean(SettingsActivity.KEY_DIVISION_QUICK_MATH_GAME, false);
        boolean kids_mode = sharedPref.getBoolean(SettingsActivity.KEY_SWITCH_KIDS_MODE, false);
        if(!addition || !subtraction || !multiplication || !division || kids_mode) {
            Toasty.error(LoadingScreen_QuickMath.this, R.string.high_score_board_disabled, Toast.LENGTH_SHORT, true).show();

        }
    }
    @Override
    public void onBackPressed() {

    }
}