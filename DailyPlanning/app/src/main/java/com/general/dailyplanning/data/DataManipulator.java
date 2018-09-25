package com.general.dailyplanning.data;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DataManipulator {
    /**
     * Saving serializable object into phone's memory
     * @param context - required
     * @param fileName - name of file which will be saved data
     * @param vault - serializable object
     */
    public static void saving(Context context, String fileName, Vault vault) {
        FileOutputStream fos;
        ObjectOutputStream os;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            os = new ObjectOutputStream(fos);

            os.writeObject(vault);

            os.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loading serializable object from phone's memory
     * @param context - required
     * @param fileName - name of file which has been saved
     * @return serializable object with data that contains in ArrayList
     */
    public static Vault loading(Context context, String fileName) {
        ObjectInputStream is = null;

        Vault vault = null;

        try {
            FileInputStream fis = context.openFileInput(fileName);
            is = new ObjectInputStream(fis);

            vault = (Vault) is.readObject();

            is.close();
            fis.close();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        return vault;
    }
}
