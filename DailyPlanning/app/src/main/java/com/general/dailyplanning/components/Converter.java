package com.general.dailyplanning.components;

import android.graphics.Point;
import android.view.Display;
import android.widget.LinearLayout;

public class Converter {
    private int unitWeight = 0;
    private Display display;
    private int height;

    public Converter (Display display) {
        this.display = display;
        Point size = new Point();
        display.getSize(size);
        height = size.y;
    }

    public void setUnit(double sole) {
        unitWeight = (int) Math.round(sole * height);
    }

    public int getHeight(double weight) {
        return (int) Math.round(weight * unitWeight);
    }


    public LinearLayout.LayoutParams getParam(int width, int height, int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(width, height);
        param.setMargins(left, top, right, bottom);
        return param;
    }
}
