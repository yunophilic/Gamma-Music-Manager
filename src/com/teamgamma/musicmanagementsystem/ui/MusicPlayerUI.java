package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.model.DatabaseManager;
import com.teamgamma.musicmanagementsystem.model.FilePersistentStorage;
import com.teamgamma.musicmanagementsystem.model.SongManager;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerConstants;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import com.teamgamma.musicmanagementsystem.model.Song;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerObserver;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import javafx.scene.text.Font;
import javafx.util.Duration;

import java.io.File;

/**
 * Class for Music Player MainUI. Acts as the controller for the media player.
 */
public class MusicPlayerUI extends VBox {

    // Constants for MusicPlayerUI.
    public static final double SEEK_BAR_Y_SCALE = 3;
    public static final int HEADER_FONT_SIZE = 20;
    public static final int SONG_TITLE_HEADER_SIZE = 13;
    public static final double FADED = 0.5;
    public static final double NOT_FADED = 1.0;
    public static final int TITLE_ANIMATION_TIME_MS = 5000;
    public static final double VOLUME_BUTTON_SCALE = 0.75;
    public static final int VOLUME_MAX_WIDTH = 60;
    public static final int VOLUME_MAX_HEIGHT = 1;

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
    public static final String DELETE_SONG_ICON_PATH = "res\\delete-song-player.png";

    public static final String PREVIOUS_SONG_TOOL_TIP_DEFUALT = "No Previous Song";
    public static final String NEXT_SONG_TOOL_TIP_DEFAULT = "No Next Song";
    private static final String DELETE_SONG_TOOL_TIP_DEFAULT = "No Song to Delete";
    public static final String MAX_VOLUME_TOOL_TIP_MESSAGE = "Max Volume";
    public static final String MUTE_VOLUME_TOOL_TIP_MESSAGE = "Mute Volume";
    public static final String PAUSE_SONG_TOOL_TIP_MESSAGE = "Pause Song";
    public static final String RESUME_SONG_TOOL_TIP_MESSAGE = "Resume Song";
    public static final String DELETE_SONG_TOOL_TIP_MESSAGE = "Delete Song";
    public static final String DEFAULT_PLAY_BUTTON_TOOL_TIP_MESSAGE = "Pick a Song To Play!";

    public static final String DEFAULT_TIME_STRING = "0:00";
    private SongManager m_model;
    private DatabaseManager m_databaseManager;

