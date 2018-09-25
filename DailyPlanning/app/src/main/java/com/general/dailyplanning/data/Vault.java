package com.general.dailyplanning.data;

import java.io.Serializable;
import java.util.ArrayList;

public class Vault implements Serializable {
    // Instance of current class
    private static Vault instance = new Vault();

    // Task-List
    private ArrayList<Task> array;

    private Vault() {
        array = new ArrayList<>();
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
        while(i < size) {
            curr = array.get(i);
            if (makeNum(h, m) < makeNum(curr.getHours(), curr.getMinutes())) {
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
    private int makeNum (int h, int m) {
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

    // Setter
    // Self made modification of Singleton which allows update unique instance from phone's file
    public static void setInstance(Vault vault) {
        instance = vault;
    }

    // Getter
    public ArrayList<Task> getArray() {
        return array;
    }

    // Getter
    public static Vault getInstance() {
        return instance;
    }
}
