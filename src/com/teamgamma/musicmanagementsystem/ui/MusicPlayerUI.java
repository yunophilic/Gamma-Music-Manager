package com.teamgamma.musicmanagementsystem.ui;


import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import com.teamgamma.musicmanagementsystem.Song;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerObserver;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

/**
 * Class for Music Player MainUI. Acts as the controller for the media player.
 */
public class MusicPlayerUI extends BorderPane {


    public static final int SECONDS_IN_MINUTE = 60;

    public MusicPlayerUI(MusicPlayerManager manager){
        super();

        VBox topWrapper = new VBox();
        topWrapper.getChildren().add(makeSongTitleHeader(manager));
        topWrapper.getChildren().addAll(createProgressBarBox(manager), new MediaView(manager.getMediaPlayer()));

        HBox musicFileBox = new HBox();

        Label songPathHeader = new Label("Song Path");
        TextField songPath = new TextField("Enter Path To Song");
        Button songPathSubmit = new Button("Play Song");
        songPathSubmit.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                manager.playSongNext(new Song(songPath.getText()));
            }
        });
        musicFileBox.getChildren().addAll(songPathHeader, songPath, songPathSubmit);
        topWrapper.getChildren().add(musicFileBox);
        this.setTop(topWrapper);


        HBox playPauseButton = new HBox();
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
        playPauseButton.getChildren().addAll(pauseButton, resumeButton);
        this.setCenter(playPauseButton);


        HBox otherControlBox = new HBox();
        Button volumnUpButton = new Button("Increase Volume");
        volumnUpButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                manager.increaseVolume();
            }
        });

        Button volumnDownButton = new Button("Decrease Volume");
        volumnDownButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                manager.decreaseVolume();
            }
        });

        ToggleButton repeatBox = new ToggleButton("Repeat Song");
        repeatBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                manager.setRepeat(repeatBox.isSelected());
            }
        });

        otherControlBox.getChildren().addAll(volumnUpButton, volumnDownButton, repeatBox);
        this.setBottom(otherControlBox);

        setCssStyle();
    }

    private void setCssStyle(){
        final String cssDefault = "-fx-border-color: black;\n";
        this.setStyle(cssDefault);
    }

    private HBox createProgressBarBox(final MusicPlayerManager manager) {
        HBox musicPlayerProgress = new HBox();

        Label songStartLable = new Label("0:00");
        Label currentTimeLabel = new Label("0:00");
        Label constantLabel = new Label("\\");
        Label songEndTime = new Label("0:00");

        // Set up an observer to update the songEndTime based on what song is being played.
        manager.registerNewSongObserver(new MusicPlayerObserver() {
            @Override
            public void updateUI() {
                Duration endtime = manager.getEndTime();
                songEndTime.setText(convertDurationToTimeString(endtime));
            }
        });
        ProgressBar songPlaybar = new ProgressBar();
        songPlaybar.setProgress(0);

        Slider playbackSlider = new Slider(0, 1.0, 0);
        playbackSlider.setBlockIncrement(0.01);
        playbackSlider.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                manager.seekSongTo(playbackSlider.getValue());
            }
        });
        manager.registerSeekObserver(new MusicPlayerObserver() {
            @Override
            public void updateUI() {
                Duration currentPlayTime = manager.getCurrentPlayTime();

                double progress = currentPlayTime.toMillis() / manager.getEndTime().toMillis();
                songPlaybar.setProgress(progress);
                playbackSlider.setValue(progress);
                // Have to run this one later. Odd that progress bar did not have this problem.
                // http://stackoverflow.com/questions/29449297/java-lang-illegalstateexception-not-on-fx-application-thread-currentthread-t
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        currentTimeLabel.setText(convertDurationToTimeString(currentPlayTime));
                    }
                });

            }
        });

        musicPlayerProgress.getChildren().addAll(songStartLable, songPlaybar, playbackSlider, currentTimeLabel, constantLabel, songEndTime);
        return musicPlayerProgress;
    }

    private String convertDurationToTimeString(Duration endtime) {
        String endingTime = "";

        double seconds = endtime.toSeconds();
        int minutes = 0;
        while ((seconds - SECONDS_IN_MINUTE) >= 0){
            minutes++;
            seconds -= SECONDS_IN_MINUTE;
        }
        endingTime = minutes + ":";

        long leftOverSeconds = Math.round(seconds);
        if (leftOverSeconds < 10){
            // Add on so it looks like 0:05 rather than 0:5
            endingTime += "0";
        }

        endingTime += leftOverSeconds;

        return endingTime;
    }

    private HBox makeSongTitleHeader(final MusicPlayerManager manager) {
        HBox songTitleWrapper = new HBox();
        Label songTitleHeader = new Label("Song Currently Playing : ");
        Label songTitle = new Label("");

        // Set up an observer that will update the name of the song when a new song is played.
        manager.registerNewSongObserver(new MusicPlayerObserver() {
            @Override
            public void updateUI() {
                songTitle.setText(manager.getCurrentSongPlaying().getM_songName());
            }
        });
        songTitleWrapper.getChildren().addAll(songTitleHeader, songTitle);

        return songTitleWrapper;
    }

}
