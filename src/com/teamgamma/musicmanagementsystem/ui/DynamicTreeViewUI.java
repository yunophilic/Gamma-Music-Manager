package com.teamgamma.musicmanagementsystem.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class DynamicTreeViewUI extends StackPane {
    public DynamicTreeViewUI(){
        super();

        this.getChildren().add(new Label("Show a specific tree view"));

        setCssStyle();
    }

    private void setCssStyle(){
        final String cssDefault = "-fx-border-color: black;\n";
        this.setStyle(cssDefault);
    }
}
