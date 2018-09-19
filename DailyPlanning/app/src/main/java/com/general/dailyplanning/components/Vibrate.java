package com.general.dailyplanning.components;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class Vibrate {
    public static void vibrate(Activity activity, long milliSeconds) {
//        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Vibrator v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(milliSeconds,VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(milliSeconds);
        }
    }
}
