package com.teamgamma.musicmanagementsystem;

import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import com.teamgamma.musicmanagementsystem.ui.MainUI;
import com.teamgamma.musicmanagementsystem.ui.PromptUI;
import com.teamgamma.musicmanagementsystem.watchservice.Watcher;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

/**
 * Class to wrap all components together.
 */
public class ApplicationController extends Application {

    private static final double MIN_WINDOW_WIDTH = 800;
    private static final double MIN_WINDOW_HEIGHT = 400;

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

        //for now!
        /*String myLibPath = System.getProperty("user.dir") +
                File.separator + "library-sample" + File.separator + "my library";
        String externLibPath = System.getProperty("user.dir") +
                File.separator + "library-sample" + File.separator + "external library";*/

        SongManager songManager = new SongManager();

        PersistentStorage persistentStorage = new PersistentStorage();
        if(!persistentStorage.isThereSavedState()) {
            System.out.println("No libraries are existent");
            System.out.println("creating new library storage file...");
            persistentStorage.createFileLibraries();
            String firstLibrary = PromptUI.initialWelcome();
            songManager.addLibrary(firstLibrary);
            persistentStorage.updatePersistentStorageLibrary(firstLibrary);
        }
        List<String> loadSongsFromLibrary = persistentStorage.getPersistentStorageLibrary();
        System.out.println("loading libraries...");
        for(int i = 0; i < loadSongsFromLibrary.size(); i++) {
            songManager.addLibrary(loadSongsFromLibrary.get(i));
        }

        //songManager.setM_externalLibrary(externLibPath);

        /*if (songManager.addLibrary(myLibPath)) {
            System.out.println("New library added");
        } else {
            System.out.println("Duplicate library");
        }
        if (songManager.addLibrary(externLibPath)) {
            System.out.println("New library added");
        } else {
            System.out.println("Duplicate library");
        }*/

        MusicPlayerManager musicPlayerManager = new MusicPlayerManager();
        MainUI root = new MainUI(songManager, musicPlayerManager);
        Watcher watcher = new Watcher(songManager);
        watcher.startWatcher();

        primaryStage.setOnCloseRequest(e -> {
            watcher.stopWatcher();
            Platform.exit();
            musicPlayerManager.stopSong();
        });

        primaryStage.setScene(new Scene(root, 1200, 900));
        primaryStage.setMinHeight(MIN_WINDOW_HEIGHT);
        primaryStage.setMinWidth(MIN_WINDOW_WIDTH);
        primaryStage.show();

    }

    @Override
    public void stop(){

    }
}
