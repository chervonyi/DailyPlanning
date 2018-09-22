package com.general.dailyplanning.data;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class Vault implements Serializable{
    private static Vault instance = new Vault();
    private ArrayList<Task> array;


    public static Vault getInstance() {
        return instance;
    }

    public static void setInstance(Vault vault) {
        instance = vault;
    }

    private Vault() {
        array = new ArrayList<>();
    }

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

    private int makeNum (int h, int m) {
        return h * 100 + m;
    }

    public ArrayList<Task> getArray() {
        return array;
    }

    public void remove(int id) {
        array.remove(id);
    }

    public Task get(int id) {
        return array.get(id);
    }
}
