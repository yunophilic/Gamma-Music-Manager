package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.model.DatabaseManager;
import com.teamgamma.musicmanagementsystem.model.FilePersistentStorage;
import com.teamgamma.musicmanagementsystem.model.SongManager;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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

    public MainUI(SongManager model,
                  MusicPlayerManager musicPlayerManager,
                  DatabaseManager databaseManager,
                  FilePersistentStorage filePersistentStorage,
                  List<String> libraryExpandedPaths,
                  List<String> dynamicTreeViewExpandedPaths) {
        super();

        m_model = model;
        m_musicPlayerManager = musicPlayerManager;
        m_databaseManager = databaseManager;
        m_filePersistentStorage = filePersistentStorage;

        this.setLeft(leftPane(libraryExpandedPaths));
        this.setRight(rightPane());
        this.setCenter(centerPane(dynamicTreeViewExpandedPaths));
        this.setTop(topPane());
        this.setBottom(bottomPane());
    }

    private Node leftPane(List<String> libraryExpandedPaths) {
        m_libraryUI = new LibraryUI(m_model, m_musicPlayerManager, m_databaseManager, libraryExpandedPaths);

        BorderPane leftPane = new BorderPane();
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

        BorderPane rightPane = new BorderPane();
        rightPane.setCenter(playlistUI);
        rightPane.setBottom(musicPlayerWrapper);
        rightPane.setPrefWidth(350);

        return rightPane;
    }

    private Node topPane() {
        HBox wrapper = new HBox();
        wrapper.getChildren().add(new MenuUI(m_model, m_databaseManager, m_filePersistentStorage));

        TextField searchText = new TextField();
        Button serach = new Button("Search");
        serach.setOnMouseClicked(event -> {
            System.out.println("Searching for " + searchText.getText());
            m_model.searchForFilesAndFolders(searchText.getText());
        });
        wrapper.getChildren().addAll(searchText, serach);
        return wrapper;
    }

    private Node bottomPane() {
        return new BorderPane();
    }

    private Node centerPane(List<String> dynamicTreeViewExpandedPaths) {
        BorderPane centerPane = new BorderPane();

        centerPane.setCenter(createFileExplorer(dynamicTreeViewExpandedPaths));
        return centerPane;
    }

    private Node createFileExplorer(List<String> dynamicTreeViewExpandedPaths) {
        VBox wrapper = new VBox();
        HBox pane = new HBox();

        ContentListUI contentListUI = new ContentListUI(m_model, m_musicPlayerManager, m_databaseManager);

        m_rightFilePane = new DynamicTreeViewUI(m_model, m_musicPlayerManager, m_databaseManager, dynamicTreeViewExpandedPaths);

        HBox.setHgrow(contentListUI, Priority.ALWAYS);
        HBox.setHgrow(m_rightFilePane, Priority.ALWAYS);

        pane.getChildren().addAll(contentListUI, m_rightFilePane);
        wrapper.getChildren().add(new SearchResultUI(m_model));
        wrapper.getChildren().add(pane);

        return wrapper;
    }

    public List<String> getLibraryUIExpandedPaths() {
        return m_libraryUI.getExpandedPaths();
    }

    public List<String> getDynamicTreeViewUIExpandedPaths() {
        return m_rightFilePane.getExpandedPaths();
    }
}
