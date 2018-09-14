package com.general.dailyplanning;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView taskView = findViewById(R.id.textViewTasks);
        TextView dateView = findViewById(R.id.textViewDate);

        SwipeListener swipeListener = new SwipeListener(this);
        taskView.setOnTouchListener(swipeListener);

        TouchDateListener touchDateListener = new TouchDateListener(this, (Button) findViewById(R.id.buttonAddNewTask));
        dateView.setOnTouchListener(touchDateListener);

    }

    public void vibrate(long milliSeconds) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(milliSeconds,VibrationEffect.DEFAULT_AMPLITUDE));
        }else{
            //deprecated in API 26
            v.vibrate(milliSeconds);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        TouchDateListener.hide();
        return true;
    }

    public void onClickAddNewTask (View view) {
        Intent intent = new Intent(this, CreatingActivity.class);
        startActivity(intent);
        TouchDateListener.hide();
    }
}
