package com.teamgamma.musicmanagementsystem.model;

import java.io.File;

/**
 * Interface for file tree objects
 */
public interface Item {
    File getFile();

    void changeFile(String newPath);

    boolean isRootPath();

    @Override
    String toString();

    @Override
    boolean equals(Object object);
}
