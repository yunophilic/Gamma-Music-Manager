package com.teamgamma.musicmanagementsystem;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.NoSuchFileException;
import java.util.List;

/**
 * Class to maintain a library in a system.
 */
public class Library {

    private List<Song> m_songList;

    private String m_rootDir;

    public Library(String folderPath) {
        m_rootDir = folderPath;
        m_songList = new FileManager().generateSongs(folderPath);
    }

    public boolean addSong(Song songToAdd) {
        return false;
    }

    public boolean removeSong(Song songToRemove) {
        try {
            FileManager fileManager = new FileManager();
            if (fileManager.removeFile(songToRemove.getM_file())) {
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
        } catch(Exception e) {
            // if any other error occurs
            e.printStackTrace();
        }
        return false;
    }

    public boolean copySong(Song songToCopy, String pathToDest) {
        return false;
    }

    public List<Song> getM_songList() {
        return m_songList;
    }

    public String getM_rootDir() {
        return m_rootDir;
    }
}
