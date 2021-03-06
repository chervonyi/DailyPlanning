package com.general.dailyplanning.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.general.dailyplanning.R;
import com.general.dailyplanning.components.Converter;
import com.general.dailyplanning.components.DateComposer;
import com.general.dailyplanning.components.TaskService;
import com.general.dailyplanning.data.DataManipulator;
import com.general.dailyplanning.data.Task;
import com.general.dailyplanning.data.Vault;
import com.general.dailyplanning.listeners.MovingToDoListListener;
import com.general.dailyplanning.listeners.SwipeDownDateListener;
import com.general.dailyplanning.listeners.TouchDateListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    // Consts
    private final int CUT_TASK_LENGTH = 30;

    // Vars
    private ArrayList<Task> tasks = new ArrayList<>();
    private ArrayList<String> toDoList = new ArrayList<>();
    private ArrayList<MovingToDoListListener> listeners = new ArrayList<>();

    // Views
    private TextView dateView;
    private Button buttonSave;
    private TextView manualView;
    private LinearLayout mainLayout;
    private TextView taskView;
    private Button buttonAddNew;

    // States
    private static boolean isRunning = false;

    // Clock
    private final Handler timeClock = new Handler();
    private Runnable minChange = new Runnable() {
        public void run() {
            String time = new SimpleDateFormat("HH:mm", Locale.US).format(new Date());
            int seconds = Integer.parseInt(new SimpleDateFormat("ss", Locale.US).format(new Date()));
            String text = time + " " + DateComposer.getDate();
            dateView.setText(text);

            /// Update time every hh:mm:00 second
            timeClock.postDelayed(this, (60 - seconds) * 1000);
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /** PRELOAD **/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Run only in portrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        isRunning = true;
        // Loading data from file
        Vault vault = DataManipulator.loading(this, "data");
        if (vault != null) {
            // Update vault instance
            Vault.setInstance(vault);
        } else {
            vault = Vault.getInstance();
        }


        if (vault.isUsedToday()) {
            // Go to UsingActivity
            Intent intent = new Intent(this, UsingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        tasks = vault.getArray();
        toDoList = vault.getTomorrowArray();

        // Start service on background
        startService(new Intent(this, TaskService.class));

        // Get components
        dateView = findViewById(R.id.textViewDate);
        buttonSave = findViewById(R.id.buttonSave);
        manualView = findViewById(R.id.textViewStart);
        mainLayout = findViewById(R.id.mainLayout);
        taskView = findViewById(R.id.textViewTasks);
        buttonAddNew = findViewById(R.id.buttonAddNewTask);

        // Set size
        setSizes();

        // Set swipeListener for Task Bar
        taskView.setOnTouchListener(new SwipeDownDateListener(this));

        // Set touchListener to show "+" button
        TouchDateListener touchDateListener = new TouchDateListener(this, buttonAddNew);
        dateView.setOnTouchListener(touchDateListener);

        // Update date
        timeClock.postDelayed(minChange, 0);

        // Set touch listener to hide "+" button
        findViewById(R.id.scrollView).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TouchDateListener.hide();
                return false;
            }
        });

        // Feature for stop buggy behavior with scrolling
        findViewById(R.id.scrollViewToDoList).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                for (MovingToDoListListener listener: listeners) {
                    listener.stopPost();
                }

                return false;
            }
        });

        // Filling up TO-DO List
        updateToDoList();

        // Filling up ScrollView with tasks
        updateTasksList();

        // Show or hide "Save" button and Start-Manual
        if (tasks.size() > 0) {
            buttonSave.setVisibility(View.VISIBLE);
            manualView.setVisibility(View.INVISIBLE);
        } else {
            buttonSave.setVisibility(View.INVISIBLE);
            manualView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Fills up TO-DO list with tasks
     */
    @SuppressLint("ClickableViewAccessibility")
    private void updateToDoList() {
        final Context context = this;
        TextView view;
        LinearLayout innerLayout;
        MovingToDoListListener mTDListListener;
        Button buttonDelete;
        Button buttonAdd;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(130, 130);
        params.setMargins(0,-3,0,0);

        Converter converter = new Converter(getWindowManager().getDefaultDisplay());
        LinearLayout scrollLayout = findViewById(R.id.toDoLayout);
        // Clear a body
        if (scrollLayout.getChildCount() > 0) {
            scrollLayout.removeAllViews();
        }

        scrollLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.todo_list_stroke));

        int id = 0;
        String tmp;
        for (String task: toDoList) {
            // Task Block
            view = new TextView(this);
            if (task.length() > CUT_TASK_LENGTH) {
                tmp = task.substring(0, CUT_TASK_LENGTH) + "...";
                view.setText(tmp);
            } else {
                view.setText(task);
            }

            view.setTextSize(18);
            view.setHeight(130);
            view.setWidth(converter.getWidth(1));
            view.setTextColor(getResources().getColor(R.color.fontWhite));
            view.setGravity(Gravity.CENTER);
            view.setPadding(70, 0, 70, 0);
            view.setTypeface(ResourcesCompat.getFont(this, R.font.light)); // Roboto-Light
            mTDListListener = new MovingToDoListListener(this);
            listeners.add(mTDListListener);
            view.setOnTouchListener(mTDListListener);

            innerLayout = new LinearLayout(this);
            innerLayout.setOrientation(LinearLayout.HORIZONTAL);
            innerLayout.setTag("task_" + id++);
            innerLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.todo_task));
            innerLayout.addView(view);

            // Optional buttons:
            buttonDelete = new Button(this);
            buttonDelete.setLayoutParams(params);
            buttonDelete.setBackground(getResources().getDrawable(R.drawable.button_delete_sq));
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout linearLayout = (LinearLayout) v.getParent();
                    int idTask = Integer.parseInt(linearLayout.getTag().toString().substring(5));
                    Vault vault = Vault.getInstance();

                    vault.removeFromToDoList(idTask);

                    // Refresh panel with new data
                    updateToDoList();
                }
            });
            innerLayout.addView(buttonDelete);

            buttonAdd = new Button(this);
            buttonAdd.setLayoutParams(params);
            buttonAdd.setBackground(getResources().getDrawable(R.drawable.button_add_sq));
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout linearLayout = (LinearLayout) v.getParent();
                    int idTask = Integer.parseInt(linearLayout.getTag().toString().substring(5));

                    Vault vault = Vault.getInstance();

                    Intent intent = new Intent(context, CreatingActivity.class);
                    intent.putExtra("type", CreatingActivity.CREATING_FROM_TODO_LIST);
                    intent.putExtra("task", vault.getTomorrowArray().get(idTask));
                    context.startActivity(intent);

                    vault.removeFromToDoList(idTask);
                    //DataManipulator.saving(context,"data", vault);

                    // Refresh panel with new data
                    updateToDoList();
                }
            });
            innerLayout.addView(buttonAdd);

            scrollLayout.addView(innerLayout);
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
            view.setTextColor(getResources().getColor(R.color.fontWhite));
            view.setBackground(ContextCompat.getDrawable(this, R.drawable.task_planning));
            view.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            view.setPadding(70, 40, 70, 40);
            view.setTypeface(ResourcesCompat.getFont(this, R.font.light));
            scrollLayout.addView(view);
        }
    }

    public void onClickAddNewTask (View view) {
        Intent intent = new Intent(this, CreatingActivity.class);
        intent.putExtra("type", CreatingActivity.CREATING_NEW);
        startActivity(intent);
        buttonAddNew.setVisibility(View.INVISIBLE);

    }

    public void onClickSave(View view) {
        Vault.getInstance().setUsedToday(true);
        Vault.getInstance().clearTomorrowTaskList();
        Intent intent = new Intent(this, UsingActivity.class);
        startActivity(intent);
    }

    /**
     * Sets actual size to all components in this Activity using Converter
     */
    private void setSizes() {
        Converter converter = new Converter(getWindowManager().getDefaultDisplay());
        converter.setUnit(1 - 0.078);

        mainLayout.setLayoutParams(converter.getParam(LinearLayout.LayoutParams.MATCH_PARENT,
                converter.getHeight(2.085),
                0,
                -converter.getHeight(1),
                0,
                0));

        findViewById(R.id.topLayout).getLayoutParams().height = converter.getHeight(1);
        findViewById(R.id.bottomLayout).getLayoutParams().height = converter.getHeight(1 - 0.078);
        taskView.setLayoutParams(converter.getParam(LinearLayout.LayoutParams.MATCH_PARENT,
                converter.getHeight(0.083), 0,0 ,0 ,0));
        buttonSave.setLayoutParams(converter.getParam(LinearLayout.LayoutParams.MATCH_PARENT,
                converter.getHeight(0.083), 0,0 ,0 ,0));

    }

    @Override
    public void onDestroy() {
        DataManipulator.saving(this, "data", Vault.getInstance());
        isRunning = false;
        super.onDestroy();
    }

    public static boolean isIsRunning() {
        return isRunning;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}