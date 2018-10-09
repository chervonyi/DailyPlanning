package com.general.dailyplanning.data;

import android.app.NotificationManager;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class Vault implements Serializable {
    // Instance of current class
    private static Vault instance = new Vault();

    // Task-List
    private ArrayList<Task> array;

    // TO-DO List on tomorrow
    private ArrayList<String> tomorrowArray;

    private boolean usedToday;

    private Vault() {
        array = new ArrayList<>();
        tomorrowArray = new ArrayList<>();
        usedToday = false;
    }

    /**
     * Adds a new task at appropriate position.
     * Thus, this list array will always be sorted correctly.
     * @param newTask - instance of Task
     */
    public void add(Task newTask) {
        boolean added = false;
        int h = newTask.getHours();
        int m = newTask.getMinutes();

        int i = 0;
        Task curr;
        int size = array.size();
        int newTaskTime = makeNum(h, m);
        while(i < size) {
            curr = array.get(i);
            if (newTaskTime < makeNum(curr.getHours(), curr.getMinutes())) {
                array.add(i, newTask);
                added = true;
                break;
            }
            i++;
        }

        if (!added) {
            array.add(newTask);
        }
    }

    /**
     * Calculate absolute number using hours and minutes.
     * The purpose: get a number that can be easily compared to others.
     * E-g:
     *     12:05 => 1205
     *     09:00 => 900
     * @param h - hours
     * @param m - minutes
     * @return an absolute number which can be used for sorting
     */
    private int makeNum(int h, int m) {
        return h * 100 + m;
    }

    /**
     * Removes the task from the arrayList by id
     * @param id - a necessary task's id
     */
    public void remove(int id) {
        array.remove(id);
    }

    /**
     * Retunrs the task from the arrayList by id
     * @param id - a necessary task's id
     */
    public Task get(int id) {
        Task task = array.get(id);
        array.remove(id);
        return task;
    }

    /**
     * Removes the task from the TO-DO List by id
     * @param id - a necessary task's id
     */
    public void removeFromToDoList(int id) {
        tomorrowArray.remove(id);
    }

    /**
     * Adds a new task to TO-DO list on tomorrow
     * @param newTask
     */
    public void addTomorrow(String newTask) {
        tomorrowArray.add(newTask);
    }

    // Setter
    // Self made modification of Singleton which allows update unique instance from phone's file
    public static void setInstance(Vault vault) {
        instance = vault;
    }

    // Getter:
    public ArrayList<Task> getArray() {
        return array;
    }

    // Getter:
    public ArrayList<String> getTomorrowArray() {
        return tomorrowArray;
    }


    public static Vault getInstance() {
        return instance;
    }

    /**
     * Returns and removes first overdue task.
     * Should be used in while loop to remove all overdue tasks.
     * @param time - current time in string (Like: "11:24")
     */
    public Task pushTimeLine(String time) {
        int h = Integer.parseInt(time.substring(0, 2));
        int m = Integer.parseInt(time.substring(3, 5));
        int currTime = makeNum(h, m);

        // On time 00:00 reset 'usedToday' flag
        if (currTime == 0) {
            usedToday = false;
        }

        // TODO: Remove loop from it. Just add 1 condition to 1st element in array (using 'get' method)
        Task toReturn;
        for (int i = 0; i < array.size(); i++) {
            toReturn = array.get(i);
            if (currTime >= makeNum(toReturn.getHours(), array.get(i).getMinutes())) {
                array.remove(i);
                return toReturn;
            } else {
                break;
            }
        }
        return null;
    }

    public void setUsedToday(boolean usedToday) {
        this.usedToday = usedToday;
    }

    public boolean isUsedToday() {
        return usedToday;
    }

    public void clearTomorowTaskList() {
        tomorrowArray.clear();
    }
}
