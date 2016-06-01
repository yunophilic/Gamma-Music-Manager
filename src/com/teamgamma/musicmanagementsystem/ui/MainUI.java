package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.Library;
import com.teamgamma.musicmanagementsystem.SongManager;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * MainUI Class.
 */
public class MainUI extends BorderPane{
    private static SongManager model;

    public MainUI(SongManager model){
        super();

        this.model = model;

        this.setLeft(leftPane());
        this.setRight(rightPane());
        this.setCenter(centerPane());
        //this.setBottom(bottomePane());
    }

    private Node leftPane(){
        return sidePane(model.getM_myLibrary());
    }

    private Node rightPane(){
        return sidePane(model.getM_externalLibrary());
    }

    private Node sidePane(Library library){
        BorderPane sidePane = new BorderPane();

        LibraryUI libraryUI = new LibraryUI(library);
        libraryUI.setMaxWidth(Double.MAX_VALUE);
        libraryUI.setMaxHeight(Double.MAX_VALUE);

        SongInfoUI songInfoUI = new SongInfoUI();
        songInfoUI.setMaxWidth(Double.MAX_VALUE);
        songInfoUI.setMaxHeight(Double.MAX_VALUE);

        sidePane.setCenter(libraryUI);
        sidePane.setBottom(songInfoUI);
        return sidePane;
    }

    private Node topNodes(){
        return new Label("Top");
    }

    private Node bottomePane(){
        //return new Label("Music Player");

        MusicPlayerManager musicManager = new MusicPlayerManager();
        return new MusicPlayerUI(musicManager);
    }

    private Node centerPane(){
        BorderPane centerPane = new BorderPane();

        MusicPlayerManager musicManager = new MusicPlayerManager();
        MusicPlayerUI musicPlayerUI = new MusicPlayerUI(musicManager);

        centerPane.setBottom(musicPlayerUI);
        centerPane.setCenter(new ContentListUI());
        return centerPane;
    }
}
