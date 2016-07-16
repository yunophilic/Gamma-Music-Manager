package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.util.ContextMenuBuilder;
import com.teamgamma.musicmanagementsystem.model.Song;
import com.teamgamma.musicmanagementsystem.model.SongManager;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;


import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;

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
    public static final String REMOVE_SONG_FROM_QUEUE_MENU_MESSAGE = "Remove Song From Queue";

    public static final int DOUBLE_CLICK = 2;
    public static final int FIRST_SONG = 1;

    private MusicPlayerManager m_manager;

    /**
     * Constructor
     * @param manager       The music player manager
     * @param songManager   The song manager
     */
    public MusicPlayerHistoryUI(MusicPlayerManager manager, SongManager songManager) {
        m_manager = manager;

        TitledPane playbackHistory = createTitlePane(PLAYBACK_HISTORY_HEADER, m_manager.getHistory(), createHistoryAction());
        TitledPane queuingList = createTitlePane(QUEUING_HEADER, m_manager.getPlayingQueue(), createPlaybackQueueAction());

        setQueuingDragActions(manager, songManager, queuingList);

        Accordion accordion = new Accordion();
        accordion.getPanes().addAll(playbackHistory, queuingList);

        HBox.setHgrow(accordion, Priority.ALWAYS);
        this.getChildren().add(accordion);

        manager.registerNewSongObserver(() -> Platform.runLater(
                () -> playbackHistory.setContent(createUIList(manager.getHistory(), createHistoryAction()))));

        manager.registerQueingObserver(() -> Platform.runLater(
                () -> queuingList.setContent(createUIList(manager.getPlayingQueue(), createPlaybackQueueAction()))));
    }

    /**
     * Function to set the dragging actions for the Queuing list.
     *
     * @param musicPlayerManager    The music player manager to interact with.
     * @param songManager           The Song manager to get the select song that is being dragged.
     * @param queuingList           The queuing list to set the actions on.
     */
    private void setQueuingDragActions(
            MusicPlayerManager musicPlayerManager,
            SongManager songManager,
            TitledPane queuingList) {

        queuingList.setOnDragDone(event -> {
            songManager.setM_itemToMove(null);
            event.consume();
        });

        queuingList.setOnDragDropped(event -> {
            musicPlayerManager.placeSongOnBackOfPlaybackQueue((Song) songManager.getM_itemToMove());
            event.consume();
        });

        queuingList.setOnDragOver(event -> {
            if (songManager.getM_itemToMove() instanceof Song) {
                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();
            }
        });
    }

    /**
     * Helper function to create a titled pane for the accordion
     *
     * @param title The title of the accordion
     * @param songs The collection of songs that is wanted to be displayed.
     * @param action The action to take when building a row.
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

            row.setOnMouseEntered(event -> row.setStyle("-fx-background-color: lightgray"));
            row.setOnMouseExited(event -> row.setStyle(baseStyle));

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
     * @return A styled label that contains the message given.
     */
    private Label createOldestStyleLabel(String message) {
        Label styledLabel = new Label(message);
        styledLabel.setStyle("-fx-font-style: italic; -fx-font-weight: bold");

        return styledLabel;
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
        return (songForRow, songNumber) -> {
            HBox row = new HBox();

            if (songNumber == FIRST_SONG) {
                row.getChildren().add(createOldestStyleLabel(Integer.toString(songNumber)));

                Label fileName = createOldestStyleLabel(songForRow.getFileName());
                row.getChildren().add(fileName);
                HBox.setHgrow(fileName, Priority.ALWAYS);

            } else {
                row.getChildren().add(new Label(Integer.toString(songNumber)));

                Label fileName = new Label(songForRow.getFileName());
                row.getChildren().add(fileName);
                HBox.setHgrow(fileName, Priority.ALWAYS);
            }

            ContextMenu playbackMenu = ContextMenuBuilder.buildPlaybackContextMenu(m_manager, songForRow);
            MenuItem removeSong = new MenuItem(REMOVE_SONG_FROM_QUEUE_MENU_MESSAGE);
            removeSong.setOnAction(event -> m_manager.removeSongFromPlaybackQueue(songNumber - 1));
            playbackMenu.getItems().add(removeSong);

            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY) {
                    playbackMenu.hide();
                    playbackMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });
            return row;
        };
    }

    /**
     * Function to create a implementation of the interface that will contain the logic for displaying songs that are
     * in the history.
     *
     * @return The implementation of the history action.
     */
    private ILabelAction createHistoryAction(){
        return (songForRow, songNumber) -> {
            HBox row = new HBox();

            if (songNumber == FIRST_SONG) {
                row.getChildren().add(createOldestStyleLabel(Integer.toString(songNumber)));

                    Label fileName = createOldestStyleLabel(songForRow.getFileName());
                    row.getChildren().add(fileName);
                    HBox.setHgrow(fileName, Priority.ALWAYS);
                } else {
                    row.getChildren().add(new Label(Integer.toString(songNumber)));

                    Label fileName = new Label(songForRow.getFileName());
                    row.getChildren().add(fileName);
                    HBox.setHgrow(fileName, Priority.ALWAYS);
                }

            // Since we are starting the list from 1 the index needs to be compensated for.
            if (m_manager.isPlayingSongOnFromHistoryList() && songNumber == m_manager.getM_historyIndex() + 1) {
                row.setStyle("-fx-background-color: lightblue");
            }

            ContextMenu playbackMenu = ContextMenuBuilder.buildPlaybackContextMenu(m_manager, songForRow);
            row.setOnContextMenuRequested(event -> {
                playbackMenu.hide();
                playbackMenu.show(row, event.getScreenX(), event.getScreenY());
            });

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == DOUBLE_CLICK) {
                    m_manager.playSongFromHistory(songNumber - 1);
                }
            });
            return row;
        };
    }
}
