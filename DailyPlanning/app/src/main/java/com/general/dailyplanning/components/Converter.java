package com.general.dailyplanning.components;

import android.graphics.Point;
import android.view.Display;
import android.widget.LinearLayout;

public class Converter {
    private int height;
    private int width;

    public Converter (Display display) {
        Point size = new Point();
        display.getSize(size);
        height = size.y;
        width = size.x;
    }

    public void setUnit(double sole) {
        height = (int) Math.round(sole * height);
    }

    public int getHeight(double weight) {
        return (int) Math.round(weight * height);
    }

    public int getWidth(double weight) { return (int) Math.round(weight * width); }

    public LinearLayout.LayoutParams getParam(int width, int height, int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(width, height);
        param.setMargins(left, top, right, bottom);
        return param;
    }
}
