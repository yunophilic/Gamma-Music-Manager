package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.model.PersistentStorage;
import com.teamgamma.musicmanagementsystem.model.SongManager;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 * MainUI Class.
 */
public class MainUI extends BorderPane {
    private SongManager model;

    private MusicPlayerManager m_musicPlayerManager;

    public MainUI(SongManager model, MusicPlayerManager musicPlayerManager) {
        super();

        this.model = model;
        m_musicPlayerManager = musicPlayerManager;

        this.setLeft(leftPane());
        this.setRight(rightPane());
        this.setCenter(centerPane());
        this.setTop(topPane());
        this.setBottom(bottomePane());
    }

    private Node leftPane() {
        BorderPane leftPane = new BorderPane();

        LibraryUI libraryUI = new LibraryUI(model);

        leftPane.setCenter(libraryUI);
        leftPane.setPrefWidth(250);
        leftPane.setBottom(new MusicPlayerHistory(m_musicPlayerManager));
        return leftPane;
    }

    private Node rightPane() {
        BorderPane rightPane = new BorderPane();
        DynamicTreeViewUI dynamicTreeViewUI = new DynamicTreeViewUI(model);

        rightPane.setCenter(dynamicTreeViewUI);
        rightPane.setPrefWidth(250);

        return rightPane;
    }

    private Node topPane() {
        return new MenuUI(model);
    }

    private Node bottomePane() {
        BorderPane musicPlayerWrapper = new BorderPane();
        //musicPlayerWrapper.setCenter(new MusicPlayerHistory(m_musicPlayerManager));
        return musicPlayerWrapper;
    }

    private Node centerPane() {
        BorderPane centerPane = new BorderPane();

        centerPane.setCenter(new ContentListUI(model, m_musicPlayerManager));
        centerPane.setBottom(new MusicPlayerUI(m_musicPlayerManager));
        return centerPane;
    }
}
