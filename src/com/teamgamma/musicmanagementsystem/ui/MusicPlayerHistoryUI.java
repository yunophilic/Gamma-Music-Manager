package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.misc.ContextMenuConstants;
import com.teamgamma.musicmanagementsystem.model.Playlist;
import com.teamgamma.musicmanagementsystem.model.Song;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerObserver;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.WindowEvent;

import java.util.Collection;

/**
 * Class to show the MusicPlayer History.
 */
public class MusicPlayerHistoryUI extends HBox{

    // Constants
    public static final int VERTICAL_SPACING = 5;
    public static final int HORIZONTAL_ELEMENT_SPACING = 5;
    public static final int MAX_HEIGHT = 400;
    public static final int PREF_HEIGHT = 175;

    public static final String PLAYBACK_HISTORY_HEADER = "History";
    public static final String QUEUING_HEADER = "Playing Next";
    public static final int DOUBLE_CLICK = 2;
    public static final int FIRST_SONG = 1;

    private MusicPlayerManager m_manager;

    public MusicPlayerHistoryUI(MusicPlayerManager manager) {
        m_manager = manager;

        TitledPane playbackHistory = createTitlePane(PLAYBACK_HISTORY_HEADER, m_manager.getHistory(), createHistoryAction());
        TitledPane queuingList = createTitlePane(QUEUING_HEADER, m_manager.getPlayingQueue(), createPlaybackQueueAction());

        Accordion accordion = new Accordion();
        accordion.getPanes().addAll(playbackHistory, queuingList);

        HBox.setHgrow(accordion, Priority.ALWAYS);
        this.getChildren().add(accordion);

        manager.registerNewSongObserver(new MusicPlayerObserver() {
            @Override
            public void updateUI() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        playbackHistory.setContent(createUIList(manager.getHistory(), createHistoryAction()));
                    }
                });
            }
        });

        manager.registerQueingObserver(new MusicPlayerObserver() {
            @Override
            public void updateUI() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        queuingList.setContent(createUIList(manager.getPlayingQueue(), createPlaybackQueueAction()));
                    }
                });

            }
        });
    }

    /**
     * Helper function to create a titled pane for the accordian
     *
     * @param title The title of the accordian
     *
     * @param songs The collection of songs that is wanted to be displayed.
     *
     * @param action The action to take when building a row.
     *
     * @return  A TitlePane with the title and collection of songs displayed.
     */
    private TitledPane createTitlePane(String title, Collection<Song> songs, ILabelAction action) {
        TitledPane titlePane = new TitledPane(title, createUIList(songs, action));
        titlePane.setAnimated(true);
        titlePane.setCollapsible(true);
        titlePane.setExpanded(false);
        return titlePane;
    }

    /**
     * Function to create UI element that will hold a list of songs in it based on the collection passed in.
     *
     * @param listOfSongs   The list of songs to display.s
     *
     * @return A scrollable UI element that contains all the songs in the collection.
     */
    private ScrollPane createUIList(Collection<Song> listOfSongs, ILabelAction rowCreation) {
        ScrollPane wrapper = new ScrollPane();

        VBox allSongs = new VBox();
        allSongs.setSpacing(VERTICAL_SPACING);

        int songNumber = 1;
        for (Song song : listOfSongs) {
            HBox row = rowCreation.createRow(song, songNumber);
            String baseStyle = row.getStyle();

            row.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    row.setStyle("-fx-background-color: lightgray");
                }
            });
            row.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    row.setStyle(baseStyle);
                }
            });

            HBox.setHgrow(row, Priority.ALWAYS);
            row.setSpacing(HORIZONTAL_ELEMENT_SPACING);
            allSongs.getChildren().add(row);

            songNumber++;
        }
        allSongs.setPrefHeight(PREF_HEIGHT);
        allSongs.setMaxHeight(MAX_HEIGHT);

        wrapper.setContent(allSongs);
        return wrapper;
    }

    /**
     * Helper Function to create a styled label for the oldest element.
     *
     * @param message The message you want the label to have
     *
     * @return A styled label that contains the message given.
     */
    private Label createOldestStyleLabel(String message) {
        Label styledLabel = new Label(message);
        styledLabel.setStyle("-fx-font-style: italic; -fx-font-weight: bold");

        return styledLabel;
    }


    /**
     * Function to create a submenu for that will be shown when the user right clicks on a element.
     *
     * @param song The song that is selected
     *
     * @return  A context menu containing actions to do.
     */
    public static ContextMenu createSubmenu(MusicPlayerManager manager, Song song) {
        ContextMenu playbackMenu = new ContextMenu();
        playbackMenu.setAutoHide(true);

        MenuItem playSong = new MenuItem(ContextMenuConstants.MENU_ITEM_PLAY_SONG);
        playSong.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                manager.playSongRightNow(song);
            }
        });

        MenuItem placeSongAtStartOfQueue = new MenuItem(ContextMenuConstants.MENU_ITEM_PLAY_SONG_NEXT);
        placeSongAtStartOfQueue.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                manager.placeSongAtStartOfQueue(song);
            }
        });

        MenuItem placeSongOnBackOfQueue = new MenuItem(ContextMenuConstants.MENU_ITEM_PLACE_SONG_ON_QUEUE);
        placeSongOnBackOfQueue.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                manager.placeSongOnBackOfPlaybackQueue(song);
            }
        });

        playbackMenu.getItems().addAll(playSong, placeSongAtStartOfQueue, placeSongOnBackOfQueue);
        return playbackMenu;
    }

    /**
     * Interface to abstract the logic needed to build a row for displaying a collection of songs. This will be used
     * so that we can have less duplicate code for displaying songs from a collection.
     */
    private interface ILabelAction {
        HBox createRow(Song songForRow, int songIndex);
    }

    /**
     * Function to create a implementation of the interface that will contain the logic for displaying songs in the
     * playback queue.
     *
     * @return The implementation of the playback action.
     */
    private ILabelAction createPlaybackQueueAction(){
        return new ILabelAction() {
            @Override
            public HBox createRow(Song songForRow, int songNumber) {
                HBox row = new HBox();

                if (songNumber == FIRST_SONG) {
                    row.getChildren().add(createOldestStyleLabel(Integer.toString(songNumber)));

                    Label fileName = createOldestStyleLabel(songForRow.getM_fileName());
                    row.getChildren().add(fileName);
                    HBox.setHgrow(fileName, Priority.ALWAYS);

                } else {
                    row.getChildren().add(new Label(Integer.toString(songNumber)));

                    Label fileName = new Label(songForRow.getM_fileName());
                    row.getChildren().add(fileName);
                    HBox.setHgrow(fileName, Priority.ALWAYS);
                }

                row.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getButton() == MouseButton.SECONDARY) {
                            createSubmenu(m_manager, songForRow).show(row, event.getScreenX(), event.getScreenY());
                        }
                    }
                });
                return row;
            }
        };
    }

    /**
     * Function to create a implementation of the interface that will contain the logic for displaying songs that are
     * in the history.
     *
     * @return The implementation of the history action.
     */
    private ILabelAction createHistoryAction(){
        return new ILabelAction() {
            @Override
            public HBox createRow(Song songForRow, int songNumber) {
                HBox row = new HBox();

                if (songNumber == FIRST_SONG) {
                    row.getChildren().add(createOldestStyleLabel(Integer.toString(songNumber)));

                    Label fileName = createOldestStyleLabel(songForRow.getM_fileName());
                    row.getChildren().add(fileName);
                    HBox.setHgrow(fileName, Priority.ALWAYS);
                } else {
                    row.getChildren().add(new Label(Integer.toString(songNumber)));

                    Label fileName = new Label(songForRow.getM_fileName());
                    row.getChildren().add(fileName);
                    HBox.setHgrow(fileName, Priority.ALWAYS);
                }

                // Since we are starting the list from 1 the index needs to be compensated for.
                if (m_manager.isPlayingSongOnFromHistoryList() && songNumber == m_manager.getM_historyIndex() + 1) {
                    row.setStyle("-fx-background-color: lightblue");
                }

                ContextMenu playbackMenu = createSubmenu(m_manager, songForRow);
                row.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
                    @Override
                    public void handle(ContextMenuEvent event) {
                        playbackMenu.hide();
                        playbackMenu.show(row, event.getScreenX(), event.getScreenY());
                    }
                });

                row.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getClickCount() == DOUBLE_CLICK) {
                            m_manager.playSongFromHistory(songNumber - 1);
                        }
                    }
                });
                return row;
            }
        };
    }
}
