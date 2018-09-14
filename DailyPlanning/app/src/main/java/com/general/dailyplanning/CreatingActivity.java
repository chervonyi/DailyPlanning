package com.general.dailyplanning;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class CreatingActivity extends AppCompatActivity {

    private EditText newTask;
    private Button buttonSelectTime;

    private boolean timeSelected = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating);

        newTask = findViewById(R.id.editNewTaskTitle);
        buttonSelectTime = findViewById(R.id.buttonSelectTime);

        newTask.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    newTask.clearFocus();

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    if (newTask.getText().length() > 0) {
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

    public void vibrate(long milliSeconds) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(milliSeconds,VibrationEffect.DEFAULT_AMPLITUDE));
        }else{
            //deprecated in API 26
            v.vibrate(milliSeconds);
        }
    }

    public void onClickSelectTime(View view) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String hours = selectedHour == 0 ? "00" : String.valueOf(selectedHour);
                String minutes = selectedMinute == 0 ? "00" : String.valueOf(selectedMinute);

                String tmp = "";
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
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public void onClickSave(View view) {

    }
}

