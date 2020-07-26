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


public class LoadingScreen_TimeTrials extends AppCompatActivity {
    private static final int loadingScreenTime = 4000;

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
                hintText.setText(getString(R.string.hint_time_resets));
                break;
            case 1:
                hintText.setText(getString(R.string.hint_improve));
                break;
            case 2:
                hintText.setText(getString(R.string.hint_difficulty));
                break;
            case 3:
                hintText.setText(getString(R.string.check_with_friends));
                break;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), Game_TimeTrials.class));
                finish();
            }
        },loadingScreenTime);
        new CountDownTimer(loadingScreenTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                TextView timer = findViewById(R.id.tvTime);
                timer.setText(getString(R.string.loading_screen_timer, (int) millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {

            }
        }.start();
        boolean addition = sharedPref.getBoolean(SettingsActivity.KEY_TIME_TRIALS_ADDITION, false);
        boolean subtraction = sharedPref.getBoolean(SettingsActivity.KEY_TIME_TRIALS_SUBTRACTION, false);
        boolean multiplication = sharedPref.getBoolean(SettingsActivity.KEY_TIME_TRIALS_MULTIPLICATION, false);
        boolean division = sharedPref.getBoolean(SettingsActivity.KEY_TIME_TRIALS_DIVISION, false);
        boolean timer_bool = sharedPref.getBoolean(SettingsActivity.KEY_SWITCH_TIME_TRIALS_TIMER,false);
        boolean kids_mode = sharedPref.getBoolean(SettingsActivity.KEY_SWITCH_KIDS_MODE, false);
        if(!addition || !subtraction || !multiplication || !division || !timer_bool || kids_mode) {
            Toasty.error(LoadingScreen_TimeTrials.this, R.string.high_score_board_disabled, Toast.LENGTH_SHORT, true).show();
        }
    }
    @Override
    public void onBackPressed() {

    }
}
