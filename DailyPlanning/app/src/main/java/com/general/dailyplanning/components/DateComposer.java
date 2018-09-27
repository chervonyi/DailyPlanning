package com.general.dailyplanning.components;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateComposer {
    /**
     * Prepares string of current date;
     * E-g: "Monday, Oct 10"
     * @return String of current date with the pattern.
     */
    public static String getDate() {
        Calendar calendar = Calendar.getInstance();
        String day = String.valueOf(calendar.get(Calendar.DATE));
        String dayOfWeek = new SimpleDateFormat("EEEE", Locale.US).format(new Date());
        int month = calendar.get(Calendar.MONTH) + 1;

        String monthTitle = "";
        switch (month) {
            case 1:  monthTitle = "Jan"; break;
            case 2:  monthTitle = "Feb"; break;
            case 3:  monthTitle = "Mar"; break;
            case 4:  monthTitle = "Apr"; break;
            case 5:  monthTitle = "May"; break;
            case 6:  monthTitle = "Jun"; break;
            case 7:  monthTitle = "Jul"; break;
            case 8:  monthTitle = "Aug"; break;
            case 9:  monthTitle = "Sep"; break;
            case 10: monthTitle = "Oct"; break;
            case 11: monthTitle = "Nov"; break;
            case 12: monthTitle = "Dec"; break;
        }

        return dayOfWeek + ", " + monthTitle + " " + day;
    }
}
