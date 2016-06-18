package com.teamgamma.musicmanagementsystem;

import com.teamgamma.musicmanagementsystem.model.DatabaseManager;
import com.teamgamma.musicmanagementsystem.model.SongManager;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import com.teamgamma.musicmanagementsystem.ui.MainUI;
import com.teamgamma.musicmanagementsystem.ui.PromptUI;
import com.teamgamma.musicmanagementsystem.watchservice.Watcher;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

/**
 * Class to wrap all components together.
 */
public class ApplicationController extends Application {

    private static final double MIN_WINDOW_WIDTH = 800;
    private static final double MIN_WINDOW_HEIGHT = 400;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Music Management System");

        SongManager songManager = new SongManager();
        DatabaseManager databaseManager = new DatabaseManager();
        if (!databaseManager.isThereSavedState()) {
            System.out.println("No libraries are existent");
            System.out.println("creating new library storage file...");
            databaseManager.createFileLibraries();
            String firstLibrary = PromptUI.initialWelcome();
            songManager.addLibrary(firstLibrary);
            databaseManager.addLibrary(firstLibrary);
        }
        List<String> librariesPath = databaseManager.getLibraries();
        System.out.println("loading libraries...");
        for (String libPath : librariesPath) {
            songManager.addLibrary(libPath);
        }

        MusicPlayerManager musicPlayerManager = new MusicPlayerManager();
        MainUI rootUI = new MainUI(songManager, musicPlayerManager, databaseManager);
        Watcher watcher = new Watcher(songManager, databaseManager);
        watcher.startWatcher();

        primaryStage.setOnCloseRequest(e -> {
            watcher.stopWatcher();
            Platform.exit();
            musicPlayerManager.stopSong();
        });

        primaryStage.setScene(new Scene(rootUI, 1200, 650));

        primaryStage.setMinHeight(MIN_WINDOW_HEIGHT);
        primaryStage.setMinWidth(MIN_WINDOW_WIDTH);
        primaryStage.show();

    }

    @Override
    public void stop() {

    }
}
