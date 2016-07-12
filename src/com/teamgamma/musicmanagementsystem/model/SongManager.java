package com.teamgamma.musicmanagementsystem.model;

import com.teamgamma.musicmanagementsystem.misc.Actions;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to manage libraries and playlists
 */
public class SongManager {
    private List<SongManagerObserver> m_songManagerObservers;
    private List<PlaylistObserver> m_playlistObservers;
    private List<Library> m_libraries;
    private List<Playlist> m_playlists;

    // Buffers
    private Item m_itemToCopy;
    private Item m_itemToMove;
    private File m_copyDest;
    private File m_moveDest;
    private File m_addedFile;
    private File m_deletedFile;
    private File m_renamedFile;

    // For observer pattern
    private File m_selectedCenterFolder;
    private File m_rightFolderSelected;
    private Playlist m_selectedPlaylist;

    // Menu Manager
    private MenuOptions m_menuOptions;

    // For actions such as paste, delete
    private Actions m_libraryAction;
    private Actions m_libraryFileAction;
    private Actions m_rightPanelFileAction;

    public SongManager() {
        m_songManagerObservers = new ArrayList<>();
        m_playlistObservers = new ArrayList<>();
        m_libraries = new ArrayList<>();
        m_playlists = new ArrayList<>();

        m_itemToCopy = null;
        m_itemToMove = null;
        m_copyDest = null;
        m_moveDest = null;
        m_addedFile = null;
        m_deletedFile = null;
        m_renamedFile = null;

        m_selectedCenterFolder = null;
        m_rightFolderSelected = null;
        m_selectedPlaylist = null;

        m_menuOptions = null;
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
            if (!newLibrary.getRootDir().exists()) {
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
            if (library.getRootDirPath().equals(directoryPath)) {
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
            if (file.getAbsolutePath().startsWith(l.getRootDirPath())) {
                return l;
            }
        }
        return null;
    }

    /**
     * Get Song object in a library
     * @param songFile File object representing the song
     * @param libraryRootDir File object representing the root dir of the the library
     * @return list of songs
     */
    /*public Song getSongInLibrary(File songFile, File libraryRootDir) {
        Library library = getLibrary(libraryRootDir);
        return (library != null) ? library.getSong(songFile) : null;
    }*/

    /**
     * Get libraries
     * @return list of libraries
     */
    public List<Library> getM_libraries() {
        return m_libraries;
    }

    /**
     * Check if given file is a library
     * @param file
     * @return
     */
    /*public boolean isLibrary(File file) {
        String filePath = file.getAbsolutePath();
        for (Library library: m_libraries) {
            String libraryPath = library.getRootDirPath();
            if (libraryPath.equals(filePath)) {
                return true;
            }
        }
        return false;
    }*/

    /**
     * Copy files in buffer to destination
     * @param dest the destination folder
     * @throws IOException
     * @throws InvalidPathException
     */
    public void copyToDestination(File dest) throws Exception {
        if (m_itemToCopy == null) {
            throw new Exception("File to copy should not be null");
        }

        if (!FileManager.copyFilesRecursively(m_itemToCopy.getFile(), dest)) {
            throw new IOException("Fail to copy");
        }

        m_copyDest = dest;

        //updateLibraries();
    }

    /**
     * Update UI and move file from source to destination
     * @param fileToMove
     * @param destDir
     * @throws IOException
     */
    public void moveFile(File fileToMove, File destDir) throws IOException {
        FileManager.moveFile(fileToMove, destDir);
        //updateLibraries();

        m_moveDest = destDir;
        notifyFileObservers(Actions.DROP, null);
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
        //updateLibraries();

        m_deletedFile = fileToDelete;
        notifyFileObservers(Actions.DELETE, fileToDelete);

        // Clear file to delete buffer
        m_deletedFile = null;
    }

    /**
     * Update the list of libraries
     */
    /*private void updateLibraries() {
        // Delete current libraries and create new libraries with same paths
        // to update songs in libraries when files are moved
        List<String> libraryPaths = new ArrayList<>();

        for (Library library : m_libraries) {
            libraryPaths.add(library.getRootDirPath());
        }

        m_libraries.clear();

        for (String libraryPath : libraryPaths) {
            File tempFile = new File(libraryPath);
            if (tempFile.exists()) {
                this.addLibrary(libraryPath);
            }
        }
    }*/

    /**
     * Get list of songs in a certain library within the library list
     * @param library
     * @return list of songs
     */
    private List<Song> getSongs(Library library) {
        return library.getSongs();
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
                for (Song song : library.getSongs()) {
                    if (m_menuOptions.getM_centerPanelShowSubfolderFiles()) {
                        String songFilePath = song.getFile().getAbsolutePath();
                        if (songFilePath.contains(m_selectedCenterFolder.getAbsolutePath())) {
                            centerPanelSongs.add(song);
                        }
                    } else {
                        String songParentPath = song.getFile().getParent();
                        //System.out.println("== Song parent path: " + songParentPath);
                        if (songParentPath.equals(m_selectedCenterFolder.getAbsolutePath())) {
                            centerPanelSongs.add(song);
                        }
                    }
                }
            }
        }

