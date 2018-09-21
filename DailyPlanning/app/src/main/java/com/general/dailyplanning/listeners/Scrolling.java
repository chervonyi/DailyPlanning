package com.general.dailyplanning.listeners;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.general.dailyplanning.activities.UsingActivity;

public class Scrolling implements OnTouchListener {
    private final String TAG = "testing";

    private int heightPanel = -1;
    private int heightHeader = 340;

    private int downY, downRawY;
    private LinearLayout linearLayout;
    private LinearLayout.LayoutParams layoutParams;
    private TranslateAnimation anim;

    public UsingActivity usingActivity;

    public Scrolling(UsingActivity usingActivity) {
        this.usingActivity = usingActivity;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int currY, deltaY, actualDelta, move;
        Log.d(TAG, "!!!!!");

        linearLayout = (LinearLayout) view.getParent();
        layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();

        if (heightPanel == -1) {
            heightPanel = linearLayout.getHeight();
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                TouchDateListener.hide();
                downY = (int) event.getY();
                downRawY = (int) event.getRawY();

                break;

            case MotionEvent.ACTION_MOVE:

                // Get current position
                currY = (int) event.getRawY();

                // Get length of move
                deltaY = currY - downY;
                actualDelta = currY - downRawY;

                Log.d(TAG, "deltaY: " + deltaY);
                Log.d(TAG, "actualDelta: " + actualDelta);

                // Scope of swiping
                if (deltaY < 0)
                    deltaY = 0;
                else if (deltaY > heightPanel - heightHeader)
                    deltaY = heightPanel - heightHeader;

                move = -heightPanel + deltaY;

                layoutParams.topMargin = move;
                linearLayout.setLayoutParams(layoutParams);

                break;
        }

        return true;
    }
}


