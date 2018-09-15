package com.general.dailyplanning.data;

import java.io.Serializable;
import java.util.ArrayList;

public class Vault implements Serializable{
    private static Vault instance = new Vault();
    private ArrayList<String> arayy;


    public static Vault getInstance() {
        return instance;
    }

    public static void setInstance(Vault vault) {
        instance = vault;
    }

    private Vault() {
        arayy = new ArrayList<>();
    }

    public void add(String newTask) {
        arayy.add(newTask);
    }

    public ArrayList<String> getArray() {
        return arayy;
    }
}
