package com.general.dailyplanning.data;

import android.util.Log;

import java.io.Serializable;

public class Task implements Serializable {
    private int hours;
    private int minutes;
    private String task;

    public Task() {
        hours = 0;
        minutes = 0;
        task = "New task!";
    }

    public Task(int hours, int minutes, String task) {
        this.hours = hours;
        this.minutes = minutes;
        this.task = task;
    }

    public Task(String taskTitle) {
        // Pattern: "12:34 - Reading a book"
        hours = Integer.parseInt(taskTitle.substring(0, 2));
        minutes = Integer.parseInt(taskTitle.substring(3, 5));
        task = taskTitle.substring(8);
    }

    @Override
    public String toString() {
        String sHours = hours < 10 ? "0" + hours : String.valueOf(hours);
        String sMin = minutes < 10 ? "0" + minutes : String.valueOf(minutes);
        return sHours + ":" + sMin + " - " + task;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }
}
