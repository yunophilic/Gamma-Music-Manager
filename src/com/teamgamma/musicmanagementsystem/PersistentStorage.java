package com.teamgamma.musicmanagementsystem;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to save state for the application.
 */
public class PersistentStorage {

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
        return new File(System.getProperty("user.dir") + File.separator + "db" +
                File.separator + "libraries.txt").exists();
    }

    /**
     * Function to create .txt file to save library names
     *
     * @return true if something is saved
     */
    public boolean createFileLibraries() {
        Path libPath = Paths.get(System.getProperty("user.dir") + File.separator + "db" +
                File.separator + "libraries.txt");
        try {
            Files.createDirectories(libPath.getParent());
            Files.createFile(libPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return isThereSavedState();
    }

    /**
     * Updates file by adding a new library
     *
     * @param libraryToSave
     */
    public void updatePersistentStorageLibrary(String libraryToSave) {
        File findLibFile = new File(System.getProperty("user.dir") + File.separator + "db" +
                File.separator + "libraries.txt");
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(findLibFile, true));
            writer.println(libraryToSave);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates file by removing an existing library
     *
     * @param libraryToRemove
     * @return true if storage update successful
     */
    public boolean removePersistentStorageLibrary(String libraryToRemove) {
        File findLibFile = new File(System.getProperty("user.dir") + File.separator + "db" +
                File.separator + "libraries.txt");
        File tempFile = new File(System.getProperty("user.dir") + File.separator + "db" +
                File.separator + "temp.txt");

        boolean success = false;
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(findLibFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            while ((line = buffer.readLine()) != null) {
                String trimmedLine = line.trim();
                if (trimmedLine.equals(libraryToRemove)) {
                    continue;
                }
                writer.write(line + System.getProperty("line.separator"));
            }
            writer.close();
            buffer.close();
            findLibFile.delete();
            success = tempFile.renameTo(new File(findLibFile.getAbsolutePath()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return success;
    }

    /**
     * Reads file and finds specified library name
     *
     * @return an ArrayList of song directories (String)
     */
    public List<String> getPersistentStorageLibrary() {
        File findLibFile = new File(System.getProperty("user.dir") + File.separator + "db" +
                File.separator + "libraries.txt");
        List<String> songs = new ArrayList<>();
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(findLibFile));
            String line;
            while ((line = buffer.readLine()) != null) {
                songs.add(line);
            }
            buffer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return songs;
    }

}