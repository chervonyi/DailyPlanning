package com.general.dailyplanning.activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import com.general.dailyplanning.R;
import com.general.dailyplanning.components.Converter;
import com.general.dailyplanning.components.DateComposer;
import com.general.dailyplanning.components.TaskService;
import com.general.dailyplanning.data.DataManipulator;
import com.general.dailyplanning.data.Task;
import com.general.dailyplanning.data.Vault;
import com.general.dailyplanning.listeners.MovingTaskListener;
import com.general.dailyplanning.listeners.TouchSwipeDateListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class UsingActivity extends AppCompatActivity {
    // Vars
    private ArrayList<MovingTaskListener> listeners = new ArrayList<>();
    private ArrayList<Task> tasks = new ArrayList<>();
    private Context context;

    // Views
    private TextView dateView;
    private TextView buttonRemind;
    private RelativeLayout layoutDate;
    private ScrollView scrollView;
    private Button buttonAddNewTask;
    private LinearLayout scrollLayout;

    // States
    private boolean taskOpened = false;

    // Notification's vars
    private NotificationCompat.Builder builder;
    private Notification notification;
    private NotificationManager notificationManager;
    private int id = 1;

    // Clock
    private final Handler timeClock = new Handler();
    private Runnable minChange = new Runnable() {
        public void run() {
            String time = new SimpleDateFormat("HH:mm", Locale.US).format(new Date());
            int h = Integer.parseInt(time.substring(0, 2));
            int m = Integer.parseInt(time.substring(3, 5));
            int seconds = Integer.parseInt(new SimpleDateFormat("ss", Locale.US).format(new Date()));
            String text = time + " " + DateComposer.getDate();
            dateView.setText(text);
            boolean removed = false;

            Vault vault = Vault.getInstance();

            Task task;

            while((task = vault.pushTimeLine(h, m)) != null) {

                builder = new NotificationCompat.Builder(context)
                    .setContentTitle(task.toString().substring(0, 5))
                    .setContentText(task.getTask())
                    .setSmallIcon(R.mipmap.small_icon_f)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                            R.mipmap.planning_icon_f))
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

                notification = builder.build();
                notificationManager.notify(id++, notification);

                removed = true;
            }

            if (removed) {
                recreate();
            }

            // Update time every hh:mm:00 second
            timeClock.postDelayed(this, (60 - seconds) * 1000);
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_using);
        // Run only in portrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        context = this;
        notificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);

        layoutDate = findViewById(R.id.layoutDate);
        dateView = findViewById(R.id.textViewDate);
        buttonRemind = findViewById(R.id.buttonRemind);
        scrollView = findViewById(R.id.scrollView);
        buttonAddNewTask = findViewById(R.id.buttonAddNewTask);
        scrollLayout = findViewById(R.id.scrollLayout_using);

        setSizes();

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Hide "+" button
                TouchSwipeDateListener.hide();

                // Feature for stop buggy behavior
                for (MovingTaskListener listener: listeners) {
                    listener.stopPost();
                    listener.translateBack();
                }
                return false;
            }
        });

        // Update date
        timeClock.postDelayed(minChange, 0);

        // Set touch listener to show "+" button
        TouchSwipeDateListener swdl = new TouchSwipeDateListener(this, buttonAddNewTask);
        layoutDate.setOnTouchListener(swdl);
    }

    /**
     * Updating task-list with tasks that were saved in memory.
     */
    @SuppressLint("ClickableViewAccessibility")
    private void updateTaskList() {
        TextView view;
        LinearLayout innerLayout;
        MovingTaskListener movingTaskListener;
        Button buttonDelete, buttonEdit;

        tasks = Vault.getInstance().getArray();

        Converter converter = new Converter(getWindowManager().getDefaultDisplay());

        // Clear a body

        if (scrollLayout.getChildCount() > 0) {
            scrollLayout.removeAllViews();
        }


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(converter.getWidth(0.14), LinearLayout.LayoutParams.WRAP_CONTENT);

        int id = 0;
        for (Task task: Vault.getInstance().getArray()) {
            // Task Block
            view = new TextView(this);
            view.setText(task.toString());
            view.setLayoutParams(new LinearLayout.LayoutParams(converter.getWidth(1), LinearLayout.LayoutParams.WRAP_CONTENT));
            view.setBackgroundColor(getResources().getColor(R.color.backgroundGrey));
            view.setTextSize(18);
            view.setTextColor(getResources().getColor(R.color.fontWhite));
            view.setBackground(ContextCompat.getDrawable(this, R.drawable.task_planning));
            view.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            view.setPadding(70, 40, 70, 40);
            view.setTypeface(ResourcesCompat.getFont(this, R.font.light)); // Roboto-Light
            movingTaskListener = new MovingTaskListener(this);
            listeners.add(movingTaskListener);
            view.setOnTouchListener(movingTaskListener);

            innerLayout = new LinearLayout(this);
            innerLayout.setOrientation(LinearLayout.HORIZONTAL);
            innerLayout.setGravity(Gravity.CENTER_VERTICAL);
            innerLayout.setTag("task_" + id++);
            innerLayout.addView(view);

            // Optional buttons:
            buttonEdit = new Button(this);
            buttonEdit.setLayoutParams(params);
            buttonEdit.setBackground(getResources().getDrawable(R.drawable.button_edit));
            buttonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout linearLayout = (LinearLayout) v.getParent();
                    int idTask = Integer.parseInt(linearLayout.getTag().toString().substring(5));
                    Vault vault = Vault.getInstance();

                    Task selectedTask = vault.get(idTask);

                    Intent intent = new Intent(context, CreatingActivity.class);

                    intent.putExtra("type", CreatingActivity.EDITING);
                    intent.putExtra("task", selectedTask.toString());

                    context.startActivity(intent);
                }
            });
            innerLayout.addView(buttonEdit);

            buttonDelete = new Button(this);
            buttonDelete.setLayoutParams(params);
            buttonDelete.setBackground(getResources().getDrawable(R.drawable.button_delete));
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout linearLayout = (LinearLayout) v.getParent();
                    // Get id from Tag. E-g: task_12 => id = 12
                    int idTask = Integer.parseInt(linearLayout.getTag().toString().substring(5));

                    Vault vault = Vault.getInstance();

                    // Remove necessary element
                    vault.remove(idTask);

                    updateTaskList();
                }
            });
            innerLayout.addView(buttonDelete);

            scrollLayout.addView(innerLayout);
        }
    }

    /**
     * Start new activity with option of creating a new task
     * @param view
     */
    public void onClickAddNewTask(View view) {
        Intent intent = new Intent(this, CreatingActivity.class);
        intent.putExtra("type", CreatingActivity.CREATING_FROM_USING);
        startActivity(intent);
        TouchSwipeDateListener.hide();
    }

    /**
     * Hide all taskBlocks except one which will be received
     * @param exp - one active taskBlock
     */
    public void hideTasks(MovingTaskListener exp) {
        if (taskOpened) {
           for (MovingTaskListener listener: listeners) {
               if (!listener.equals(exp)) {
                   listener.translateBack();
               }
           }
        }
    }

    /**
     * Hide all taskBlocks and set flag about it
     */
    public void hideAllTasks() {
       if (taskOpened) {
           for (MovingTaskListener listener: listeners) {
               listener.translateBack();
           }
           taskOpened = false;
       }
    }

    public void setTaskOpened(boolean b) {
        taskOpened = b;
    }

    /**
     * Sets actual size to all components in this Activity using Converter
     */
    private void setSizes() {
        Converter converter = new Converter(getWindowManager().getDefaultDisplay());

        FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) layoutDate.getLayoutParams();
        params1.width = converter.getWidth(2);
        params1.topMargin = converter.getHeight(0.02);
        layoutDate.setLayoutParams(params1);

        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) dateView.getLayoutParams();
        params2.width = converter.getWidth(1);
        params2.height = converter.getHeight(0.078);
        dateView.setLayoutParams(params2);

        params2 = (RelativeLayout.LayoutParams) buttonRemind.getLayoutParams();
        params2.width = converter.getWidth(1);
        params2.height = converter.getHeight(0.078);
        buttonRemind.setLayoutParams(params2);

        FrameLayout.LayoutParams params3 = (FrameLayout.LayoutParams) scrollView.getLayoutParams();
        params3.topMargin = converter.getHeight(0.15);
        scrollView.setLayoutParams(params3);

        params3 = (FrameLayout.LayoutParams) buttonAddNewTask.getLayoutParams();
        params3.topMargin = converter.getHeight(0.078);
        buttonAddNewTask.setLayoutParams(params3);

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTaskList();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