        return centerPanelSongs;
    }

    /**
     * Check if playlistName exist
     *
     * @param playlistName name of playlist
     */
    public boolean playlistNameExist(String playlistName) {
        for(Playlist p : m_playlists) {
            if(p.getM_playlistName().equals(playlistName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add new playlist to m_playlists
     *
     * @param playlistName name of new playlist
     * @return new Playlist object created
     */
    public Playlist addAndCreatePlaylist(String playlistName) {
        Playlist newPlaylist = new Playlist(playlistName);
        m_playlists.add(newPlaylist);
        return newPlaylist;
    }

    public void addPlaylist(Playlist playlist) {
        m_playlists.add(playlist);
    }
    /**
     * Remove existing playlist
     *
     * @param playlistToRemove playlist to remove
     * @return new Playlist object created
     */
    public boolean removePlaylist(Playlist playlistToRemove) {
        return m_playlists.remove(playlistToRemove);
    }

    /**
     * Add song to playlist
     *
     * @param selectedSong Song
     * @param playlistName String
     * @return true if song added successfully, false otherwise
     */
    public boolean addSongToPlaylist(Song selectedSong, String playlistName) {
        // Find playlist and add song to it if found
        Playlist playlist = findPlaylist(playlistName);
        if (playlist != null) {
            boolean isAdded = playlist.addSong(selectedSong);
            if (isAdded) {
                // Notify playlist observers of changes
                notifyPlaylistSongsObservers();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Add song to playlist
     *
     * @param selectedSong Song
     * @param playlistToAdd Playlist
     * @return true if song added successfully, false otherwise
     */
    public boolean addSongToPlaylist(Song selectedSong, Playlist playlistToAdd) {
        if (playlistToAdd.addSong(selectedSong)) {
            // Notify playlist observers of changes
            notifyPlaylistSongsObservers();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Find the playlist with the playlistName
     *
     * @param playlistName
     * @return Playlist
     */
    private Playlist findPlaylist(String playlistName) {
        for (Playlist playlist: m_playlists){
            if (playlist.getM_playlistName().equals(playlistName)){
                return playlist;
            }
        }
        return null;
    }


    /**
     * Rename a file and notify file observers
     * @param fileToRename
     * @param newPath
     */
    public void renameFile(File fileToRename, Path newPath) {
        m_renamedFile = new File(newPath.toString());

        //updateLibraries();

        notifyFileObservers(Actions.RENAME, fileToRename);
    }

    /**
     * Notify file changes detected from File system
     * @param action
     * @param file
     */
    public void fileSysChanged(Actions action, File file) {
        //updateLibraries();
        notifyFileObservers(action, file);
    }

    /*public void fileDeleted(File fileToDelete) {

    }*/


    /**********
     * Getters and setters
     *************/

    public File getM_addedFile(){
        return m_addedFile;
    }

    public File getM_deletedFile() {
        return m_deletedFile;
    }

    public File getM_renamedFile() {
        return m_renamedFile;
    }

    public File getM_moveDest() {
        return m_moveDest;
    }

    public File getM_copyDest() {
        return m_copyDest;
    }

    public File getM_rightFolderSelected() {
        return m_rightFolderSelected;
    }

    public void setM_rightFolderSelected(File m_rightFolderSelected) {
        this.m_rightFolderSelected = m_rightFolderSelected;
    }

    public File getM_selectedCenterFolder() {
        return m_selectedCenterFolder;
    }

    public void setM_selectedCenterFolder(File m_newFolderSelected) {
        this.m_selectedCenterFolder = m_newFolderSelected;
    }

    public Playlist getM_selectedPlaylist() {
        return m_selectedPlaylist;
    }

    public void setM_selectedPlaylist(Playlist m_selectedPlaylist) {
        this.m_selectedPlaylist = m_selectedPlaylist;
    }

    public Item getM_itemToCopy() {
        return m_itemToCopy;
    }

    public void setM_itemToCopy(Item m_itemToCopy) {
        this.m_itemToCopy = m_itemToCopy;
    }

    public Item getM_itemToMove() {
        return m_itemToMove;
    }

    public void setM_itemToMove(Item m_itemToMove) {
        this.m_itemToMove = m_itemToMove;
    }

    public List<Playlist> getM_playlists() {
        return m_playlists;
    }

    public MenuOptions getM_menuOptions(){
        return m_menuOptions;
    }

    public void setM_menuOptions(MenuOptions options) {
        m_menuOptions = options;
    }

    public Actions getM_libraryAction() {
        return m_libraryAction;
    }

    public void setM_libraryAction(Actions libraryAction) {
        m_libraryAction = libraryAction;
    }

    public Actions getM_libraryFileAction() {
        return m_libraryFileAction;
    }

    public void setM_libraryFileAction(Actions fileAction) {
        m_libraryFileAction = fileAction;
    }

    public Actions getM_rightPanelFileAction() {
        return m_rightPanelFileAction;
    }

    public void setM_rightPanelFileAction(Actions fileAction) {
        m_rightPanelFileAction = fileAction;
    }


    /**********
     * Functions for observer pattern
     *************/

    public void addSongManagerObserver(SongManagerObserver observer) {
        m_songManagerObservers.add(observer);
    }

    public void addPlaylistObserver(PlaylistObserver observer) {
        m_playlistObservers.add(observer);
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

    /*public void notifySongObservers() {

    }*/

    public void notifyFileObservers(Actions action, File file) {
        for (SongManagerObserver observer : m_songManagerObservers) {
            observer.fileChanged(action, file);
        }
    }

    public void notifyLeftPanelObservers() {
        for (SongManagerObserver observer : m_songManagerObservers) {
            observer.leftPanelOptionsChanged();
        }
    }

    public void notifyPlaylistSongsObservers() {
        for (PlaylistObserver observer : m_playlistObservers) {
            observer.songsChanged();
        }
    }

    public void notifyPlaylistsObservers() {
        for (PlaylistObserver observer : m_playlistObservers) {
            observer.playlistsChanged();
        }
    }

    public File getFileToCopy() {
        return m_itemToCopy.getFile();
    }

    public File getFileToMove() {
        return m_itemToMove.getFile();
    }
}