package com.teamgamma.musicmanagementsystem;

import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import com.teamgamma.musicmanagementsystem.ui.MusicPlayerUI;
import com.teamgamma.musicmanagementsystem.ui.UI;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Class to wrap all components together.
 */
public class ApplicationController extends Application {
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
