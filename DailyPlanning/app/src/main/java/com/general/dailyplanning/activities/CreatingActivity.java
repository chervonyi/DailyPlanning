package com.general.dailyplanning.activities;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.general.dailyplanning.components.Converter;
import com.general.dailyplanning.components.DateComposer;
import com.general.dailyplanning.data.*;

import com.general.dailyplanning.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreatingActivity extends AppCompatActivity {
    // Constants
    public final static int CREATING_NEW = 0x0000001;
    public final static int EDITING = 0x0000002;
    public final static int CREATING_NEW_ON_TOMORROW = 0x0000003;
    public final static int CREATING_FROM_TODO_LIST = 0x0000004;
    public final static int CREATING_FROM_USING = 0x0000005;

    // Views
    private EditText newTask;
    private Button buttonSelectTime;
    private TextView dateView;
    private Button buttonSave;

    // Extras variables
    private int type;

    // States
    private boolean timeSelected = false;

    // Clock
    private final Handler timeClock = new Handler();
    private Runnable minChange = new Runnable() {
        public void run() {
            String time = new SimpleDateFormat("HH:mm", Locale.US).format(new Date());
            int seconds = Integer.parseInt(new SimpleDateFormat("ss", Locale.US).format(new Date()));
            String text = time + " " + DateComposer.getDate();
            dateView.setText(text);

            // Update time every hh:mm:00 second
            timeClock.postDelayed(this, (60 - seconds) * 1000);
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating);
        // Run only in portrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        dateView = findViewById(R.id.textViewDate);
        newTask = findViewById(R.id.editNewTaskTitle);
        buttonSelectTime = findViewById(R.id.buttonSelectTime);
        buttonSave = findViewById(R.id.buttonSave);


        setSizes();

        newTask.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        // Update date
        timeClock.postDelayed(minChange, 0);

        Intent intent = getIntent();
        type = intent.getIntExtra("type", -1);

        // Upload extras for different activities purpose
        switch (type) {
            case EDITING:
                timeSelected = true;
                buttonSelectTime.setVisibility(View.VISIBLE);
                newTask.setText(intent.getStringExtra("task"));
                break;

            case CREATING_NEW_ON_TOMORROW:
                buttonSave.setText("REMIND TOMORROW");
                break;

            case CREATING_FROM_TODO_LIST:
                buttonSelectTime.setVisibility(View.VISIBLE);
                newTask.setText(intent.getStringExtra("task"));
                break;
        }

        // Hide 'selectTime' button while task have not been entered
        newTask.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    newTask.clearFocus();

                    // Hide keyboard on click 'Done'
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }

                    if (newTask.getText().length() > 0 && type != CreatingActivity.CREATING_NEW_ON_TOMORROW) {
                        buttonSelectTime.setVisibility(View.VISIBLE);
                    } else {
                        buttonSelectTime.setVisibility(View.INVISIBLE);
                    }

                    if (newTask.getText().length() > 0 && type == CreatingActivity.CREATING_NEW_ON_TOMORROW) {
                        buttonSave.setVisibility(View.VISIBLE);
                    } else {
                        buttonSave.setVisibility(View.INVISIBLE);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Display TimePicker and then given time sets to the beginning of task's title
     * @param view
     */
    public void onClickSelectTime(View view) {
        // Default position of time picker
        Calendar mcurrentTime = Calendar.getInstance();
        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        final int minute = mcurrentTime.get(Calendar.MINUTE);
        final Context context = this;

        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                // Check if selected time less than a current one
                if ((selectedHour * 100 + selectedMinute) < (hour * 100 + minute)) {
                    Toast.makeText(context, "Bad time!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String hours = selectedHour < 10 ? "0" + selectedHour : String.valueOf(selectedHour);
                String minutes = selectedMinute < 10 ? "0" + selectedMinute : String.valueOf(selectedMinute);


                String tmp;
                // Pattern: "12:34 - Reading a book"
                if (timeSelected) {
                    tmp = newTask.getText().toString();
                    tmp = tmp.substring(5);
                    tmp = hours + ":" + minutes + tmp;
                } else {
                    buttonSelectTime.setText("CHANGE TIME");
                    timeSelected = true;
                    tmp = hours + ":" + minutes + " - " + newTask.getText().toString();
                }

                newTask.setText(tmp);

                if (timeSelected) {
                    buttonSave.setVisibility(View.VISIBLE);
                } else {
                    buttonSave.setVisibility(View.INVISIBLE);
                }

            }
        }, hour + 1, 0, true);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    /**
     * Saving a new task with Vault into phone's memory
     * @param view
     */
    public void onClickSave(View view) {
        String titleOfTask = newTask.getText().toString();

        // Get current state of Vault
        Vault vault = Vault.getInstance();
        Intent intent;
        switch (type) {
            case CREATING_NEW_ON_TOMORROW:
                intent = new Intent(this, UsingActivity.class);
                vault.addTomorrow(titleOfTask);
                break;

            case EDITING:
            case CREATING_FROM_USING:
                vault.add(new Task(titleOfTask));
                intent = new Intent(this, UsingActivity.class);
                break;

            case CREATING_NEW:
            case CREATING_FROM_TODO_LIST:
                intent = new Intent(this, MainActivity.class);
                vault.add(new Task(titleOfTask));
                break;

            default:
                intent = new Intent(this, MainActivity.class);

        }

        startActivity(intent);
    }

    /**
     * Sets actual size to all components in this Activity using Converter
     */
    private void setSizes() {
        Converter converter = new Converter(getWindowManager().getDefaultDisplay());

        newTask.setLayoutParams(converter.getParam(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,
                       20, 50, 20, 0));

        buttonSelectTime.setLayoutParams(converter.getParam(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,
                0, converter.getHeight(0.05), 0, 0));

        // TODO: Add editView and SelectTimebtn to separate LinearLayout with static height (To fix moving up and down of btnSave)
        buttonSave.setLayoutParams(converter.getParam(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,
                0, converter.getHeight(0.39), 0, 0));
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

