package com.teamgamma.musicmanagementsystem;

import com.teamgamma.musicmanagementsystem.model.DatabaseManager;
import com.teamgamma.musicmanagementsystem.model.Library;
import com.teamgamma.musicmanagementsystem.model.SongManager;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import com.teamgamma.musicmanagementsystem.ui.MainUI;
import com.teamgamma.musicmanagementsystem.ui.PromptUI;
import com.teamgamma.musicmanagementsystem.watchservice.Watcher;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

/**
 * Class to wrap all components together.
 */
public class ApplicationController extends Application {

    private static final double MIN_WINDOW_WIDTH = 800;
    private static final double MIN_WINDOW_HEIGHT = 400;

    private DatabaseManager m_databaseManager;
    private MainUI m_rootUI;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Gamma Music Manager");

        SongManager songManager = new SongManager();
        m_databaseManager = new DatabaseManager();
        if (!m_databaseManager.isDatabaseFileExist()) {
            System.out.println("No libraries are existent");
            System.out.println("creating new database file...");
            m_databaseManager.createDatabaseFile();
            m_databaseManager.setupDatabase();
            String firstLibrary = PromptUI.initialWelcome();
            if (firstLibrary != null) {
                songManager.addLibrary(firstLibrary);
                m_databaseManager.addLibrary(firstLibrary);
            }
        } else {
            m_databaseManager.setupDatabase();
        }
        List<String> libraryPathList = m_databaseManager.getLibraries();
        System.out.println("loading libraries...");
        for (String libraryPath : libraryPathList) {
            songManager.addLibrary(libraryPath);
        }
        List<String> playlistNameList = m_databaseManager.getPlaylists();
        System.out.println("loading playlists...");
        for (String playlistName : playlistNameList) {
            songManager.addPlaylist(playlistName);
        }

        MusicPlayerManager musicPlayerManager = new MusicPlayerManager(m_databaseManager);
        m_rootUI = new MainUI(songManager, musicPlayerManager, m_databaseManager);
        Watcher watcher = new Watcher(songManager, m_databaseManager);
        watcher.startWatcher();

        primaryStage.setOnCloseRequest(e -> {
            watcher.stopWatcher();
            Platform.exit();
            musicPlayerManager.stopSong();
        });

        primaryStage.setScene(new Scene(m_rootUI, 1200, 650));
        primaryStage.setMinHeight(MIN_WINDOW_HEIGHT);
        primaryStage.setMinWidth(MIN_WINDOW_WIDTH);
        primaryStage.getIcons().add(new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator +
                "gamma-logo.png")));
        primaryStage.show();
    }

    @Override
    public void stop() {
        List<String> libraryUIExpandedPaths = m_rootUI.getLibraryUIExpandedPaths();
        if (libraryUIExpandedPaths != null) {
            // TODO: save to database
            for (String path : libraryUIExpandedPaths) {
                System.out.println("LIBRARY EXPANDED PATH: " + path);
            }
        }

        List<String> dynamicTreeViewUIExpandedPaths = m_rootUI.getDynamicTreeViewUIExpandedPaths();
        if (dynamicTreeViewUIExpandedPaths != null) {
            // TODO: save to database
            for (String path : dynamicTreeViewUIExpandedPaths) {
                System.out.println("DYNAMIC TREEVIEW EXPANDED PATH: " + path);
            }
        }

        m_databaseManager.closeConnection();
    }
}
