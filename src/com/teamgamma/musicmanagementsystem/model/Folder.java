package com.teamgamma.musicmanagementsystem.model;

import java.io.File;

/**
 * Class that represents a Folder
 */
public class Folder implements Item {
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
    public void changeFile(String newPath) {
        m_file = new File(newPath);
    }

    @Override
    public boolean isRootPath() {
        return m_isRootPath;
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

        return thisFilePath.equals(otherFilePath);
    }
}
