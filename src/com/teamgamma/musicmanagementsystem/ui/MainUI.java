package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.model.DatabaseManager;
import com.teamgamma.musicmanagementsystem.model.SongManager;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

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
        leftPane.setBottom(new MusicPlayerHistoryUI(m_musicPlayerManager));
        return leftPane;
    }

    private Node rightPane() {
        BorderPane rightPane = new BorderPane();
        DynamicTreeViewUI dynamicTreeViewUI = new DynamicTreeViewUI(m_model, m_musicPlayerManager, m_databaseManager);

        rightPane.setCenter(dynamicTreeViewUI);
        rightPane.setPrefWidth(250);

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

        centerPane.setCenter(setContentListAndPlaylist());
        centerPane.setBottom(new MusicPlayerUI(m_musicPlayerManager));
        return centerPane;
    }

    private Node setContentListAndPlaylist() {
        HBox pane = new HBox();

        pane.getChildren().add(new ContentListUI(m_model, m_musicPlayerManager, m_databaseManager));
        pane.getChildren().add(new PlaylistUI(m_model, m_musicPlayerManager));

        return pane;
    }
}
