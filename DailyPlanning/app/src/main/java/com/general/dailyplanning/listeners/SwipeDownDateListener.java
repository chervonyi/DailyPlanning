package com.general.dailyplanning.listeners;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.general.dailyplanning.activities.MainActivity;
import com.general.dailyplanning.components.Converter;

public class SwipeDownDateListener implements OnTouchListener {
    // Constants
    private final int USUAL_POS = 0x000001;
    private final int SWIPED_POS = 0x000002;
    private int HEIGHT_HIDDEN_PANEL;
    private int SWIPE_LINE_1;
    private int SWIPE_LINE_2;

    // Vars
    private int currPosition = USUAL_POS;
    private LinearLayout linearLayout;
    private LinearLayout.LayoutParams layoutParams;
    private TranslateAnimation anim;
    private int downY, downRawY;
    public MainActivity mainActivity;

    // States
    private boolean moving = true;
    private boolean toReturn = true;

    public SwipeDownDateListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        Converter converter = new Converter(mainActivity.getWindowManager().getDefaultDisplay());
        converter.setUnit(1 - 0.078);
        HEIGHT_HIDDEN_PANEL = converter.getHeight(1);
        SWIPE_LINE_1 = -converter.getHeight(0.85);
        SWIPE_LINE_2 = -converter.getHeight(0.1);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int currY, deltaY, actualDelta, move;

        linearLayout = (LinearLayout) view.getParent();
        layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Get info about a touch
                TouchDateListener.hide();
                downY = (int) event.getY();
                downRawY = (int) event.getRawY();
                moving = true;
                toReturn = true;
                break;

            case MotionEvent.ACTION_MOVE:
                if (moving) {
                    // Get the current position
                    currY = (int) event.getRawY();

                    // Get the length of move
                    deltaY = currY - downY;
                    actualDelta = currY - downRawY;

                    // Scope of swiping
                    if (deltaY < 0) {
                        deltaY = 0;
                    } else if (deltaY > HEIGHT_HIDDEN_PANEL) {
                        deltaY = HEIGHT_HIDDEN_PANEL;
                    }

                    // Calculate the right value of moving
                    move = -HEIGHT_HIDDEN_PANEL + deltaY;

                    // Move layout
                    layoutParams.topMargin = move;
                    linearLayout.setLayoutParams(layoutParams);

                    // Start animation in necessary conditions
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

                    // Returning layout back to appropriate position with animation
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

    /**
     * Attaching the block to necessary position with animation
     * @param newPos - necessary position
     * @param newTopMargin appropriate value of newPos
     * @param fromYDelta - animation's setting
     * @param animDuration - animation's setting
     */
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


