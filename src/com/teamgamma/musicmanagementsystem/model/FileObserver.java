package com.teamgamma.musicmanagementsystem.model;

import com.teamgamma.musicmanagementsystem.util.FileActions;

/**
 * Interface for a file observer,
 */
public interface FileObserver {
    void changed(FileActions fileActions);
}

