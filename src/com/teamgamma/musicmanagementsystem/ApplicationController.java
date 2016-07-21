package com.teamgamma.musicmanagementsystem;

import com.teamgamma.musicmanagementsystem.model.*;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import com.teamgamma.musicmanagementsystem.ui.MainUI;
import com.teamgamma.musicmanagementsystem.ui.PromptUI;
import com.teamgamma.musicmanagementsystem.ui.*;
import com.teamgamma.musicmanagementsystem.watchservice.Watcher;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to wrap all components together.
 */
public class ApplicationController extends Application {
    private static final double MINI_MODE_WIDTH = 367;
    private static final double MINI_MODE_HEIGHT = 400;
    private static final double ORIGINAL_WINDOW_WIDTH = 1200;
    private static final double ORIGINAL_WINDOW_HEIGHT = 650;
    private static final double MIN_WINDOW_WIDTH = 0;
    private static final double MIN_WINDOW_HEIGHT = 0;
    private static final String APP_TITLE = "Gamma Music Manager";
    private static final String GAMMA_LOGO_IMAGE_URL = "res" + File.separator + "gamma-logo.png";
    private static final String START_SOUND_PATH = System.getProperty("user.dir") + File.separator + "src" + File.separator + "res" + File.separator +"start-sound.mp3";

    private SongManager m_songManager;
    private MusicPlayerManager m_musicPlayerManager;
    private DatabaseManager m_databaseManager;
    private FilePersistentStorage m_filePersistentStorage;
    private MainUI m_rootUI;
    private Stage stageCopy;

    /**
     * Load previously saved session states
     * Modified from https://blog.codecentric.de/en/2015/09/javafx-how-to-easily-implement-application-preloader-2/
     *
     * @throws Exception
     */
    @Override
    public void init() throws Exception {
        Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
        m_songManager = new SongManager();
        m_databaseManager = new DatabaseManager();
        m_musicPlayerManager = new MusicPlayerManager(m_databaseManager);
        m_filePersistentStorage = new FilePersistentStorage();
        if (m_databaseManager.isDatabaseFileExist()) {
            m_databaseManager.setupDatabase();
            loadSessionState();
        }
    }

    /**
     * Load the previous state of the application
     */
    private void loadSessionState() {
        System.out.println("loading libraries...");
        List<String> libraryPathList = m_databaseManager.getLibraries();
        for (String libraryPath : libraryPathList) {
            m_songManager.addLibrary(libraryPath);
        }

        List<Song> allSongsInModel = m_songManager.getAllSongs();

        System.out.println("loading playlists...");
        List<String> playlistNameList = m_databaseManager.getPlaylists();
        for (String playlistName : playlistNameList) {
            int lastSongPlayedIndex = m_databaseManager.getPlaylistLastPlayedSongIndex(playlistName);

            Playlist playlist = new Playlist(playlistName, lastSongPlayedIndex);
            List<String> songPaths = m_databaseManager.getSongsInPlaylist(playlist.getM_playlistName());
            playlist.addSongs(filterSongs(allSongsInModel, songPaths));

            m_songManager.addPlaylist(playlist);
        }

        // Get previous menu options from file
        MenuOptions menuOptions = new MenuOptions(m_filePersistentStorage.getCenterPanelOption(),
                m_filePersistentStorage.getLeftPanelOption());
        m_songManager.setM_menuOptions(menuOptions);

        // Get previously selected right panel folder from file
        File previousRightPanelFolder = null;
        String previousRightFolderPath = m_filePersistentStorage.getRightPanelFolder();
        System.out.println("PREVIOUS RIGHT FOLDER PATH: " + previousRightFolderPath);
        if (!previousRightFolderPath.isEmpty()) {
            previousRightPanelFolder = new File(previousRightFolderPath);
        }
        m_songManager.setM_rightFolderSelected(previousRightPanelFolder);

        // Get previously selected center panel folder from file
        File previousCenterPanelFolder = null;
        String previousCenterFolderPath = m_filePersistentStorage.getCenterPanelFolder();
        System.out.println("PREVIOUS CENTER FOLDER PATH: " + previousCenterFolderPath);
        if (!previousCenterFolderPath.isEmpty()) {
            previousCenterPanelFolder = new File(previousCenterFolderPath);
        }
        m_songManager.setM_selectedCenterFolder(previousCenterPanelFolder);

        System.out.println("loading history");
        List<String> historySongPaths = m_databaseManager.getHistory();
        m_musicPlayerManager.loadHistory(filterSongs(allSongsInModel, historySongPaths));

        System.out.println("loading playback queue");
        List<String> playbackQueueSongPaths = m_databaseManager.getHistory();
        m_musicPlayerManager.loadPlaybackQueue(filterSongs(allSongsInModel, playbackQueueSongPaths));
    }

    /**
     * Filter songs based on the song paths given
     *
     * @return list of filtered songs
     */
    private List<Song> filterSongs(List<Song> songs, List<String> songPaths) {
        List<Song> filteredSongs = new ArrayList<>();
        for (Song song : songs){
            if (songPaths.contains(song.getFile().getAbsolutePath())) {
                filteredSongs.add(song);
            }
        }
        return filteredSongs;
    }

