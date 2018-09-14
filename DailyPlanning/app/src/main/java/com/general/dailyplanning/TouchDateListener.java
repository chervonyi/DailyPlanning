package com.general.dailyplanning;

import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.LinearLayout;

public class TouchDateListener implements View.OnTouchListener {
    private int posX = 0;
    private static boolean isHidden = true;
    private final int LEFT_SCOPE = 150;
    private final int RIGHT_SCOPE = 550;



    public MainActivity mainActivity;
    private static Button buttonAdd;

    private LinearLayout linearLayout;
    private LinearLayout.LayoutParams layoutParams;

    public TouchDateListener(MainActivity mainActivity, Button buttonAdd) {
        posX = -1;
        this.mainActivity = mainActivity;
        this.buttonAdd = buttonAdd;

        linearLayout = (LinearLayout) buttonAdd.getParent();
        layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (buttonAdd.getVisibility() == View.INVISIBLE) {
            posX = (int) event.getRawX();

            if (posX > LEFT_SCOPE && posX < RIGHT_SCOPE) {
                // Move button on touch position
                layoutParams.leftMargin = posX;
                linearLayout.setLayoutParams(layoutParams);

                // Show button
                buttonAdd.setVisibility(View.VISIBLE);

                // Animate appearance
                Animation anim = new ScaleAnimation(
                        0, 1f, // Start and end values for the X axis scaling
                        0, 1, // Start and end values for the Y axis scaling
                        Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                        Animation.RELATIVE_TO_SELF, 0f); // Pivot point of Y scaling
                anim.setFillAfter(false); // Needed to keep the result of the animation
                anim.setDuration(200);
                buttonAdd.startAnimation(anim);

                isHidden = false;
            }
        }
        return true;
    }

    public static void hide() {
       if (!isHidden) {
           buttonAdd.setVisibility(View.INVISIBLE);
       }
    }

}
