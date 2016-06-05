package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.Song;
import com.teamgamma.musicmanagementsystem.SongManager;
import com.teamgamma.musicmanagementsystem.SongManagerObserver;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.io.File;
import java.util.List;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
/**
 * UI class for list of songs in center of application
 */
public class ContentListUI extends StackPane {
    private SongManager model;
    private GridPane gridPane;

    public ContentListUI(SongManager model) {
        super();

        this.model = model;

        setEmptyText();

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

            @Override
            public void fileChanged() {
                clearList();
                //setEmptyText();
                updateList();
            }
        });
    }

    private void setEmptyText() {
        this.getChildren().add(new Label("Contents in folder"));
    }

    private void clearList() {
        System.out.println("Clearing list...");
        gridPane.getChildren().clear();
        this.getChildren().clear();
    }

    private void updateList() {
        if (model.getM_selectedCenterFolder() == null) {
            setEmptyText();
        } else {
            gridPane = new GridPane();
            System.out.println("Updating list...");
            List<Song> songs = model.getCenterPanelSongs();
            gridPane.add(new Label("Song Name   "), 0, 0);
            gridPane.add(new Label("Genre   "), 1, 0);
            gridPane.add(new Label("Artist   "), 2, 0);
            gridPane.add(new Label("Rating   "), 3, 0);

            int row = 1;
            for (Song song : songs) {
                System.out.println("Found new song: " + song.getM_file().getAbsolutePath());
                //HBox rowOfSongInfo = new HBox();
                Label titleLabel = new Label(song.getM_title() + "   ");
                Label genreLabel = new Label(song.getM_genre() + "   ");
                Label artistLabel = new Label(song.getM_artist() + "   ");
                Label ratingLabel = new Label(song.getM_rating() + "   ");
                gridPane.add(titleLabel, 0, row);
                gridPane.add(genreLabel, 1, row);
                gridPane.add(artistLabel, 2, row);
                gridPane.add(ratingLabel, 3, row);

//                rowOfSongInfo.getChildren().addAll(titleLabel, genreLabel, artistLabel, ratingLabel);
//                gridPane.add(rowOfSongInfo, 0, row);

                row++;

                titleLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override public void handle(MouseEvent e) {
                        // Code to play song goes here
                    }
                });

                genreLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override public void handle(MouseEvent e) {
                        // Code to play song goes here
                    }
                });

                artistLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override public void handle(MouseEvent e) {
                        // Code to play song goes here
                    }
                });

                ratingLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override public void handle(MouseEvent e) {
                        // Code to play song goes here
                    }
                });
            }
            this.getChildren().add(gridPane);
        }
    }

    private void setCssStyle() {
        final String cssDefault = "-fx-border-color: black;\n";
        this.setStyle(cssDefault);
    }
}
