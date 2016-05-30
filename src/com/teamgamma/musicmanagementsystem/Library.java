package com.teamgamma.musicmanagementsystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.util.List;

/**
 * Class to maintain a library in a system.
 */
public class Library {
    private List<Song> m_songList;
    private String m_rootDir;

    /**
     * Constructor
     * @param folderPath
     */
    public Library(String folderPath) {
        m_rootDir = folderPath;
        m_songList = FileManager.generateSongs(folderPath);
    }

    /**
     * Add Song object to Library
     * @param songToAdd
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

    public Song getSong(String songName) {
        for (Song song : m_songList) {
            if (song.getM_songName().equals(songName)) {
                return song;
            }
        }
        return null;
    }

    public Song getSong(Song song) {
        if (m_songList.contains(song)) {
            int index = m_songList.indexOf(song);
            return m_songList.get(index);
        }
        return null;
    }

    public List<Song> getM_songList() {
        return m_songList;
    }

    public String getM_rootDir() {
        return m_rootDir;
    }
}
