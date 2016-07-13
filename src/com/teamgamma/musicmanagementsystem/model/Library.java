package com.teamgamma.musicmanagementsystem.model;

import com.teamgamma.musicmanagementsystem.util.FileTreeUtil;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to maintain a library in a system.
 */
public class Library {
    /*private List<Song> m_songList;
    private File m_rootDir;*/

    private TreeItem<Item> m_treeRoot;

    /**
     * Constructor
     *
     * @param folderPath: root path to folder
     */
    public Library(String folderPath) {
        /*m_songList = FileManager.generateSongs(folderPath);*/

        File rootDir = new File(folderPath);
        m_treeRoot = FileTreeUtil.generateTreeItems(rootDir, rootDir.getAbsolutePath(), null);
    }

    /**
     * Constructor
     *
     * @param folderPath: root path to folder
     * @param expandedPaths: list of expanded paths if exist
     */
    public Library(String folderPath, List<String> expandedPaths) {
        File rootDir = new File(folderPath);
        m_treeRoot = FileTreeUtil.generateTreeItems(rootDir, rootDir.getAbsolutePath(), expandedPaths);
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
            return FileManager.copyFile(songToCopy.getFile(), new File(pathToDest));
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
            if (song.getFileName().equals(songName)) {
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

    private List<Song> getSongs(TreeItem<Item> node) {
        List<Song> songs = new ArrayList<>();
        if(node.getValue() instanceof Song) {
            songs.add( (Song) node.getValue() );
        }

        List<TreeItem<Item>> children = node.getChildren();
        for (TreeItem<Item> child : children) {
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
        return m_treeRoot.getValue().getFile().getAbsolutePath();
    }

    /**
     * Get root directory file of Library
     *
     * @return File of root directory
     */
    public File getRootDir() {
        return m_treeRoot.getValue().getFile();
    }

    public TreeItem<Item> getM_treeRoot() {
        return m_treeRoot;
    }

    public Song getSong(File fileToMove) {
        return getSong(m_treeRoot, fileToMove);
    }

    private Song getSong(TreeItem<Item> node, File fileToMove) {
        if (node.getValue().getFile().getAbsolutePath().equals(fileToMove.getAbsolutePath())) {
            return (Song) node.getValue();
        }

        Song song = null;
        List<TreeItem<Item>> children = node.getChildren();
        for (TreeItem<Item> child : children) {
            song = getSong(child, fileToMove);
        }

        return song;
    }
}
