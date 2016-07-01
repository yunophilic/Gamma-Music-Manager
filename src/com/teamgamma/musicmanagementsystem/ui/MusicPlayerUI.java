package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import com.teamgamma.musicmanagementsystem.model.Song;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerObserver;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;

import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import javafx.scene.text.Font;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import javax.tools.Tool;
import java.io.File;

/**
 * Class for Music Player MainUI. Acts as the controller for the media player.
 */
public class MusicPlayerUI extends VBox {

    // Constants for MusicPlayerUI.
    public static final int SECONDS_IN_MINUTE = 60;
    public static final double SEEK_BAR_Y_SCALE = 3;
    public static final int HEADER_FONT_SIZE = 20;
    public static final int SONG_TITLE_HEADER_SIZE = 13;
    public static final double FADED = 0.5;
    public static final double NOT_FADED = 1.0;
    public static final int TITLE_ANIMATION_TIME_MS = 5000;
    public static final double VOLUME_BUTTON_SCALE = 0.75;

    public static final String PREVIOUS_ICON_PATH = "res\\ic_skip_previous_black_48dp_1x.png";
    public static final String PLAY_ICON_PATH = "res\\ic_play_arrow_black_48dp_1x.png";
    public static final String PAUSE_ICON_PATH = "res\\ic_pause_black_48dp_1x.png";
    public static final String NEXT_SONG_ICON_PATH = "res\\ic_skip_next_black_48dp_1x.png";
    public static final String VOLUME_UP_ICON_PATH = "res\\ic_volume_up_black_48dp_1x.png";
    public static final String VOLUME_DOWN_ICON_PATH = "res\\ic_volume_down_black_48dp_1x.png";
    public static final String VOLUME_MUTE_ICON_PATH = "res\\ic_volume_mute_black_48dp_1x.png";
    public static final String PLAYLIST_REPEAT_ICON_PATH = "res\\ic_repeat_black_48dp_1x.png";
    public static final String ADD_TO_PLAYLIST_ICON_PATH = "res/ic_playlist_add_black_48dp_1x.png";
    public static final String SONG_REPEAT_ICON_PATH = "res\\ic_repeat_one_black_48dp_1x.png";

    public static final String PREVIOUS_SONG_TOOLTIP_DEFUALT = "No Previous Song";
    public static final String NEXT_SONG_TOOLTIP_DEFAULT = "No Next Song";
    public static final String DEFAULT_TIME_STRING = "0:00";

