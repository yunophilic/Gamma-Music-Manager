package com.teamgamma.musicmanagementsystem;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;

/**
 * Class to save state for the application.
 */
public class PersistentStorage {
    private File m_savedPaths;
    private File m_savedLibraries;

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
    public boolean isThereSavedState(String fileName) {
        // Can change directory for saved files
        return new File("src" + File.separator + "com" + File.separator +
                "teamgamma" + File.separator + "musicmanagementsystem" + File.separator +
                fileName).exists();
    }

    /**
     * Function to create .txt file to save directories
     *
     * @return true if something is saved
     */
    public boolean createFileDirectories() {
        // Can change directory for saved files
        m_savedPaths = new File("src" + File.separator + "com" + File.separator +
                "teamgamma" + File.separator + "musicmanagementsystem" + File.separator +
                "directories.txt");

        return isThereSavedState("directories.txt");
    }

    /**
     * Function to create .txt file to save library names
     *
     * @return true if something is saved
     */
    public boolean createFileLibraries() {
        // Can change directory for saved files
        m_savedLibraries = new File("src" + File.separator + "com" + File.separator +
                "teamgamma" + File.separator + "musicmanagementsystem" + File.separator +
                "libraries.txt");

        return isThereSavedState("libraries.txt");
    }

    /**
     * Updates file by adding a new directory
     *
     * @param directoryToSave
     */
    public void updatePersistentStorageDirectory(String directoryToSave) {
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
     * Updates file by adding a new library
     *
     * @param libraryToSave
     */
    public void updatePersistentStorageLibrary(String libraryToSave) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(m_savedLibraries, true));
            writer.println(libraryToSave);
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
    public void getPersistentStorageDirectory(String directoryToRead) {
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

    /**
     * Reads file and finds specified library name
     *
     * @param libraryToRead
     */
    public void getPersistentStorageLibrary(String libraryToRead) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(m_savedLibraries));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(libraryToRead)) {
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