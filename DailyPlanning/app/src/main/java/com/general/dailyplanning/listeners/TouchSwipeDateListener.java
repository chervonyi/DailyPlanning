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
import com.general.dailyplanning.components.Vibrate;

public class TouchSwipeDateListener implements View.OnTouchListener {
    private int posX = 0;
    private static boolean isHidden = true;
    private static boolean swiped = false;

    private static RelativeLayout relativeLayout;
    private static FrameLayout.LayoutParams layoutParams;

    private static final int SWIPED_POS = -720;
    private static final int USUAL_POS = 0;
    private  final int SWIPE_BORDER = 200;

    private static int currPosition = USUAL_POS;
    private int downRawX;

    public UsingActivity usingActivity;
    private static Button buttonAdd;
    private static TranslateAnimation anim;

    public TouchSwipeDateListener(UsingActivity usingActivity, Button buttonAdd) {
        this.usingActivity = usingActivity;
        this.buttonAdd = buttonAdd;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        int currX, actualDelta;

        relativeLayout = (RelativeLayout) view;
        layoutParams = (FrameLayout.LayoutParams) relativeLayout.getLayoutParams();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                swiped = false;
                downRawX = (int) event.getRawX();
                break;

            case MotionEvent.ACTION_MOVE:
                // Get current position
                currX = (int) event.getRawX();

                // Get length of move
                actualDelta = currX - downRawX;

                // Check if touch indeed is moving
                if (Math.abs(actualDelta) > 30) {
                    hide();
                    swiped = true;
                }

                if (currPosition == USUAL_POS && actualDelta < -SWIPE_BORDER) {
                    alignTo(SWIPED_POS);
                } else if(currPosition == SWIPED_POS && actualDelta > SWIPE_BORDER) {
                    alignTo(USUAL_POS);
                }
                break;

            case MotionEvent.ACTION_UP:
                if (!swiped) {
                    if (currPosition == USUAL_POS) {
                        // Show "+" button
                        if (buttonAdd.getVisibility() == View.INVISIBLE) {
                            buttonAdd.setVisibility(View.VISIBLE);

                            Animation anim = new ScaleAnimation(
                                    0, 1f, // Start and end values for the X axis scaling
                                    0, 1, // Start and end values for the Y axis scaling
                                    Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                                    Animation.RELATIVE_TO_SELF, 0f); // Pivot point of Y scaling
                            anim.setFillAfter(false); // Needed to keep the result of the animation
                            anim.setDuration(200);
                            buttonAdd.startAnimation(anim);

                            isHidden = false;
                        } else {
                            hide();
                        }
                    } else  {
                        // Go to CreatingActivity with some flags that this Activity should create task in "Tomorrow TO-DO List"
                        Intent intent = new Intent(usingActivity, CreatingActivity.class);
                        // TODO Put some extra
                        usingActivity.startActivity(intent);
                    }
                }

                break;
        }
        return true;
    }

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
