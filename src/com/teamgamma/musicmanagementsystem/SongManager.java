package com.teamgamma.musicmanagementsystem;

/**
 * Class to manage libraries.
 */
public class SongManager {

    private String m_rootDirectory;

    private Library m_myLibrary;

    public SongManager(String directoryPath){
        m_rootDirectory = directoryPath;
        m_myLibrary = new Library(directoryPath);
    }

    public String getM_rootDirectory() {
        return m_rootDirectory;
    }

    public Library getM_myLibrary() {
        return m_myLibrary;
    }

}
