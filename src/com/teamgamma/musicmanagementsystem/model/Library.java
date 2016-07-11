package com.teamgamma.musicmanagementsystem.model;

import com.teamgamma.musicmanagementsystem.misc.TreeViewUtil;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to maintain a library in a system.
 */
public class Library {
    /*private List<Song> m_songList;
    private File m_rootDir;*/

    private TreeItem<TreeViewItem> m_treeRoot;

    /**
     * Constructor
     *
     * @param folderPath: root path to folder
     */
    public Library(String folderPath) {
        /*m_songList = FileManager.generateSongs(folderPath);*/

        File rootDir = new File(folderPath);
        m_treeRoot = TreeViewUtil.generateTreeItems(rootDir, rootDir.getAbsolutePath(), null);
    }

    /**
     * Constructor
     *
     * @param folderPath: root path to folder
     * @param expandedPaths: list of expanded paths if exist
     */
    public Library(String folderPath, List<String> expandedPaths) {
        File rootDir = new File(folderPath);
        m_treeRoot = TreeViewUtil.generateTreeItems(rootDir, rootDir.getAbsolutePath(), expandedPaths);
    }

    /**
     * Add Song object to Library
     *
     * @param songToAdd: Song object to add
     * @return true on successful add
     */
    /*public boolean addSong(Song songToAdd) {
        try {
            return m_songList.add(songToAdd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }*/

    /**
     * Remove song from Library and System
     *
     * @param songToRemove: Song object to remove
     * @return true on successful remove
     */
    /*public boolean removeSong(Song songToRemove) {
        try {
            if (FileManager.removeFile(songToRemove)) {
                m_songList.remove(songToRemove);
                return true;
            }
        } catch (NoSuchFileException x) {
            System.err.format("%s: no such" + " file or directory%n", songToRemove.getAbsolutePath());
        } catch (DirectoryNotEmptyException x) {
            System.err.format("%s not empty%n", songToRemove.getAbsolutePath());
        } catch (IOException x) {
            // File permission problems are caught here.
            System.err.println(x);
        } catch (Exception e) {
            // if any other error occurs
            e.printStackTrace();
        }
        return false;
    }*/

    /**
     * Copy source Song object to destination path
     *
     * @param songToCopy: Song object to copy
     * @param pathToDest: Directory to copy to
     * @return true on successful copy
     */
    /*public boolean copySong(Song songToCopy, String pathToDest) {
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
    }*/

    /**
     * Get Song object in List
     *
     * @param songName: String of Song object name
     * @return Song object if found in Library.
     * Returns null if not found
     */
    /*public Song getSong(String songName) {
        for (Song song : m_songList) {
            if (song.getM_fileName().equals(songName)) {
                return song;
            }
        }
        return null;
    }*/

    /**
     * Get Song object in List
     *
     * @param file: File object of the song to retrieve
     * @return Song object if found in Library.
     * Returns null if not found
     */
    /*public Song getSong(File file) {
        for (Song song : m_songList) {
            if (song.equals(file)) {
                return song;
            }
        }
        return null;
    }*/

    /**
     * Get Song object in List
     *
     * @param song: Song object to retrieve
     * @return Song object if found in Library.
     * Returns null if not found
     */
    /*public Song getSong(Song song) {
        if (m_songList.contains(song)) {
            int index = m_songList.indexOf(song);
            return m_songList.get(index);
        }
        return null;
    }*/

    /**
     * Get List of Song objects in Library
     *
     * @return List of Song objects in Library
     */
    public List<Song> getSongs() {
        return getSongs(m_treeRoot);
    }

    private List<Song> getSongs(TreeItem<TreeViewItem> node) {
        List<Song> songs = new ArrayList<>();
        if(node.getValue() instanceof Song) {
            songs.add( (Song) node.getValue() );
        }

        List<TreeItem<TreeViewItem>> children = node.getChildren();
        for (TreeItem<TreeViewItem> child : children) {
            songs.addAll(getSongs(child));
        }

        return songs;
    }

    /**
     * Get root directory path of Library
     *
     * @return String to root directory
     */
    public String getRootDirPath() {
        return m_treeRoot.getValue().getM_file().getAbsolutePath();
    }

    /**
     * Get root directory file of Library
     *
     * @return File of root directory
     */
    public File getRootDir() {
        return m_treeRoot.getValue().getM_file();
    }

    public TreeItem<TreeViewItem> getM_treeRoot() {
        return m_treeRoot;
    }

    public Song getSong(File fileToMove) {
        return getSong(m_treeRoot, fileToMove);
    }

    private Song getSong(TreeItem<TreeViewItem> node, File fileToMove) {
        if (node.getValue().getM_file().getAbsolutePath().equals(fileToMove.getAbsolutePath())) {
            return (Song) node.getValue();
        }

        Song song = null;
        List<TreeItem<TreeViewItem>> children = node.getChildren();
        for (TreeItem<TreeViewItem> child : children) {
            song = getSong(child, fileToMove);
        }

        return song;
    }
}
