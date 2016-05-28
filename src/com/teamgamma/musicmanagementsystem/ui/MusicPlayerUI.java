package com.teamgamma.musicmanagementsystem.ui;


import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import com.teamgamma.musicmanagementsystem.Song;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.media.MediaView;

/**
 * Class for Music Player UI. Acts as the controller for the media player.
 */
public class MusicPlayerUI extends GridPane {

    public MusicPlayerUI(MusicPlayerManager manager){
        super();

        this.add(new MediaView(manager.getMediaPlayer()),0,0);

        Label songPathHeader = new Label("Song Path");
        TextField songPath = new TextField("Enter Path To Song");
        Button songPathSubmit = new Button("Play Song");
        songPathSubmit.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                manager.playSongNext(new Song(songPath.getText()));
            }
        });
        this.add(songPathHeader,0,1);
        this.add(songPath,1,1);
        this.add(songPathSubmit,2,1);

        Button pauseButton = new Button("Pause");
        pauseButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                manager.pause();
            }
        });
        Button resumeButton = new Button("Resume");
        resumeButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                manager.resume();
            }
        });

        this.add(resumeButton, 0, 2);
        this.add(pauseButton, 1, 2);

        Button volumnUpButton = new Button("Increase Volume");
        volumnUpButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                manager.increaseVolume();
            }
        });
        this.add(volumnUpButton, 0, 3);

        Button volumnDownButton = new Button("Decrease Volume");
        volumnDownButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                manager.decreaseVolume();
            }
        });
        this.add(volumnDownButton, 1, 3);

        Label repeatTitle = new Label("Repeat Song");
        CheckBox repeatBox = new CheckBox();
        repeatBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                manager.setRepeat(repeatBox.isSelected());
            }
        });
        this.add(repeatTitle, 0, 4);
        this.add(repeatBox, 1, 4);
    }
}
