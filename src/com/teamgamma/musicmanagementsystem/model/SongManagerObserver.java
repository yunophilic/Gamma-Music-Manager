package com.teamgamma.musicmanagementsystem.model;

import com.teamgamma.musicmanagementsystem.misc.Actions;

import java.io.File;

public interface SongManagerObserver {

    // Update UI if file location is changed (ie. copy, paste, remove)
    void changed(Actions action, File file);

}

