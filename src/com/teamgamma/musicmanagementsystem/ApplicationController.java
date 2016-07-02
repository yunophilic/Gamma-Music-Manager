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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Path;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to wrap all components together.
 */
public class ApplicationController extends Application {

    private static final double MIN_WINDOW_WIDTH = 800;
    private static final double MIN_WINDOW_HEIGHT = 400;

    private DatabaseManager m_databaseManager;
    private MainUI m_rootUI;
    private SongManager m_songManager;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Gamma Music Manager");

        m_songManager = new SongManager();
        m_databaseManager = new DatabaseManager();
        if (!m_databaseManager.isDatabaseFileExist()) {
            System.out.println("No libraries are existent");
            System.out.println("creating new database file...");
            m_databaseManager.createDatabaseFile();
            m_databaseManager.setupDatabase();
            String firstLibrary = PromptUI.initialWelcome();
            if (firstLibrary != null) {
                m_songManager.addLibrary(firstLibrary);
                m_databaseManager.addLibrary(firstLibrary);
            }
        } else {
            m_databaseManager.setupDatabase();
        }

        List<String> libraryPathList = m_databaseManager.getLibraries();
        System.out.println("loading libraries...");
        for (String libraryPath : libraryPathList) {
            m_songManager.addLibrary(libraryPath);
        }

        List<String> playlistNameList = m_databaseManager.getPlaylists();
        System.out.println("loading playlists...");
        for (String playlistName : playlistNameList) {
            m_songManager.addPlaylist(playlistName);
        }

        // TODO: get selected right panel folder from database
        // For testing
        //File previousRightPanelFolder = new File("G:\\SFU\\Homework\\CMPT373\\prj\\library-sample\\external library\\EGOIST");
        File previousRightPanelFolder = null;
        m_songManager.setM_rightFolderSelected(previousRightPanelFolder);

        MusicPlayerManager musicPlayerManager = new MusicPlayerManager(m_databaseManager);

        createRootUI(m_songManager, musicPlayerManager);


        Watcher watcher = new Watcher(m_songManager, m_databaseManager);
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

        Media sound = new Media(new File("src" + File.separator + "res" + File.separator +"start-sound.mp3").toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

    /**
     * Create root UI
     * @param songManager
     * @param musicPlayerManager
     */
    private void createRootUI(SongManager songManager, MusicPlayerManager musicPlayerManager) {
        // Get previous expanded states
        // TODO: get previous expanded states from database
        List<String> libraryUIExpandedPaths = new ArrayList<>();
        // For testing:
        /*libraryUIExpandedPaths.add("G:\\SFU\\Homework\\CMPT373\\prj");
        libraryUIExpandedPaths.add("G:\\SFU\\Homework\\CMPT373\\prj\\library-sample\\my library");
        libraryUIExpandedPaths.add("G:\\SFU\\Homework\\CMPT373\\prj\\library-sample\\my library\\external library");
        libraryUIExpandedPaths.add("G:\\SFU\\Homework\\CMPT373\\prj\\library-sample\\my library\\external library\\fallen");*/

        // TODO: get previous expanded states from database
        List<String> rightPanelExpandedPaths = new ArrayList<>();
        // For testing:
        /*rightPanelExpandedPaths.add("G:\\SFU\\Homework\\CMPT373\\prj\\library-sample\\my library");
        rightPanelExpandedPaths.add("G:\\SFU\\Homework\\CMPT373\\prj\\library-sample\\external library\\EGOIST");
        rightPanelExpandedPaths.add("G:\\SFU\\Homework\\CMPT373\\prj\\library-sample\\external library\\EGOIST\\fallen");
        rightPanelExpandedPaths.add("G:\\SFU\\Homework\\CMPT373\\prj\\library-sample\\external library\\EGOIST\\fallen\\Fallen");*/

        m_rootUI = new MainUI(songManager, musicPlayerManager, m_databaseManager, libraryUIExpandedPaths, rightPanelExpandedPaths);
    }

    @Override
    public void stop() {
        saveFileTreeState();

        m_databaseManager.closeConnection();
    }

    /**
     * Save file tree expanded states
     */
    private void saveFileTreeState() {
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

        // TODO: save to database
        File rightPanelFolder = m_songManager.getM_rightFolderSelected();
    }
}
