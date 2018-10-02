package com.general.dailyplanning.listeners;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.general.dailyplanning.activities.CreatingActivity;
import com.general.dailyplanning.activities.UsingActivity;
import com.general.dailyplanning.components.Converter;
import com.general.dailyplanning.components.Vibrate;

public class TouchSwipeDateListener implements View.OnTouchListener {
    // Constants
    private static final int USUAL_POS = 0;
    private static int SWIPED_POS;
    private int SWIPE_BORDER;

    // Vars
    private static RelativeLayout relativeLayout;
    private static FrameLayout.LayoutParams layoutParams;
    private static int currPosition = USUAL_POS;
    private int downRawX;
    private static Button buttonAdd;
    private static TranslateAnimation anim;
    public UsingActivity usingActivity;

    // States
    private static boolean isHidden = true;
    private static boolean swiped = false;

    public TouchSwipeDateListener(UsingActivity usingActivity, Button buttonAdd) {
        this.usingActivity = usingActivity;
        TouchSwipeDateListener.buttonAdd = buttonAdd;
        Converter converter = new Converter(usingActivity.getWindowManager().getDefaultDisplay());
        SWIPE_BORDER = converter.getWidth(0.28);
        SWIPED_POS = -converter.getWidth(1);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int currX, actualDelta;

        usingActivity.hideAllTasks();
        relativeLayout = (RelativeLayout) view;
        layoutParams = (FrameLayout.LayoutParams) relativeLayout.getLayoutParams();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Get information about a touch
                swiped = false;
                downRawX = (int) event.getRawX();
                break;

            case MotionEvent.ACTION_MOVE:
                // Get the current position
                currX = (int) event.getRawX();

                // Get the length of move
                actualDelta = currX - downRawX;

                // Check if a touch is moving
                if (Math.abs(actualDelta) > 30) {
                    hide();
                    swiped = true;
                }

                // Detect swipe
                if (currPosition == USUAL_POS && actualDelta < -SWIPE_BORDER) {
                    alignTo(SWIPED_POS);
                } else if(currPosition == SWIPED_POS && actualDelta > SWIPE_BORDER) {
                    alignTo(USUAL_POS);
                }
                break;

            case MotionEvent.ACTION_UP:
                if (!swiped) {
                    if (currPosition == USUAL_POS) {
                        // Showing "+" button
                        if (isHidden) {
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
                    } else  {
                        currPosition = USUAL_POS;
                        // Click on "Remind tomorrow" button
                        Intent intent = new Intent(usingActivity, CreatingActivity.class);
                        intent.putExtra("type", CreatingActivity.CREATING_NEW_ON_TOMORROW);
                        usingActivity.startActivity(intent);
                    }
                }

                break;
        }
        return true;
    }

    /**
     * Attaching a block to one side
     * @param side - necessary sude
     */
    private static void alignTo(int side) {
        currPosition = side;
        layoutParams.leftMargin = side;
        relativeLayout.setLayoutParams(layoutParams);
        swiped = true;

        if (side == USUAL_POS) {
            anim = new TranslateAnimation(SWIPED_POS, 0, 0, 0);
        } else {
            anim = new TranslateAnimation(-SWIPED_POS, 0, 0, 0);
        }

        anim.setDuration(200);
        anim.setFillAfter(true);
        relativeLayout.startAnimation(anim);
    }

    /**
     * Hiding the optional button
     */
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
