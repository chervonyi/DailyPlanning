package com.general.dailyplanning.listeners;

import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.LinearLayout;

import com.general.dailyplanning.activities.MainActivity;

public class TouchDateListener implements View.OnTouchListener {
    private int posX = 0;
    private static boolean isHidden = true;
    private final int LEFT_SCOPE = 150;
    private final int RIGHT_SCOPE = 550;

    public MainActivity mainActivity;
    private static Button buttonAdd;

    public TouchDateListener(MainActivity mainActivity, Button buttonAdd) {
        posX = -1;
        this.mainActivity = mainActivity;
        this.buttonAdd = buttonAdd;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (buttonAdd.getVisibility() == View.INVISIBLE) {
                buttonAdd.setVisibility(View.VISIBLE);

                Animation anim = new ScaleAnimation(
                        0, 1f, // Start and end values for the X axis scaling
                        0, 1, // Start and end values for the Y axis scaling
                        Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                        Animation.RELATIVE_TO_SELF, 0f); // Pivot point of Y scaling
                anim.setFillAfter(false); // Needed to keep the result of the animation
                anim.setDuration(300);
                buttonAdd.startAnimation(anim);

                isHidden = false;
            } else {
                hide();
            }
        }
        return true;
    }

    public static void hide() {
       if (!isHidden) {
           isHidden = true;
           Animation anim = new ScaleAnimation(
                   1, 0, // Start and end values for the X axis scaling
                   1, 0, // Start and end values for the Y axis scaling
                   Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                   Animation.RELATIVE_TO_SELF, 0f); // Pivot point of Y scaling
           anim.setFillAfter(false); // Needed to keep the result of the animation
           anim.setDuration(300);
           buttonAdd.startAnimation(anim);

           buttonAdd.setVisibility(View.INVISIBLE);
       }
    }

}
