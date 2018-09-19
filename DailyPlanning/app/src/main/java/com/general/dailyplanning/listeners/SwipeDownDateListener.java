package com.general.dailyplanning.listeners;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.general.dailyplanning.activities.MainActivity;

public class SwipeDownDateListener implements OnTouchListener {
    private final int USUAL_POS = 0;
    private final int SWIPED_POS = 1;

    private int HEIGHT_HIDDEN_PANEL = 1180;
    private int SWIPE_LINE_1 = -1000;
    private int SWIPE_LINE_2 = -100;

    private int currPosition = USUAL_POS;
    private int downY, downRawY;
    private boolean moving = true;
    private boolean toReturn = true;
    private LinearLayout linearLayout;
    private LinearLayout.LayoutParams layoutParams;
    private TranslateAnimation anim;

    public MainActivity mainActivity;

    public SwipeDownDateListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int currY, deltaY, actualDelta, move;

        linearLayout = (LinearLayout) view.getParent();
        layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                TouchDateListener.hide();
                downY = (int) event.getY();
                downRawY = (int) event.getRawY();
                moving = true;
                toReturn = true;
                break;

            case MotionEvent.ACTION_MOVE:
                if (moving) {
                    // Get current position
                    currY = (int) event.getRawY();

                    // Get length of move
                    deltaY = currY - downY;
                    actualDelta = currY - downRawY;

                    // Scope of swiping
                    if (deltaY < 0)
                        deltaY = 0;
                    else if (deltaY > HEIGHT_HIDDEN_PANEL)
                        deltaY = HEIGHT_HIDDEN_PANEL;

                    move = -HEIGHT_HIDDEN_PANEL + deltaY;

                    layoutParams.topMargin = move;
                    linearLayout.setLayoutParams(layoutParams);

                    if (currPosition == USUAL_POS && layoutParams.topMargin > SWIPE_LINE_1) {
                        translateTo(SWIPED_POS, 0, -HEIGHT_HIDDEN_PANEL + actualDelta, 200);
                    } else if (currPosition == SWIPED_POS && layoutParams.topMargin < SWIPE_LINE_2) {
                        translateTo(USUAL_POS, -HEIGHT_HIDDEN_PANEL, HEIGHT_HIDDEN_PANEL + SWIPE_LINE_2, 200);
                    }
                }

                break;

            case MotionEvent.ACTION_UP:
                moving = true;
                if (toReturn) {
                    actualDelta = (int) event.getRawY() - downRawY;

                    if (currPosition == USUAL_POS) {
                        if (actualDelta < 0) {
                            actualDelta = 0;
                        }

                        translateTo(currPosition, -HEIGHT_HIDDEN_PANEL, actualDelta, 50);
                    } else {
                        if (actualDelta > 0) {
                            actualDelta = 0;
                        }

                        translateTo(currPosition, 0, actualDelta, 50);
                    }
                }
                break;
        }

        return true;
    }



    private void translateTo(int newPos, int newTopMargin, int fromYDelta, int animDuration ) {
        moving = false;
        toReturn = false;
        currPosition = newPos;

        layoutParams.topMargin = newTopMargin;
        linearLayout.setLayoutParams(layoutParams);
        anim = new TranslateAnimation(0, 0, fromYDelta, 0);
        anim.setDuration(animDuration);
        anim.setFillAfter(true);
        linearLayout.startAnimation(anim);
    }
}


