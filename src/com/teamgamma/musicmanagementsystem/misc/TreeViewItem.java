package com.teamgamma.musicmanagementsystem.misc;

import java.io.File;

/**
 * Class that represents an item in the file tree
 */
public class TreeViewItem {
    private File path;
    private boolean isRootPath;

    public TreeViewItem(File path, boolean isRootPath) {
        this.path = path;
        this.isRootPath = isRootPath;
    }

    /**
     * For tree view, show absolute path if this is an root path, otherwise, show just the file name
     *
     * @return string to show in TreeItem
     */
    @Override
    public String toString() {
        if (isRootPath) {
            return path.getAbsolutePath();
        } else {
            return path.getName();
        }
    }

    public File getPath() {
        return path;
    }

    public boolean isRootPath() {
        return isRootPath;
    }
}
