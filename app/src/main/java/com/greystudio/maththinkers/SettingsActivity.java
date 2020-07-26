package com.greystudio.maththinkers;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import es.dmoral.toasty.Toasty;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_SWITCH_KIDS_MODE = "switchKidMode";
    public static final String KEY_SWITCH_MUSIC = "switchMute";
    public static final String KEY_SWITCH_VIBRATION = "switchVibration";
    public static final String KEY_SWITCH_FLASH_TEXT = "switchFlashText";
    static final String KEY_ADDITION_QUICK_MATH_GAME = "quickMathAddition";
    static final String KEY_SUBTRACTION_QUICK_MATH_GAME = "quickMathSubtraction";
    static final String KEY_MULTIPLICATION_QUICK_MATH_GAME = "quickMathMultiplication";
    static final String KEY_DIVISION_QUICK_MATH_GAME = "quickMathDivision";
    static final String KEY_SWITCH_QUICK_MATCH_TIMER = "switchQuickMathTimer";

    static final String KEY_TIME_TRIALS_ADDITION = "timeTrialsAddition";
    static final String KEY_TIME_TRIALS_SUBTRACTION = "timeTrialsSubtraction";
    static final String KEY_TIME_TRIALS_MULTIPLICATION = "timeTrialsMultiplication";
    static final String KEY_TIME_TRIALS_DIVISION = "timeTrialsDivision";
    static final String KEY_SWITCH_TIME_TRIALS_TIMER = "switchTimeTrialsTimer";

    public static final String KEY_ADVANCED_ADDITION = "AdvancedAddition";
    public static final String KEY_ADVANCED_SUBTRACTION = "AdvancedSubtraction";
    public static final String KEY_ADVANCED_MULTI_ADD = "AdvancedMultiAdd";
    public static final String KEY_ADVANCED_MULTI_SUB = "AdvancedMultiSub";
    public static final String KEY_ADVANCED_DIV_ADD = "AdvancedDivAdd";
    public static final String KEY_ADVANCED_DIV_SUB = "AdvancedDivSub";

    private static boolean kidsModeNotDisabled = false;
    private static boolean vibrationNotDisabled = false;
    private static boolean flashTextNotDisabled = false;
    private static boolean timerDisabled = false;
    private static boolean typeDisabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        boolean rankDisabled;
        if (typeDisabled && kidsModeNotDisabled && timerDisabled) {
            rankDisabled = true;
            sharedPreferences.edit().putBoolean("ranking", rankDisabled).apply();
        } else {
            rankDisabled = false;
            sharedPreferences.edit().putBoolean("ranking", rankDisabled).apply();
        }

        if (key.equals(KEY_SWITCH_KIDS_MODE)) {
            if (sharedPreferences.getBoolean(KEY_SWITCH_KIDS_MODE, false)) {
                kidsModeNotDisabled = true;
                Toasty.warning(SettingsActivity.this, R.string.kids_mode_disabled, Toast.LENGTH_LONG, true).show();

            } else {
                kidsModeNotDisabled = false;
                if (!timerDisabled && !typeDisabled) {
                    Toasty.success(SettingsActivity.this, R.string.score_ranking_reenabled, Toast.LENGTH_SHORT, true).show();

                }
            }
        }

        if (key.equals(KEY_SWITCH_VIBRATION)) {
            if (sharedPreferences.getBoolean(KEY_SWITCH_VIBRATION, false)) {
                vibrationNotDisabled = true;
                Toasty.success(SettingsActivity.this, R.string.vibration_enabled, Toast.LENGTH_SHORT, true).show();

            } else {
                Toasty.error(SettingsActivity.this, R.string.vibration_disabled, Toast.LENGTH_SHORT, true).show();

            }
        }

        if (key.equals(KEY_SWITCH_FLASH_TEXT)) {
            if (sharedPreferences.getBoolean(KEY_SWITCH_FLASH_TEXT, false)) {
                flashTextNotDisabled = true;
                Toasty.success(SettingsActivity.this, R.string.flashing_text_enabled, Toast.LENGTH_SHORT, true).show();

            } else {
                Toasty.error(SettingsActivity.this, R.string.flashing_text_disabled, Toast.LENGTH_SHORT, true).show();

            }
        }

        if (!sharedPreferences.getBoolean(KEY_ADDITION_QUICK_MATH_GAME, false) && !sharedPreferences.getBoolean(KEY_SUBTRACTION_QUICK_MATH_GAME, false) && !sharedPreferences.getBoolean(KEY_MULTIPLICATION_QUICK_MATH_GAME, false) && !sharedPreferences.getBoolean(KEY_DIVISION_QUICK_MATH_GAME, false)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_ADDITION_QUICK_MATH_GAME, true);
            editor.apply();
            Toasty.warning(SettingsActivity.this, R.string.keep_least_one_operation_enabled, Toast.LENGTH_SHORT).show();
        }
        if (!sharedPreferences.getBoolean(KEY_TIME_TRIALS_ADDITION, false) && !sharedPreferences.getBoolean(KEY_TIME_TRIALS_SUBTRACTION, false) && !sharedPreferences.getBoolean(KEY_TIME_TRIALS_MULTIPLICATION, false) && !sharedPreferences.getBoolean(KEY_TIME_TRIALS_DIVISION, false)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_TIME_TRIALS_ADDITION, true);
            editor.apply();
            Toasty.warning(SettingsActivity.this, R.string.keep_least_one_operation_enabled, Toast.LENGTH_SHORT).show();
        }
        if (!sharedPreferences.getBoolean(KEY_ADVANCED_ADDITION, false)
                && !sharedPreferences.getBoolean(KEY_ADVANCED_SUBTRACTION, false)
                && !sharedPreferences.getBoolean(KEY_ADVANCED_MULTI_ADD, false)
                && !sharedPreferences.getBoolean(KEY_ADVANCED_MULTI_SUB, false)
                && !sharedPreferences.getBoolean(KEY_ADVANCED_DIV_ADD, false)
                && !sharedPreferences.getBoolean(KEY_ADVANCED_DIV_SUB, false)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_ADVANCED_ADDITION, true);
            editor.apply();
            Toasty.warning(SettingsActivity.this, R.string.keep_least_one_operation_enabled, Toast.LENGTH_SHORT).show();
        }
        if (key.equals(KEY_ADDITION_QUICK_MATH_GAME) || key.equals(KEY_SUBTRACTION_QUICK_MATH_GAME) || key.equals(KEY_MULTIPLICATION_QUICK_MATH_GAME) || key.equals(KEY_DIVISION_QUICK_MATH_GAME)) {
            if (!sharedPreferences.getBoolean(KEY_ADDITION_QUICK_MATH_GAME, false) || !sharedPreferences.getBoolean(KEY_SUBTRACTION_QUICK_MATH_GAME, false) || !sharedPreferences.getBoolean(KEY_MULTIPLICATION_QUICK_MATH_GAME, false) || !sharedPreferences.getBoolean(KEY_DIVISION_QUICK_MATH_GAME, false)) {
                typeDisabled = true;
            } else {
                typeDisabled = false;
                if (!kidsModeNotDisabled && !timerDisabled) {
                    Toasty.success(SettingsActivity.this, R.string.score_ranking_reenabled, Toast.LENGTH_SHORT, true).show();
                } else {
                    Toasty.error(SettingsActivity.this, R.string.score_ranking_disabled, Toast.LENGTH_SHORT, true).show();
                }
            }
        }
        if (key.equals(KEY_TIME_TRIALS_ADDITION) || key.equals(KEY_TIME_TRIALS_SUBTRACTION) || key.equals(KEY_TIME_TRIALS_MULTIPLICATION) || key.equals(KEY_TIME_TRIALS_DIVISION)) {
            if (!sharedPreferences.getBoolean(KEY_TIME_TRIALS_ADDITION, false) || !sharedPreferences.getBoolean(KEY_TIME_TRIALS_SUBTRACTION, false) || !sharedPreferences.getBoolean(KEY_TIME_TRIALS_MULTIPLICATION, false) || !sharedPreferences.getBoolean(KEY_TIME_TRIALS_DIVISION, false)) {
                typeDisabled = true;
            } else {
                typeDisabled = false;
                if (!kidsModeNotDisabled && !timerDisabled) {
                    Toasty.success(SettingsActivity.this, R.string.score_ranking_reenabled, Toast.LENGTH_SHORT, true).show();
                } else {
                    Toasty.error(SettingsActivity.this, R.string.score_ranking_disabled, Toast.LENGTH_SHORT, true).show();
                }

            }
        }
        if (key.equals(KEY_ADVANCED_ADDITION)
                || key.equals(KEY_ADVANCED_SUBTRACTION)
                || key.equals(KEY_ADVANCED_MULTI_ADD)
                || key.equals(KEY_ADVANCED_MULTI_SUB)
                || key.equals(KEY_ADVANCED_DIV_ADD)
                || key.equals(KEY_ADVANCED_DIV_SUB)) {
            if (!sharedPreferences.getBoolean(KEY_ADVANCED_ADDITION, false)
                    || !sharedPreferences.getBoolean(KEY_ADVANCED_SUBTRACTION, false)
                    || !sharedPreferences.getBoolean(KEY_ADVANCED_MULTI_ADD, false)
                    || !sharedPreferences.getBoolean(KEY_ADVANCED_MULTI_SUB, false)
                    || !sharedPreferences.getBoolean(KEY_ADVANCED_DIV_ADD, false)
                    || !sharedPreferences.getBoolean(KEY_ADVANCED_DIV_SUB, false)) {
                typeDisabled = true;
            } else {
                typeDisabled = false;
                if (!kidsModeNotDisabled && !timerDisabled) {
                    Toasty.success(SettingsActivity.this, R.string.score_ranking_reenabled, Toast.LENGTH_SHORT, true).show();
                }

            }
        }

        if (key.equals(KEY_SWITCH_QUICK_MATCH_TIMER) || key.equals(KEY_SWITCH_TIME_TRIALS_TIMER)) {
            if (!sharedPreferences.getBoolean(KEY_SWITCH_QUICK_MATCH_TIMER, false) || !sharedPreferences.getBoolean(KEY_SWITCH_TIME_TRIALS_TIMER, false)) {
                Toasty.error(SettingsActivity.this, R.string.score_ranking_disabled, Toast.LENGTH_SHORT, true).show();
                timerDisabled = true;
            } else {
                timerDisabled = false;
                if (!kidsModeNotDisabled && !typeDisabled) {
                    Toasty.success(SettingsActivity.this, R.string.score_ranking_reenabled, Toast.LENGTH_SHORT, true).show();
                }
            }
        }
    }

}
