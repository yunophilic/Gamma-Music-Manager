package com.teamgamma.musicmanagementsystem;

import java.util.ArrayList;

/**
 * Class to manage libraries.
 */
public class SongManager {

    private String m_rootDirectory;

    private Library m_myLibrary;

    private Library m_externalLibrary;

    private ArrayList<PlayList> m_playLists;

    public SongManager(String directoryPath) {
        m_rootDirectory = directoryPath;
        m_myLibrary = new Library(directoryPath);
        m_playLists = new ArrayList<PlayList>();
    }

    public String getM_rootDirectory() {
        return m_rootDirectory;
    }

    public Library getM_myLibrary() {
        return m_myLibrary;
    }




}
