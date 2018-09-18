package com.general.dailyplanning.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.general.dailyplanning.activities.CreatingActivity;
import com.general.dailyplanning.data.DataManipulator;
import com.general.dailyplanning.data.Task;
import com.general.dailyplanning.data.Vault;
import com.general.dailyplanning.listeners.TouchDateListener;
import com.general.dailyplanning.listeners.TouchSwipeDateListener;

import java.util.ArrayList;

public class UsingActivity extends AppCompatActivity implements View.OnTouchListener {

    private ArrayList<Task> tasks = new ArrayList<>();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_using);

        TextView dateView = findViewById(R.id.textViewDate);

        // Set touch listener to show "+" button
        TouchSwipeDateListener swdl = new TouchSwipeDateListener(this, (Button) findViewById(R.id.buttonAddNewTask));
        dateView.setOnTouchListener(swdl);

        // Set touch listener to hide "+" button
        findViewById(R.id.scrollLayout).setOnTouchListener(this);

        // Loading data
        Vault vault = DataManipulator.loading(this, "data");
        if (vault != null) {
            Vault.setInstance(vault);
            tasks = vault.getArray();
        }

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
            view.setTypeface(ResourcesCompat.getFont(this, R.font.light)); // Roboto-Light
            scrollLayout.addView(view);
        }
    }


    public void onClickAddNewTask(View view) {
        Intent intent = new Intent(this, CreatingActivity.class);
        startActivity(intent);
        TouchSwipeDateListener.hide();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        TouchSwipeDateListener.hide();
        return false;
    }
}