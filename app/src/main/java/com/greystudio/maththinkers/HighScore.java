package com.greystudio.maththinkers;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class HighScore extends AppCompatActivity {

    private final String[] listViewTitles = new String[]{"Quick Math State", "Time Trials State", "Advanced State"};
    private final String[] listViewScore = new String[]{"Score : ", "Score : ", "Score : "};
    private final String[] listViewDescriptions = new String[]{"Times played : ", "Times taken : ", "Times taken : "};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        int quickTimeHighScore = sharedPreferences.getInt("quickMathHighScore", 0);
        int quickTimeHighScoreTotal = sharedPreferences.getInt("quickMathHighScoreWrong", 0);

        int timeTrailsPlayed = sharedPreferences.getInt("quickTimesPlayed", 0);
        int timeTrialsHighScore = sharedPreferences.getInt("timeTrialsHighScore", 0);
        int timeTrialsTimeTaken = sharedPreferences.getInt("tTimeTaken", 1200);

        int advancedHighScore = sharedPreferences.getInt("advancedHighScore", 0);
        int advancedHighScoreTotal = sharedPreferences.getInt("advancedHighScoreTotal", 0);
        int advancedTimeTaken = sharedPreferences.getInt("m_advancedTimeTaken", 1200);


        if (timeTrialsTimeTaken == 1200)
            timeTrialsTimeTaken = 0;

        if (advancedTimeTaken == 1200)
            advancedTimeTaken = 0;

        listViewTitles[0] = getString(R.string.quick);
        listViewScore[0] = getString(R.string.highest_quick, quickTimeHighScore, quickTimeHighScoreTotal);
        listViewDescriptions[0] = getString(R.string.timesPlayed, timeTrailsPlayed);

        listViewTitles[1] = getString(R.string.timeTrials);
        listViewScore[1] = getString(R.string.highest_time_trials,timeTrialsHighScore);
        listViewDescriptions[1] = (getString(R.string.timeTaken, timeTrialsTimeTaken));

        listViewTitles[2] = getString(R.string.advanced);
        listViewScore[2] = (getString(R.string.highest_advanced, advancedHighScore, advancedHighScoreTotal));
        listViewDescriptions[2] = (getString(R.string.timeTaken, advancedTimeTaken));
        List<HashMap<String, String>> aList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            HashMap<String, String> hs = new HashMap<>();
            hs.put("listViewTitles", listViewTitles[i]);
            hs.put("listViewScore", listViewScore[i]);
            hs.put("listViewDescriptions", listViewDescriptions[i]);
            aList.add(hs);
        }

        String[] from = {"listViewTitles", "listViewScore", "listViewDescriptions"};
        int[] to = {R.id.tvGameName, R.id.tvScore, R.id.tvTime};
        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), aList, R.layout.highscore_item, from, to);
        ListView simpleListView = findViewById(R.id.lvScore);
        simpleListView.setAdapter(simpleAdapter);

        simpleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View view, final int i, long l) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                resetScore(i, editor);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage(R.string.reset_score).setPositiveButton(R.string.yes, dialogClickListener)
                        .setNegativeButton(R.string.no, dialogClickListener).show();
            }
        });

        ImageView animation_image_frame = findViewById(R.id.ivAnimationHighscore);
        AnimationDrawable animationDrawable = (AnimationDrawable)animation_image_frame.getDrawable();
        animationDrawable.start();
    }

    public void openScoredHow(View view) {
        startActivity(new Intent(this, HighScore_Info.class));
    }

    private void resetScore(int i, final SharedPreferences.Editor edit) {

        if (i == 0) {
            edit.putInt("quickTimesPlayed", 0).apply();
            edit.putInt("advancedDifference", 100).apply();
            edit.putInt("quickMathHighScoreWrong", 0).apply();
            edit.putInt("quickMathHighScore", 0).apply();

        } else if (i == 1) {
            edit.putInt("tTimeTaken", 1200).apply();
            edit.putFloat("hiddenElo", 0).apply();
            edit.putInt("timeTrialsHighScore", 0).apply();

        } else if (i == 2) {
            edit.putInt("m_advancedTimeTaken", 1200).apply();
            edit.putInt("advancedHighScore", 0).apply();
            edit.putInt("advancedHighScoreTotal", 0).apply();
        }

        Toasty.success(HighScore.this, R.string.high_score_board_reset, Toast.LENGTH_SHORT, true).show();
        recreate();
    }

    public void back(View view) {
        finish();
    }
}