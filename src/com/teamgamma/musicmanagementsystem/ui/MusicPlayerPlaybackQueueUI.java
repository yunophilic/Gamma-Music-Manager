package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.model.Item;
import com.teamgamma.musicmanagementsystem.model.Song;
import com.teamgamma.musicmanagementsystem.model.SongManager;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import com.teamgamma.musicmanagementsystem.util.Action;
import com.teamgamma.musicmanagementsystem.util.ContextMenuBuilder;
import com.teamgamma.musicmanagementsystem.util.UserInterfaceUtils;
import com.teamgamma.musicmanagementsystem.util.UserInterfaceUtils.ILabelAction;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.io.File;

/**
 * Class to show the MusicPlayer Playback queue UI .
 */
public class MusicPlayerPlaybackQueueUI extends Accordion{

    // Constants
    private static final String QUEUING_HEADER = "Playing Next";
    private static final String REMOVE_SONG_FROM_QUEUE_MENU_MESSAGE = "Remove Song From Queue";
    private static final String BRIGHT_BACKGROUND_COLOR = "-fx-background-color: #65EFFF";
    private static final Color BRIGHT_BACKGROUND_COLOR_OBJ = Color.rgb(101, 239, 255);

    private MusicPlayerManager m_manager;

    /**
     * Constructor
     * @param manager       The music player manager
     * @param songManager   The song manager
     */
    public MusicPlayerPlaybackQueueUI(MusicPlayerManager manager, SongManager songManager) {
        m_manager = manager;

        TitledPane queuingList = UserInterfaceUtils.createTitlePane(QUEUING_HEADER, m_manager.getPlayingQueue(),
                createPlaybackQueueAction(songManager), BRIGHT_BACKGROUND_COLOR);

        queuingList.setStyle(BRIGHT_BACKGROUND_COLOR);

        Background background = new Background(new BackgroundFill(BRIGHT_BACKGROUND_COLOR_OBJ, null, null));
        queuingList.setBackground(background);
        this.setBackground(background);

        setQueuingDragActions(manager, songManager, queuingList);

        this.getPanes().add(queuingList);

        manager.registerQueingObserver(
            () -> Platform.runLater(
                () -> queuingList.setContent(UserInterfaceUtils.createUIList(manager.getPlayingQueue(),
                        createPlaybackQueueAction(songManager), BRIGHT_BACKGROUND_COLOR))
            )
        );

        manager.registerNewSongObserver(
            () -> Platform.runLater(
                () -> {
                    if (!m_manager.isPlayingSongOnFromHistoryList() && !m_manager.getPlayingQueue().isEmpty()){
                        this.setExpandedPane(queuingList);
                    }
                }
            )
        );

        songManager.addFileObserver(fileActions -> {
            for (Pair<Action, File> action : fileActions){
                if (action.getKey() == Action.DELETE) {
                    m_manager.removeAllInstancesOfSongFromPlaybackQueue(action.getValue().getAbsolutePath());
                }
            }
            Platform.runLater(
                () -> queuingList.setContent(UserInterfaceUtils.createUIList(
                            manager.getPlayingQueue(),
                            createPlaybackQueueAction(songManager),
                            BRIGHT_BACKGROUND_COLOR))
            );
        });

        songManager.addMinimodeObserver(() -> {
            TitledPane expandedPane = this.getExpandedPane();
            if (expandedPane != null) {
                expandedPane.setExpanded(false);
            }
        });
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

        queuingList.setOnDragDone((event) -> {
            songManager.setM_itemsToMove(null);
            event.consume();
        });

        queuingList.setOnDragDropped((event) -> {
            for (Item itemToMove : songManager.getM_itemsToMove()) {
                musicPlayerManager.placeSongOnBackOfPlaybackQueue((Song) itemToMove);
            }
            event.consume();
        });

        queuingList.setOnDragOver((event) -> {
            if (songManager.itemsToMoveAreAllSongs()) {
                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();
            }
        });
    }

    /**
     * Function to create a implementation of the interface that will contain the logic for displaying songs in the
     * playback queue.
     *
     * @param songManager The SongManager model
     *
     * @return The implementation of the playback action.
     */
    private ILabelAction createPlaybackQueueAction(SongManager songManager){
        return (songForRow, songNumber) -> {
            HBox row = new HBox();

            row.getChildren().add(new Label(Integer.toString(songNumber)));

            Label fileName = new Label(songForRow.getFileName());
            row.getChildren().add(fileName);
            HBox.setHgrow(fileName, Priority.ALWAYS);

            ContextMenu playbackMenu = ContextMenuBuilder.buildPlaybackContextMenu(m_manager, songManager, songForRow);
            MenuItem removeSong = new MenuItem(REMOVE_SONG_FROM_QUEUE_MENU_MESSAGE);
            removeSong.setOnAction((event) -> m_manager.removeSongFromPlaybackQueue(songNumber - 1));
            playbackMenu.getItems().add(removeSong);

            row.setOnMouseClicked((event) -> {
                if (event.getButton() == MouseButton.SECONDARY) {
                    playbackMenu.hide();
                    playbackMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });

            row.setStyle(BRIGHT_BACKGROUND_COLOR);
            return row;
        };
    }
}
