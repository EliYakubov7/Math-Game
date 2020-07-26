package com.greystudio.maththinkers;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


public class Game_Advanced extends AppCompatActivity {
    private Button btn00, btn01, btn10, btn11;
    private TextView tvQuestion, tvScoreAdvanced, tvCongratulations, tvTotalCorrect, tvTotalQ;
    private int locationOfCorrect, score = 0, numOfQuestions = 0, musicPos = 0;
    private final ArrayList<Integer> answers = new ArrayList<>();
    private Animation buttonsInit;
    private Dialog scorePopUp;
    private Chronometer chronometer;
    private Chronometer chrometer;
    private Boolean add, sub, divAdd, divSub, multiAdd, multiSub, mute, kids_mode, flashText,vibrate;
    private MediaPlayer mediaPlayer;
    private SharedPreferences sharedPreferences;

    private ImageButton btnMute;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_advanced);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ImageView animation_image_frame = findViewById(R.id.ivAnimationTeacher);
        AnimationDrawable animationDrawable = (AnimationDrawable)animation_image_frame.getDrawable();
        animationDrawable.start();


        chrometer = new Chronometer(this);
        chrometer.setBase(SystemClock.elapsedRealtime());
        chrometer.start();
        btn00 = findViewById(R.id.btn00);
        btn01 = findViewById(R.id.btn01);
        btn10 = findViewById(R.id.btn10);
        btn11 = findViewById(R.id.btn11);
        tvQuestion = findViewById(R.id.tvQuestions);
        tvScoreAdvanced = findViewById(R.id.tvScoreAdvanced);
        scorePopUp = new Dialog(this);
        scorePopUp.setContentView(R.layout.score_popup);
        Objects.requireNonNull(scorePopUp.getWindow()).getAttributes().windowAnimations = R.style.ScorePopUpAnimation;
        scorePopUp.getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.MATCH_PARENT);
        tvCongratulations = scorePopUp.findViewById(R.id.tvCongratulations);
        tvTotalCorrect = scorePopUp.findViewById(R.id.btnTotalCorrect);
        tvTotalQ = scorePopUp.findViewById(R.id.btnTotalQ);
        chronometer = findViewById(R.id.cmChrono);
        btnMute = findViewById(R.id.ivMute);
        chronometer.start();
        buttonsInit = AnimationUtils.loadAnimation(this,R.anim.question0);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //Gets user preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mute = sharedPreferences.getBoolean(SettingsActivity.KEY_SWITCH_MUSIC, false);
        add = sharedPreferences.getBoolean(SettingsActivity.KEY_ADVANCED_ADDITION, false);
        sub = sharedPreferences.getBoolean(SettingsActivity.KEY_ADVANCED_SUBTRACTION,false);
        multiAdd = sharedPreferences.getBoolean(SettingsActivity.KEY_ADVANCED_MULTI_ADD,false);
        divAdd = sharedPreferences.getBoolean(SettingsActivity.KEY_ADVANCED_DIV_ADD,false);
        multiSub = sharedPreferences.getBoolean(SettingsActivity.KEY_ADVANCED_MULTI_SUB,false);
        divSub = sharedPreferences.getBoolean(SettingsActivity.KEY_ADVANCED_DIV_SUB,false);
        kids_mode = sharedPreferences.getBoolean(SettingsActivity.KEY_SWITCH_KIDS_MODE,false);
        flashText = sharedPreferences.getBoolean(SettingsActivity.KEY_SWITCH_FLASH_TEXT,true);
        vibrate = sharedPreferences.getBoolean(SettingsActivity.KEY_SWITCH_VIBRATION,true);

        mediaPlayer = MediaPlayer.create(this,R.raw.audio_advanced);
        mediaPlayer.setLooping(true);
        if(!mute) {
            mediaPlayer.start();
            btnMute.setImageResource(R.drawable.ic_volume_up_black_24dp);
        }else{
            btnMute.setImageResource(R.drawable.ic_volume_off_black_24dp);
        }

        //Generate the starting question
        generateQuestion();

        if(isFirstTime()){
            ShowcaseConfig config = new ShowcaseConfig();
            config.setRenderOverNavigationBar(true);
            config.setShapePadding(50);
            config.setDelay(500);


            final MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(Game_Advanced.this, "gall");
            sequence.setConfig(config);
            chronometer.post(new Runnable() {
                @Override
                public void run() {
                    sequence.addSequenceItem(chronometer,((getString(R.string.first_sequence_item_ag)))
                            ,((getString(R.string.first_sequence_item_next_ag))));

                    sequence.addSequenceItem(tvQuestion,((getString(R.string.second_sequence_item_ag)))
                            ,((getString(R.string.second_sequence_item_next_ag))));

                    switch (locationOfCorrect){
                        case 0: sequence.addSequenceItem(
                                new MaterialShowcaseView.Builder(Game_Advanced.this)
                                        .setTarget(btn00)
                                        .setContentText(((getString(R.string.third_sequence_item_ag))) + btn00.getText() +
                                                ((getString(R.string.fourth_sequence_item_ag))))
                                        .withRectangleShape()
                                        .setDismissOnTargetTouch(true)
                                        .setTargetTouchable(true)
                                        .build()
                        );
                            break;
                        case 1: sequence.addSequenceItem(
                                new MaterialShowcaseView.Builder(Game_Advanced.this)
                                        .setTarget(btn01)
                                        .setContentText(((getString(R.string.third_sequence_item_ag))) + btn01.getText() +
                                                ((getString(R.string.fourth_sequence_item_ag))))
                                        .withRectangleShape()
                                        .setDismissOnTargetTouch(true)
                                        .setTargetTouchable(true)
                                        .build()
                        );
                            break;
                        case 2: sequence.addSequenceItem(
                                new MaterialShowcaseView.Builder(Game_Advanced.this)
                                        .setTarget(btn10)
                                        .setContentText(((getString(R.string.third_sequence_item_ag))) + btn10.getText() +
                                                ((getString(R.string.fourth_sequence_item_ag))))
                                        .withRectangleShape()
                                        .setDismissOnTargetTouch(true)
                                        .setTargetTouchable(true)
                                        .build()
                        );
                            break;
                        case 3: sequence.addSequenceItem(
                                new MaterialShowcaseView.Builder(Game_Advanced.this)
                                        .setTarget(btn11)
                                        .setContentText(((getString(R.string.third_sequence_item_ag))) + btn11.getText() +
                                                ((getString(R.string.fourth_sequence_item_ag))))
                                        .withRectangleShape()
                                        .setDismissOnTargetTouch(true)
                                        .setTargetTouchable(true)
                                        .build()
                        );
                            break;
                    }
                    sequence.addSequenceItem(tvScoreAdvanced,((getString(R.string.fifth_sequence_item_ag)))
                            ,((getString(R.string.sixth_sequence_item_ag))));
                    sequence.start();
                }
            });
        }
    }

    public void muteTemp(View view){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            sharedPreferences.edit().putBoolean(SettingsActivity.KEY_SWITCH_MUSIC,true).apply();
            btnMute.setImageResource(R.drawable.ic_volume_off_black_24dp);
        }else{
            mediaPlayer.start();
            sharedPreferences.edit().putBoolean(SettingsActivity.KEY_SWITCH_MUSIC,false).apply();
            btnMute.setImageResource(R.drawable.ic_volume_up_black_24dp);
        }
    }

    @Override
    public void onBackPressed() {
        chronometer.callOnClick();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mediaPlayer.isPlaying() && !mute) {
            musicPos = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
        }
        if(add && sub && divAdd && multiAdd && divSub && multiSub && !kids_mode) {
            finish();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(!mediaPlayer.isPlaying() && mute) {
            mediaPlayer.seekTo(musicPos);
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @SuppressLint("SetTextI18n")
    public void showPopUp(View view){
        boolean newHigh = false;
        if(add && sub && divAdd && multiAdd && divSub && multiSub && !kids_mode) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            int largest = sharedPreferences.getInt("advancedHighScore", 0);
            int largestDifference = sharedPreferences.getInt("advancedDifference",100);
            int difference = numOfQuestions - score;
            int largestTimeTaken = sharedPreferences.getInt("m_advancedTimeTaken",1200);
            long elapsedMillis = SystemClock.elapsedRealtime() - chrometer.getBase();
            int timeTaken = (int) elapsedMillis / 1000;
            if (score >= largest && difference <= largestDifference ) {
                largest = score;
                editor.putInt("advancedDifference", difference).apply();
                editor.putInt("advancedHighScore", largest).apply();
                editor.putInt("m_advancedTimeTaken", timeTaken).apply();
                editor.putInt("advancedHighScoreTotal", numOfQuestions).apply();
                chrometer.stop();
                newHigh = true;
            }
        }
        if(newHigh){
            tvCongratulations.setText(R.string.new_high_score);
        }else {
            if (numOfQuestions - score < 4 && numOfQuestions > 10) {
                tvCongratulations.setText(getString(R.string.hello_genius));
            } else if (numOfQuestions - score > 0 && numOfQuestions - score < 5) {
                tvCongratulations.setText(getString(R.string.nice));
            } else if (numOfQuestions < 10 && numOfQuestions > 1) {
                tvCongratulations.setText(getString(R.string.you_can_do_better));
            } else if (numOfQuestions == 0) {
                tvCongratulations.setText(getString(R.string.hey_buddy));
            } else {
                tvCongratulations.setText(getString(R.string.need_more_practice));
            }
        }
        tvTotalCorrect.setText(Integer.toString(score));

        tvTotalQ.setText(Integer.toString(numOfQuestions));
        Objects.requireNonNull(scorePopUp.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        scorePopUp.setCanceledOnTouchOutside(false);
        scorePopUp.show();
        chronometer.stop();
        scorePopUp.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
    }
    public void playAgain(View view){
        finish();
        startActivity(new Intent(getApplicationContext(), LoadingScreen_Advanced.class));
    }

    public  void quit(View view){
        startActivity(new Intent(getApplicationContext(),MainMenu.class));
        finish();
    }

    public void choose(View view){
        if(view.getTag().toString().equals(Integer.toString(locationOfCorrect))) {
            score++;
            numOfQuestions++;
            generateQuestion();
        }else {
            tvScoreAdvanced.startAnimation(AnimationUtils.loadAnimation(this,R.anim.correct_button));
            numOfQuestions++;

            if (vibrate){ vibrator.vibrate(500);
            }
            else{
                vibrator.cancel();
            }

            if(locationOfCorrect == 0){
                btn00.startAnimation(AnimationUtils.loadAnimation(this,R.anim.correct_button));
            } else if (locationOfCorrect == 1){
                btn01.startAnimation(AnimationUtils.loadAnimation(this, R.anim.correct_button));
            } else if (locationOfCorrect == 2){
                btn10.startAnimation(AnimationUtils.loadAnimation(this, R.anim.correct_button));
            } else if (locationOfCorrect == 3){
                btn11.startAnimation(AnimationUtils.loadAnimation(this, R.anim.correct_button));
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    generateQuestion();
                }
            }, 1000);
        }
        tvScoreAdvanced.setText(getString(R.string.advanced_score,score,numOfQuestions));
        if(flashText) {
            tvQuestion.startAnimation(AnimationUtils.loadAnimation(this, R.anim.flicker_question));
        }
        answers.clear();
    }
    private void generateQuestion(){
        Random rd = new Random();
        int option = rd.nextInt(6);

        if(option == 0){
            if(sub)
                subComplexQuestions();
            else{
                if(add){
                    sumComplexQuestions();
                }else{
                    if(multiSub)
                        multiSubQuestions();
                    else{
                        if(divSub)
                            divSubQuestions();
                        else{
                            if(multiAdd)
                                multiSumQuestions();
                            else{
                                if(divAdd)
                                    divSumQuestions();
                            }
                        }
                    }
                }
            }
        }else if(option == 1){
            if(add)
                sumComplexQuestions();
            else{
                if(sub){
                    subComplexQuestions();
                }else{
                    if(multiSub)
                        multiSubQuestions();
                    else{
                        if(divSub)
                            divSubQuestions();
                        else{
                            if(multiAdd)
                                multiSumQuestions();
                            else{
                                if(divAdd)
                                    divSumQuestions();
                            }
                        }
                    }
                }
            }
        }else if(option == 2){
            if(multiAdd)
                multiSumQuestions();
            else{
                if(sub){
                    subComplexQuestions();
                }else{
                    if(multiSub)
                        multiSubQuestions();
                    else{
                        if(divSub)
                            divSubQuestions();
                        else{
                            if(add)
                                sumComplexQuestions();
                            else{
                                if(divAdd)
                                    divSumQuestions();
                            }
                        }
                    }
                }
            }
        }else if(option == 3){
            if(divAdd)
                divSumQuestions();
            else{
                if(sub){
                    subComplexQuestions();
                }else{
                    if(multiSub)
                        multiSubQuestions();
                    else{
                        if(divSub)
                            divSubQuestions();
                        else{
                            if(add)
                                sumComplexQuestions();
                            else{
                                if(multiAdd)
                                    multiSumQuestions();
                            }
                        }
                    }
                }
            }
        }else if(option == 4){
            if(multiSub)
                multiSubQuestions();
            else{
                if(sub){
                    subComplexQuestions();
                }else{
                    if(divAdd)
                        divSumQuestions();
                    else{
                        if(divSub)
                            divSubQuestions();
                        else{
                            if(add)
                                sumComplexQuestions();
                            else{
                                if(multiAdd)
                                    multiSumQuestions();
                            }
                        }
                    }
                }
            }
        }else if(option == 5){
            if(divSub)
                multiSubQuestions();
            else{
                if(sub){
                    subComplexQuestions();
                }else{
                    if(divAdd)
                        divSumQuestions();
                    else{
                        if(multiSub)
                            multiSubQuestions();
                        else{
                            if(add)
                                sumComplexQuestions();
                            else{
                                if(multiAdd)
                                    multiSumQuestions();
                            }
                        }
                    }
                }
            }
        }
        answers.clear();
        btn00.startAnimation(buttonsInit);
        btn01.startAnimation(AnimationUtils.loadAnimation(this, R.anim.question1));
        btn10.startAnimation(AnimationUtils.loadAnimation(this,R.anim.question2));
        btn11.startAnimation(AnimationUtils.loadAnimation(this,R.anim.question3));
    }

    //Generates question with format - a + b + c
    private void sumComplexQuestions(){
        Random rd = new Random();
        int num1 = rd.nextInt(25)+20;
        int num2 = rd.nextInt(15)+10;
        int num3 = rd.nextInt(10)+5;
        int incorrectAnswer;
        locationOfCorrect = rd.nextInt(4);
        tvQuestion.setText(getString(R.string.triple_sum,num1,num2,num3));
        for(int i = 0; i < 4; i++){
            if(i == locationOfCorrect){
                answers.add(num1+num2+num3);
            }else{
                incorrectAnswer = rd.nextInt(50)+35;
                while(incorrectAnswer == num1+num2+num3){
                    incorrectAnswer = rd.nextInt(50)+35;
                }
                answers.add(incorrectAnswer);
            }
        }
        btn00.setText(getString(R.string.box,answers.get(0)));
        btn01.setText(getString(R.string.box,answers.get(1)));
        btn10.setText(getString(R.string.box,answers.get(2)));
        btn11.setText(getString(R.string.box,answers.get(3)));
    }


    //Generates question with format - a - b - c
    private void subComplexQuestions(){
        Random rd = new Random();
        int num1 = rd.nextInt((50-25)+1)+25;
        int num2 = rd.nextInt((15-10)+1)+10;
        int num3 = rd.nextInt((10-5)+1)+5;
        int incorrectAnswer;
        locationOfCorrect = rd.nextInt(4);
        tvQuestion.setText(getString(R.string.triple_sub,num1,num2,num3));
        for(int i = 0; i < 4; i++){
            if(i == locationOfCorrect){
                answers.add(num1-num2-num3);
            }else{
                incorrectAnswer = rd.nextInt(25)+10;
                while(incorrectAnswer == num1-num2-num3){
                    incorrectAnswer = rd.nextInt(25)+10;
                }
                answers.add(incorrectAnswer);
            }
        }
        btn00.setText(getString(R.string.box,answers.get(0)));
        btn01.setText(getString(R.string.box,answers.get(1)));
        btn10.setText(getString(R.string.box,answers.get(2)));
        btn11.setText(getString(R.string.box,answers.get(3)));
    }


    //Generates question with format - (a - b) x c
    private void multiSubQuestions(){
        Random rd = new Random();
        int num1 = rd.nextInt((50-25)+1)+25;
        int num2 = rd.nextInt((15-10)+1)+10;
        int num3 = rd.nextInt((5-1)+1)+1;
        int incorrectAnswer;
        locationOfCorrect = rd.nextInt(4);
        tvQuestion.setText(getString(R.string.triple_sub_multiply,num1,num2,num3));
        for(int i = 0; i < 4; i++){
            if(i == locationOfCorrect){
                answers.add((num1-num2)*num3);
            }else{
                incorrectAnswer = rd.nextInt((80-20)+1)+20;
                while(incorrectAnswer == (num1-num2)*num3){
                    incorrectAnswer = rd.nextInt((80-20)+1)+20;
                }
                answers.add(incorrectAnswer);
            }
        }
        btn00.setText(getString(R.string.box,answers.get(0)));
        btn01.setText(getString(R.string.box,answers.get(1)));
        btn10.setText(getString(R.string.box,answers.get(2)));
        btn11.setText(getString(R.string.box,answers.get(3)));
    }

    //Generates question with format - (a + b) x c

    private void multiSumQuestions(){
        Random rd = new Random();
        int num1 = rd.nextInt((25-20)+1)+20;
        int num2 = rd.nextInt((15-10)+1)+10;
        int num3 = rd.nextInt((5-1)+1)+1;
        int incorrectAnswer;
        locationOfCorrect = rd.nextInt(4);
        tvQuestion.setText(getString(R.string.triple_sum_multiply,num1,num2,num3));
        for(int i = 0; i < 4; i++){
            if(i == locationOfCorrect){
                answers.add((num1+num2)*num3);
            }else{
                incorrectAnswer = rd.nextInt((150-80)+1)+80;
                while(incorrectAnswer == (num1+num2)*num3){
                    incorrectAnswer = rd.nextInt((150-80)+1)+80;
                }
                answers.add(incorrectAnswer);
            }
        }
        btn00.setText(getString(R.string.box,answers.get(0)));
        btn01.setText(getString(R.string.box,answers.get(1)));
        btn10.setText(getString(R.string.box,answers.get(2)));
        btn11.setText(getString(R.string.box,answers.get(3)));
    }


    //Generates question with format - (a - b) / c
    private void divSubQuestions(){
        Random rd = new Random();
        int num1 = rd.nextInt((50-25)+1)+25;
        int num2 = rd.nextInt((15-10)+1)+10;
        int num3 = rd.nextInt((10-1)+1)+1;
        int incorrectAnswer;
        while((num1-num2) % num3 != 0){
            num1 = rd.nextInt((50-25)+1)+25;
            num2 = rd.nextInt((15-10)+1)+10;
            num3 = rd.nextInt((10-1)+1)+1;
        }
        locationOfCorrect = rd.nextInt(4);
        tvQuestion.setText(getString(R.string.triple_sub_div,num1,num2,num3));
        for(int i = 0; i < 4; i++){
            if(i == locationOfCorrect){
                answers.add((num1-num2)/num3);
            }else{
                incorrectAnswer = rd.nextInt((40-15)+1)+15;
                while(incorrectAnswer == (num1-num2)/num3){
                    incorrectAnswer = rd.nextInt((40-15)+1)+15;
                }
                answers.add(incorrectAnswer);
            }
        }
        btn00.setText(getString(R.string.box,answers.get(0)));
        btn01.setText(getString(R.string.box,answers.get(1)));
        btn10.setText(getString(R.string.box,answers.get(2)));
        btn11.setText(getString(R.string.box,answers.get(3)));
    }

    //Generates question with format - (a + b) / c

    private void divSumQuestions(){
        Random rd = new Random();
        int num1 = rd.nextInt((25-20)+1)+20;
        int num2 = rd.nextInt((15-10)+1)+10;
        int num3 = rd.nextInt((10-1)+1)+1;
        int incorrectAnswer;

        //Checks for remainder.
        while((num1+num2) % num3 != 0){
            num1 = rd.nextInt((50-25)+1)+25;
            num2 = rd.nextInt((15-10)+1)+10;
            num3 = rd.nextInt((10-1)+1)+1;
        }
        locationOfCorrect = rd.nextInt(4);
        tvQuestion.setText(getString(R.string.triple_sum_div,num1,num2,num3));
        for(int i = 0; i < 4; i++){
            if(i == locationOfCorrect){
                answers.add((num1+num2)/num3);
            }else{
                incorrectAnswer = rd.nextInt((40-10)+1)+10;
                while(incorrectAnswer == (num1+num2)/num3){
                    incorrectAnswer = rd.nextInt((40-10)+1)+10;
                }
                answers.add(incorrectAnswer);
            }
        }
        btn00.setText(getString(R.string.box,answers.get(0)));
        btn01.setText(getString(R.string.box,answers.get(1)));
        btn10.setText(getString(R.string.box,answers.get(2)));
        btn11.setText(getString(R.string.box,answers.get(3)));
    }

    private boolean isFirstTime()
    {

        boolean runBefore = sharedPreferences.getBoolean("RunBefore_Advanced", false);
        if (!runBefore) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("RunBefore_Advanced", true);
            editor.apply();
        }
        return !runBefore;
    }
}