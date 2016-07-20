package com.teamgamma.musicmanagementsystem.model;

import java.io.File;

/**
 * Class that represents a Folder
 */
public class Folder implements Item {
    private File m_file;
    private boolean m_isRoot;

    public Folder(File file, boolean isRootPath) {
        m_file = file;
        m_isRoot = isRootPath;
    }

    @Override
    public File getFile() {
        return m_file;
    }

    @Override
    public void changeFile(String path) {
        m_file = new File(path);
    }

    @Override
    public boolean isRootItem() {
        return m_isRoot;
    }

    @Override
    public void setAsRootItem() {
        m_isRoot = true;
    }

    @Override
    public String toString() {
        if (m_isRoot) {
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

        return thisFilePath.equals(otherFilePath);
    }
}
