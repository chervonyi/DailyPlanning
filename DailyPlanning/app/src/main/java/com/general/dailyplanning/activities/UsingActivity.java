package com.general.dailyplanning.activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.general.dailyplanning.R;
import com.general.dailyplanning.activities.CreatingActivity;
import com.general.dailyplanning.components.Vibrate;
import com.general.dailyplanning.data.DataManipulator;
import com.general.dailyplanning.data.Task;
import com.general.dailyplanning.data.Vault;
import com.general.dailyplanning.listeners.MovingTaskListener;
import com.general.dailyplanning.listeners.Scrolling;
import com.general.dailyplanning.listeners.TouchDateListener;
import com.general.dailyplanning.listeners.TouchSwipeDateListener;

import java.util.ArrayList;

public class UsingActivity extends AppCompatActivity {

    private ArrayList<MovingTaskListener> listeners = new ArrayList<>();

    private ArrayList<Task> tasks = new ArrayList<>();

    @TargetApi(Build.VERSION_CODES.M)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_using);

        RelativeLayout layoutDate = findViewById(R.id.layoutDate);
        ScrollView scrollView = findViewById(R.id.scrollView);

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // if == MotionEvent.ACTION_MOVE) ??
                for (MovingTaskListener listener: listeners) {
                    listener.stopPost();
                }
                return false;
            }
        });

        // Set touch listener to show "+" button
        TouchSwipeDateListener swdl = new TouchSwipeDateListener(this, (Button) findViewById(R.id.buttonAddNewTask));
        layoutDate.setOnTouchListener(swdl);

        // Loading data
        Vault vault = DataManipulator.loading(this, "data");
        if (vault != null) {
            Vault.setInstance(vault);
            tasks = vault.getArray();
        }

        updateTasksList();
    }


    @SuppressLint("ClickableViewAccessibility")
    private void updateTasksList() {
        LinearLayout scrollLayout = findViewById(R.id.scrollLayout);
        if (scrollLayout.getChildCount() > 0) {
            scrollLayout.removeAllViews();
        }

        final Context context = this;
        TextView view;
        LinearLayout innerLayout;
        MovingTaskListener movingTaskListener;
        Button buttonDelete, buttonEdit;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
        params.setMargins(0,10,0,0);

        int id = 0;
        for (Task task: tasks) {
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

                    // Go to CreatingActivity with some flags that this Activity should create task in "Tomorrow TO-DO List"
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

                    // Remove element
                    vault.remove(idTask);

                    // Save vault
                    DataManipulator.saving(context, "data", vault);

                    updateTasksList();
                    // +- Add anim
                }
            });
            innerLayout.addView(buttonDelete);

            scrollLayout.addView(innerLayout);
        }
    }

    public void onClickAddNewTask(View view) {
        Intent intent = new Intent(this, CreatingActivity.class);
        intent.putExtra("type", CreatingActivity.CREATING_NEW);
        startActivity(intent);
        TouchSwipeDateListener.hide();
    }
}
