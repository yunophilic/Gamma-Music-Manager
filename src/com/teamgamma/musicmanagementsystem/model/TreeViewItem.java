package com.teamgamma.musicmanagementsystem.model;

import java.io.File;

/**
 * Interface for file tree objects
 */
public interface TreeViewItem {
    File getM_file();

    boolean getM_isRootPath();

    File getSongToPlay();

    String toString();

    boolean equals(Object object);
}
