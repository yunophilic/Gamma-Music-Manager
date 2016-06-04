package com.teamgamma.musicmanagementsystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.*;

/**
 * Class to manage libraries and playlists
 */
public class SongManager {
    private List<SongManagerObserver> m_songManagerObservers;
    private List<Library> m_libraries;
    private File m_fileBuffer; //can be a directory
    private List<Playlist> m_playlists;

    // For observer pattern
    private File m_selectedCenterFolder;
    private File m_rightFolderSelected;

    public SongManager() {
        m_songManagerObservers = new ArrayList<>();
        m_libraries = new ArrayList<>();
        m_fileBuffer = null;
        m_playlists = new ArrayList<>();
        m_selectedCenterFolder = null;
        m_rightFolderSelected = null;
    }

    /**
     * Add new library (root folder path) to m_libraries if it is not already in the list
     * @param directoryPath path to the library
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

    /**
     * Get the library where the specified file resides in
     * @param file specified file
     * @return true if found, null otherwise
     */
    private Library getLibrary(File file) {
        for(Library l : m_libraries) {
            if( file.exists() && file.getAbsolutePath().startsWith(l.getM_rootDirPath()) ) {
                return l;
            }
        }
        return null;
    }

    public List<Library> getM_libraries(){
        return m_libraries;
    }

    public void registerNewObserver(SongManagerObserver observer) {
        m_songManagerObservers.add(observer);
    }

    /*public boolean addSong(Song songToAdd, Library library) {
        return library.addSong(songToAdd);
    }

    public boolean removeSong(Song songToRemove, Library library) {
        return library.removeSong(songToRemove);
    }*/

    public void setM_fileBuffer(File m_fileBuffer) {
        this.m_fileBuffer = m_fileBuffer;
    }

    public boolean copyToDestination(File dest) throws IOException, InvalidPathException {
        if (m_fileBuffer == null) {
            return false;
        }

        //copy in file system
        if (!FileManager.copyFilesRecursively(m_fileBuffer, dest)) {
            return false;
        }

        //update song objects inside the model
        /*Library targetLib = getLibrary(dest);
        if (targetLib == null) {
            return false;
        }
        if (m_fileBuffer.isDirectory()) {
            List<Song> songsToBeCopied = FileManager.generateSongs(m_fileBuffer.getAbsolutePath());
            for (Song s : songsToBeCopied) {
                if (!targetLib.addSong(s)) {
                    return false;
                }
            }
        } else {
            Library sourceLib = getLibrary(m_fileBuffer);
            if (sourceLib == null) {
                //this shouldn't happen but just in case
                return false;
            }
            Song songToAdd = sourceLib.getSong(m_fileBuffer);
            targetLib.addSong(songToAdd);
        }

        m_fileBuffer = null;*/

        updateLibraries();

        return true;
    }

    private void updateLibraries(){
        // Delete current libraries and create new libraries with same paths
        // to update songs in libraries when files are moved
        List<String> libraryPaths = new ArrayList<>();

        for (Library library: m_libraries){
            libraryPaths.add(library.getM_rootDirPath());
        }

        m_libraries.clear();

        for (String libraryPath: libraryPaths){
            File tempFile = new File(libraryPath);
            if (tempFile.exists()) {
                this.addLibrary(libraryPath);
            }
        }
    }

    public List<Song> getSongs(Library library) {
        return library.getM_songList();
    }

    public List<Song> getCenterPanelSongs() {
        List<Song> centerPanelSongs = new ArrayList<>();

        if (m_selectedCenterFolder != null) {
            for (Library library : m_libraries) {
                for (Song song : this.getSongs(library)) {
                    String songFilePath = song.getM_file().getAbsolutePath();
                    //System.out.println("    Path of song file: " + songFilePath);
                    if (songFilePath.contains(m_selectedCenterFolder.getAbsolutePath())) {
                        centerPanelSongs.add(song);
                    }
                }
            }
        }

        return centerPanelSongs;
    }


    public File getRightFolderSelected() {
        return m_rightFolderSelected;
    }


    public void setRightFolderSelected(File rightFolderSelected) {
        m_rightFolderSelected = rightFolderSelected;
    }


    public void deleteFile(File fileToDelete) {
        try {
            FileManager.removeFile(fileToDelete);
            updateLibraries();
        } catch (Exception e) {
            // TODO: show popup dialog
            e.printStackTrace();
        }

        if (m_rightFolderSelected.getAbsolutePath().equals(fileToDelete.getAbsolutePath())){
            m_rightFolderSelected = null;
        }
        if (m_selectedCenterFolder.getAbsolutePath().equals(fileToDelete.getAbsolutePath())){
            m_selectedCenterFolder = null;
        }
    }

    public void setCenterFolder(File newFolderSelected){
        this.m_selectedCenterFolder = newFolderSelected;

        notifyCenterFolderObservers();
    }

    public File getM_selectedCenterFolder() {
        return m_selectedCenterFolder;
    }


    /********** Functions for observer pattern *************/

    public void addObserver(SongManagerObserver observer){
        m_songManagerObservers.add(observer);
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
        for (SongManagerObserver observer : m_songManagerObservers) {
            observer.rightFolderChanged();
        }
    }

    public void notifySongObservers() {

    }

    public void notifyFileObservers() {
        for (SongManagerObserver observer : m_songManagerObservers) {
            observer.fileChanged();
        }
    }

}