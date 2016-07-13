package com.teamgamma.musicmanagementsystem.model;

import com.teamgamma.musicmanagementsystem.misc.Actions;

import java.io.File;

public interface FileManagerObserver {

    // Update UI if change is detected in the file manager
    void changed(Actions action, File file);

}

