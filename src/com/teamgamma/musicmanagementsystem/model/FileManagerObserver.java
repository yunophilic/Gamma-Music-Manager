package com.teamgamma.musicmanagementsystem.model;

import com.teamgamma.musicmanagementsystem.util.Action;

import java.io.File;

public interface FileManagerObserver {

    // Update UI if change is detected in the file manager
    void changed(Action action, File file);

}

