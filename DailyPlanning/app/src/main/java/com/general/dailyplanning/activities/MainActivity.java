package com.general.dailyplanning.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.general.dailyplanning.R;
import com.general.dailyplanning.data.DataManipulator;
import com.general.dailyplanning.data.Task;
import com.general.dailyplanning.data.Vault;
import com.general.dailyplanning.listeners.SwipeListener;
import com.general.dailyplanning.listeners.TouchDateListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private ArrayList<Task> tasks = new ArrayList<>();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView taskView = findViewById(R.id.textViewTasks);
        TextView dateView = findViewById(R.id.textViewDate);

        // Set swipe listener for Task Bar
        SwipeListener swipeListener = new SwipeListener(this);
        taskView.setOnTouchListener(swipeListener);

        // Set touch listener to show "+" button
        TouchDateListener touchDateListener = new TouchDateListener(this, (Button) findViewById(R.id.buttonAddNewTask));
        dateView.setOnTouchListener(touchDateListener);

        // Set touch listener to hide "+" button
        findViewById(R.id.scrollLayout).setOnTouchListener(this);

        // Loading data
        Vault vault = DataManipulator.loading(this, "data");
        if (vault != null) {
            Vault.setInstance(vault);
            tasks = vault.getArray();
        }

        // Filling ScrollView with tasks
        updateTasksList();
    }


    private void updateTasksList() {
        LinearLayout scrollLayout = findViewById(R.id.scrollLayout);
        if (scrollLayout.getChildCount() > 0) {
            scrollLayout.removeAllViews();
        }

        TextView view = null;

        for (Task task: tasks) {
            view = new TextView(this);
            view.setText(task.toString());
            view.setBackgroundColor(getResources().getColor(R.color.backgroundGrey));
            view.setTextSize(18);
            view.setHeight(150);
            view.setTextColor(getResources().getColor(R.color.fontWhite));
            view.setBackground(ContextCompat.getDrawable(this, R.drawable.task_planning));
            view.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            view.setPadding(70, 0, 70, 0);
            view.setTypeface(ResourcesCompat.getFont(this, R.font.light));
            scrollLayout.addView(view);
        }
    }


    // Touch listener to hide "+" button
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

    public void onClickSave(View view) {
        Intent intent = new Intent(this, UsingActivity.class);
        startActivity(intent);
        // TODO Hide this button
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
    public boolean onTouch(View v, MotionEvent event) {
        TouchDateListener.hide();
        return false;
    }
}
