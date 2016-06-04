package com.teamgamma.musicmanagementsystem.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * UI class for song information
 */
public class SongInfoUI extends StackPane {
    public SongInfoUI() {
        super();

        this.getChildren().add(new Label("Song Info"));

        setCssStyle();
    }

    private void setCssStyle() {
        final String cssDefault = "-fx-border-color: black;\n";
        this.setStyle(cssDefault);
    }
}
