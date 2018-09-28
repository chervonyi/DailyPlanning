package com.general.dailyplanning.components;

import android.content.Context;

public class Converter {
    private static Context context = null;
    public static void setContext(Context context) {
        if (Converter.context == null) {
            Converter.context = context;
        }
    }
    public static int calculatePixels(int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
         return (int) (dp * scale + 0.5f);
    }
}
