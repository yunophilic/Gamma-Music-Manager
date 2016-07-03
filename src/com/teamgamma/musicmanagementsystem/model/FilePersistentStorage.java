package com.teamgamma.musicmanagementsystem.model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to save state for the application.
 */
public class FilePersistentStorage {
    private static String RIGHT_FOLDER_FILE_NAME = "rightFolder.txt";

    /**
     * Function to see if there is something that is saved.
     *
     * @return true if something, false otherwise
     */
    public static boolean isRightFolderStateFileExist() {
        return new File(System.getProperty("user.dir") + File.separator + "db" +
                File.separator + RIGHT_FOLDER_FILE_NAME).exists();
    }

    /**
     * Function to create .txt file to save library names
     *
     * @return true if something is saved
     */
    public static boolean createRightFolderFile() {
        Path filePath = Paths.get(System.getProperty("user.dir") + File.separator + "db" +
                File.separator + RIGHT_FOLDER_FILE_NAME);
        try {
            Files.createDirectories(filePath.getParent());
            Files.createFile(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return isRightFolderStateFileExist();
    }

    /**
     * Updates right folder file path, overwrites the previous file path
     *
     * @param rightFolderPath
     */
    public static void updateRightFolder(String rightFolderPath) {
        File rightFolderFile = new File(System.getProperty("user.dir") + File.separator + "db" +
                File.separator + RIGHT_FOLDER_FILE_NAME);
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(rightFolderFile, false));
            writer.println(rightFolderPath);
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
    public static boolean removeRightFolder(String libraryToRemove) {
        File findLibFile = new File(System.getProperty("user.dir") + File.separator + "db" +
                File.separator + RIGHT_FOLDER_FILE_NAME);
        File tempFile = new File(System.getProperty("user.dir") + File.separator + "db" +
                File.separator + "tempRightFolder.txt");

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
     * @return File path as String
     */
    public static String getRightFolder() {
        File findLibFile = new File(System.getProperty("user.dir") + File.separator + "db" +
                File.separator + RIGHT_FOLDER_FILE_NAME);
        String filePath = "";
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(findLibFile));
            String line;
            while ((line = buffer.readLine()) != null) {
                filePath = line;
            }
            buffer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }

}