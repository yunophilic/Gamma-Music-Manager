package com.teamgamma.musicmanagementsystem.misc;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.FileFilter;

/**
 * Utility class that provides functionality for the FileTree
 */
public class TreeViewUtil {
    private static final Image folderImage = new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator + "folder-icon.png"));
    private static final Image songImage = new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator + "musical-note.png"));

    /**
     * Recursively create tree items from the files in a directory and return a reference to the root item
     *
     * @return TreeItem<String> to the root item
     */
    public static TreeItem<TreeViewItem> generateTreeItems(File file, String dirPath, boolean showFolderOnly) {
        TreeItem<TreeViewItem> item = new TreeItem<>(
                (file.getAbsolutePath().equals(dirPath)) ? new TreeViewItem(file, true) : new TreeViewItem(file, false)
        );

        File treeItemFile = item.getValue().getM_file();
        if (treeItemFile.isDirectory()) {
            item.setGraphic(new ImageView(folderImage));
        } else {
            item.setGraphic(new ImageView(songImage));
        }

        File[] children = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (showFolderOnly) {
                    return f.isDirectory();
                } else {
                    return f.isDirectory() || f.getAbsolutePath().endsWith(".mp3");
                }
            }
        });

        if (children != null) {
            for (File child : children) {
                item.getChildren().add(generateTreeItems(child, dirPath, showFolderOnly)); //recursion here
            }
        }

        return item;
    }

    /**
     * Search for the TreeItem<TreeViewItem> from the specified TreeView<TreeViewItem> based on the given path
     *
     * @param path the specified path
     * @return TreeItem<TreeViewItem> or null if not found
     */
    private static TreeItem<TreeViewItem> searchTreeItem(TreeView<TreeViewItem> tree, String path) {
        return searchTreeItem(tree.getRoot(), path);
    }

    /**
     * Search for the TreeItem<TreeViewItem> from the sub-tree rooted at the specified node based on the given path
     *
     * @param node the specified node
     * @param path the specified path
     * @return TreeItem<TreeViewItem> or null if not found
     */
    private static TreeItem<TreeViewItem> searchTreeItem(TreeItem<TreeViewItem> node, String path) {
        //base case
        if (node.getValue().getM_file().getAbsolutePath().equals(path)) {
            return node;
        }

        //recursive case
        for (TreeItem<TreeViewItem> child : node.getChildren()) {
            TreeItem<TreeViewItem> target = searchTreeItem(child, path);
            if (target != null) {
                return target;
            }
        }

        return null;
    }
}
