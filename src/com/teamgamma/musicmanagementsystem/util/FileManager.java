package com.teamgamma.musicmanagementsystem.util;

import com.sun.jna.platform.FileUtils;
import com.teamgamma.musicmanagementsystem.model.Song;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to manage files in the program.
 */
public class FileManager {
    private static final String[] extensions = new String[]{".mp3"}; //edit this if later support more file types.

    /**
     * Generate list of Song objects based on path
     *
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
            listOfSongs.add(new Song(musicFile));
        }

        return listOfSongs;
    }

    /**
     * Helper function to find music files in a directory
     *
     * @param path: the directory path
     * @return ArrayList of File objects
     */
    private static List<File> getMusicFiles(File path) {
        List<File> musicFiles = new ArrayList<>();
        File[] files = path.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    if (isAccept(file)) {
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
     * Move file to the destDir
     *
     * @param fileToMove: File to be moved
     * @param destDir:    File object with path to destination directory
     * @throws IOException If failed to move file
     */
    public static void moveFile(File fileToMove, File destDir) throws IOException {
        Path sourceFilePath = fileToMove.toPath();
        Path destDirPath = destDir.toPath();
        Path destFilePath = destDirPath.resolve(sourceFilePath.getFileName());
        Path resultPath = Files.move(fileToMove.toPath(), destFilePath);
        resultPath.getParent().equals(destDir.toPath());
    }

    /**
     * Move to trash if trash exist in OS, else remove from file system
     *
     * @param fileToRemove: file to be removed
     * @return true if file is removed successfully
     * @throws IOException
     */
    public static boolean removeFile(File fileToRemove) throws IOException {
        FileUtils fileUtils = FileUtils.getInstance();
        if (fileUtils.hasTrash()) {
            fileUtils.moveToTrash( new File[]{fileToRemove} );
            return true;
        }
        else {
            return deleteFolderOrFile(fileToRemove);
        }
    }

    /**
     * Delete a whole folder or a file
     *
     * @param path File with path of the folder/file to delete
     * @return True if delete was successful, false otherwise
     */
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
     *
     * @param fileToCopy: File to be copied (one file only)
     * @param destDir:    File object with path to destination directory
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
     * Export a file to a destination
     *
     * @param file:     File to export
     * @param dest:     File object with path to destination directory
     * @param prefix:   Prefix to be added to file name
     * @return true if successful export
     * @throws IOException
     * @throws InvalidPathException
     */
    public static boolean exportFile(File file, File dest, String prefix) throws IOException, InvalidPathException {
        if (!copyFile(file, dest)) { //one of the files failed to be copied
            return false;
        }

        // Resolve file name
        File resultFile = new File(dest.getAbsolutePath() + File.separator + file.getName());
        String resultName = resultFile.getName();
        String regex = "^(\\p{Digit}.*?)(?=\\p{Alpha})";
        resultName = resultName.replaceFirst(regex, "");

        // Rename file
        String targetName = resultFile.getParent() + File.separator + prefix + "_" + resultName;
        File targetFile = new File(targetName);
        Path result = Files.move(resultFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return result.toFile().equals(targetFile);
    }

    /**
     * Copy src to dest recursively
     *
     * @param src:  File object with path to file or directory to be copied
     * @param dest: File object with path to destination directory
     * @return true if path of new file destination equals destinationDir path, false otherwise
     * @throws IOException
     * @throws InvalidPathException
     */
    public static boolean copyFilesRecursively(File src, File dest) throws IOException, InvalidPathException {
        assert dest.isDirectory();
        if (src.isDirectory() && src.equals(dest)) {
            throw new IOException("Cannot copy a directory to itself!");
        } else if (src.isDirectory() && dest.getAbsolutePath().contains(src.getAbsolutePath() + File.separator)) {
            throw new IOException("Cannot copy a directory into its subfolder!");
        }

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
     *
     * @param file file to check
     * @return true if file is accepted, false otherwise
     */
    public static boolean isAccept(File file) {
        for (String extension : extensions) {
            if (file.getName().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

}
