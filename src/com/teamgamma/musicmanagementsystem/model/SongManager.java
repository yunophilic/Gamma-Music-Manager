package com.teamgamma.musicmanagementsystem.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.InvalidPathException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to manage libraries and playlists
 */
public class SongManager {
    private List<SongManagerObserver> m_songManagerObservers;
    private List<Library> m_libraries;
    private File m_fileBuffer;
    private List<Playlist> m_playlists;

    // For observer pattern
    private File m_selectedCenterFolder;
    private File m_rightFolderSelected;

    // Menu Manager
    private MenuOptions m_menuOptions;

    public SongManager() {
        m_songManagerObservers = new ArrayList<>();
        m_libraries = new ArrayList<>();
        m_fileBuffer = null;
        m_playlists = new ArrayList<>();
        m_selectedCenterFolder = null;
        m_rightFolderSelected = null;

        m_menuOptions = new MenuOptions();
    }

    /**
     * Add new library (root folder path) to m_libraries if it is not already in the list
     *
     * @param directoryPath path to the library
     * @return true if new library is added to the list, false otherwise
     */
    public boolean addLibrary(String directoryPath) {
        if (isInLibrary(directoryPath)) {
            return false;
        }
        try {
            Library newLibrary = new Library(directoryPath);
            if (!newLibrary.getM_rootDir().exists()) {
                return false;
            }
            m_libraries.add(newLibrary);
            return true;
        } catch(NullPointerException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Remove a library (this doesn't actually delete the files in the filesystem)
     *
     * @param file any file in the library (can be the library root dir itself)
     * @return true if new library is added to the list, false otherwise
     */
    public boolean removeLibrary(File file) {
        return m_libraries.remove(getLibrary(file));
    }

    private boolean isInLibrary(String directoryPath) {
        for (Library library : m_libraries) {
            if (library.getM_rootDirPath().equals(directoryPath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the library where the specified file resides in
     *
     * @param file specified file
     * @return true if found, null otherwise
     */
    private Library getLibrary(File file) {
        for (Library l : m_libraries) {
            if (file.getAbsolutePath().startsWith(l.getM_rootDirPath())) {
                return l;
            }
        }
        return null;
    }

    /**
     * Get libraries
     * @return list of libraries
     */
    public List<Library> getM_libraries() {
        return m_libraries;
    }

    /**
     * Set file(s) to be copied
     * @param m_fileBuffer
     */
    public void setM_fileBuffer(File m_fileBuffer) {
        this.m_fileBuffer = m_fileBuffer;
    }

    /**
     * Copy files in buffer to destination
     * @param dest
     * @return
     * @throws IOException
     * @throws InvalidPathException
     */
    public boolean copyToDestination(File dest) throws IOException, InvalidPathException {
        if (m_fileBuffer == null) {
            return false;
        }

        //copy in file system
        if (!FileManager.copyFilesRecursively(m_fileBuffer, dest)) {
            return false;
        }

        //update song objects inside the model
        updateLibraries();

        return true;
    }

    /**
     * Move file from source to destination
     * @param fileToMove
     * @param destDir
     * @throws IOException
     */
    public void moveFile(File fileToMove, File destDir) throws IOException {
        FileManager.moveFile(fileToMove, destDir);
        updateLibraries();
    }

    /**
     * Delete a file
     * @param fileToDelete
     * @throws Exception
     */
    public void deleteFile(File fileToDelete) throws Exception {
        if (m_rightFolderSelected != null && m_rightFolderSelected.getAbsolutePath().equals(fileToDelete.getAbsolutePath())) {
            m_rightFolderSelected = null;
        }
        if (m_selectedCenterFolder != null && m_selectedCenterFolder.getAbsolutePath().equals(fileToDelete.getAbsolutePath())) {
            m_selectedCenterFolder = null;
        }

        if (!FileManager.removeFile(fileToDelete)) {
            throw new FileSystemException("File " + fileToDelete.getAbsolutePath() + " could not be deleted");
        }
        updateLibraries();
    }

    /**
     * Update the list of libraries
     */
    private void updateLibraries() {
        // Delete current libraries and create new libraries with same paths
        // to update songs in libraries when files are moved
        List<String> libraryPaths = new ArrayList<>();

        for (Library library : m_libraries) {
            libraryPaths.add(library.getM_rootDirPath());
        }

        m_libraries.clear();

        for (String libraryPath : libraryPaths) {
            File tempFile = new File(libraryPath);
            if (tempFile.exists()) {
                this.addLibrary(libraryPath);
            }
        }
    }

    /**
     * Get list of songs in a certain library within the library list
     * @param library
     * @return list of songs
     */
    public List<Song> getSongs(Library library) {
        return library.getM_songList();
    }

    /**
     * Get songs to display in center panel
     * @return list of songs
     */
    public List<Song> getCenterPanelSongs() {
        List<Song> centerPanelSongs = new ArrayList<>();
        System.out.println("== Selected center folder: " + m_selectedCenterFolder.getAbsolutePath());

        if (m_selectedCenterFolder != null) {
            for (Library library : m_libraries) {
                for (Song song : this.getSongs(library)) {
                    if (m_menuOptions.getM_centerPanelShowSubfolderFiles()) {
                        String songFilePath = song.getM_file().getAbsolutePath();
                        if (songFilePath.contains(m_selectedCenterFolder.getAbsolutePath())) {
                            centerPanelSongs.add(song);
                        }
                    } else {
                        String songParentPath = song.getM_file().getParent();
                        System.out.println("== Song parent path: " + songParentPath);
                        if (songParentPath.equals(m_selectedCenterFolder.getAbsolutePath())) {
                            centerPanelSongs.add(song);
                        }
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

    public void setCenterFolder(File newFolderSelected) {
        this.m_selectedCenterFolder = newFolderSelected;
    }

    public File getM_selectedCenterFolder() {
        return m_selectedCenterFolder;
    }

    public File getM_fileBuffer() {
        return m_fileBuffer;
    }

    public MenuOptions getM_menuOptions(){
        return m_menuOptions;
    }

    /**********
     * Functions for observer pattern
     *************/

    public void addObserver(SongManagerObserver observer) {
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

    public void notifyLeftPanelObservers() {
        for (SongManagerObserver observer : m_songManagerObservers) {
            observer.leftPanelOptionsChanged();
        }
    }
}