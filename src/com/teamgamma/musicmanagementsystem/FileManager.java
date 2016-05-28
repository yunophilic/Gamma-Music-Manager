package com.teamgamma.musicmanagementsystem;

import com.sun.org.apache.xpath.internal.SourceTree;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 *  Class to manage files in the program.
 */
public class FileManager {

    /**
     * Generate list of Song objects based on path
     * @param pathToDirectory
     * @return ArrayList of Song objects
     */
    public static List<Song> generateSongs(String pathToDirectory) {
        List<Song> listOfSongs = new ArrayList<>();

        // Get all music files in the path (including subdirectories)
        File path = new File(pathToDirectory);
        List<File> musicFiles = getMusicFiles(path);

        // Create Song object and add to array list
        for (File musicFile: musicFiles) {
            listOfSongs.add(new Song(musicFile.getAbsolutePath()));
        }

        // For debugging
        /*System.out.println("Number of music files found: " + listOfSongs.size());
        for (Song song: listOfSongs) {
            System.out.println("Song: " + song.getM_songName());
            System.out.println("      " + song.getM_file().getAbsolutePath());
        }*/

        return listOfSongs;
    }

    /**
     * Remove file from file system
     * @param fileToRemove
     * @return true if file is removed successfully
     * @throws Exception
     */
    public static boolean removeFile(File fileToRemove) throws Exception {
        return fileToRemove.delete();
    }

    /**
     * Copy sourceFile to destinationDir
     * @param sourceFile
     * @param destinationDir: File object with path to destination directory
     * @return true if path of new file destination equals destinationDir path, false otherwise
     * @throws IOException
     * @throws InvalidPathException
     */
    public static boolean copyFile(File sourceFile, File destinationDir) throws IOException, InvalidPathException {
        Path sourceFilePath = sourceFile.toPath();
        Path destDirPath = destinationDir.toPath();
        Path destFilePath = destDirPath.resolve(sourceFilePath.getFileName());
        Path resultPath = Files.copy(sourceFile.toPath(), destFilePath);
        return (resultPath.equals(destinationDir.toPath()));
    }

    /**
     * Helper function to find music files in a directory
     * @param path
     * @return ArrayList of File objects
     */
    private static List<File> getMusicFiles(File path) {
        List<File> musicFiles = new ArrayList<>();
        File[] files = path.listFiles();
        for (File file: files) {
            if (file.isFile()) {
                String[] extensions = new String[] {".mp3"};
                if (isAccept(file, extensions)) {
                    musicFiles.add(file);
                }
            } else {
                musicFiles.addAll(getMusicFiles(file));
            }
        }

        return musicFiles;
    }

    /**
     * File filter for finding music files
     * @param file
     * @param extensions
     * @return true if file is accepted, false otherwise
     */
    private static boolean isAccept(File file, String[] extensions){
        for (String extension: extensions) {
            if (file.getName().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

}
