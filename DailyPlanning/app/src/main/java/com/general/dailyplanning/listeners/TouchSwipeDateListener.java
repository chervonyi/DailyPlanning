package com.general.dailyplanning.listeners;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.general.dailyplanning.activities.UsingActivity;

public class TouchSwipeDateListener implements View.OnTouchListener {
    private int posX = 0;
    private String TAG = "testing";
    private static boolean isHidden = true;

    private RelativeLayout relativeLayout;
    private FrameLayout.LayoutParams layoutParams;

    private final int LONG_PRESS_TIMEOUT = 500;
    private final int MAX_RADIUS_OF_TOUCH = 50;

    private final int SWIPED_POS = -720;
    private final int USUAL_POS = 0;
    private final int SWIPE_BORDER = 200;

    private int currPosition = USUAL_POS;
    private int downRawX;

    public UsingActivity usingActivity;
    private static Button buttonAdd;
    private TranslateAnimation anim;

    public TouchSwipeDateListener(UsingActivity usingActivity, Button buttonAdd) {
        posX = -1;
        this.usingActivity = usingActivity;
        this.buttonAdd = buttonAdd;
    }

    /*
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (buttonAdd.getVisibility() == View.INVISIBLE) {
            buttonAdd.setVisibility(View.VISIBLE);

            Animation anim = new ScaleAnimation(q2
                    0, 1f, // Start and end values for the X axis scaling
                    0, 1, // Start and end values for the Y axis scaling
                    Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                    Animation.RELATIVE_TO_SELF, 0f); // Pivot point of Y scaling
            anim.setFillAfter(false); // Needed to keep the result of the animation
            anim.setDuration(200);
            buttonAdd.startAnimation(anim);


            isHidden = false;
        }
        return true;
    }

    */


    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int currX, actualDelta;

        relativeLayout = (RelativeLayout) view;
        layoutParams = (FrameLayout.LayoutParams) relativeLayout.getLayoutParams();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // On touch get horizontal pos and absPos
                downRawX = (int) event.getRawX();
                break;

            case MotionEvent.ACTION_MOVE:
                // Get current position
                currX = (int) event.getRawX();

                // Get length of move
                actualDelta = currX - downRawX;

                if (currPosition == USUAL_POS && actualDelta < -SWIPE_BORDER) {
                    currPosition = SWIPED_POS;
                    alignTo(SWIPED_POS);

                } else if(currPosition == SWIPED_POS && actualDelta > SWIPE_BORDER) {
                    currPosition = USUAL_POS;
                    alignTo(USUAL_POS);
                }
                break;
        }

        return true;
    }


    private void alignTo(int side) {
        currPosition = side;
        layoutParams.leftMargin = side;
        relativeLayout.setLayoutParams(layoutParams);

        if (side == USUAL_POS) {
            anim = new TranslateAnimation(SWIPED_POS, 0, 0, 0);
        } else {
            anim = new TranslateAnimation(-SWIPED_POS, 0, 0, 0);
        }

        anim.setDuration(200);
        anim.setFillAfter(true);
        relativeLayout.startAnimation(anim);
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
           anim.setDuration(200);
           buttonAdd.startAnimation(anim);

           buttonAdd.setVisibility(View.INVISIBLE);
       }
    }




}
