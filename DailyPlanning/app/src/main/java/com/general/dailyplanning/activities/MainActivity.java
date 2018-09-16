package com.general.dailyplanning.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.general.dailyplanning.R;
import com.general.dailyplanning.data.DataManipulator;
import com.general.dailyplanning.data.Task;
import com.general.dailyplanning.data.Vault;
import com.general.dailyplanning.listeners.SwipeListener;
import com.general.dailyplanning.listeners.TouchDateListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Task> tasks = new ArrayList<>();

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


        // Loading data
        Vault vault = DataManipulator.loading(this, "data");
        if (vault != null) {
            Vault.setInstance(vault);
            tasks = vault.getArray();
        }


        // TODO: Continue from here - Add scrolling panel and fill it from ArrayList<String> tasks
    }

    public void vibrate(long milliSeconds) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(milliSeconds,VibrationEffect.DEFAULT_AMPLITUDE));
        }else{
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
