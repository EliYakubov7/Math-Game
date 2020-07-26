package com.greystudio.maththinkers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import es.dmoral.toasty.Toasty;


public class MainMenu extends AppCompatActivity
{
    private MediaPlayer mediaPlayer;
    private Boolean mute;
    private SharedPreferences sharedPref;
    private ImageView muteButton;
    private int length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ImageView animation = findViewById(R.id.mathAnim);
        AnimationDrawable animationDrawable = (AnimationDrawable)animation.getDrawable();
        animationDrawable.start();

        Button quickMathButton, timeTrialsButton, advancedMathButton;
        Animation fromLeftQuickMath, fromLeftTimeTrials, fromLeftAdvanced, fromLeftHighScores;
        quickMathButton = findViewById(R.id.btnQuickMath);
        timeTrialsButton = findViewById(R.id.btntimeTrials);
        advancedMathButton = findViewById(R.id.btnadvancedMath);
        Button highScoreButton = findViewById(R.id.btnHighScores);
        muteButton = findViewById(R.id.ivMute);

        //Animation for the game mode buttons
        fromLeftQuickMath = AnimationUtils.loadAnimation(this,R.anim.main_menu_quick_math);
        quickMathButton.setAnimation(fromLeftQuickMath);
        fromLeftTimeTrials = AnimationUtils.loadAnimation(this,R.anim.main_menu_time_trials);
        timeTrialsButton.setAnimation(fromLeftTimeTrials);
        fromLeftAdvanced = AnimationUtils.loadAnimation(this, R.anim.main_menu_advanced);
        advancedMathButton.setAnimation(fromLeftAdvanced);
        fromLeftHighScores = AnimationUtils.loadAnimation(this, R.anim.main_menu_highscores);
        highScoreButton.setAnimation(fromLeftHighScores);

        PreferenceManager.setDefaultValues(this,R.xml.preference,true);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mute = sharedPref.getBoolean(SettingsActivity.KEY_SWITCH_MUSIC,false);

        //Initializes music
        mediaPlayer = MediaPlayer.create(this,R.raw.audio_main_menu);
        mediaPlayer.setLooping(true);
        if(!mute) {
            mediaPlayer.start();
            muteButton.setImageResource(R.drawable.ic_volume_up_black_24dp);
        }
        else {
            muteButton.setImageResource(R.drawable.ic_volume_off_black_24dp);
            }

        muteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                    sharedPref.edit().putBoolean(SettingsActivity.KEY_SWITCH_MUSIC,false).apply();
                    muteButton.setImageResource(R.drawable.ic_volume_up_black_24dp);
                }
                else
                {
                    mediaPlayer.pause();
                    sharedPref.edit().putBoolean(SettingsActivity.KEY_SWITCH_MUSIC,true).apply();
                    muteButton.setImageResource(R.drawable.ic_volume_off_black_24dp);
                }
            }
        });

        isFirst();
    }

    protected void onPause() {
        super.onPause();
        if(mediaPlayer.isPlaying() && !mute){
            mediaPlayer.pause();
            length = mediaPlayer.getCurrentPosition();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(!mediaPlayer.isPlaying() && !mute) {
            mediaPlayer.seekTo(length);
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    public void openAppInfo(View View){
        startActivity(new Intent(this,AppInfo.class));
    }

    public void openQuickMaths(View view) {
        startActivity(new Intent(this, LoadingScreen_QuickMath.class));
        finish();
    }

    public void openTimeTrials(View view){
        startActivity(new Intent(this, LoadingScreen_TimeTrials.class));
        finish();
    }

    public void openAdvanced(View view){
        startActivity(new Intent(this, LoadingScreen_Advanced.class));
        finish();
    }

    public void openHighScore(View view){
        startActivity(new Intent(this, HighScore.class));
    }

    public void openSettings(View view)
    {
        startActivity(new Intent(this,SettingsActivity.class));
    }

    public void resetInst(View view) {
        String[] prefsArr = {"material_showcaseview_prefs", "RunBefore_Advanced", "RunBefore_QuickMath", "RunBefore_TimeTrails"};
        for (String pref : prefsArr) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(pref, false);
            editor.apply();
        }
        Toasty.success(MainMenu.this, R.string.instructions_enabled, Toast.LENGTH_LONG, true).show();
        TextView ibReset = findViewById(R.id.tvResetInst);
        ibReset.setVisibility(View.GONE);
    }

    private void isFirst() {
        String[] prefsArr = {"material_showcaseview_prefs", "RunBefore_Advanced", "RunBefore_QuickMath", "RunBefore_TimeTrails"};
        for (String pref : prefsArr) {
            boolean runBefore = sharedPref.getBoolean(pref, false);
            if (runBefore) {
                TextView ibReset = findViewById(R.id.tvResetInst);
                ibReset.setPaintFlags(ibReset.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                ibReset.setVisibility(View.VISIBLE);
                break;
            }
        }
    }
}