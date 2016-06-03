package com.teamgamma.musicmanagementsystem;

import java.io.File;
import java.util.*;

/**
 * Class to manage libraries and playlists
 */
public class SongManager {
    private Library m_myLibrary;
    private Library m_externalLibrary;
    private List<Playlist> m_playlists;
    private List<SongManagerObserver> m_songManagerObservers;
    private List<Library> m_libraries;

    // For observer pattern
    private File m_selectedCenterFolder;

    public SongManager(String directoryPath) {
        m_myLibrary = new Library(directoryPath);
        m_externalLibrary = null;
        m_playlists = new ArrayList<>();
        m_libraries = new ArrayList<>();
        m_selectedCenterFolder = null;
        m_songManagerObservers = new ArrayList<>();
    }

    /**
     * Add new library (root folder path) to m_libraries if it is not already in the list
     * @param directoryPath
     * @return true if new library is added to the list, false otherwise
     */
    public boolean addLibrary(String directoryPath){
        if (!isInLibrary(directoryPath)) {
            Library newLibrary = new Library(directoryPath);
            m_libraries.add(newLibrary);
            return true;
        } else {
            return false;
        }
    }

    private boolean isInLibrary(String directoryPath){
        for (Library library: m_libraries){
            if (library.getM_rootDirPath().equals(directoryPath)){
                return true;
            }
        }
        return false;
    }

    public List<Library> getM_libraries(){
        return m_libraries;
    }

    public void registerNewObserver(SongManagerObserver observer) {
        m_songManagerObservers.add(observer);
    }

    public String getLibraryRootDirPath() {
        return m_myLibrary.getM_rootDirPath();
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


    public List<Song> getCenterPanelSongs() {
        List<Song> centerPanelSongs = new ArrayList<>();

        for (Library library: m_libraries){
            for (Song song: this.getSongs(library)){
                String songFilePath = song.getM_file().getAbsolutePath();
                //System.out.println("    Path of song file: " + songFilePath);
                if (songFilePath.contains(m_selectedCenterFolder.getAbsolutePath())){
                    centerPanelSongs.add(song);
                }
            }
        }

        return centerPanelSongs;
    }

    /********** Functions for observer pattern *************/

    public void addObserver(SongManagerObserver observer){
        m_songManagerObservers.add(observer);
    }

    public void setCenterFolder(File newFolderSelected){
        this.m_selectedCenterFolder = newFolderSelected;

        notifyCenterFolderObservers();
    }

    public void notifyLibraryObservers() {
        for (SongManagerObserver observer : m_songManagerObservers) {
            observer.librariesChanged();
        }
    }

    public void notifyCenterFolderObservers() {
        for (SongManagerObserver observer : m_songManagerObservers) {
            observer.centerFolderChanged();
        }
    }

    public void notifyRightFolderObservers() {

    }

    public void notifySongObservers() {

    }
}