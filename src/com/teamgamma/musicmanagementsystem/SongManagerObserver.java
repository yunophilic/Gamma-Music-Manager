package com.teamgamma.musicmanagementsystem;

public interface SongManagerObserver {
    // Update UI if a library is added or deleted
    void librariesChanged();

    // Update UI if folder selection to be displayed in the center panel is changed
    void centerFolderChanged();

    // Update UI if folder selection to be displayed in the right panel (as tree view) is changed
    void rightFolderChanged();

    // Update UI if song to be played is changed
    void songChanged();

    // Update UI if file location is changed (ie. copy, paste, remove)
    void fileChanged();
}

