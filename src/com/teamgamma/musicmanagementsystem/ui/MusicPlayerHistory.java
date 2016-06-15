package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.model.Song;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerObserver;
import javafx.event.EventHandler;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.util.Collection;
import java.util.List;

/**
 * Created by Eric on 2016-06-14.
 */
public class MusicPlayerHistory extends HBox{

    public MusicPlayerHistory(MusicPlayerManager manager) {

        TitledPane playbackHistory = new TitledPane("History", createUIList(manager.getHistory()));
        playbackHistory.setAnimated(true);
        playbackHistory.setCollapsible(true);
        playbackHistory.setExpanded(false);



        TitledPane queuingList = new TitledPane("Whats Playing Next", createUIList(manager.getPlayingQueue()));
        Accordion accordion = new Accordion();
        accordion.getPanes().addAll(playbackHistory, queuingList);

        this.getChildren().add(accordion);
        HBox.setHgrow(accordion, Priority.ALWAYS);
        manager.registerNewSongObserver(new MusicPlayerObserver() {
            @Override
            public void updateUI() {
                playbackHistory.setContent(createUIList(manager.getHistory()));

            }
        });

        manager.registerQueingObserver(new MusicPlayerObserver() {
            @Override
            public void updateUI() {
                queuingList.setContent(createUIList(manager.getPlayingQueue()));
            }
        });
    }

    private VBox createUIList(Collection<Song> listOfSongs) {
        VBox allSongs = new VBox();
        boolean isFirst = true;
        for (Song song : listOfSongs) {
            Button songButton = new Button(song.getM_fileName());
            songButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    System.out.println("Selected " + song.getM_fileName());
                }
            });
            allSongs.getChildren().add(songButton);
        }
        return allSongs;
    }



}
