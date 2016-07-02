package com.teamgamma.musicmanagementsystem;

import com.teamgamma.musicmanagementsystem.model.DatabaseManager;
import com.teamgamma.musicmanagementsystem.model.Playlist;
import com.teamgamma.musicmanagementsystem.model.Song;
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
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to wrap all components together.
 */
public class ApplicationController extends Application {

    private static final double MIN_WINDOW_WIDTH = 800;
    private static final double MIN_WINDOW_HEIGHT = 400;

    private SongManager m_songManager;
    private DatabaseManager m_databaseManager;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        //disable jaudiotagger logging
        Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);

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

        System.out.println("loading libraries...");
        List<String> libraryPathList = m_databaseManager.getLibraries();
        for (String libraryPath : libraryPathList) {
            m_songManager.addLibrary(libraryPath);
        }

        System.out.println("loading playlists...");
        List<String> playlistNameList = m_databaseManager.getPlaylists();
        for (String playlistName : playlistNameList) {
            Playlist playlist = m_songManager.addPlaylist(playlistName);
            List<String> songPaths = m_databaseManager.getSongsInPlaylist(playlist.getM_playlistName());
            for(String songPath : songPaths) {
                playlist.addSong(new Song(songPath));
            }
        }

        MusicPlayerManager musicPlayerManager = new MusicPlayerManager(m_databaseManager);
        MainUI rootUI = new MainUI(m_songManager, musicPlayerManager, m_databaseManager);
        Watcher watcher = new Watcher(m_songManager, m_databaseManager);
        watcher.startWatcher();

        primaryStage.setOnCloseRequest(e -> {
            watcher.stopWatcher();
            Platform.exit();
            musicPlayerManager.stopSong();
        });

        primaryStage.setScene(new Scene(rootUI, 1200, 650));
        primaryStage.setMinHeight(MIN_WINDOW_HEIGHT);
        primaryStage.setMinWidth(MIN_WINDOW_WIDTH);
        primaryStage.getIcons().add(
                new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator + "gamma-logo.png"))
        );
        primaryStage.show();

        Media sound = new Media(
                new File("src" + File.separator + "res" + File.separator +"start-sound.mp3").toURI().toString()
        );
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

    @Override
    public void stop() {
        for (Playlist playlist : m_songManager.getM_playlists()) {
            m_databaseManager.savePlaylistSongs(playlist);
        }
        m_databaseManager.closeConnection();
    }
}