     /** Starting routine of the application
     *
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) {
        stageCopy = primaryStage;
        //disable jaudiotagger logging

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
            loadSessionState();
        }

        primaryStage.setTitle(APP_TITLE);

        createRootUI(m_songManager, m_musicPlayerManager);

        Watcher watcher = new Watcher(m_songManager);
        watcher.startWatcher();

        primaryStage.setOnCloseRequest(e -> closeApp(m_musicPlayerManager, watcher));

        primaryStage.setScene(new Scene(m_rootUI, 1200, 650));
        primaryStage.setMinHeight(MIN_WINDOW_HEIGHT);
        primaryStage.setMinWidth(MIN_WINDOW_WIDTH);
        primaryStage.getIcons().add(new Image(GAMMA_LOGO_IMAGE_URL));
        primaryStage.show();

        Media sound = new Media(new File(START_SOUND_PATH).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

    /**
     * Save session states in new thread and show a progress bar
     *
     * @param musicPlayerManager
     * @param watcher
     */
    private void closeApp(final MusicPlayerManager musicPlayerManager, final Watcher watcher) {
        final int CLOSING_WINDOW_WIDTH = 400;
        final int CLOSING_WINDOW_HEIGHT = 80;
        watcher.stopWatcher();

        this.minimodeTurnOff();
        m_rootUI.minimodeTurnOff();

        ProgressBar progressBar = new ProgressBar();
        BorderPane closingWindow = new BorderPane();
        closingWindow.setCenter(progressBar);

        Label text = new Label("Saving current session...");
        text.setFont(new Font(16));
        text.setPadding(new Insets(10, CLOSING_WINDOW_WIDTH/4, 10, CLOSING_WINDOW_WIDTH/4));
        closingWindow.setTop(text);

        Stage closingStage = new Stage();
        closingStage.setTitle(APP_TITLE);
        closingStage.getIcons().add(new Image(GAMMA_LOGO_IMAGE_URL));
        closingStage.setScene(new Scene(closingWindow, CLOSING_WINDOW_WIDTH, CLOSING_WINDOW_HEIGHT));
        closingStage.show();

        Task closeTask = new Task() {
            @Override
            protected Object call() throws Exception {
                musicPlayerManager.stopSong();
                savePlaylistSongs();
                saveFileTreeState();
                m_filePersistentStorage.saveConfigFile(m_songManager.getM_rightFolderSelected(),
                        m_songManager.getM_selectedCenterFolder(), m_songManager.getM_menuOptions());
                m_databaseManager.closeConnection();

                Platform.exit();

                return null;
            }
        };

        progressBar.progressProperty().bind(closeTask.progressProperty());

        new Thread(closeTask).start();
    }

    /**
     * Save songs in all playlist
     * Save Playlist songs to database
     */
    private void savePlaylistSongs() {
        for (Playlist playlist : m_songManager.getM_playlists()) {
            m_databaseManager.savePlaylistSongs(playlist);
        }
    }

    /**
     * Create root UI
     *
     * @param songManager
     * @param musicPlayerManager
     */
    private void createRootUI(SongManager songManager, MusicPlayerManager musicPlayerManager) {
        // Get previous expanded states
        List<String> libraryUIExpandedPaths = m_databaseManager.getExpandedLeftTreeViewItems();
        List<String> rightPanelExpandedPaths = m_databaseManager.getExpandedRightTreeViewItems();

        // Create main UI
        m_rootUI = new MainUI(songManager,
                              musicPlayerManager,
                              m_databaseManager,
                              m_filePersistentStorage,
                              libraryUIExpandedPaths,
                              rightPanelExpandedPaths,
                              this
                              );
    }

    /**
     * Save left and right file tree expanded states
     */
    private void saveFileTreeState() {
        saveLeftFileTreeExpandedState();
        saveRightFileTreeExpandedState();
    }

    /**
     * Save left file tree expanded states
     * Save left panel file tree expanded state
     */
    private void saveLeftFileTreeExpandedState() {
        List<String> libraryUIExpandedPaths = m_rootUI.getLibraryUIExpandedPaths();
        if (libraryUIExpandedPaths != null) {
            m_databaseManager.saveLeftTreeViewState(libraryUIExpandedPaths);
            for (String path : libraryUIExpandedPaths) {
                System.out.println("LIBRARY EXPANDED PATH: " + path);
            }
        }
    }

    /**
     * Save right file tree expanded states
     * Save right panel file tree expanded state
     */
    private void saveRightFileTreeExpandedState() {
        List<String> dynamicTreeViewUIExpandedPaths = m_rootUI.getDynamicTreeViewUIExpandedPaths();
        if (dynamicTreeViewUIExpandedPaths != null) {
            m_databaseManager.saveRightTreeViewState(dynamicTreeViewUIExpandedPaths);
            for (String path : dynamicTreeViewUIExpandedPaths) {
                System.out.println("DYNAMIC TREEVIEW EXPANDED PATH: " + path);
            }
        }
    }

    /* Toggles minimode on, shrinks window
    */
    public void minimodeTurnOn() {
        stageCopy.setHeight(MINI_MODE_HEIGHT);
        stageCopy.setWidth(MINI_MODE_WIDTH);
    }

    /* Toggles minimode off, re-expands window to original size
    */
    public void minimodeTurnOff() {
        stageCopy.setWidth(ORIGINAL_WINDOW_WIDTH);
        stageCopy.setHeight(ORIGINAL_WINDOW_HEIGHT);
    }
}
