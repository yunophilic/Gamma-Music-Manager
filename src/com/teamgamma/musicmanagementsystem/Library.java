package com.teamgamma.musicmanagementsystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to maintain a library in a system.
 */
public class Library {
    private List<Song> m_songList;
    private File m_rootDir;

    /**
     * Constructor
     * @param folderPath: root path to folder
     */
    public Library(String folderPath) {
        m_rootDir = new File(folderPath);
        m_songList = FileManager.generateSongs(folderPath);
    }

    /**
     * Add Song object to Library
     * @param songToAdd: Song object to add
     * @return true on successful add
     */
    public boolean addSong(Song songToAdd) {
        try {
            return m_songList.add(songToAdd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Remove song from Library and System
     * @param songToRemove: Song object to remove
     * @return true on successful remove
     */
    public boolean removeSong(Song songToRemove) {
        try {
            if (FileManager.removeFile(songToRemove.getM_file())) {
                m_songList.remove(songToRemove);
                return true;
            }
        } catch (NoSuchFileException x) {
            System.err.format("%s: no such" + " file or directory%n", songToRemove.getM_file().getAbsolutePath());
        } catch (DirectoryNotEmptyException x) {
            System.err.format("%s not empty%n", songToRemove.getM_file().getAbsolutePath());
        } catch (IOException x) {
            // File permission problems are caught here.
            System.err.println(x);
        } catch (Exception e) {
            // if any other error occurs
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Copy source Song object to destination path
     * @param songToCopy: Song object to copy
     * @param pathToDest: Directory to copy to
     * @return true on successful copy
     */
    public boolean copySong(Song songToCopy, String pathToDest) {
        try {
            return FileManager.copyFile(songToCopy.getM_file(), new File(pathToDest));
        } catch (InvalidPathException x) {
            System.err.format("Invalid path %s", pathToDest);
        } catch (IOException x) {
            System.err.println(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get Song object in List
     * @param songName: String of Song object name
     * @return Song object if found in Library.
     * Returns null if not found
     */
    public Song getSong(String songName) {
        for (Song song : m_songList) {
            if (song.getM_songName().equals(songName)) {
                return song;
            }
        }
        return null;
    }

    /**
     * Get Song object in List
     * @param file: File object of the song to retrieve
     * @return Song object if found in Library.
     * Returns null if not found
     */
    public Song getSong(File file) {
        for (Song song : m_songList) {
            if (song.getM_file().equals(file)) {
                return song;
            }
        }
        return null;
    }

    /**
     * Get Song object in List
     * @param song: Song object to retrieve
     * @return Song object if found in Library.
     * Returns null if not found
     */
    public Song getSong(Song song) {
        if (m_songList.contains(song)) {
            int index = m_songList.indexOf(song);
            return m_songList.get(index);
        }
        return null;
    }

    /**
     * Get List of Song objects in Library
     * @return List of Song objects in Library
     */
    public List<Song> getM_songList() {
        return m_songList;
    }

    /**
     * Get root directory path of Library
     * @return String to root directory
     */
    public String getM_rootDirPath() {
        return m_rootDir.getAbsolutePath();
    }

    /**
     * Get root directory file of Library
     * @return File of root directory
     */
    public File getM_rootDir() {
        return m_rootDir;
    }

    /**
     * Get all the directories and their sub directories in the root directory (including the root dir itself)
     * @return List<File> of all all the directories and their sub directories
     */
    public List<File> getFolders() {
        return getFoldersRecursively(m_rootDir);
    }

    /**
     * Helper function to recursively trace directories and sub directories
     * @return List<File>
     */
    private static List<File> getFoldersRecursively(File file) {
        List<File> fileList = new ArrayList<>();
        if (file.isDirectory()) {
            fileList.add(file);
        }
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                fileList.addAll(getFoldersRecursively(child));
            }
        }
        return fileList;
    }
}
