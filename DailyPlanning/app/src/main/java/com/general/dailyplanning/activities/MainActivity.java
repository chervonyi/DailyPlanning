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
import android.widget.Toast;

import com.general.dailyplanning.R;
import com.general.dailyplanning.data.DataManipulator;
import com.general.dailyplanning.data.Task;
import com.general.dailyplanning.data.Vault;
import com.general.dailyplanning.listeners.SwipeDownDateListener;
import com.general.dailyplanning.listeners.TouchDateListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Task> tasks = new ArrayList<>();
    private ArrayList<String> toDoList = new ArrayList<>();


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set swipeListener for Task Bar
        SwipeDownDateListener swipeListener = new SwipeDownDateListener(this);
        findViewById(R.id.textViewTasks).setOnTouchListener(swipeListener);

        // Set touchListener to show "+" button
        TouchDateListener touchDateListener = new TouchDateListener(this, (Button) findViewById(R.id.buttonAddNewTask));
        findViewById(R.id.textViewDate).setOnTouchListener(touchDateListener);

        // Set touch listener to hide "+" button
        findViewById(R.id.scrollView).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TouchDateListener.hide();
                return true;
            }
        });

        // Loading data from file
        Vault vault = DataManipulator.loading(this, "data");
        if (vault != null) {
            Vault.setInstance(vault);
            tasks = vault.getArray();
            toDoList = vault.getTomorrowArray();
        }

        // Filling up TO-DO List
        updateToDoList();

        // Filling up ScrollView with tasks
        updateTasksList();
    }


    /**
     * Fills up TO-DO list with tasks
     */
    private void updateToDoList() {
        LinearLayout toDoLayout = findViewById(R.id.toDoLayout);
        if (toDoLayout.getChildCount() > 0) {
            toDoLayout.removeAllViews();
        }
        toDoLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.todo_list_stroke));

        TextView view;

        for (String task: toDoList) {
            view = new TextView(this);
            view.setText(task);
            view.setBackgroundColor(getResources().getColor(R.color.backgroundGrey));
            view.setTextSize(18);
            view.setHeight(150);
            view.setTextColor(getResources().getColor(R.color.fontWhite));
            view.setBackground(ContextCompat.getDrawable(this, R.drawable.todo_task));
            view.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
            //view.setPadding(0, 10, 0, 10);
            view.setTypeface(ResourcesCompat.getFont(this, R.font.light));
            toDoLayout.addView(view);
        }
    }


    /**
     * Fills up the body of ScrollView with task blocks
     */
    private void updateTasksList() {
        LinearLayout scrollLayout = findViewById(R.id.scrollLayout);
        if (scrollLayout.getChildCount() > 0) {
            scrollLayout.removeAllViews();
        }

        TextView view;

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


    public void onClickAddNewTask (View view) {
        Intent intent = new Intent(this, CreatingActivity.class);
        intent.putExtra("type", CreatingActivity.CREATING_NEW);
        startActivity(intent);
        //TouchDateListener.hide();
    }

    public void onClickSave(View view) {
        Intent intent = new Intent(this, UsingActivity.class);
        startActivity(intent);
        // TODO Hide this button
        // TODO CLEAR TOMORROW_ARRAY IN VAULT
    }
}
