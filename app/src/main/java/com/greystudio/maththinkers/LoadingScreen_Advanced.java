package com.greystudio.maththinkers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;
import es.dmoral.toasty.Toasty;

public class LoadingScreen_Advanced extends AppCompatActivity {
    private static final int loadingScreenTime = 4000;
    private TextView timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean addition,subtraction,addition_multiplication,
                subtraction_multiplication,addition_division,subtraction_division,kids_mode;


        TextView hintText = findViewById(R.id.tvHint);
        Random rd = new Random();
        switch (rd.nextInt(4)){
            case 0:
                hintText.setText(getString(R.string.hint_not_enemy));
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
        timer = findViewById(R.id.tvTime);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), Game_Advanced.class));
                finish();
            }
        },loadingScreenTime);
        new CountDownTimer(loadingScreenTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText(getString(R.string.loading_screen_timer, (int) millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {

            }
        }.start();

        addition = sharedPref.getBoolean(SettingsActivity.KEY_ADVANCED_ADDITION, false);
        subtraction = sharedPref.getBoolean(SettingsActivity.KEY_ADVANCED_SUBTRACTION,false);
        addition_multiplication = sharedPref.getBoolean(SettingsActivity.KEY_ADVANCED_MULTI_ADD,false);
        addition_division = sharedPref.getBoolean(SettingsActivity.KEY_ADVANCED_DIV_ADD,false);
        subtraction_multiplication = sharedPref.getBoolean(SettingsActivity.KEY_ADVANCED_MULTI_SUB,false);
        subtraction_division = sharedPref.getBoolean(SettingsActivity.KEY_ADVANCED_DIV_SUB,false);
        kids_mode = sharedPref.getBoolean(SettingsActivity.KEY_SWITCH_KIDS_MODE,false);
        if(!addition || !subtraction || ! addition_division || !addition_multiplication || ! subtraction_division || ! subtraction_multiplication || kids_mode) {
            Toasty.error(LoadingScreen_Advanced.this, R.string.high_score_board_disabled, Toast.LENGTH_SHORT, true).show();

        }
    }
    @Override
    public void onBackPressed()
    {

    }
}
