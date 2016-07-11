package com.teamgamma.musicmanagementsystem.model;

import java.io.File;

/**
 * Class that represents a Folder
 */
public class Folder implements TreeViewItem {
    private File m_file;
    private boolean m_isRootPath;

    public Folder(File file, boolean isRootPath) {
        m_file = file;
        m_isRootPath = isRootPath;
    }

    @Override
    public File getFile() {
        return m_file;
    }

    @Override
    public boolean isRootPath() {
        return m_isRootPath;
    }

    @Override
    public Song getSong() {
        return null;
    }

    @Override
    public String toString() {
        if (m_isRootPath) {
            return m_file.getAbsolutePath();
        } else {
            return m_file.getName();
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (!(object instanceof Folder)) {
            return false;
        }

        Folder otherFolder = (Folder)object;
        String thisFilePath = m_file.getAbsolutePath();
        String otherFilePath = otherFolder.getFile().getAbsolutePath();

        if (thisFilePath.equals(otherFilePath)) {
            return true;
        } else {
            return false;
        }
    }
}
