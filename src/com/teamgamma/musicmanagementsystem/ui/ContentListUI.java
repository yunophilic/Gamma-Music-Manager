package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.Song;
import com.teamgamma.musicmanagementsystem.SongManager;
import com.teamgamma.musicmanagementsystem.SongManagerObserver;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.io.File;
import java.util.List;

/**
 * UI class for list of songs in center of application
 */
public class ContentListUI extends StackPane{
    private SongManager model;
    private GridPane gridPane;

    public ContentListUI(SongManager model){
        super();

        this.model = model;

        this.getChildren().add(new Label("Contents in folder"));

        gridPane = new GridPane();

        //gridPane.add(new Label("Contents in folder"), 10, 20);

        setCssStyle();

        registerAsCenterFolderObserver();

    }

    /**
     * Register as a observer to changes for the folder selected to be displayed here
     */
    private void registerAsCenterFolderObserver() {
        model.addObserver(new SongManagerObserver() {
            @Override
            public void librariesChanged() {
                /* Do nothing */
            }

            @Override
            public void centerFolderChanged() {
                clearList();
                updateList();
            }

            @Override
            public void rightFolderChanged() {
                /* Do nothing */
            }

            @Override
            public void songChanged() {
                /* Do nothing */
            }
        });
    }

    private void clearList(){
        System.out.println("Clearing list...");
        gridPane.getChildren().clear();
        this.getChildren().clear();
    }

    private void updateList(){
        gridPane = new GridPane();
        System.out.println("Updating list...");
        List<Song> songs = model.getCenterPanelSongs();
        int i = 1;
        for (Song song: songs){
            System.out.println("Found new song: " + song.getM_file().getAbsolutePath());
            gridPane.add(new Label("Song Name   "), 0, 0);
            gridPane.add(new Label("Genre   "), 1, 0);
            gridPane.add(new Label("Artist   "), 2, 0);
            gridPane.add(new Label("Rating   "), 3, 0);
            gridPane.add(new Label(song.getM_title() + "   "), 0, i);
            gridPane.add(new Label(song.getM_genre() + "   "), 1, i);
            gridPane.add(new Label(song.getM_artist() + "   "), 2, i);
            gridPane.add(new Label(song.getM_rating() + "   "), 3, i);
            i++;
        }
        this.getChildren().add(gridPane);
    }

    private void setCssStyle(){
        final String cssDefault = "-fx-border-color: black;\n";
        this.setStyle(cssDefault);
    }
}
