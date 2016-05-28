package com.teamgamma.musicmanagementsystem;

import com.teamgamma.musicmanagementsystem.ui.UI;

/**
 * Class to wrap all components together.
 */
public class ApplicationController {
    public static void main(String[] args) {

        UI userInterface = new UI();

        PersistentStorage persistentStorage = new PersistentStorage();
        if (persistentStorage.isThereSavedState()) {
            // Load state from file. Use it to initialize stuff.
        }
        String pathToDir = userInterface.getUserInputForDirectory();

        SongManager songManager = new SongManager(pathToDir);

        System.out.println("The path in song manager is " + songManager.getLibraryRootDirPath());
    }
}
