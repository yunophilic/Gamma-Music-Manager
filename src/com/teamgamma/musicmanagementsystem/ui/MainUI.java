package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.ApplicationController;
import com.teamgamma.musicmanagementsystem.model.DatabaseManager;
import com.teamgamma.musicmanagementsystem.model.FilePersistentStorage;
import com.teamgamma.musicmanagementsystem.model.SongManager;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * MainUI Class.
 */
public class MainUI extends BorderPane {
    private final double TWO_HUNDRED_AND_FIFTY = 250;
    private final double THREE_HUNDRED_AND_FIFTY = 350;
    private SongManager m_model;
    private MusicPlayerManager m_musicPlayerManager;
    private DatabaseManager m_databaseManager;
    private FilePersistentStorage m_filePersistentStorage;
    private LibraryUI m_libraryUI;
    private DynamicTreeViewUI m_rightFilePane;
    private ApplicationController m_applicationController;
    private BorderPane m_leftPane;
    private BorderPane m_centerPane;
    private BorderPane m_rightPane;
    private List<String> m_library;
    private List<String> m_dynamicTree;

    public MainUI(SongManager model,
                  MusicPlayerManager musicPlayerManager,
                  DatabaseManager databaseManager,
                  FilePersistentStorage filePersistentStorage,
                  List<String> libraryExpandedPaths,
                  List<String> dynamicTreeViewExpandedPaths,
                  ApplicationController applicationController) {
        super();

        m_model = model;
        m_musicPlayerManager = musicPlayerManager;
        m_databaseManager = databaseManager;
        m_filePersistentStorage = filePersistentStorage;
        m_applicationController = applicationController;

        m_library = libraryExpandedPaths;
        m_dynamicTree = dynamicTreeViewExpandedPaths;

        this.setLeft(leftPane(libraryExpandedPaths));
        this.setRight(rightPane());
        this.setCenter(centerPane(dynamicTreeViewExpandedPaths));
        this.setTop(topPane());
        this.setBottom(bottomPane());
    }

    private Node leftPane(List<String> libraryExpandedPaths) {
        m_libraryUI = new LibraryUI(m_model, m_musicPlayerManager, m_databaseManager, libraryExpandedPaths);

        m_leftPane = new BorderPane();
        m_leftPane.setCenter(m_libraryUI);
        m_leftPane.setPrefWidth(TWO_HUNDRED_AND_FIFTY);
        return m_leftPane;
    }

    private Node rightPane() {
        PlaylistUI playlistUI = new PlaylistUI(m_model, m_musicPlayerManager, m_databaseManager);

        VBox musicPlayerWrapper = new VBox();
        musicPlayerWrapper.getChildren().add(new MusicPlayerHistoryUI(m_model, m_musicPlayerManager));
        musicPlayerWrapper.getChildren().add(new MusicPlayerUI(m_model, m_musicPlayerManager, m_databaseManager,
                m_filePersistentStorage));
        musicPlayerWrapper.getChildren().add(new MusicPlayerPlaybackQueueUI(m_musicPlayerManager, m_model));

        m_rightPane = new BorderPane();
        m_rightPane.setCenter(playlistUI);
        m_rightPane.setBottom(musicPlayerWrapper);
        m_rightPane.setPrefWidth(THREE_HUNDRED_AND_FIFTY);

        return m_rightPane;
    }

    private Node topPane() {
        return new MenuUI(m_model, m_databaseManager, m_filePersistentStorage, this, m_applicationController);
    }

    private Node bottomPane() {
        return new BorderPane();
    }

    private Node centerPane(List<String> dynamicTreeViewExpandedPaths) {
        m_centerPane = new BorderPane();
        m_centerPane.setCenter(createFileExplorer(dynamicTreeViewExpandedPaths));
        return m_centerPane;
    }

    private Node createFileExplorer(List<String> dynamicTreeViewExpandedPaths) {
        HBox pane = new HBox();

        ContentListUI contentListUI = new ContentListUI(m_model, m_musicPlayerManager, m_databaseManager);
        m_rightFilePane = new DynamicTreeViewUI(m_model, m_musicPlayerManager, m_databaseManager, dynamicTreeViewExpandedPaths);

        HBox.setHgrow(contentListUI, Priority.ALWAYS);
        HBox.setHgrow(m_rightFilePane, Priority.ALWAYS);

        pane.getChildren().addAll(contentListUI, m_rightFilePane);

        return pane;
    }

    public List<String> getLibraryUIExpandedPaths() {
        return m_libraryUI.getExpandedPaths();
    }

    public List<String> getDynamicTreeViewUIExpandedPaths() {
        return m_rightFilePane.getExpandedPaths();
    }

    /**
     * Turns on minimode. Sets leftPane to be
     *  the rightPane (MusicPlayer)
     */
    public void minimodeTurnOn() {
        this.setLeft(rightPane());
    }

    /**
     * Turns off minimode, resetting the panes to their
     *  original places
     */
    public void minimodeTurnOff() {
        this.setLeft(leftPane(m_library));
        this.setRight(rightPane());
        this.setCenter(centerPane(m_dynamicTree));
        this.setTop(topPane());
        this.setBottom(bottomPane());
        m_centerPane.setVisible(true);
        m_rightPane.setVisible(true);
    }
}
