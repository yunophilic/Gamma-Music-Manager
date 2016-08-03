package com.teamgamma.musicmanagementsystem;

import com.teamgamma.musicmanagementsystem.model.*;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import com.teamgamma.musicmanagementsystem.ui.MainUI;
import com.teamgamma.musicmanagementsystem.ui.PromptUI;
import com.teamgamma.musicmanagementsystem.watchservice.Watcher;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
    private static final double MIN_WINDOW_WIDTH = 100;
    private static final double MIN_WINDOW_HEIGHT = 100;
    private static final double UNDO_SET_MAX_WIDTH = 9000;
    private static final String GAMMA_LOGO_IMAGE_URL = "res" + File.separator + "gamma-logo.png";
    private static final String START_SOUND_PATH = System.getProperty("user.dir") + File.separator + "src" + File.separator
            + "res" + File.separator + "start-sound.mp3";
    public static final String SAVING_MESSAGE = "Saving current session...";
    public static final String APP_TITLE = "Gamma Music Manager";

    private SongManager m_songManager;
    private MusicPlayerManager m_musicPlayerManager;
    private DatabaseManager m_databaseManager;
    private FilePersistentStorage m_filePersistentStorage;
    private MainUI m_rootUI;
    private Stage m_stageCopy;

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

        System.out.println("loading playlists...");
        List<String> playlistNameList = m_databaseManager.getPlaylists();
        for (String playlistName : playlistNameList) {
            int lastSongPlayedIndex = m_databaseManager.getPlaylistLastPlayedSongIndex(playlistName);
            double percentage = m_databaseManager.getResumeTime(playlistName);

            Playlist playlist = new Playlist(playlistName, lastSongPlayedIndex);
            playlist.setM_songResumeTime(percentage);

            List<String> playlistSongPaths = m_databaseManager.getSongsInPlaylist(playlist.getM_playlistName());
            playlist.addSongs(m_songManager.getSongs(playlistSongPaths));

            m_songManager.addPlaylist(playlist);
        }

        // Get previous menu options from file
        MenuOptions menuOptions = new MenuOptions(
                m_filePersistentStorage.getShowAllFilesInCenterPanelOption(),
                m_filePersistentStorage.getLeftPanelShowOnlyFoldersOption(),
                m_filePersistentStorage.getShowFilesInFolderHit(),
                m_filePersistentStorage.getHideRightFilePane()
        );
        m_songManager.setM_menuOptions(menuOptions);

        // Get previously selected right panel folder from file
        String previousRightFolderPath = m_filePersistentStorage.getRightPanelFolder();
        System.out.println("PREVIOUS RIGHT FOLDER PATH: " + previousRightFolderPath);
        if (!previousRightFolderPath.isEmpty()) {
            File previousRightPanelFolder = new File(previousRightFolderPath);

            if (previousRightPanelFolder.exists()) {
                m_songManager.setM_rightFolderSelected(previousRightPanelFolder);
            }
        }

        // Get previously selected center panel folder from file
        String previousCenterFolderPath = m_filePersistentStorage.getCenterPanelFolder();
        System.out.println("PREVIOUS CENTER FOLDER PATH: " + previousCenterFolderPath);
        if (!previousCenterFolderPath.isEmpty()) {
            File previousCenterPanelFolder = new File(previousCenterFolderPath);

            if (previousCenterPanelFolder.exists()) {
                m_songManager.setM_selectedCenterFolder(previousCenterPanelFolder);
            }
        }

        // Get previously selected playlist from file
        String previousSelectedPlaylist = m_filePersistentStorage.getSelectedPlaylist();
        System.out.println("PREVEIOUS SELECTED PLAYLIST: " + previousSelectedPlaylist);
        if (!previousSelectedPlaylist.isEmpty()) {
            Playlist playlist = m_songManager.findPlaylist(previousSelectedPlaylist);
            if (playlist != null) {
                m_songManager.setM_selectedPlaylist(playlist);
            }
        }

        System.out.println("loading history");
        List<String> historySongPaths = m_databaseManager.getHistory();
        m_musicPlayerManager.loadHistory(m_songManager.getSongs(historySongPaths));

        System.out.println("loading playback queue");
        List<String> playbackQueueSongPaths = m_databaseManager.getPlaybackQueue();
        m_musicPlayerManager.loadPlaybackQueue(m_songManager.getSongs(playbackQueueSongPaths));
    }

    /**
     * Starting routine of the application
     *
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) {
        m_stageCopy = primaryStage;
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

        playStartUpSound();
    }

    /**
     * Play the start up sound when the application is launched or when user switches out of minimode
     */
    public void playStartUpSound() {
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

        watcher.stopWatcher();
        musicPlayerManager.setCurrentPlaylistSongPercentage();
        Stage closingStage = new Stage();
        Task closeTask = new Task() {
            @Override
            protected Object call() throws Exception {
                musicPlayerManager.stopSong();
                savePlaylistSongs();
                savePlaylistsResumeTimes();
                savePlaybackQueue();
                saveFileTreeState();
                m_filePersistentStorage.saveConfigFile(
                        m_songManager.getM_rightFolderSelected(),
                        m_songManager.getM_selectedCenterFolder(),
                        m_songManager.getM_selectedPlaylist(),
                        m_songManager.getM_menuOptions(),
                        m_rootUI.getCenterTableColumnsVisibility(),
                        m_rootUI.getPlaylistTableColumnsVisibility()
                );
                m_databaseManager.closeConnection();

                Platform.exit();

                return null;
            }
        };

        PromptUI.createLoadingScreen(closingStage, SAVING_MESSAGE ,closeTask);
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
     * Save resume time for each existing playlist
     */
    public void savePlaylistsResumeTimes() {
        m_databaseManager.clearResumeTime();
        for (Playlist playlist : m_songManager.getM_playlists()) {
            m_databaseManager.savePlaylistResumeTime(playlist.getM_playlistName(), playlist.getM_songResumeTime());
        }
    }

    /**
     * Clear the PlaybackQueue table in the database and re-insert songs in the current queue
     */
    private void savePlaybackQueue() {
        Collection<Song> songsFromQueue = m_musicPlayerManager.getPlayingQueue();
        m_databaseManager.clearPlaybackQueue();
        for (Song song : songsFromQueue) {
            m_databaseManager.addToPlaybackQueueTail(song.getFile().getAbsolutePath());
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
        Map<String, Boolean> centerTableColumnVisibilityMap = m_filePersistentStorage.getCenterTableColumnsVisibility();
        Map<String, Boolean> playlistTableColumnVisibilityMap = m_filePersistentStorage.getPlaylistTableColumnsVisibility();

        // Create main UI
        m_rootUI = new MainUI(songManager,
                              musicPlayerManager,
                              m_databaseManager,
                              m_filePersistentStorage,
                              libraryUIExpandedPaths,
                              rightPanelExpandedPaths,
                              centerTableColumnVisibilityMap,
                              playlistTableColumnVisibilityMap,
                              this);
    }

    /**
     * Save left and right panel file tree expanded states
     */
    private void saveFileTreeState() {
        saveLeftFileTreeExpandedState();
        saveRightFileTreeExpandedState();
    }

    /**
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

    /**
     *  Toggles minimode on, shrinks window
     */
    public void minimodeTurnOn() {
        m_stageCopy.setHeight(MINI_MODE_HEIGHT);
        m_stageCopy.setWidth(MINI_MODE_WIDTH);
        m_stageCopy.setMaxWidth(MINI_MODE_WIDTH);
    }

    /**
     *  Toggles minimode off, re-expands window to original size
     */
    public void minimodeTurnOff() {
        m_stageCopy.setWidth(ORIGINAL_WINDOW_WIDTH);
        m_stageCopy.setHeight(ORIGINAL_WINDOW_HEIGHT);
        m_stageCopy.setMaxWidth(MINI_MODE_WIDTH + UNDO_SET_MAX_WIDTH);
    }
}
