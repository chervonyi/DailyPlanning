package com.general.dailyplanning.activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import com.general.dailyplanning.listeners.MovingTaskListener;
import com.general.dailyplanning.listeners.TouchSwipeDateListener;

import java.util.ArrayList;

public class UsingActivity extends AppCompatActivity {

    private boolean taskOpened = false;

    private ArrayList<MovingTaskListener> listeners = new ArrayList<>();

    private ArrayList<Task> tasks = new ArrayList<>();

    @TargetApi(Build.VERSION_CODES.M)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_using);

        findViewById(R.id.scrollView).setOnTouchListener(new View.OnTouchListener() {
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

        // Set touch listener to show "+" button
        TouchSwipeDateListener swdl = new TouchSwipeDateListener(this, (Button) findViewById(R.id.buttonAddNewTask));
        findViewById(R.id.layoutDate).setOnTouchListener(swdl);

        // Loading data
        Vault vault = DataManipulator.loading(this, "data");
        if (vault != null) {
            Vault.setInstance(vault);
            tasks = vault.getArray();
        }

        // Filling up taskList with some information
        updateTaskList();
    }

    /**
     * Updating task-list with tasks that were saved in memory.
     */
    @SuppressLint("ClickableViewAccessibility")
    private void updateTaskList() {
        final Context context = this;
        TextView view;
        LinearLayout innerLayout;
        MovingTaskListener movingTaskListener;
        Button buttonDelete, buttonEdit;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
        params.setMargins(0,10,0,0);

        LinearLayout scrollLayout = findViewById(R.id.scrollLayout);
        // Clear a body
        if (scrollLayout.getChildCount() > 0) {
            scrollLayout.removeAllViews();
        }

        int id = 0;
        for (Task task: tasks) {
            // Task Block
            view = new TextView(this);
            view.setText(task.toString());
            view.setBackgroundColor(getResources().getColor(R.color.backgroundGrey));
            view.setTextSize(18);
            view.setHeight(150);
            view.setWidth(720);
            view.setTextColor(getResources().getColor(R.color.fontWhite));
            view.setBackground(ContextCompat.getDrawable(this, R.drawable.task_planning));
            view.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            view.setPadding(70, 0, 70, 0);
            view.setTypeface(ResourcesCompat.getFont(this, R.font.light)); // Roboto-Light
            movingTaskListener = new MovingTaskListener(this);
            listeners.add(movingTaskListener);
            view.setOnTouchListener(movingTaskListener);

            innerLayout = new LinearLayout(this);
            innerLayout.setOrientation(LinearLayout.HORIZONTAL);
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

                    // Save vault
                    DataManipulator.saving(context, "data", vault);

                    updateTaskList();
                    // TODO: Add anim
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
        intent.putExtra("type", CreatingActivity.CREATING_NEW);
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
}