    /**
     * Constructor
     *
     * @param manager The MusicPlayerManager to setup the actions for the UI panel.
     */
    public MusicPlayerUI(MusicPlayerManager manager) {
        super();

        VBox topWrapper = new VBox();
        topWrapper.setSpacing(0);
        topWrapper.getChildren().add(makeSongTitleHeader(manager));

        // For testing purposes
        //HBox musicFileBox = createFilePathBox(manager);
        //topWrapper.getChildren().add(musicFileBox);

        topWrapper.getChildren().addAll(createProgressBarBox(manager));
        this.getChildren().add(topWrapper);
        HBox playbackControls = createPlayBackControlBox(manager);
        this.getChildren().add(playbackControls);

        //  Removed for now until volume is working.
        HBox otherControlBox = createOtherOptionsBox(manager);
        this.getChildren().add(otherControlBox);

        manager.registerErrorObservers(new MusicPlayerObserver() {
            @Override
            public void updateUI() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Exception e = manager.getError();
                        if (e == null) {
                            PromptUI.unexpectedCrash();
                        } else {
                            PromptUI.customPromptError("Music Player Error", e.getMessage(), e.toString());
                        }
                    }
                });

            }
        });
        setCssStyle();
    }

    /**
     * Function to create the file text box for control of what song you want to play. This is used for testing purposes.
     *
     * @param manager The MusicPlayerManager to use for observers
     * @return HBox containing a the components needed to control the music player by typing in the path to the song.
     */
    private HBox createFilePathBox(final MusicPlayerManager manager) {
        HBox musicFileBox = new HBox();
        Label songPathHeader = new Label("Song Path");
        TextField songPath = new TextField("Enter Path To Song");
        Button addSong = createIconButton(ADD_TO_PLAYLIST_ICON_PATH);
        addSong.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                manager.placeSongOnBackOfPlaybackQueue(new Song(songPath.getText()));
            }
        });
        musicFileBox.getChildren().addAll(songPathHeader, songPath, addSong);
        return musicFileBox;
    }

    /**
     * Function to create the playback control UI component. This would be Previous Song, Play/Pause, Next song.
     *
     * @param manager The music manager to setup observers.
     * @return The Playback controls for the UI.
     */
    private HBox createPlayBackControlBox(final MusicPlayerManager manager) {
        HBox playbackControls = new HBox();
        playbackControls.setAlignment(Pos.CENTER);

        Button previousSongButton = createIconButton(PREVIOUS_ICON_PATH);
        previousSongButton.setOpacity(FADED);
        previousSongButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                manager.playPreviousSong();
            }
        });
        createMouseOverUIChange(previousSongButton);
        previousSongButton.setAlignment(Pos.CENTER_LEFT);
        Tooltip previousSongToolTip = new Tooltip(PREVIOUS_SONG_TOOLTIP_DEFUALT);
        manager.registerNewSongObserver(new MusicPlayerObserver() {
            @Override
            public void updateUI() {
                previousSongToolTip.setText(getSongDisplayName(manager.getPreviousSong()));
            }
        });
        previousSongButton.setTooltip(previousSongToolTip);
        playbackControls.getChildren().add(previousSongButton);

        manager.registerNewSongObserver(new MusicPlayerObserver() {
            @Override
            public void updateUI() {
                if (manager.isNothingPrevious()) {
                    previousSongButton.setOpacity(FADED);
                } else {
                    previousSongButton.setOpacity(NOT_FADED);
                }
            }
        });

        ToggleButton playPauseButton = new ToggleButton();
        playPauseButton.setStyle("-fx-background-color: transparent");
        playPauseButton.setGraphic(createImageViewForImage(PLAY_ICON_PATH));
        playPauseButton.setSelected(false);
        playPauseButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (playPauseButton.isSelected()) {
                    // Selected means that something is playing so we want to pause it
                    manager.pause();
                } else {
                    manager.resume();
                }
            }
        });

        createMouseOverUIChange(playPauseButton);
        manager.registerChangeStateObservers(new MusicPlayerObserver() {
            @Override
            public void updateUI() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (!manager.isSomethingPlaying()) {
                            playPauseButton.setGraphic(createImageViewForImage(PLAY_ICON_PATH));
                            playPauseButton.setSelected(true);
                        } else {
                            playPauseButton.setGraphic(createImageViewForImage(PAUSE_ICON_PATH));
                            playPauseButton.setSelected(false);
                        }
                    }
                });
            }
        });

        playPauseButton.setAlignment(Pos.CENTER);
        playbackControls.getChildren().add(playPauseButton);

        Button skipButton = createIconButton(NEXT_SONG_ICON_PATH);
        skipButton.setOpacity(FADED);
        skipButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                manager.playNextSong();
            }
        });
        createMouseOverUIChange(skipButton);

        Tooltip nextSongTip = new Tooltip(NEXT_SONG_TOOLTIP_DEFAULT);
        skipButton.setTooltip(nextSongTip);
        manager.registerQueingObserver(createNextSongToolTipObserver(manager, nextSongTip));
        manager.registerNewSongObserver(createNextSongToolTipObserver(manager, nextSongTip));
        playbackControls.getChildren().add(skipButton);

        manager.registerQueingObserver(createNextSongButtonFadedAction(manager, skipButton));
        manager.registerNewSongObserver(createNextSongButtonFadedAction(manager, skipButton));
        return playbackControls;
    }

    /**
     * Helper function to create the next song observer for the tool tip.
     *
     * @param manager   The MusicPlayerManager to use.
     * @param nextSongTip   The tooltip to use.
     *
     * @return An observer that will update the tooltip using the manager for next song.
     */
    private MusicPlayerObserver createNextSongToolTipObserver(final MusicPlayerManager manager, final Tooltip nextSongTip) {
        return new MusicPlayerObserver() {
            @Override
            public void updateUI() {
                Song nextSong = manager.getNextSong();
                if (nextSong != null ) {
                    String songTitle = getSongDisplayName(nextSong);
                    nextSongTip.setText(songTitle);
                } else {
                    nextSongTip.setText(NEXT_SONG_TOOLTIP_DEFAULT);
                }
            }
        };
    }

    /**
     * Helper function to get the song name to display. If the metadata is empty then we will use file name.
     *
     * @param nextSong  The song to get the title for
     *
     * @return  Either the song of the title if its there or the filename.
     */
    private String getSongDisplayName(Song nextSong) {
        String songTitle = nextSong.getM_title();
        if (songTitle.isEmpty()) {
            songTitle = nextSong.getM_fileName();
        }
        return songTitle;
    }

    /**
     * Function to create a UI indication when mousing over something.
     *
     * @param element  The element to apply UI effect on.
     */
    private void createMouseOverUIChange(final Node element) {
        element.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                element.setStyle("-fx-background-color: #BFDCF5;");
            }
        });
        element.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                element.setStyle("-fx-background-color: transparent");
            }
        });
    }

    /**
     * Helper function to create the observer that will be used to fade the next button or not.
     *
     * @param manager       The music player manager to query.
     * @param skipButton    The button to update.
     *
     * @return  The observer containing the next song faded logic.
     */
    private MusicPlayerObserver createNextSongButtonFadedAction(final MusicPlayerManager manager, final Button skipButton) {
        return new MusicPlayerObserver() {
            @Override
            public void updateUI() {
                if (manager.isThereANextSong()) {
                    skipButton.setOpacity(NOT_FADED);
                } else {
                    skipButton.setOpacity(FADED);
                }
            }
        };
    }

    /**
     * Function to create the other playback options list. This would be volume control and repeat control.
     *
     * @param manager The music manager to set up actions.
     * @return
     */
    private HBox createOtherOptionsBox(final MusicPlayerManager manager) {
        HBox otherControlBox = new HBox();

        Button volumeDownIcon = createIconButton(VOLUME_MUTE_ICON_PATH);
        volumeDownIcon.setScaleY(VOLUME_BUTTON_SCALE);
        volumeDownIcon.setScaleX(VOLUME_BUTTON_SCALE);

        Button volumeUpIcon = createIconButton(VOLUME_UP_ICON_PATH);
        volumeUpIcon.setScaleY(VOLUME_BUTTON_SCALE);
        volumeUpIcon.setScaleX(VOLUME_BUTTON_SCALE);

        Slider volumeControlSider = createSliderVolumeControl(manager);
        otherControlBox.getChildren().addAll(volumeDownIcon,  volumeControlSider, volumeUpIcon);
        HBox.setHgrow(volumeControlSider, Priority.ALWAYS);
        otherControlBox.setAlignment(Pos.CENTER);
        otherControlBox.setSpacing(0);

        otherControlBox.setMargin(volumeControlSider, new Insets(0));
        return otherControlBox;
    }

    /**
     * Function to set the global CSS style the panel
     */
    private void setCssStyle() {
        final String cssDefault = "-fx-border-color: black;\n";
        this.setStyle(cssDefault);
    }

    /**
     * Function to create the progress bar and the seek slider UI component.
     *
     * @param manager The music player manager to set up the observers.
     * @return The progress bar and seek slider UI pane.
     */
    private StackPane createProgressBarBox(final MusicPlayerManager manager) {
        StackPane musicPlayerProgress = new StackPane();

        HBox progressWrapper = new HBox();
        Label songStartLable = new Label(DEFAULT_TIME_STRING);

        Label songEndTimeProgressBar = new Label(DEFAULT_TIME_STRING);
        Label songEndTimeSeekBar = new Label(DEFAULT_TIME_STRING);

        // Set up an observer to update the songEndTime based on what song is being played.
        manager.registerNewSongObserver(new MusicPlayerObserver() {
            @Override
            public void updateUI() {
                String endTimeString = convertDurationToTimeString(manager.getEndTime());
                songEndTimeProgressBar.setText(endTimeString);
                songEndTimeSeekBar.setText(endTimeString);
            }
        });

        // Resizing from https://docs.oracle.com/javafx/2/api/javafx/scene/layout/HBox.html
        ProgressBar songPlaybar = new ProgressBar();
        songPlaybar.setMaxWidth(Double.MAX_VALUE);
        songPlaybar.setProgress(0);
        progressWrapper.getChildren().addAll(songStartLable, songPlaybar, songEndTimeProgressBar);
        HBox.setHgrow(songPlaybar, Priority.ALWAYS);

        // Have a slider for the underlying control but do not show it.
        HBox playbackSliderWrapper = new HBox();
        Slider playbackSlider = new Slider(0, 1.0, 0);
        playbackSlider.setBlockIncrement(0.01);
        HBox.setHgrow(playbackSlider, Priority.ALWAYS);
        playbackSlider.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double sliderVal = playbackSlider.getValue();
                manager.seekSongTo(sliderVal);

            }
        });
        // The labels here are for spacing and are there for no other purpose.
        playbackSliderWrapper.getChildren().addAll(new Label("0:0"), playbackSlider, new Label("0:0"));
        playbackSliderWrapper.setOpacity(0.0);

        // Make the slider always bigger than the progress bar to make it so the user only can click on the slider.
        playbackSliderWrapper.setScaleY(SEEK_BAR_Y_SCALE);

        Tooltip playbackTimeToolTip = new Tooltip(convertDurationToTimeString(manager.getCurrentPlayTime()));
        playbackSlider.setTooltip(playbackTimeToolTip);
        // Setup the observer pattern stuff for UI updates to the current play time.
        manager.registerPlaybackObserver(new MusicPlayerObserver() {
            @Override
            public void updateUI() {
                Duration currentPlayTime = manager.getCurrentPlayTime();

                double progress = currentPlayTime.toMillis() / manager.getEndTime().toMillis();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        songPlaybar.setProgress(progress);
                        playbackSlider.setValue(progress);
                        playbackTimeToolTip.setText(convertDurationToTimeString(manager.getCurrentPlayTime()));
                    }
                });
            }
        });

        musicPlayerProgress.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

            }
        });

        musicPlayerProgress.getChildren().addAll(progressWrapper, playbackSliderWrapper);
        return musicPlayerProgress;
    }

    /**
     * Helper function to convert the duration obejct to a human readable format. The format is like the following MM:SS
     *
     * @param duration The duration to convert.
     * @return A human readable string of the duration.
     */
    private String convertDurationToTimeString(Duration duration) {
        String timeString = "";

        double seconds = duration.toSeconds();
        int minutes = 0;
        while ((seconds - SECONDS_IN_MINUTE) >= 0) {
            minutes++;
            seconds -= SECONDS_IN_MINUTE;
        }
        timeString = minutes + ":";

        long leftOverSeconds = (int) seconds;
        if (leftOverSeconds < 10) {
            // Add on so it looks like 0:05 rather than 0:5
            timeString += "0";
        }
        timeString += leftOverSeconds;
        return timeString;
    }

    /**
     * Function to create the current song playing UI component.
     *
     * @param manager The music player manager to setup the observer pattern.
     *
     * @return The song header component.
     */
    private VBox makeSongTitleHeader(final MusicPlayerManager manager) {
        VBox songTitleWrapper = new VBox();

        Font songHeaderFont = new Font(SONG_TITLE_HEADER_SIZE);
        Label songTitle = new Label("");
        songTitle.setFont(songHeaderFont);
        songTitle.setWrapText(true);

        // Set up an observer that will update the name of the song when a new song is played.
        manager.registerNewSongObserver(new MusicPlayerObserver() {
            @Override
            public void updateUI() {
                Song currentPlayingSong = manager.getCurrentSongPlaying();
                if (currentPlayingSong == null) {
                    songTitle.setText("");
                } else {
                    songTitle.setText(manager.getCurrentSongPlaying().getM_fileName());
                
                TranslateTransition songTitleAnimation = new TranslateTransition(
                        new Duration(TITLE_ANIMATION_TIME_MS), songTitle);
                songTitleAnimation.setFromX(songTitleWrapper.getWidth());
                songTitleAnimation.setToX(0);
                songTitleAnimation.play();
                }
            }
        });
        songTitleWrapper.getChildren().addAll(songTitle);

        return songTitleWrapper;
    }

    /**
     * Helper function to convert a path to a image to a actual image you can use.
     *
     * @param imagePath The path to a image.
     * @return A ImageView that contains the image that is passed in.
     */
    private ImageView createImageViewForImage(String imagePath) {
        // Replace path separator to correct OS.
        imagePath = imagePath.replace("\\", File.separator);
        imagePath = imagePath.replace("/", File.separator);

        // Idea for background image from http://stackoverflow.com/questions/29984228/javafx-button-background-image
        return new ImageView(new Image(getClass().getClassLoader().getResourceAsStream(imagePath)));
    }

    /**
     * Helper function to create a button that displays the image passed in.
     *
     * @param pathToIcon The path to the image to use.
     * @return The button with the image being used.
     */
    private Button createIconButton(String pathToIcon) {
        Button button = new Button();
        button.setStyle("-fx-background-color: transparent");
        button.setGraphic(createImageViewForImage(pathToIcon));
        return button;
    }

    /**
     * Helper function to create a Heading Label for text.
     *
     * @param textForLabel The Text to use.
     * @return A Label styled in the MusicPlayer Heading style.
     */
    private Label createHeadingLabel(String textForLabel) {
        Label label = new Label(textForLabel);
        label.setFont(new Font(HEADER_FONT_SIZE));
        return label;
    }

    /**
     * Function to create the playback time UI component.
     *
     * @param manager The manager to setup the observer pattern.
     * @return The current playback time UI component.
     */
    private HBox createCurrentTimeBox(MusicPlayerManager manager) {
        HBox songTimesWrapper = new HBox();
        Label currentTimeLabel = createHeadingLabel(DEFAULT_TIME_STRING);
        Label constantLabel = createHeadingLabel("/");
        Label songEndTimeText = createHeadingLabel(DEFAULT_TIME_STRING);
        songTimesWrapper.getChildren().addAll(currentTimeLabel, constantLabel, songEndTimeText);
        songTimesWrapper.setAlignment(Pos.CENTER_RIGHT);

        manager.registerNewSongObserver(new MusicPlayerObserver() {
            @Override
            public void updateUI() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        songEndTimeText.setText(convertDurationToTimeString(manager.getEndTime()));
                    }
                });

            }
        });
        manager.registerPlaybackObserver(new MusicPlayerObserver() {
            @Override
            public void updateUI() {
                // Have to run this one later. Odd that progress bar did not have this problem.
                // http://stackoverflow.com/questions/29449297/java-lang-illegalstateexception-not-on-fx-application-thread-currentthread-t
                // https://bugs.openjdk.java.net/browse/JDK-8088376
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        currentTimeLabel.setText(convertDurationToTimeString(manager.getCurrentPlayTime()));
                    }
                });
            }
        });
        return songTimesWrapper;
    }

    /**
     * Function to create the slider for volume control.
     *
     * @param manager The music player manager to interact with.
     *
     * @return A slider to control volume.
     */
    private Slider createSliderVolumeControl(MusicPlayerManager manager) {
        Slider volumeSlider = new Slider(0, 1, 1);

        volumeSlider.setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                manager.setVolumeLevel(volumeSlider.getValue());
            }
        });
        volumeSlider.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                manager.setVolumeLevel(volumeSlider.getValue());
            }
        });

        return volumeSlider;
    }
}
