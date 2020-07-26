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
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


public class Game_TimeTrials extends AppCompatActivity {
    private TextView timeLeftText;
    private TextView scoreText;
    private TextView userFeedback;
    private TextView tvTotalCorrect;
    private TextView tvTotalQ;
    private TextView tvCongratulations;
    private Button btn00, btn01, btn10, btn11;
    private final ArrayList<String> questions = new ArrayList<>();
    private int locationOfCorrect;
    private int score = 0;
    private int numOfQuestions = 0;
    private int feedBackNum;
    private int musicPos;
    private CountDownTimer countDownTimer;
    private Animation correctAnimation, feedBackAnimation;
    private Dialog scorePopUp;
    private MediaPlayer mediaPlayer;
    private Boolean mute, flashingText,vibrate;
    private Chronometer chronometer;
    private SharedPreferences sharedPreferences;
    private ImageButton muteButton;
    private Vibrator vibrator;
    private boolean runBefore;
    private Boolean add, sub, multi, div, timer, kids_mode;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_time_trials);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ImageView animation_image_frame = findViewById(R.id.ivAnimationTeacher);
        AnimationDrawable animationDrawable = (AnimationDrawable)animation_image_frame.getDrawable();
        animationDrawable.start();


        chronometer = new Chronometer(this);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        timeLeftText = findViewById(R.id.tvTimeTrailsTimeLeft);
        scoreText = findViewById(R.id.tvTimeTrailsScore);
        btn00 = findViewById(R.id.btn00);
        btn01 = findViewById(R.id.btn01);
        btn10 = findViewById(R.id.btn10);
        btn11 = findViewById(R.id.btn11);
        userFeedback = findViewById(R.id.tvFeedback);
        TextView whichOneIsCorrect = findViewById(R.id.tvTimeTrailsInst);
        scorePopUp = new Dialog(this);
        scorePopUp.setContentView(R.layout.score_popup);
        Objects.requireNonNull(scorePopUp.getWindow()).getAttributes().windowAnimations = R.style.ScorePopUpAnimation;
        scorePopUp.getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.MATCH_PARENT);
        tvCongratulations = scorePopUp.findViewById(R.id.tvCongratulations);
        tvTotalCorrect = scorePopUp.findViewById(R.id.btnTotalCorrect);
        tvTotalQ = scorePopUp.findViewById(R.id.btnTotalQ);
        muteButton = findViewById(R.id.ivMute);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //Variables to hold animation values.
        correctAnimation = AnimationUtils.loadAnimation(this,R.anim.correct_button);
        feedBackAnimation = AnimationUtils.loadAnimation(this,R.anim.flicker_text0);

        //Required to generate the first question
        //Initial animation
        btn00.startAnimation(AnimationUtils.loadAnimation(this,R.anim.question0));
        btn01.startAnimation(AnimationUtils.loadAnimation(this,R.anim.question1));
        btn10.startAnimation(AnimationUtils.loadAnimation(this,R.anim.question2));
        btn11.startAnimation(AnimationUtils.loadAnimation(this,R.anim.question3));
        if (Build.VERSION.SDK_INT >= 23) {
            getWindow().setStatusBarColor(ResourcesCompat.getColor(getResources(),R.color.colorPrimaryDark,null));
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mute = sharedPreferences.getBoolean(SettingsActivity.KEY_SWITCH_MUSIC,false);

        //Gets user preferences
        add = sharedPreferences.getBoolean(SettingsActivity.KEY_TIME_TRIALS_ADDITION,false);
        sub = sharedPreferences.getBoolean(SettingsActivity.KEY_TIME_TRIALS_SUBTRACTION,false);
        multi = sharedPreferences.getBoolean(SettingsActivity.KEY_TIME_TRIALS_MULTIPLICATION,false);
        div = sharedPreferences.getBoolean(SettingsActivity.KEY_TIME_TRIALS_DIVISION,false);
        timer = sharedPreferences.getBoolean(SettingsActivity.KEY_SWITCH_TIME_TRIALS_TIMER,false);
        kids_mode = sharedPreferences.getBoolean(SettingsActivity.KEY_SWITCH_KIDS_MODE,false);
        flashingText = sharedPreferences.getBoolean(SettingsActivity.KEY_SWITCH_FLASH_TEXT,true);
        vibrate = sharedPreferences.getBoolean(SettingsActivity.KEY_SWITCH_VIBRATION,true);

        if(timer)
            timer();
        else {
            timeLeftText.setText(getString(R.string.timer_disabled));
        }

        generateQuestions();

        mediaPlayer = MediaPlayer.create(this,R.raw.audio_time_trials);
        mediaPlayer.setLooping(true);
        if(!mute) {
            mediaPlayer.start();
            muteButton.setImageResource(R.drawable.ic_volume_up_black_24dp);
        }else{
            muteButton.setImageResource(R.drawable.ic_volume_off_black_24dp);
        }

        if(isFirstTime()){
            countDownTimer.cancel();
            timeLeftText.setText(R.string.time_left_ten);
            whichOneIsCorrect.setText(R.string.find_correct_equation);
            ShowcaseConfig config = new ShowcaseConfig();
            config.setRenderOverNavigationBar(true);
            config.setShapePadding(50);
            config.setDelay(500);


            final MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(Game_TimeTrials.this, "timeTrialsOnBoarding");
            sequence.setConfig(config);
            timeLeftText.post(new Runnable() {
                @Override
                public void run() {
                    sequence.addSequenceItem(timeLeftText,((getString(R.string.first_sequence_item_ttg)))
                            ,((getString(R.string.first_sequence_item_next_ttg))));

                    switch (locationOfCorrect){
                        case 0: sequence.addSequenceItem(
                                new MaterialShowcaseView.Builder(Game_TimeTrials.this)
                                        .setTarget(btn00)
                                        .setContentText(((getString(R.string.second_sequence_item_ttg))) + btn00.getText() +
                                                ((getString(R.string.third_sequence_item_ttg))))
                                        .setDismissOnTargetTouch(true)
                                        .setTargetTouchable(true)
                                        .withRectangleShape()
                                        .build()
                        );
                            break;
                        case 1: sequence.addSequenceItem(
                                new MaterialShowcaseView.Builder(Game_TimeTrials.this)
                                        .setTarget(btn01)
                                        .setContentText(((getString(R.string.second_sequence_item_ttg))) + btn01.getText() +
                                                ((getString(R.string.third_sequence_item_ttg))))
                                        .setDismissOnTargetTouch(true)
                                        .setTargetTouchable(true)
                                        .withRectangleShape()
                                        .build()
                        );
                            break;
                        case 2: sequence.addSequenceItem(
                                new MaterialShowcaseView.Builder(Game_TimeTrials.this)
                                        .setTarget(btn10)
                                        .setContentText(((getString(R.string.second_sequence_item_ttg))) + btn10.getText() +
                                                ((getString(R.string.third_sequence_item_ttg))))
                                        .setDismissOnTargetTouch(true)
                                        .setTargetTouchable(true)
                                        .withRectangleShape()
                                        .build()
                        );
                            break;
                        case 3: sequence.addSequenceItem(
                                new MaterialShowcaseView.Builder(Game_TimeTrials.this)
                                        .setTarget(btn11)
                                        .setContentText(((getString(R.string.second_sequence_item_ttg))) + btn11.getText() +
                                                ((getString(R.string.third_sequence_item_ttg))))
                                        .setDismissOnTargetTouch(true)
                                        .setTargetTouchable(true)
                                        .withRectangleShape()
                                        .build()
                        );
                            break;
                    }
                    sequence.addSequenceItem(userFeedback,(getString(R.string.fourth_sequence_item_ttg)),
                            (getString(R.string.fifth_sequence_item_ttg)));

                    sequence.addSequenceItem(scoreText,(getString(R.string.sixth_sequence_item_ttg)),
                            (getString(R.string.seventh_sequence_item_ttg)));

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
        if(add && sub && multi && div && timer && !kids_mode) {
            finish();
        }
        if(timer){
            finish();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(!mediaPlayer.isPlaying() && !mute) {
            mediaPlayer.seekTo(musicPos);
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scorePopUp.dismiss();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Override
    public void onBackPressed() {
        showPopUp();
    }

    private void timer(){
        countDownTimer = new CountDownTimer(10000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished < 4000)
                    if(flashingText) {
                        timeLeftText.startAnimation(AnimationUtils.loadAnimation(Game_TimeTrials.this, R.anim.flicker_timer));
                    }
                if (millisUntilFinished > 10000)
                    timeLeftText.setText(getString(R.string.time_left, (int) millisUntilFinished / 1000));
                else
                    timeLeftText.setText(getString(R.string.time_left_ten_less, (int) millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                showPopUp();
            }
        }.start();
    }
    @SuppressLint("SetTextI18n")
    private void showPopUp(){
        boolean newHigh = false;
        if(timer) {
            countDownTimer.cancel();
        }
        long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        int timeTaken = (int) elapsedMillis / 1000;
        if(add && sub && multi && div && timer && !kids_mode) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            int largest;
            float hiddenElo = (float) score / timeTaken;
            float elo = sharedPreferences.getFloat("hiddenElo",0);
            if (hiddenElo > elo) {
                largest = score;
                editor.putFloat("hiddenElo",hiddenElo).apply();
                editor.putInt("timeTrialsHighScore", largest).apply();
                editor.putInt("tTimeTaken", timeTaken).apply();
                Log.i("HIDDEN ELO", Float.toString(sharedPreferences.getFloat("hiddenElo",0)));
                chronometer.stop();
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

        TextView typeText = scorePopUp.findViewById(R.id.tvType);
        typeText.setText(R.string.times_taken);
        tvTotalQ.setText(Integer.toString(timeTaken));
        Objects.requireNonNull(scorePopUp.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        scorePopUp.setCanceledOnTouchOutside(false);
        if (!Game_TimeTrials.this.isFinishing()) {
            scorePopUp.show();
        }
        scorePopUp.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
    }
    public void playAgain(View view){
        finish();
        startActivity(new Intent(getApplicationContext(), LoadingScreen_TimeTrials.class));
    }



    //Pop up quit button
    public  void quit(View view){
        startActivity(new Intent(getApplicationContext(),MainMenu.class));
        finish();
    }

    private String sumQuestion(){
        Random rd = new Random();
        int num1 = rd.nextInt((15-4)+1)+4;
        int num2 = rd.nextInt((15-4)+1)+4;
        return getString(R.string.exmaple_addition, num1, num2, num1 + num2);

    }
    private String subtractQuestion(){
        Random rd = new Random();
        int num1 = rd.nextInt((15-4)+1)+4;
        int num2 = rd.nextInt((15-num1)+1)+num1;
        return getString(R.string.example_subtraction,num2,num1,num2-num1);
    }
    private String multiplyQuestion(){
        Random rd = new Random();
        int num1 = rd.nextInt((12-1)+1)+1;
        int num2 = rd.nextInt((12-1)+1)+1;
        return getString(R.string.example_multiplication,num1,num2,num1*num2);

    }
    private String divisionQuestion(){
        Random rd = new Random();
        int num1 = rd.nextInt(10)+1;
        int num2 = rd.nextInt(50)+1;
        while(num2 % num1 != 0) {
            num1 = rd.nextInt(10)+1;
            num2 = rd.nextInt(50)+1;
        }
        return getString(R.string.example_division,num2,num1,num2/num1);

    }
    private String wrongTypeOfQuestion(){
        Random rd = new Random();
        switch (rd.nextInt(4)){
            case 0:
                if(add){
                    int num1 = rd.nextInt(15)+1;
                    int num2 = rd.nextInt(12)+1;
                    int number_3 = rd.nextInt(40)+1;
                    while(number_3 == num1+num2){
                        number_3 = rd.nextInt(40)+1;
                    }
                    return getString(R.string.exmaple_addition,num1,num2,number_3);
                }else{
                    if(sub){
                        int num1 = rd.nextInt(15)+1;
                        int num2 = rd.nextInt((10-5)+1)+5;
                        int number_3 = rd.nextInt(15)+1;
                        while(number_3 == num1-num2){
                            number_3 = rd.nextInt(15)+1;
                        }
                        return getString(R.string.example_subtraction,num1,num2,number_3);
                    }else{
                        if(multi){
                            int num1 = rd.nextInt(12)+1;
                            int num2 = rd.nextInt(12)+1;
                            int number_3 = rd.nextInt(50)+1;
                            while(number_3 == num1*num2){
                                number_3 = rd.nextInt(50)+1;
                            }
                            return getString(R.string.example_multiplication,num1,num2,number_3);
                        }else{
                            if(div){
                                int num1 = rd.nextInt(25)+1;
                                int num2 = rd.nextInt(12)+1;
                                int number_3 = rd.nextInt(12)+1;
                                while(number_3 == num1/num2){
                                    number_3 = rd.nextInt(12)+1;
                                }
                                return getString(R.string.example_division,num1,num2,number_3);
                            }
                        }
                    }
                }
            case 1:
                if(sub){
                    int num1 = rd.nextInt(15)+1;
                    int num2 = rd.nextInt((10-5)+1)+5;
                    int number_3 = rd.nextInt(15)+1;
                    while(number_3 == num1-num2){
                        number_3 = rd.nextInt(15)+1;
                    }
                    return getString(R.string.example_subtraction,num1,num2,number_3);
                }else{
                    if(add){
                        int num1 = rd.nextInt(15)+1;
                        int num2 = rd.nextInt(12)+1;
                        int number_3 = rd.nextInt(40)+1;
                        while(number_3 == num1+num2){
                            number_3 = rd.nextInt(40)+1;
                        }
                        return getString(R.string.exmaple_addition,num1,num2,number_3);
                    }else{
                        if(multi){
                            int num1 = rd.nextInt(12)+1;
                            int num2 = rd.nextInt(12)+1;
                            int number_3 = rd.nextInt(50)+1;
                            while(number_3 == num1*num2){
                                number_3 = rd.nextInt(50)+1;
                            }
                            return getString(R.string.example_multiplication,num1,num2,number_3);
                        }else{
                            if(div){
                                int num1 = rd.nextInt(25)+1;
                                int num2 = rd.nextInt(12)+1;
                                int number_3 = rd.nextInt(12)+1;
                                while(number_3 == num1/num2){
                                    number_3 = rd.nextInt(12)+1;
                                }
                                return getString(R.string.example_division,num1,num2,number_3);
                            }
                        }
                    }
                }
            case 2:
                if(multi){
                    int num1 = rd.nextInt(12)+1;
                    int num2 = rd.nextInt(12)+1;
                    int number_3 = rd.nextInt(50)+1;
                    while(number_3 == num1*num2){
                        number_3 = rd.nextInt(50)+1;
                    }
                    return getString(R.string.example_multiplication,num1,num2,number_3);
                }else{
                    if(add){
                        int num1 = rd.nextInt(15)+1;
                        int num2 = rd.nextInt(12)+1;
                        int number_3 = rd.nextInt(40)+1;
                        while(number_3 == num1+num2){
                            number_3 = rd.nextInt(40)+1;
                        }
                        return getString(R.string.exmaple_addition,num1,num2,number_3);
                    }else{
                        if(sub){
                            int num1 = rd.nextInt(15)+1;
                            int num2 = rd.nextInt((10-5)+1)+5;
                            int number_3 = rd.nextInt(15)+1;
                            while(number_3 == num1-num2){
                                number_3 = rd.nextInt(15)+1;
                            }
                            return getString(R.string.exmaple_addition,num1,num2,number_3);
                        }else{
                            if(div){
                                int num1 = rd.nextInt(25)+1;
                                int num2 = rd.nextInt(12)+1;
                                int number_3 = rd.nextInt(12)+1;
                                while(number_3 == num1/num2){
                                    number_3 = rd.nextInt(12)+1;
                                }
                                return getString(R.string.example_division,num1,num2,number_3);
                            }
                        }
                    }
                }
            case 3:
                if(div){
                    int num1 = rd.nextInt(25)+1;
                    int num2 = rd.nextInt(12)+1;
                    int number_3 = rd.nextInt(12)+1;
                    while(number_3 == num1/num2){
                        number_3 = rd.nextInt(12)+1;
                    }
                    return getString(R.string.example_division,num1,num2,number_3);
                }else{
                    if(add){
                        int num1 = rd.nextInt(15)+1;
                        int num2 = rd.nextInt(12)+1;
                        int number_3 = rd.nextInt(40)+1;
                        while(number_3 == num1+num2){
                            number_3 = rd.nextInt(40)+1;
                        }
                        return getString(R.string.exmaple_addition,num1,num2,number_3);
                    }else{
                        if(sub){
                            int num1 = rd.nextInt(15)+1;
                            int num2 = rd.nextInt((10-5)+1)+5;
                            int number_3 = rd.nextInt(15)+1;
                            while(number_3 == num1-num2){
                                number_3 = rd.nextInt(15)+1;
                            }
                            return getString(R.string.example_subtraction,num1,num2,number_3);
                        }else{
                            if(multi){
                                int num1 = rd.nextInt(12)+1;
                                int num2 = rd.nextInt(12)+1;
                                int number_3 = rd.nextInt(50)+1;
                                while(number_3 == num1*num2){
                                    number_3 = rd.nextInt(50)+1;
                                }
                                return getString(R.string.example_multiplication,num1,num2,number_3);
                            }
                        }
                    }
                }
            default:
                return "Enable at least one type of question.";
        }
    }
    private String typeOfQuestion(){
        Random rd = new Random();
        switch (rd.nextInt(4)){
            case 0:
                if(add) {
                    return sumQuestion();
                }else{
                    if(sub){
                        return subtractQuestion();
                    }else{
                        if(div){
                            return divisionQuestion();
                        }else{
                            if(multi){
                                return multiplyQuestion();
                            }
                        }
                    }
                }
            case 1:
                if(sub) {
                    return subtractQuestion();
                }else{
                    if(add){
                        return sumQuestion();
                    }else{
                        if(div){
                            return divisionQuestion();
                        }else{
                            if(multi){
                                return multiplyQuestion();
                            }
                        }
                    }
                }
            case 2:
                if(multi){
                    return multiplyQuestion();
                }else{
                    if(add){
                        return sumQuestion();
                    }else{
                        if(sub){
                            return subtractQuestion();
                        }else{
                            if(div){
                                return divisionQuestion();
                            }
                        }
                    }
                }
            case 3:
                if(div){
                    return divisionQuestion();
                }else{
                    if(add){
                        return sumQuestion();
                    }else{
                        if(sub){
                            return subtractQuestion();
                        }else{
                            if(multi){
                                return multiplyQuestion();
                            }
                        }
                    }
                }
            default:
                return "Enable at least one type of question.";
        }
    }
    private void generateQuestions(){
        Random rd = new Random();
        int wrongOrCorrect = rd.nextInt(2);
        locationOfCorrect = rd.nextInt(4);
        for(int i = 0; i < 4; i++){
            if(i == locationOfCorrect){
                questions.add(typeOfQuestion());
            }else{
                questions.add(wrongTypeOfQuestion());
            }
        }
        feedBackNum = rd.nextInt(12);
        scoreText.setText(getString(R.string.score_calc, score));
        btn00.setText(questions.get(0));
        btn01.setText(questions.get(1));
        btn10.setText(questions.get(2));
        btn11.setText(questions.get(3));
        questions.clear();
    }

    public void choose(View view){
        if(flashingText) {
            userFeedback.startAnimation(feedBackAnimation);
        }
        if(view.getTag().toString().equals(Integer.toString(locationOfCorrect))){
            score++;
            numOfQuestions++;
            generateQuestions();
            if(timer && !runBefore) {
                countDownTimer.start();}
            if(feedBackNum == 0 || numOfQuestions == 1){
                userFeedback.setText(getString(R.string.good_job));
            }else if(feedBackNum == 1){
                userFeedback.setText(getString(R.string.amazing));
            }else if(feedBackNum == 2){
                userFeedback.setText(getString(R.string.fantastic));
            }else if(feedBackNum == 3){
                userFeedback.setText(getString(R.string.damn));
            }else if(feedBackNum == 4){
                userFeedback.setText(getString(R.string.genius));
            }else if(feedBackNum == 5){
                userFeedback.setText(getString(R.string.sweet));
            }else if(feedBackNum == 6){
                userFeedback.setText(getString(R.string.wow));
            }else if(feedBackNum == 7){
                userFeedback.setText(getString(R.string.nice));
            }else if(feedBackNum == 8){
                userFeedback.setText(getString(R.string.excellent));
            }else if(feedBackNum == 9){
                userFeedback.setText(getString(R.string.o_o));
            }else if(feedBackNum == 10){
                userFeedback.setText(getString(R.string.brilliant));
            }else if(feedBackNum == 11){
                userFeedback.setText(getString(R.string.bananas));
            }
        }else {
            numOfQuestions++;

            if (vibrate){ vibrator.vibrate(500);
            }
            else{
                vibrator.cancel();
            }

            if(locationOfCorrect == 0){
                btn00.startAnimation(correctAnimation);
            }else if(locationOfCorrect == 1){
                btn01.startAnimation(correctAnimation);
            }else if(locationOfCorrect == 2){
                btn10.startAnimation(correctAnimation);
            }else if(locationOfCorrect == 3){
                btn11.startAnimation(correctAnimation);
            }
            Random rd = new Random();
            switch (rd.nextInt(3)){
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
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showPopUp();
                }
            }, 1000);

        }
    }

    private boolean isFirstTime()
    {
        boolean runBefore = sharedPreferences.getBoolean("RunBefore_TimeTrails", false);
        if (!runBefore) {
            // first time
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("RunBefore_TimeTrails", true);
            editor.apply();
        }
        return !runBefore;
    }



}