package com.general.dailyplanning.components;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class Vibrate {
    /**
     * Universal vibration method
     * @param activity - certain Activity
     * @param milliSeconds - time of vibration
     */
    public static void vibrate(Activity activity, long milliSeconds) {
        Vibrator v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(milliSeconds,VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(milliSeconds);
            }
        }
    }
}
