package com.teamgamma.musicmanagementsystem;

import javafx.scene.control.TreeItem;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to manage files in the program.
 */
public class FileManager {
    /**
     * Recursively create tree items from the files in a directory and return a reference to the root item
     * @return TreeItem<String> to the root item
     */
    public static TreeItem<TreeViewItem> generateTreeItems(File file, String dirPath) {
        TreeItem<TreeViewItem> item = new TreeItem<>(
                (file.getAbsolutePath().equals(dirPath)) ? new TreeViewItem(file, true) : new TreeViewItem(file, false)
        );

        File[] children = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getAbsolutePath().endsWith(".mp3");
            }
        });

        if (children != null) {
            for (File child : children) {
                item.getChildren().add(generateTreeItems(child, dirPath)); //recursion here
            }
        }

        return item;
    }

    /**
     * Generate list of Song objects based on path
     * @param pathToDirectory: the directory path
     * @return ArrayList of Song objects
     */
    public static List<Song> generateSongs(String pathToDirectory) {
        List<Song> listOfSongs = new ArrayList<>();

        // Get all music files in the path (including subdirectories)
        File path = new File(pathToDirectory);
        List<File> musicFiles = getMusicFiles(path);

        // Create Song object and add to array list
        for (File musicFile : musicFiles) {
            listOfSongs.add(new Song(musicFile.getAbsolutePath()));
        }

        // For debugging
        /*System.out.println("Number of music files found: " + listOfSongs.size());
        for (Song song: listOfSongs) {
            System.out.println("Song: " + song.getM_songName());
            System.out.println("      " + song.getM_rootDir().getAbsolutePath());
        }*/

        return listOfSongs;
    }

    /**
     * Helper function to find music files in a directory
     * @param path: the directory path
     * @return ArrayList of File objects
     */
    private static List<File> getMusicFiles(File path) {
        List<File> musicFiles = new ArrayList<>();
        File[] files = path.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String[] extensions = new String[]{".mp3"};
                    if (isAccept(file, extensions)) {
                        musicFiles.add(file);
                    }
                } else {
                    musicFiles.addAll(getMusicFiles(file));
                }
            }
        }

        return musicFiles;
    }

    /**
     * Remove file from file system
     * @param fileToRemove: file to be removed
     * @return true if file is removed successfully
     * @throws Exception
     */
    public static boolean removeFile(File fileToRemove) throws Exception {
        return deleteFolderOrFile(fileToRemove);
    }

    private static boolean deleteFolderOrFile(File path) {
        if (path.exists()) {
            if (path.isDirectory()) {
                File[] files = path.listFiles();
                if (files != null) {
                    for (File f : files) {
                        if (f.isDirectory()) {
                            deleteFolderOrFile(f);
                        } else {
                            if (!f.delete()) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return (path.delete());
    }

    /**
     * Copy fileToCopy to destDir
     * @param fileToCopy: File to be copied (one file only)
     * @param destDir: File object with path to destination directory
     * @return true if path of new file destination equals destDir path, false otherwise
     * @throws IOException
     * @throws InvalidPathException
     */
    public static boolean copyFile(File fileToCopy, File destDir) throws IOException, InvalidPathException {
        Path sourceFilePath = fileToCopy.toPath();
        Path destDirPath = destDir.toPath();
        Path destFilePath = destDirPath.resolve(sourceFilePath.getFileName());
        Path resultPath = Files.copy(fileToCopy.toPath(), destFilePath);
        return (resultPath.getParent().equals(destDir.toPath()));
    }

    /**
     * Copy src to dest recursively
     * @param src: File object with path to source directory
     * @param dest: File object with path to destination directory
     * @return true if path of new file destination equals destinationDir path, false otherwise
     * @throws IOException
     * @throws InvalidPathException
     */
    public static boolean copyFilesRecursively(File src, File dest) throws IOException, InvalidPathException {
        if (!copyFile(src, dest)) { //one of the files failed to be copied
            return false;
        }
        File[] children = src.listFiles();
        if (children != null) {
            for (File child : children) {
                File nextDest = new File(dest.toPath() + File.separator + src.getName());
                copyFilesRecursively(child, nextDest);
            }
        }
        return true;
    }

    /**
     * File filter for finding music files
     * @param file
     * @param extensions
     * @return true if file is accepted, false otherwise
     */
    private static boolean isAccept(File file, String[] extensions) {
        for (String extension : extensions) {
            if (file.getName().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

}
