package com.teamgamma.musicmanagementsystem.misc;

import java.io.File;

/**
 * Class that represents an item in the file tree
 */
public class TreeViewItem {
    private File m_file;
    private boolean m_isRootPath;

    public TreeViewItem(File path, boolean isRootPath) {
        m_file = path;
        m_isRootPath = isRootPath;
    }

    /**
     * For tree view, show absolute m_file if this is an root m_file, otherwise, show just the file name
     *
     * @return string to show in TreeItem
     */
    @Override
    public String toString() {
        if (m_isRootPath) {
            return m_file.getAbsolutePath();
        } else {
            return m_file.getName();
        }
    }

    public File getM_file() {
        return m_file;
    }

    public boolean isM_isRootPath() {
        return m_isRootPath;
    }
}
