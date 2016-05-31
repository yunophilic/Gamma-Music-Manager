package com.teamgamma.musicmanagementsystem;

import java.util.*;

/**
 * Class to manage libraries and playlists
 */
public class SongManager {
    private Library m_myLibrary;
    private Library m_externalLibrary;
    private List<Playlist> m_playlists;

    public SongManager(String directoryPath) {
        m_myLibrary = new Library(directoryPath);
        m_externalLibrary = null;
        m_playlists = new ArrayList<>();
    }

    public String getLibraryRootDirPath() {
        return m_myLibrary.getM_rootDir();
    }

    public Library getM_myLibrary() {
        return m_myLibrary;
    }

    public Library getM_externalLibrary() {
        return m_externalLibrary;
    }

    public void setM_externalLibrary(String directoryPath) {
        m_externalLibrary = new Library(directoryPath);
    }

    public boolean addSong(Song songToAdd, Library library) {
        return library.addSong(songToAdd);
    }

    public boolean removeSong(Song songToRemove, Library library) {
        return library.removeSong(songToRemove);
    }

    public List<Song> getSongs(Library library) {
        return library.getM_songList();
    }
}