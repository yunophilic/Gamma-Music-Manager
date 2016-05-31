package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * MainUI Class. For now used for user input on the command line.
 */
public class MainUI extends BorderPane{

    public MainUI(){
        // Do Nothing.
        super();

        //BorderPane root = new BorderPane();

        this.setLeft(leftPane());
        this.setRight(rightPane());
        this.setCenter(centerPane());
        this.setBottom(bottomePane());
    }

    private Node leftPane(){
        return new LibraryUI("Library UI (myLibrary)");
    }

    private Node rightPane(){
        VBox rightNodes = new VBox();

        LibraryUI libraryUI = new LibraryUI("Library UI (externLibrary)");
        libraryUI.setMaxWidth(Double.MAX_VALUE);
        libraryUI.setMaxHeight(Double.MAX_VALUE);

        SongInfoUI songInfoUI = new SongInfoUI();
        songInfoUI.setMaxWidth(Double.MAX_VALUE);
        songInfoUI.setMaxHeight(Double.MAX_VALUE);

        rightNodes.getChildren().add(libraryUI);
        rightNodes.getChildren().add(songInfoUI);

        return rightNodes;
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
        return new ContentListUI();
    }
}
