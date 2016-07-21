package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.ApplicationController;
import com.teamgamma.musicmanagementsystem.model.DatabaseManager;
import com.teamgamma.musicmanagementsystem.model.FilePersistentStorage;
import com.teamgamma.musicmanagementsystem.model.SongManager;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;

import com.teamgamma.musicmanagementsystem.watchservice.ApplicationControllerObserver;
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
    private SongManager m_model;
    private MusicPlayerManager m_musicPlayerManager;
    private DatabaseManager m_databaseManager;
    private FilePersistentStorage m_filePersistentStorage;
    private LibraryUI m_libraryUI;
    private DynamicTreeViewUI m_rightFilePane;
    private MainUI m_main = this;
    private MenuUI m_menuUI;
    private ApplicationController m_applicationController;
    private BorderPane leftPane;
    private BorderPane centerPane;
    private BorderPane rightPane;

    private List<String> library;
    private List<String> dynamicTree;




public MainUI(SongManager model,
                  MusicPlayerManager musicPlayerManager,
                  DatabaseManager databaseManager,
                  FilePersistentStorage filePersistentStorage,
                  List<String> libraryExpandedPaths,
                  List<String> dynamicTreeViewExpandedPaths,
                  MenuUI menuUI,
                  ApplicationController applicationController) {
        super();

        m_model = model;
        m_musicPlayerManager = musicPlayerManager;
        m_databaseManager = databaseManager;
        m_filePersistentStorage = filePersistentStorage;
        m_applicationController = applicationController;
        m_menuUI = menuUI;

        library = libraryExpandedPaths;
        dynamicTree = dynamicTreeViewExpandedPaths;

        this.setLeft(leftPane(libraryExpandedPaths));
        this.setRight(rightPane());
        this.setCenter(centerPane(dynamicTreeViewExpandedPaths));
        this.setTop(topPane());
        this.setBottom(bottomPane());
    }

    private Node leftPane(List<String> libraryExpandedPaths) {
        m_libraryUI = new LibraryUI(m_model, m_musicPlayerManager, m_databaseManager, libraryExpandedPaths);

        leftPane = new BorderPane();
        leftPane.setCenter(m_libraryUI);
        leftPane.setPrefWidth(250);
        return leftPane;
    }

    private Node rightPane() {
        PlaylistUI playlistUI = new PlaylistUI(m_model, m_musicPlayerManager, m_databaseManager);

        VBox musicPlayerWrapper = new VBox();
        musicPlayerWrapper.getChildren().add(new MusicPlayerHistoryUI(m_musicPlayerManager));
        musicPlayerWrapper.getChildren().add(new MusicPlayerUI(m_model, m_musicPlayerManager, m_databaseManager,
                m_filePersistentStorage));
        musicPlayerWrapper.getChildren().add(new MusicPlayerPlaybackQueueUI(m_musicPlayerManager, m_model));

        rightPane = new BorderPane();
        rightPane.setCenter(playlistUI);
        rightPane.setBottom(musicPlayerWrapper);
        rightPane.setPrefWidth(350);

        return rightPane;
    }

    private Node topPane() {
        return new MenuUI(m_model, m_databaseManager, m_filePersistentStorage, m_main, m_applicationController);
}

    private Node bottomPane() {
        return new BorderPane();
    }

    private Node centerPane(List<String> dynamicTreeViewExpandedPaths) {
        centerPane = new BorderPane();

        centerPane.setCenter(createFileExplorer(dynamicTreeViewExpandedPaths));
        return centerPane;
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

    public void miniOn() {
        this.setLeft(rightPane());
    }
    public void miniOff() {
        this.setLeft(leftPane(library));
        this.setRight(rightPane());
        this.setCenter(centerPane(dynamicTree));
        this.setTop(topPane());
        this.setBottom(bottomPane());
        centerPane.setVisible(true);
        rightPane.setVisible(true);
    }
}
