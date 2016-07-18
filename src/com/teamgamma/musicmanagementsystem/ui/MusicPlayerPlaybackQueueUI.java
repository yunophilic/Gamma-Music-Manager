package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.model.Song;
import com.teamgamma.musicmanagementsystem.model.SongManager;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import com.teamgamma.musicmanagementsystem.util.ContextMenuBuilder;
import com.teamgamma.musicmanagementsystem.util.UserInterfaceUtils;
import com.teamgamma.musicmanagementsystem.util.UserInterfaceUtils.ILabelAction;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * Class to show the MusicPlayer Playback queue UI .
 */
public class MusicPlayerPlaybackQueueUI extends Accordion{

    // Constants
    public static final String QUEUING_HEADER = "Playing Next";
    public static final String REMOVE_SONG_FROM_QUEUE_MENU_MESSAGE = "Remove Song From Queue";

    private MusicPlayerManager m_manager;

    /**
     * Constructor
     * @param manager       The music player manager
     * @param songManager   The song manager
     */
    public MusicPlayerPlaybackQueueUI(MusicPlayerManager manager, SongManager songManager) {
        m_manager = manager;

        TitledPane queuingList = UserInterfaceUtils.createTitlePane(QUEUING_HEADER, m_manager.getPlayingQueue(),
                createPlaybackQueueAction());

        setQueuingDragActions(manager, songManager, queuingList);

        this.getPanes().add(queuingList);

        manager.registerQueingObserver(
                () -> Platform.runLater(
                    () -> queuingList.setContent(UserInterfaceUtils.createUIList(manager.getPlayingQueue(),
                            createPlaybackQueueAction()))
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
     * Function to create a implementation of the interface that will contain the logic for displaying songs in the
     * playback queue.
     *
     * @return The implementation of the playback action.
     */
    private ILabelAction createPlaybackQueueAction(){
        return (songForRow, songNumber) -> {
            HBox row = new HBox();

            row.getChildren().add(new Label(Integer.toString(songNumber)));

            Label fileName = new Label(songForRow.getFileName());
            row.getChildren().add(fileName);
            HBox.setHgrow(fileName, Priority.ALWAYS);

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
}
