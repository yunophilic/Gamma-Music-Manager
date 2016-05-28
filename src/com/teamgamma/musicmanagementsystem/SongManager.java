package com.teamgamma.musicmanagementsystem;

import java.util.ArrayList;

/**
 * Class to manage libraries.
 */
public class SongManager {
    private Library m_myLibrary;
    private Library m_externLibrary;
    private List<PlayList> m_playLists;

    public SongManager(String directoryPath){
        m_myLibrary = new Library(directoryPath);
        m_externLibrary = null;
        m_playLists = new ArrayList<PlayList>();
    }

    public String getLibraryRootDirPath() {
        return m_myLibrary.getM_rootDir();
    }

    public Library getM_myLibrary() {
        return m_myLibrary;
    }

    public Library getM_externLibrary() {
        return m_externLibrary;
    }

    public void setM_externLibrary(String directoryPath) {
        m_externLibrary = new Library(directoryPath);
    }

    public boolean addSong(String songPath) {
        return false;
    }

    public boolean removeSong(String songPath) {
        for(Song s : m_myLibrary.getM_songList()) {
            if(s.getM_file().getAbsolutePath().equals(songPath)) {
                return m_myLibrary.removeSong(s);
            }
        }
        return false;
    }
}
