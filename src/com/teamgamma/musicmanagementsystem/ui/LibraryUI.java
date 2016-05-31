package com.teamgamma.musicmanagementsystem.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * UI for the user's or external library
 */
public class LibraryUI extends StackPane{

    public LibraryUI(String text){
        super();

        this.getChildren().add(new Label(text));

        setCssStyle();
    }

    private void setCssStyle(){
        final String cssDefault = "-fx-border-color: black;\n";
        this.setStyle(cssDefault);
    }
}
