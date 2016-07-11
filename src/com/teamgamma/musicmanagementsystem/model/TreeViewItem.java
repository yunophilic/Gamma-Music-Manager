package com.teamgamma.musicmanagementsystem.model;

import java.io.File;

/**
 * Interface for file tree objects
 */
public interface TreeViewItem {
    File getFile();

    boolean isRootPath();

    Song getSong();

    @Override
    String toString();

    @Override
    boolean equals(Object object);
}
