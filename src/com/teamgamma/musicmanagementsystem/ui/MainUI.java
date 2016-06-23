package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.model.DatabaseManager;
import com.teamgamma.musicmanagementsystem.model.SongManager;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * MainUI Class.
 */
public class MainUI extends BorderPane {
    private SongManager m_model;
    private MusicPlayerManager m_musicPlayerManager;
    private DatabaseManager m_databaseManager;

    public MainUI(SongManager model, MusicPlayerManager musicPlayerManager, DatabaseManager databaseManager) {
        super();

        m_model = model;
        m_musicPlayerManager = musicPlayerManager;
        m_databaseManager = databaseManager;

        this.setLeft(leftPane());
        this.setRight(rightPane());
        this.setCenter(centerPane());
        this.setTop(topPane());
        this.setBottom(bottomPane());
    }

    private Node leftPane() {
        BorderPane leftPane = new BorderPane();

        LibraryUI libraryUI = new LibraryUI(m_model, m_musicPlayerManager, m_databaseManager);

        leftPane.setCenter(libraryUI);
        leftPane.setPrefWidth(250);
        return leftPane;
    }

    private Node rightPane() {
        BorderPane rightPane = new BorderPane();
        rightPane.setPrefWidth(250);

        PlaylistUI playlistUI = new PlaylistUI(m_model, m_musicPlayerManager, m_databaseManager);
        rightPane.setCenter(playlistUI);

        VBox musicPlayerWrapper = new VBox();
        musicPlayerWrapper.getChildren().add(new MusicPlayerUI(m_musicPlayerManager));
        musicPlayerWrapper.getChildren().add(new MusicPlayerHistoryUI(m_musicPlayerManager));

        rightPane.setBottom(musicPlayerWrapper);

        return rightPane;
    }

    private Node topPane() {
        return new MenuUI(m_model, m_databaseManager);
    }

    private Node bottomPane() {
        BorderPane musicPlayerWrapper = new BorderPane();
        return musicPlayerWrapper;
    }

    private Node centerPane() {
        BorderPane centerPane = new BorderPane();

        centerPane.setCenter(createFileExplorer());
        return centerPane;
    }

    private Node createFileExplorer() {
        HBox pane = new HBox();

        ContentListUI contentListUI = new ContentListUI(m_model, m_musicPlayerManager, m_databaseManager);
        DynamicTreeViewUI rightFilePane = new DynamicTreeViewUI(m_model, m_musicPlayerManager, m_databaseManager);

        HBox.setHgrow(contentListUI, Priority.ALWAYS);
        HBox.setHgrow(rightFilePane, Priority.ALWAYS);

        pane.getChildren().addAll(contentListUI, rightFilePane);

        return pane;
    }
}
