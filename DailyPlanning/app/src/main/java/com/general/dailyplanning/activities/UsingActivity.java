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

    private boolean isScrolling = false;
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

        // TODO-LIST:
        // [X] Post on GitHub about fixing bug
        // [X] Post on GitHub about a new bug - Swiping on LEFT textViewDate show "+" button
        // [X] Try to fix a new bug
        // [ ] Add EditButton on updateTasksList()
        // [ ] Move to create a method onClick for "Remind tomorrow" (Put extra for adding into another TaskList)

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


        TextView view = null;
        LinearLayout innerLayout = null;
        LinearLayout optionalButtons = null;
        MovingTaskListener movingTaskListener;
        Button buttonDelete = null;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,10,0,0);

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
            innerLayout.addView(view);

            buttonDelete = new Button(this);
            buttonDelete.setLayoutParams(params);
            buttonDelete.setBackground(getResources().getDrawable(R.drawable.button_delete));

            innerLayout.addView(buttonDelete);
            // here create edit button

            scrollLayout.addView(innerLayout);
        }
    }

    public void onClickAddNewTask(View view) {
        Intent intent = new Intent(this, CreatingActivity.class);
        startActivity(intent);
        TouchSwipeDateListener.hide();
    }

    /*
    @Override
    public boolean dispatchTouchEvent(final MotionEvent motionEvent) {
        // Hide "+" button
        TouchSwipeDateListener.hide();
        return super.dispatchTouchEvent(motionEvent);
    }
    */
}
