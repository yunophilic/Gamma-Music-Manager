package com.teamgamma.musicmanagementsystem.model;

import com.teamgamma.musicmanagementsystem.util.FileTreeUtils;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that defines a library in the system.
 */
public class Library {
    private TreeItem<Item> m_treeRoot;

    /**
     * Constructor
     *
     * @param folderPath: root path to folder
     */
    public Library(String folderPath) {
        File rootDir = new File(folderPath);
        m_treeRoot = FileTreeUtils.generateTreeItems(rootDir, rootDir.getAbsolutePath(), null);
    }

    /**
     * Constructor
     *
     * @param folderPath: root path to folder
     * @param expandedPaths: list of expanded paths if exist
     */
    public Library(String folderPath, List<String> expandedPaths) {
        File rootDir = new File(folderPath);
        m_treeRoot = FileTreeUtils.generateTreeItems(rootDir, rootDir.getAbsolutePath(), expandedPaths);
    }

    /**
     * Search node containing the specified file in this Library
     *
     * @param file: The specified Item
     * @return The tree node if found, null if not found
     */
    public TreeItem<Item> search(File file) {
        return FileTreeUtils.searchTreeItem(m_treeRoot, file.getAbsolutePath());
    }

    /**
     * Get List of Song objects in Library
     *
     * @return List of Song objects in Library
     */
    public List<Song> getSongs() {
        return getSongsRecursively(m_treeRoot);
    }

    /**
     * Recursively fetch all songs under this node
     *
     * @return List of Song objects in specified node
     */
    private List<Song> getSongsRecursively(TreeItem<Item> node) {
        List<Song> songs = new ArrayList<>();
        if(node.getValue() instanceof Song) {
            songs.add( (Song) node.getValue() );
        }

        List<TreeItem<Item>> children = node.getChildren();
        for (TreeItem<Item> child : children) {
            songs.addAll(getSongsRecursively(child));
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

    /**
     * Getter for m_treeRoot
     */
    public TreeItem<Item> getM_treeRoot() {
        return m_treeRoot;
    }
}
