package com.teamgamma.musicmanagementsystem;

import com.sun.javaws.Main;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import com.teamgamma.musicmanagementsystem.ui.MusicPlayerUI;
import com.teamgamma.musicmanagementsystem.ui.MainUI;
import com.teamgamma.musicmanagementsystem.ui.TextUI;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Class to wrap all components together.
 */
public class ApplicationController extends Application {
    public static void main(String[] args) {

        /*TextUI userInterface = new TextUI();

        PersistentStorage persistentStorage = new PersistentStorage();
        if (persistentStorage.isThereSavedState()) {
            // Load state from file. Use it to initialize stuff.
        }
        String pathToDir = userInterface.getUserInputForDirectory();

        SongManager songManager = new SongManager(pathToDir);

        System.out.println("The path in song manager is " + songManager.getLibraryRootDirPath());*/
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Music Management");

        SongManager songManager = new SongManager("G:\\SFU\\Homework\\CMPT373\\music");
        songManager.setM_externalLibrary("G:\\SFU\\Homework\\CMPT373\\music2");

        MainUI root = new MainUI(songManager);

        primaryStage.setScene(new Scene(root, 1200, 900));
        primaryStage.show();
    }
}
