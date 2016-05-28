package com.teamgamma.musicmanagementsystem;

import java.io.File;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;

/**
 * Class to save state for the application.
 */
public class PersistentStorage {
    private File m_savedPaths;

    /**
     * Class constructor
     */
    public PersistentStorage() {
    }

    /**
     * Function to see if there is something that is saved.
     *
     * @return true if something, false otherwise
     */
    public boolean isThereSavedState() {
        // Can change directory for saved files
        return new File("src" + File.separator + "com" + File.separator +
                "teamgamma" + File.separator + "musicmanagementsystem" + File.separator +
                "directories.txt").exists();
    }

    /**
     * Function to create .txt file to save directories
     *
     * @return true if something is saved
     */
    public boolean createFile() {
        // Can change directory for saved files
        m_savedPaths = new File("src" + File.separator + "com" + File.separator +
                "teamgamma" + File.separator + "musicmanagementsystem" + File.separator +
                "directories.txt");

        return isThereSavedState();
    }

    /**
     * Updates file by adding a new directory
     *
     * @param directoryToSave
     */
    public void updatePersistentStorage(String directoryToSave) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(m_savedPaths, true));
            writer.println(directoryToSave);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads file and finds specified directory
     *
     * @param directoryToRead
     */
    public void getPersistentStorage(String directoryToRead) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(m_savedPaths));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(directoryToRead)) {
                    System.out.println(line);
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}