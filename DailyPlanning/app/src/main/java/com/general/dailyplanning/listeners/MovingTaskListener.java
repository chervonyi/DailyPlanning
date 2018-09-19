package com.general.dailyplanning.listeners;

import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.general.dailyplanning.activities.UsingActivity;
import com.general.dailyplanning.components.Vibrate;

public class MovingTaskListener implements OnTouchListener {
    private final int LONG_PRESS_TIMEOUT = 1000;
    private final int MAX_RADIUS_OF_TOUCH = 50;

    private int counter = 0;
    private final int SWIPED_POS = -200;
    private final int USUAL_POS = 0;

    private int currPosition = USUAL_POS;
    private int downX, downRawX;

    private boolean isLong = false;
    private boolean toReturn;
    private LinearLayout linearLayout;
    private LinearLayout.LayoutParams layoutParams;

    public UsingActivity usingActivity;

    public MovingTaskListener(UsingActivity mainActivity) {
        this.usingActivity = mainActivity;
    }

    // Timer for checking Long Touch
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
        int deltaX, actualDelta;

        TranslateAnimation anim;

        linearLayout = (LinearLayout) view.getParent();
        layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // On touch get horizontal pos and absPos
                downX = (int) event.getX();
                downRawX = (int) event.getRawX();

                // Start timer on check if press is long
                handler.postDelayed(mLongPressed, LONG_PRESS_TIMEOUT);
                break;

            case MotionEvent.ACTION_MOVE:
                toReturn = true;
                // Get current position
                currX = (int) event.getRawX();

                // Get length of move
                deltaX = currX - downX;
                actualDelta = currX - downRawX;

                if (isLong) {
                    if (deltaX > USUAL_POS)
                        deltaX = USUAL_POS;
                    else if (deltaX < SWIPED_POS)
                        deltaX = SWIPED_POS;

                    layoutParams.leftMargin = deltaX;
                    linearLayout.setLayoutParams(layoutParams);

                    if (layoutParams.leftMargin == SWIPED_POS) {
                        alignTo(SWIPED_POS);
                    } else if (layoutParams.leftMargin == USUAL_POS) {
                        alignTo(USUAL_POS);
                    }

                    // Check if a long touch was not moved
                } else if (Math.abs(actualDelta) > MAX_RADIUS_OF_TOUCH) {
                    handler.removeCallbacks(mLongPressed);
                }
                break;

            case MotionEvent.ACTION_UP:
                /*
                if (toReturn && isLong) {
                    // Translation back to the usual position
                    actualDelta = (int) event.getRawX() - downRawX;

                    layoutParams.leftMargin = USUAL_POS;
                    linearLayout.setLayoutParams(layoutParams);

                    if (currPosition == USUAL_POS) {
                        if (actualDelta > USUAL_POS) {
                            actualDelta = USUAL_POS;
                        }

                        anim = new TranslateAnimation(actualDelta, 0, 0, 0);
                    } else {
                        currPosition = USUAL_POS;
                        anim = new TranslateAnimation(SWIPED_POS + actualDelta, 0, 0, 0);
                    }

                    anim.setDuration(200);
                    anim.setFillAfter(true);
                    linearLayout.startAnimation(anim);
                }*/

                Log.d("couner", "> " + counter++);
                isLong = false;
                handler.removeCallbacks(mLongPressed);
                break;
        }

        return true;
    }

    private void alignTo(int side) {
        currPosition = side;
        layoutParams.leftMargin = side;
        linearLayout.setLayoutParams(layoutParams);
        toReturn = false;
    }
}