    /**
     * Constructor
     *
     * @param manager The MusicPlayerManager to setup the actions for the UI panel.
     * @param config  The FilePersistentStorage to save the states for the UI panel.
     */
    public MusicPlayerUI(SongManager model,
                         MusicPlayerManager manager,
                         DatabaseManager databaseManager,
                         FilePersistentStorage config) {
        super();
        m_model = model;
        m_databaseManager = databaseManager;
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

        HBox otherControlBox = createOtherOptionsBox(manager, config);
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
        Button addSong = UserInterfaceUtils.createIconButton(ADD_TO_PLAYLIST_ICON_PATH);
        addSong.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                File newFile = new File(songPath.getText());
                manager.placeSongOnBackOfPlaybackQueue(new Song(newFile));
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

        Button previousSongButton = UserInterfaceUtils.createIconButton(PREVIOUS_ICON_PATH);
        previousSongButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                manager.playPreviousSong();
            }
        });
        UserInterfaceUtils.createMouseOverUIChange(previousSongButton, previousSongButton.getStyle());
        previousSongButton.setAlignment(Pos.CENTER_LEFT);
        Tooltip previousSongToolTip = new Tooltip(PREVIOUS_SONG_TOOL_TIP_DEFUALT);

        if (manager.isNothingPrevious()) {
            previousSongButton.setOpacity(FADED);
        } else {
            previousSongButton.setOpacity(NOT_FADED);
            previousSongToolTip.setText(getSongDisplayName(manager.getPreviousSong()));
        }

        manager.registerNewSongObserver(new MusicPlayerObserver() {
            @Override
            public void updateUI() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (manager.isNothingPrevious()) {
                            previousSongToolTip.setText(PREVIOUS_SONG_TOOL_TIP_DEFUALT);
                        } else {
                            previousSongToolTip.setText(getSongDisplayName(manager.getPreviousSong()));
                        }
                    }
                });
            }
        });
        previousSongButton.setTooltip(previousSongToolTip);
        playbackControls.getChildren().add(previousSongButton);
        manager.registerNewSongObserver(new MusicPlayerObserver() {
            @Override
            public void updateUI() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (manager.isNothingPrevious()) {
                            previousSongButton.setOpacity(FADED);

                        } else {
                            previousSongButton.setOpacity(NOT_FADED);
                        }
                    }
                });
            }
        });

        ToggleButton playPauseButton = new ToggleButton();
        playPauseButton.setStyle("-fx-background-color: transparent");
        playPauseButton.setGraphic(UserInterfaceUtils.createImageViewForImage(PLAY_ICON_PATH));
        playPauseButton.setSelected(false);
        Tooltip playPauseToolTip = new Tooltip(DEFAULT_PLAY_BUTTON_TOOL_TIP_MESSAGE);
        playPauseButton.setOpacity(FADED);
        playPauseButton.setTooltip(playPauseToolTip);
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

        UserInterfaceUtils.createMouseOverUIChange(playPauseButton, playPauseButton.getStyle());
        manager.registerChangeStateObservers(new MusicPlayerObserver() {
            @Override
            public void updateUI() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (!manager.isSomethingPlaying()) {
                            playPauseButton.setOpacity(NOT_FADED);
                            playPauseButton.setGraphic(UserInterfaceUtils.createImageViewForImage(PLAY_ICON_PATH));
                            playPauseButton.setSelected(true);
                            playPauseToolTip.setText(RESUME_SONG_TOOL_TIP_MESSAGE);
                        } else {
                            playPauseButton.setOpacity(NOT_FADED);
                            playPauseButton.setGraphic(UserInterfaceUtils.createImageViewForImage(PAUSE_ICON_PATH));
                            playPauseButton.setSelected(false);
                            playPauseToolTip.setText(PAUSE_SONG_TOOL_TIP_MESSAGE);
                        }
                    }
                });
            }
        });

        playPauseButton.setAlignment(Pos.CENTER);
        playbackControls.getChildren().add(playPauseButton);

        Button skipButton = UserInterfaceUtils.createIconButton(NEXT_SONG_ICON_PATH);
        if (manager.isThereANextSong()) {
            skipButton.setOpacity(NOT_FADED);
        } else {
            skipButton.setOpacity(FADED);
        }

        skipButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                manager.playNextSong();
            }
        });
        UserInterfaceUtils.createMouseOverUIChange(skipButton, skipButton.getStyle());

        Tooltip nextSongTip = new Tooltip(NEXT_SONG_TOOL_TIP_DEFAULT);
        skipButton.setTooltip(nextSongTip);
        setToolTipToNextSong(manager, nextSongTip);

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
     * @param manager     The MusicPlayerManager to use.
     * @param nextSongTip The tooltip to use.
     * @return An observer that will update the tooltip using the manager for next song.
     */
    private MusicPlayerObserver createNextSongToolTipObserver(final MusicPlayerManager manager, final Tooltip nextSongTip) {
        return new MusicPlayerObserver() {
            @Override
            public void updateUI() {
                setToolTipToNextSong(manager, nextSongTip);
            }
        };
    }

    /**
     * Fuunction to set the tooltip to the next song in the music player
     *
     * @param manager     Music Player manager to query
     * @param nextSongTip The tooltip to set
     */
    private void setToolTipToNextSong(MusicPlayerManager manager, Tooltip nextSongTip) {
        Song nextSong = manager.getNextSong();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (nextSong != null) {
                    String songTitle = getSongDisplayName(nextSong);
                    nextSongTip.setText(songTitle);
                } else {
                    nextSongTip.setText(NEXT_SONG_TOOL_TIP_DEFAULT);
                }
            }
        });
    }

    /**
     * Helper function to get the song name to display. If the metadata is empty then we will use file name.
     *
     * @param nextSong The song to get the title for
     * @return Either the song of the title if its there or the filename.
     */
    private String getSongDisplayName(Song nextSong) {
        String songTitle = nextSong.getM_title();
        if (songTitle.isEmpty()) {
            songTitle = nextSong.getFileName();
        }
        return songTitle;
    }

    /**
     * Helper function to create the observer that will be used to fade the next button or not.
     *
     * @param manager    The music player manager to query.
     * @param skipButton The button to update.
     * @return The observer containing the next song faded logic.
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
     * Function to create the other playback options list. This would be volume control, repeat control, and delete song.
     *
     * @param manager The music manager to set up actions.
     * @param config  The file persistent storage to save state.
     * @return The HBox containing the other options.
     */
    private HBox createOtherOptionsBox(final MusicPlayerManager manager, final FilePersistentStorage config) {
        HBox otherControlBox = new HBox();

        Button volumeDownIcon = makeVolumeIcon(VOLUME_MUTE_ICON_PATH, MUTE_VOLUME_TOOL_TIP_MESSAGE);
        Button volumeUpIcon = makeVolumeIcon(VOLUME_UP_ICON_PATH, MAX_VOLUME_TOOL_TIP_MESSAGE);

        Slider volumeControlSlider = createSliderVolumeControl(manager, config);
        HBox.setHgrow(volumeControlSlider, Priority.ALWAYS);
        volumeControlSlider.setMaxSize(VOLUME_MAX_WIDTH, VOLUME_MAX_HEIGHT);

        Button deleteSongIcon = createDeleteSongButton(manager);

        otherControlBox.getChildren().addAll(volumeDownIcon, volumeControlSlider, volumeUpIcon, deleteSongIcon);
        otherControlBox.setAlignment(Pos.BASELINE_CENTER);
        otherControlBox.setSpacing(0);

        volumeDownIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                volumeControlSlider.adjustValue(MusicPlayerConstants.MIN_VOLUME);
                manager.setVolumeLevel(MusicPlayerConstants.MIN_VOLUME);
                config.saveVolumeState(MusicPlayerConstants.MIN_VOLUME);
            }
        });

        volumeUpIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                volumeControlSlider.adjustValue(MusicPlayerConstants.MAX_VOLUME);
                manager.setVolumeLevel(MusicPlayerConstants.MAX_VOLUME);
                config.saveVolumeState(MusicPlayerConstants.MAX_VOLUME);
            }
        });


        otherControlBox.setMargin(volumeControlSlider, new Insets(0));

        return otherControlBox;
    }

    /**
     * Helper function to create an volume icon.
     *
     * @param iconPath          The path to the icon for the button.
     * @param toolTipMessage    The message that will be displayed in the tooltip.
     *
     * @return The button with styling for volume control.
     */
    private Button makeVolumeIcon(String iconPath, String toolTipMessage) {
        Button volumeDownIcon = UserInterfaceUtils.createIconButton(iconPath);
        volumeDownIcon.setScaleY(VOLUME_BUTTON_SCALE);
        volumeDownIcon.setScaleX(VOLUME_BUTTON_SCALE);
        UserInterfaceUtils.createMouseOverUIChange(volumeDownIcon, volumeDownIcon.getStyle());
        volumeDownIcon.setTooltip(new Tooltip(toolTipMessage));
        return volumeDownIcon;
    }

    /**
     * Function to create the delete Song button in the player.
     *
     * @param manager   The music manager to use register the observer.
     *
     * @return The button that will contain the user action for deleting song in the music palyer
     */
    private Button createDeleteSongButton(final MusicPlayerManager manager) {
        Button deleteSongIcon= UserInterfaceUtils.createIconButton(DELETE_SONG_ICON_PATH);
        UserInterfaceUtils.createMouseOverUIChange(deleteSongIcon, deleteSongIcon.getStyle());
        deleteSongIcon.setAlignment(Pos.BASELINE_RIGHT);
        deleteSongIcon.setOpacity(FADED);
        deleteSongIcon.setTooltip(new Tooltip(DELETE_SONG_TOOL_TIP_DEFAULT));

        manager.registerChangeStateObservers(new MusicPlayerObserver() {
            @Override
            public void updateUI() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (manager.isSomethingPlaying() || !manager.isSomethingPlaying()) {
                            final Song currentSongPlaying = manager.getCurrentSongPlaying();

                            if (currentSongPlaying == null) {
                                deleteSongIcon.setOpacity(FADED);
                            } else {
                                deleteSongIcon.setOpacity(NOT_FADED);
                            }

                            deleteSongIcon.setTooltip(new Tooltip(DELETE_SONG_TOOL_TIP_MESSAGE));

                            deleteSongIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent event) {
                                    if (currentSongPlaying != null) {
                                        UserInterfaceUtils.deleteFileAction(m_model, manager, m_databaseManager, currentSongPlaying.getFile());
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        return deleteSongIcon;
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
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        String endTimeString = UserInterfaceUtils.convertDurationToTimeString(manager.getEndTime());
                        songEndTimeProgressBar.setText(endTimeString);
                        songEndTimeSeekBar.setText(endTimeString);
                    }
                });
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

        Tooltip playbackTimeToolTip = new Tooltip(UserInterfaceUtils.convertDurationToTimeString(manager.getCurrentPlayTime()));
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
                        playbackTimeToolTip.setText(UserInterfaceUtils.convertDurationToTimeString(manager.getCurrentPlayTime()));
                    }
                });
            }
        });

        musicPlayerProgress.getChildren().addAll(progressWrapper, playbackSliderWrapper);
        return musicPlayerProgress;
    }

    /**
     * Function to create the current song playing UI component.
     *
     * @param manager The music player manager to setup the observer pattern.
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
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Song currentPlayingSong = manager.getCurrentSongPlaying();
                        if (currentPlayingSong == null) {
                            songTitle.setText("");
                        } else {
                            songTitle.setText(manager.getCurrentSongPlaying().getFileName());

                            TranslateTransition songTitleAnimation = new TranslateTransition(
                                    new Duration(TITLE_ANIMATION_TIME_MS), songTitle);
                            songTitleAnimation.setFromX(songTitleWrapper.getWidth());
                            songTitleAnimation.setToX(0);
                            songTitleAnimation.play();
                        }
                    }
                });

            }
        });
        songTitleWrapper.getChildren().addAll(songTitle);

        return songTitleWrapper;
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
                        songEndTimeText.setText(UserInterfaceUtils.convertDurationToTimeString(manager.getEndTime()));
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
                        currentTimeLabel.setText(UserInterfaceUtils.convertDurationToTimeString(manager.getCurrentPlayTime()));
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
     * @param config  The file persistent storage to save volume state.
     * @return A slider to control volume.
     */
    private Slider createSliderVolumeControl(MusicPlayerManager manager, FilePersistentStorage config) {
        Slider volumeSlider = new Slider(
                MusicPlayerConstants.MIN_VOLUME,
                MusicPlayerConstants.MAX_VOLUME,
                config.getVolumeConfig());

        volumeSlider.setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                manager.setVolumeLevel(volumeSlider.getValue());
                config.saveVolumeState(volumeSlider.getValue());
            }
        });
        volumeSlider.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                manager.setVolumeLevel(volumeSlider.getValue());
                config.saveVolumeState(volumeSlider.getValue());
            }
        });

        return volumeSlider;
    }
}
