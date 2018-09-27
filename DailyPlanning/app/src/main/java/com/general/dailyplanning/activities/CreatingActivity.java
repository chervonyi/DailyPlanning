package com.general.dailyplanning.activities;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

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

    // Views
    private EditText newTask;
    private Button buttonSelectTime;

    // Extras variables
    private int type;

    // States
    private boolean timeSelected = false;

    private TextView dateView;

    // Clock
    private final Handler timeClock = new Handler();
    private Runnable minChange = new Runnable() {
        public void run() {
            String time = new SimpleDateFormat("HH:mm", Locale.US).format(new Date());
            String text = time + " " + DateComposer.getDate();
            dateView.setText(text);
            // Update time every 10 sec
            timeClock.postDelayed(this, 10000);
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating);

        dateView = findViewById(R.id.textViewDate);
        newTask = findViewById(R.id.editNewTaskTitle);
        buttonSelectTime = findViewById(R.id.buttonSelectTime);

        // Update date
        timeClock.postDelayed(minChange, 0);

        Intent intent = getIntent();
        type = intent.getIntExtra("type", -1);

        // Upload extras for different activities purpose
        switch (type) {
            case CREATING_NEW:
                break;

            case EDITING:
                timeSelected = true;
                buttonSelectTime.setVisibility(View.VISIBLE);
                newTask.setText(intent.getStringExtra("task"));
                break;

            case CREATING_NEW_ON_TOMORROW:
                Button btn = findViewById(R.id.buttonSave);
                btn.setText("REMIND TOMORROW");
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
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                String hours = selectedHour < 10 ? "0" + selectedHour : String.valueOf(selectedHour);
                String minutes = selectedMinute < 10 ? "0" + selectedMinute : String.valueOf(selectedMinute);

                String tmp;
                // Pattern: "12:34 - Reading a book"
                if (timeSelected) {
                    tmp = newTask.getText().toString();
                    tmp = tmp.substring(5);
                    tmp = hours + ":" + minutes + tmp;
                } else {
                    timeSelected = true;
                    tmp = hours + ":" + minutes + " - " + newTask.getText().toString();
                }
                newTask.setText(tmp);

            }
        }, hour, minute, true);
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
            case CREATING_NEW:
            case EDITING:
                intent = new Intent(this, UsingActivity.class);
                vault.add(new Task(titleOfTask));
                break;

            case CREATING_NEW_ON_TOMORROW:
                intent = new Intent(this, UsingActivity.class);
                vault.addTomorrow(titleOfTask);
                break;

            case CREATING_FROM_TODO_LIST:
                intent = new Intent(this, MainActivity.class);
                vault.add(new Task(titleOfTask));
                break;

            default:
                intent = new Intent(this, MainActivity.class);

        }

        // Save vault
        // TODO: Move Vault's saving into onClose() method
        DataManipulator.saving(this, "data", vault);

        startActivity(intent);
    }
}

