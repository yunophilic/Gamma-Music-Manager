package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.SongManager;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class DynamicTreeViewUI extends StackPane {
    private SongManager model;
    public DynamicTreeViewUI(SongManager model){
        super();

        this.model = model;

        this.getChildren().add(new Label("Show a specific tree view"));

        setCssStyle();
    }

    private void setCssStyle(){
        final String cssDefault = "-fx-border-color: black;\n";
        this.setStyle(cssDefault);
    }
}
