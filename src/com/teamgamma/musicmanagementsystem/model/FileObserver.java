package com.teamgamma.musicmanagementsystem.model;

import com.teamgamma.musicmanagementsystem.util.FileActions;

public interface FileObserver {

    // Update UI if change is detected in the file manager
    void changed(FileActions fileActions);

}

