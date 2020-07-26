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
import android.graphics.drawable.TransitionDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import java.util.Objects;
import java.util.Random;


import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


public class Game_QuickMath extends AppCompatActivity {
    private TextView timerText, quickMathQuestion, quickMathScore, tvCongratulations;
    private TextView tvTotalCorrect, tvTotalQ, userFeedback;
    private Button correctButton, wrongButton;
    private int numCorrect, score, wrongOrCorrect, numQuestions, numFeedback, musicPos, timesPlayed;
    private Dialog scorePopUp;
    private Vibrator vibrator;
    private Animation animation;
    private Boolean add, sub, multi, division, kids_mode,timer, mute, flashText,vibrate;
    private MediaPlayer mediaPlayer;
    private CountDownTimer countDownTimer;

    private SharedPreferences sharedPreferences;
    private ImageButton muteButton;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_quick_math);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ImageView animation_image_frame = findViewById(R.id.ivAnimationTeacher);
        AnimationDrawable animationDrawable = (AnimationDrawable) animation_image_frame.getDrawable();
        animationDrawable.start();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        timesPlayed = sharedPreferences.getInt("quickTimesPlayed", 0);
        timesPlayed++;
        timerText = findViewById(R.id.tvStopWatch);
        quickMathQuestion = findViewById(R.id.tvQuickMathQuestion);
        quickMathScore = findViewById(R.id.tvQuickMathsScore);
        correctButton = findViewById(R.id.btnQuickMathCorrect);
        wrongButton = findViewById(R.id.btnQuickMathWrong);
        scorePopUp = new Dialog(this);
        scorePopUp.setContentView(R.layout.score_popup);
        Objects.requireNonNull(scorePopUp.getWindow()).getAttributes().windowAnimations = R.style.ScorePopUpAnimation;
        scorePopUp.getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        tvCongratulations = scorePopUp.findViewById(R.id.tvCongratulations);
        tvTotalCorrect = scorePopUp.findViewById(R.id.btnTotalCorrect);
        tvTotalQ = scorePopUp.findViewById(R.id.btnTotalQ);
        userFeedback = findViewById(R.id.tvGo);
        muteButton = findViewById(R.id.ivMute);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        animation = AnimationUtils.loadAnimation(this, R.anim.correct_button);



        //Gets user preferences
        add = sharedPreferences.getBoolean(SettingsActivity.KEY_ADDITION_QUICK_MATH_GAME, false);
        sub = sharedPreferences.getBoolean(SettingsActivity.KEY_SUBTRACTION_QUICK_MATH_GAME, false);
        multi = sharedPreferences.getBoolean(SettingsActivity.KEY_MULTIPLICATION_QUICK_MATH_GAME, false);
        division = sharedPreferences.getBoolean(SettingsActivity.KEY_DIVISION_QUICK_MATH_GAME, false);
        kids_mode = sharedPreferences.getBoolean(SettingsActivity.KEY_SWITCH_KIDS_MODE, false);
        timer = sharedPreferences.getBoolean(SettingsActivity.KEY_SWITCH_QUICK_MATCH_TIMER, false);
        mute = sharedPreferences.getBoolean(SettingsActivity.KEY_SWITCH_MUSIC, false);
        flashText = sharedPreferences.getBoolean(SettingsActivity.KEY_SWITCH_FLASH_TEXT, true);
        vibrate = sharedPreferences.getBoolean(SettingsActivity.KEY_SWITCH_VIBRATION, true);

        generateQuestion();
        if (timer) {
            timer();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                final ConstraintLayout constraintLayout = findViewById(R.id.constraintLayoutQuickMath);
                final TransitionDrawable transitionDrawable = (TransitionDrawable) constraintLayout.getBackground();

                @Override
                public void run() {
                    transitionDrawable.startTransition(10000);
                    if (Build.VERSION.SDK_INT >= 23) {
                        getWindow().setStatusBarColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null));
                    }
                }
            }, 15000);
        } else {

            timerText.setText(getString(R.string.timer_disabled));
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.audio_quick_math);
        mediaPlayer.setLooping(true);
        if (!mute) {
            mediaPlayer.start();
            muteButton.setImageResource(R.drawable.ic_volume_up_black_24dp);
        } else {
            muteButton.setImageResource(R.drawable.ic_volume_off_black_24dp);
        }

        if (isFirstTime()) {
            countDownTimer.cancel();
            timerText.setText(R.string.start_timer);
            ShowcaseConfig config = new ShowcaseConfig();
            config.setRenderOverNavigationBar(true);
            config.setShapePadding(50);
            config.setDelay(500);


            final MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(Game_QuickMath.this, "quickMathOnBoarding");
            sequence.setConfig(config);
            timerText.post(new Runnable() {
                @Override
                public void run() {
                    sequence.addSequenceItem(timerText, (getString(R.string.first_sequence_item_qmg)),
                            (getString(R.string.first_sequence_item_next_qmg)));

                    sequence.addSequenceItem(quickMathQuestion, (getString(R.string.second_sequence_item_qmg)),
                            (getString(R.string.second_sequence_item_next_qmg)));

                    if (wrongOrCorrect == 0)
                        sequence.addSequenceItem(
                                new MaterialShowcaseView.Builder(Game_QuickMath.this)
                                        .setTarget(correctButton)
                                        .setContentText((getString(R.string.third_sequence_item_qmg)))
                                        .withRectangleShape()
                                        .setDismissOnTargetTouch(true)
                                        .setTargetTouchable(true)
                                        .build()
                        );
                    else
                        sequence.addSequenceItem(
                                new MaterialShowcaseView.Builder(Game_QuickMath.this)
                                        .setTarget(wrongButton)
                                        .setContentText((getString(R.string.fourth_sequence_item_qmg)))
                                        .withRectangleShape()
                                        .setDismissOnTargetTouch(true)
                                        .setTargetTouchable(true)
                                        .build()
                        );
                    sequence.addSequenceItem(
                            new MaterialShowcaseView.Builder(Game_QuickMath.this)
                                    .setTarget(userFeedback)
                                    .setContentText((getString(R.string.fifth_sequence_item_qmg)))
                                    .setDismissOnTargetTouch(true)
                                    .setTargetTouchable(true)
                                    .build()
                    );
                    sequence.addSequenceItem(
                            new MaterialShowcaseView.Builder(Game_QuickMath.this)
                                    .setTarget(quickMathQuestion)
                                    .setContentText((getString(R.string.sixth_sequence_item_qmg)))
                                    .setDismissOnTargetTouch(true)
                                    .setTargetTouchable(true)
                                    .withRectangleShape()
                                    .build()
                    );
                    sequence.start();
                }
            });
        }
    }

    public void muteTemp(View view){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            sharedPreferences.edit().putBoolean(SettingsActivity.KEY_SWITCH_MUSIC,true).apply();
            muteButton.setImageResource(R.drawable.ic_volume_off_black_24dp);
        }else{
            mediaPlayer.start();
            sharedPreferences.edit().putBoolean(SettingsActivity.KEY_SWITCH_MUSIC,false).apply();
            muteButton.setImageResource(R.drawable.ic_volume_up_black_24dp);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mediaPlayer.isPlaying() && !mute) {
            musicPos = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
        }
        if(add && sub && multi && division && timer && !kids_mode){
            finish();
        }
        if(timer){
            finish();
        }
        scorePopUp.dismiss();
    }

    @Override
    public void onBackPressed() {
        quickMathQuestion.callOnClick();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(!mediaPlayer.isPlaying() && !mute) {
            mediaPlayer.seekTo(musicPos);
            mediaPlayer.start();
        }
        scorePopUp.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        scorePopUp.dismiss();
    }

    @SuppressLint("SetTextI18n")
    public void showPopUp(View view){
        if(timer) {
            countDownTimer.cancel();
        }
        boolean newHigh = false;
        if(add && sub && multi && division && !kids_mode && timer){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            int largest = sharedPreferences.getInt("quickMathHighScore",0);
            int totalAnswered;
            int difference = numQuestions - score;
            if(score >= largest){
                totalAnswered = numQuestions;
                largest = score;
                editor.putInt("quickMathDifference",difference);
                editor.putInt("quickMathHighScore",largest);
                editor.putInt("quickMathHighScoreWrong",totalAnswered);
                editor.apply();
                newHigh = true;
            }
            editor.putInt("quickTimesPlayed",timesPlayed).apply();
        }
        if(newHigh){
            tvCongratulations.setText(R.string.new_high_score);
        }else {
            if (numQuestions - score < 4 && numQuestions > 10) {
                tvCongratulations.setText(getString(R.string.hello_genius));
            } else if (numQuestions - score > 0 && numQuestions - score < 5) {
                tvCongratulations.setText(getString(R.string.nice));
            } else if (numQuestions < 10 && numQuestions > 1) {
                tvCongratulations.setText(getString(R.string.you_can_do_better));
            } else if (numQuestions == 0) {
                tvCongratulations.setText(getString(R.string.hey_buddy));
            } else {
                tvCongratulations.setText(getString(R.string.need_more_practice));
            }
        }
        tvTotalCorrect.setText(Integer.toString(score));

        tvTotalQ.setText(Integer.toString(numQuestions));
        Objects.requireNonNull(scorePopUp.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        scorePopUp.setCanceledOnTouchOutside(false);
        if(!Game_QuickMath.this.isFinishing()) {
            scorePopUp.show();
        }
        scorePopUp.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showPopUp(){
        boolean newHigh = false;
        if(add && sub && multi && division && !kids_mode){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            int largest = sharedPreferences.getInt("quickMathHighScore",0);
            int totalAnswered;
            int largestDifference = sharedPreferences.getInt("quickMathDifference",100);
            int difference = numQuestions - score;
            if(score > largest && difference <= largestDifference){
                totalAnswered = numQuestions;
                largest = score;
                editor.putInt("quickMathDifference",difference);
                editor.putInt("quickMathHighScore",largest);
                editor.putInt("quickMathHighScoreWrong",totalAnswered);
                editor.apply();
                newHigh = true;
            }
            editor.putInt("quickTimesPlayed",timesPlayed).apply();
        }
        if(newHigh){
            tvCongratulations.setText(R.string.new_high_score);
        }else {
            if (numQuestions - score < 4 && numQuestions > 10) {
                tvCongratulations.setText(getString(R.string.hello_genius));
            } else if (numQuestions - score > 0 && numQuestions - score < 5) {
                tvCongratulations.setText(getString(R.string.nice));
            } else if (numQuestions < 10 && numQuestions > 1) {
                tvCongratulations.setText(getString(R.string.you_can_do_better));
            } else if (numQuestions == 0) {
                tvCongratulations.setText(getString(R.string.hey_buddy));
            } else {
                tvCongratulations.setText(getString(R.string.need_more_practice));
            }
        }
        tvTotalCorrect.setText(Integer.toString(score));

        tvTotalQ.setText(Integer.toString(numQuestions));
        Objects.requireNonNull(scorePopUp.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        scorePopUp.setCanceledOnTouchOutside(false);
        if(!Game_QuickMath.this.isFinishing()) {
            scorePopUp.show();
        }
        scorePopUp.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });

    }

    //Pop up Play again button
    public void playAgain(View view){
        finish();
        startActivity(new Intent(getApplicationContext(), LoadingScreen_QuickMath.class));
    }

    //Pop up quit button
    public  void quit(View view){
        startActivity(new Intent(getApplicationContext(),MainMenu.class));
        finish();
    }


    public void choose(final View view){
        if(flashText) {
            userFeedback.startAnimation(AnimationUtils.loadAnimation(this, R.anim.flicker_text0));
        }
        if(view.getTag().toString().equals(Integer.toString(wrongOrCorrect))){
            score++;
            generateQuestion();
            if(flashText) {
                quickMathQuestion.startAnimation(AnimationUtils.loadAnimation(this, R.anim.flicker_question));
            }
            quickMathScore.setText(getString(R.string.score_calc,score));
            numQuestions++;
            if(numFeedback == 0 || numQuestions == 1){
                userFeedback.setText(getString(R.string.good_job));
            }else if(numFeedback == 1){
                userFeedback.setText(getString(R.string.amazing));
            }else if(numFeedback == 2){
                userFeedback.setText(getString(R.string.fantastic));
            }else if(numFeedback == 3){
                userFeedback.setText(getString(R.string.damn));
            }else if(numFeedback == 4){
                userFeedback.setText(getString(R.string.genius));
            }else if(numFeedback == 5){
                userFeedback.setText(getString(R.string.sweet));
            }else if(numFeedback == 6){
                userFeedback.setText(getString(R.string.wow));
            }else if(numFeedback == 7){
                userFeedback.setText(getString(R.string.nice));
            }else if(numFeedback == 8){
                userFeedback.setText(getString(R.string.excellent));
            }else if(numFeedback == 9){
                userFeedback.setText(getString(R.string.o_o));
            }else if(numFeedback == 10){
                userFeedback.setText(getString(R.string.brilliant));
            }else if(numFeedback == 11){
                userFeedback.setText(getString(R.string.bananas));
            }
        }else{
            quickMathScore.startAnimation(AnimationUtils.loadAnimation(this,R.anim.correct_button));

            if (vibrate){ vibrator.vibrate(500);
            }
            else{
                vibrator.cancel();
            }


            if(wrongOrCorrect == 1){
                wrongButton.startAnimation(animation);
            }else{
                correctButton.startAnimation(animation);
            }
            Random rd = new Random();
            switch (rd.nextInt(3)) {
                case 0:
                    userFeedback.setText(getString(R.string.oh_no));
                    break;
                case 1:
                    userFeedback.setText(getString(R.string.next));
                    break;
                case 2:
                    userFeedback.setText(getString(R.string.sad));
                    break;
            }
            generateQuestion();
            numQuestions++;

        }
        view.setEnabled(false);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        },500);
    }

    //Generates write or wrong question randomly depending on user preference
    private void generateQuestion(){
        Random rd = new Random();
        int questionType = rd.nextInt(4);
        numFeedback = rd.nextInt(12);
        if(questionType == 0){
            if(add) {
                sumQuestion();
            }else{
                if(sub) {
                    subtractQuestion();
                }else{
                    if(multi)
                        multiplyQuestions();
                    else{
                        if(division)
                            divisionQuestion();
                    }
                }
            }
        }else if(questionType == 1){
            if(sub) {
                subtractQuestion();
            }else{
                if(add) {
                    sumQuestion();
                }else{
                    if(multi)
                        multiplyQuestions();
                    else{
                        if(division)
                            divisionQuestion();
                    }
                }
            }

        }else if(questionType == 2){
            if(multi) {
                multiplyQuestions();
            }else{
                if(add) {
                    sumQuestion();
                }else{
                    if(sub)
                        subtractQuestion();
                    else{
                        if(division)
                            divisionQuestion();
                    }
                }
            }
        }else if(questionType == 3){
            if(division) {
                divisionQuestion();
            }else{
                if(add) {
                    sumQuestion();
                }else{
                    if(sub)
                        subtractQuestion();
                    else{
                        if(multi)
                            multiplyQuestions();
                    }
                }
            }
        }
    }
    //Creates a sum questions
    private void sumQuestion(){
        Random rd = new Random();
        int num1,num2;
        if(!kids_mode) {
            num1 = rd.nextInt((25 - 10) + 1) + 10;
            num2 = rd.nextInt((25 - 10) + 1) + 10;
        }else {
            num1 = rd.nextInt((12 - 1) + 1) + 1;
            num2 = rd.nextInt((12 - 1) + 1) + 1;
        }
        wrongOrCorrect = rd.nextInt(2);
        int incorrectAnswer;
        if(wrongOrCorrect == 0){
            numCorrect = num1 + num2;
            quickMathQuestion.setText(getString(R.string.exmaple_addition,num1,num2, numCorrect));
        }else{
            incorrectAnswer = rd.nextInt((50-12)+1)+12;
            while(incorrectAnswer == numCorrect){
                incorrectAnswer = rd.nextInt((50-12)+1)+12;
            }
            quickMathQuestion.setText(getString(R.string.exmaple_addition,num1,num2,incorrectAnswer));
        }
    }

    //Creates a subtract question
    private void subtractQuestion(){
        Random rd = new Random();
        int num1,num2;
        if(!kids_mode) {
            num1 = rd.nextInt(25) + 1;
            num2 = rd.nextInt(10) + num1;
        }else{
            num1 = rd.nextInt(12) + 1;
            num2 = rd.nextInt(1) + num1;
        }
        int incorrectAnswer;
        wrongOrCorrect = rd.nextInt(2);
        if(wrongOrCorrect == 0 ) {
            quickMathQuestion.setText(getString(R.string.example_subtraction,num2,num1,num2-num1));
        }else{
            numCorrect = num2 - num1;
            incorrectAnswer = rd.nextInt(20)+1;
            while(incorrectAnswer == numCorrect){
                incorrectAnswer = rd.nextInt(20)+1;
            }
            quickMathQuestion.setText(getString(R.string.example_subtraction,num2,num1,incorrectAnswer));
        }
    }

    //Creates a multiply question
    private void multiplyQuestions(){
        Random rd = new Random();
        int num1,num2;
        if(!kids_mode) {
            num1 = rd.nextInt((12 - 1) + 1) + 1;
            num2 = rd.nextInt((12 - 1) + 1) + 1;
        }else{
            num1 = rd.nextInt((9 - 1) + 1) + 1;
            num2 = rd.nextInt((9 - 1) + 1) + 1;
        }
        numCorrect = num1 * num2;
        wrongOrCorrect = rd.nextInt(2);
        int incorrectAnswer;
        if(wrongOrCorrect == 0){
            quickMathQuestion.setText(getString(R.string.example_multiplication,num1,num2, numCorrect));
        }else{
            incorrectAnswer = rd.nextInt((100-20)+1)+20;
            while(incorrectAnswer == numCorrect){
                incorrectAnswer = rd.nextInt((100-20)+1)+20;
            }
            quickMathQuestion.setText(getString(R.string.example_multiplication,num1,num2,incorrectAnswer));
        }
    }

    //Creates a division question
    private void divisionQuestion(){
        Random rd = new Random();
        int num1,num2;
        if(!kids_mode) {
            num1 = rd.nextInt((25 - 10) + 1) + 10;
            num2 = rd.nextInt((25 - 10) + 1) + 10;
        }else{
            num1 = rd.nextInt((12 - 1) + 1) + 1;
            num2 = rd.nextInt((12 - 1) + 1) + 1;
        }
        int incorrectAnswer;
        wrongOrCorrect = rd.nextInt(2);
        if(wrongOrCorrect == 0){
            while(num2 % num1 != 0){
                num1 = rd.nextInt(10)+1;
                num2 = rd.nextInt(10)+num1;
            }
            numCorrect = num2 / num1;
            quickMathQuestion.setText(getString(R.string.example_division,num2,num1, numCorrect));
        }else{
            incorrectAnswer = rd.nextInt(24)+1;
            numCorrect = num2 / num1;
            while(num2 % num1 != 0 || incorrectAnswer == numCorrect){
                num1 = rd.nextInt(10)+1;
                num2 = rd.nextInt(10)+num1;
            }
            quickMathQuestion.setText(getString(R.string.example_division,num2,num1,incorrectAnswer));
        }
    }

    //Timer that keeps track of time
    private void timer(){
        countDownTimer = new CountDownTimer(30000,1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                if(millisUntilFinished > 10000)
                    timerText.setText(getString(R.string.timer_quick_math,(int) millisUntilFinished / 1000));
                else if(millisUntilFinished < 10000 && millisUntilFinished > 5000){
                    //Timer flickering gets faster as time runs out
                    //Adds a 0 before last digit
                    timerText.setText(getString(R.string.timer_quick_math_ten_less,(int) millisUntilFinished / 1000));
                    //Flickers only if its enabled in the settings.
                    if(flashText) {
                        timerText.startAnimation(AnimationUtils.loadAnimation(Game_QuickMath.this, R.anim.flicker_text2));
                    }
                }else if(millisUntilFinished < 5000 && millisUntilFinished > 3000){
                    timerText.setText(getString(R.string.timer_quick_math_ten_less,(int) millisUntilFinished / 1000));
                    if(flashText) {
                        timerText.startAnimation(AnimationUtils.loadAnimation(Game_QuickMath.this, R.anim.flicker_text1));
                    }
                }else {
                    timerText.setText(getString(R.string.timer_quick_math_ten_less,(int) millisUntilFinished / 1000));
                    if(flashText) {
                        timerText.startAnimation(AnimationUtils.loadAnimation(Game_QuickMath.this, R.anim.flicker_text0));
                    }
                }
            }
            @Override
            public void onFinish() {
                //Shows user results
                showPopUp();
            }
        }.start();
    }

    private boolean isFirstTime()
    {

        boolean runBefore = sharedPreferences.getBoolean("RunBefore_QuickMath", false);
        if (!runBefore) {
            // first time
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("RunBefore_QuickMath", true);
            editor.apply();
        }
        return !runBefore;
    }
}