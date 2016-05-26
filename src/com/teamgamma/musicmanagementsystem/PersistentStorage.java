package com.teamgamma.musicmanagementsystem;

import java.io.File;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

/**
 * Class to save state for the application.
 */
public class PersistentStorage {

    private File savedPaths;

    public PersistentStorage() {
    }

    /**
     * Function to see if there is something that is saved.
     *
     * @return true if something, false otherwise
     */
    public Boolean isThereSavedState() {
        // Can change directory for saved files
        return new File("src" + File.separator + "com" + File.separator +
                "teamgamma" + File.separator + "musicmanagementsystem" + File.separator +
                "directories.txt").exists();
    }

    public Boolean createFile() {
        // Can change directory for saved files
        savedPaths = new File("src" + File.separator + "com" + File.separator +
                "teamgamma" + File.separator + "musicmanagementsystem" + File.separator +
                "directories.txt");

        return isThereSavedState();
    }

    public void updatePersistentStorage(String directoryToSave) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(savedPaths, true));
            writer.println(directoryToSave);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
       }
    }
}