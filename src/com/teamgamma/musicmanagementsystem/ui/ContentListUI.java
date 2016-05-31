package com.teamgamma.musicmanagementsystem.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class ContentListUI extends StackPane{
    public ContentListUI(){
        super();

        this.getChildren().add(new Label("Contents in folder"));

        setCssStyle();
    }

    private void setCssStyle(){
        final String cssDefault = "-fx-border-color: black;\n";
        this.setStyle(cssDefault);
    }
}
