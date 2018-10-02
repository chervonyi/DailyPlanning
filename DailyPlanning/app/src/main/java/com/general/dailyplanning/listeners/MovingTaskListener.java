package com.general.dailyplanning.listeners;

import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.general.dailyplanning.activities.UsingActivity;
import com.general.dailyplanning.components.Converter;
import com.general.dailyplanning.components.Vibrate;

public class MovingTaskListener implements OnTouchListener {
    // Constants
    private final int LONG_PRESS_TIMEOUT = 500;
    private final int MAX_RADIUS_OF_TOUCH = 50;
    private int SWIPED_POS;
    private final int USUAL_POS = 0;

    private int currPosition = USUAL_POS;
    private int downX, downRawX;

    private boolean isLong = false;
    private boolean toReturn;
    private LinearLayout linearLayout;
    private LinearLayout.LayoutParams layoutParams;
    public UsingActivity usingActivity;

    public MovingTaskListener(UsingActivity usingActivity) {
        this.usingActivity = usingActivity;
        Converter converter = new Converter(usingActivity.getWindowManager().getDefaultDisplay());
        SWIPED_POS = -converter.getWidth(0.28);
    }

    // Timer for checking LongTouch
    private final Handler handler = new Handler();
    private Runnable mLongPressed = new Runnable() {
        public void run() {
            isLong = true;
            Vibrate.vibrate(usingActivity, 50);
        }
    };

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int currX;
        int deltaX;
        int actualDeltaX;
        TranslateAnimation anim;

        // Hide "+" button
        TouchSwipeDateListener.hide();

        linearLayout = (LinearLayout) view.getParent();
        layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // On touch get horizontal pos and absPos
                downX = (int) event.getX();
                downRawX = (int) event.getRawX();

                // Start the timer on check if press is long
                handler.postDelayed(mLongPressed, LONG_PRESS_TIMEOUT);
                break;

            case MotionEvent.ACTION_MOVE:
                toReturn = true;
                // Get the current position
                currX = (int) event.getRawX();

                // Get the length of move
                deltaX = currX - downX;
                actualDeltaX = currX - downRawX;

                if (isLong) {
                    usingActivity.hideTasks(this);
                    // Lock scrolling in ScrollView
                    view.getParent().requestDisallowInterceptTouchEvent(true);

                    // Round the values of deltaX at the edges
                    if (deltaX > USUAL_POS)
                        deltaX = USUAL_POS;
                    else if (deltaX < SWIPED_POS)
                        deltaX = SWIPED_POS;

                    layoutParams.leftMargin = deltaX;
                    linearLayout.setLayoutParams(layoutParams);

                    // Pin a block at the edges
                    if (layoutParams.leftMargin == SWIPED_POS && currPosition == USUAL_POS) {
                        usingActivity.setTaskOpened(true);
                        alignTo(SWIPED_POS);
                    } else if (layoutParams.leftMargin == USUAL_POS && currPosition == SWIPED_POS) {
                        usingActivity.setTaskOpened(false);
                        alignTo(USUAL_POS);
                    }

                    // Check if a long touch was not moved
                } else if (Math.abs(actualDeltaX) > MAX_RADIUS_OF_TOUCH) {
                    handler.removeCallbacks(mLongPressed);
                }
                break;

            case MotionEvent.ACTION_UP:
                if (toReturn && isLong) {
                    // Translation back to the usual position
                    actualDeltaX = (int) event.getRawX() - downRawX;

                    layoutParams.leftMargin = USUAL_POS;
                    linearLayout.setLayoutParams(layoutParams);

                    if (currPosition == USUAL_POS) {
                        if (actualDeltaX > USUAL_POS) {
                            actualDeltaX = USUAL_POS;
                        }
                        anim = new TranslateAnimation(actualDeltaX, 0, 0, 0);
                    } else {
                        currPosition = USUAL_POS;
                        anim = new TranslateAnimation(SWIPED_POS + actualDeltaX, 0, 0, 0);
                    }

                    anim.setDuration(200);
                    anim.setFillAfter(true);
                    linearLayout.startAnimation(anim);
                }

                isLong = false;
                handler.removeCallbacks(mLongPressed);
                // Unlock scrolling in ScrollView
                view.getParent().getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }

        return true;
    }

    /**
     * Feature for stop buggy behavior with scrolling
     */
    public void stopPost() {
        isLong = false;
        handler.removeCallbacks(mLongPressed);
    }

    /**
     * Attaching a block to one side
     * @param side
     */
    private void alignTo(int side) {
        isLong = false;
        currPosition = side;
        layoutParams.leftMargin = side;
        linearLayout.setLayoutParams(layoutParams);
        toReturn = false;
    }

    /**
     * Moves opened taskBlock to usual position with animation
     * @return
     */
    public boolean translateBack() {
        if (layoutParams == null)
            return false;

        if (currPosition == USUAL_POS) {
            return false;
        }
        currPosition = USUAL_POS;
        layoutParams.leftMargin = USUAL_POS;
        linearLayout.setLayoutParams(layoutParams);

        TranslateAnimation anim = new TranslateAnimation(SWIPED_POS, 0, 0, 0);
        anim.setDuration(200);
        anim.setFillAfter(true);
        linearLayout.startAnimation(anim);

        return true;
    }
}


