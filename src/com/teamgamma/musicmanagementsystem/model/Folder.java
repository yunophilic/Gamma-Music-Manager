package com.teamgamma.musicmanagementsystem.model;

import java.io.File;

/**
 * Class that represents a Folder
 */
public class Folder implements Item {
    private File m_file;
    private boolean m_isRoot;
    private boolean m_isRightRoot;

    public Folder(File file, boolean isRootPath) {
        m_file = file;
        m_isRoot = isRootPath;
        m_isRightRoot = false;
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
    public void setAsRightRootItem() {
        m_isRightRoot = true;
    }

    @Override
    public boolean isRightRootItem() {
        return m_isRightRoot;
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
