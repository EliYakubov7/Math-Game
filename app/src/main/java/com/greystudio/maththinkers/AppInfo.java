package com.greystudio.maththinkers;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AppInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ImageView animation_image_frame = findViewById(R.id.ivAnimationKid);
        AnimationDrawable animationDrawable = (AnimationDrawable)animation_image_frame.getDrawable();
        animationDrawable.start();

        ForegroundColorSpan fcs = new ForegroundColorSpan(Color.RED);
        ForegroundColorSpan fcs2 = new ForegroundColorSpan(Color.RED);
        ForegroundColorSpan fcs3 = new ForegroundColorSpan(Color.RED);
        ForegroundColorSpan fcs4 = new ForegroundColorSpan(Color.RED);
        TextView tvCreated, tvCreators;
        SpannableString ss1 = new SpannableString(getString(R.string.created_by));
        SpannableString ss2 = new SpannableString(getString(R.string.creators));
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        StyleSpan boldSpan2 = new StyleSpan(Typeface.BOLD);
        StyleSpan boldSpan3 = new StyleSpan(Typeface.BOLD);
        StyleSpan boldSpan4 = new StyleSpan(Typeface.BOLD);
        tvCreated = findViewById(R.id.tvCreated);
        tvCreators = findViewById(R.id.tvCreators);
        ss1.setSpan(fcs,11,15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss1.setSpan(boldSpan,11,15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ss2.setSpan(fcs,0,1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss2.setSpan(fcs2,4,5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss2.setSpan(fcs3,13,14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss2.setSpan(fcs4,21,22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss2.setSpan(boldSpan,0,1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss2.setSpan(boldSpan2,4,5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss2.setSpan(boldSpan3,13,14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss2.setSpan(boldSpan4,21,22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvCreated.setText(ss1);
        tvCreators.setText(ss2);

    }

    public void back(View view) {
        finish();
    }
}
