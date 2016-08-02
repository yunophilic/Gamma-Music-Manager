package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.model.SongManager;
import com.teamgamma.musicmanagementsystem.util.Action;
import com.teamgamma.musicmanagementsystem.util.ContextMenuBuilder;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import com.teamgamma.musicmanagementsystem.util.UserInterfaceUtils;
import com.teamgamma.musicmanagementsystem.util.UserInterfaceUtils.ILabelAction;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Pair;

import java.io.File;

/**
 * Class to show the MusicPlayer History.
 */
public class MusicPlayerHistoryUI extends Accordion{

    // Constants
    public static final String PLAYBACK_HISTORY_HEADER = "History";

    public static final int DOUBLE_CLICK = 2;

    private MusicPlayerManager m_manager;

    /**
     * Constructor
     *
     * @param model         The Songmanager model
     * @param manager       The music player manager
     */
    public MusicPlayerHistoryUI(SongManager model, MusicPlayerManager manager) {
        m_manager = manager;

        TitledPane playbackHistory = UserInterfaceUtils.createTitlePane(PLAYBACK_HISTORY_HEADER, m_manager.getHistory(),
                createHistoryAction(model));

        this.getPanes().add(playbackHistory);

        manager.registerNewSongObserver(
                () -> Platform.runLater(
                    () -> playbackHistory.setContent(UserInterfaceUtils.createUIList(manager.getHistory(),
                            createHistoryAction(model)))
                )
        );

        model.addFileObserver(fileActions -> {
            for (Pair<Action, File> action : fileActions) {
                if (action.getKey() == Action.DELETE) {
                    manager.removeAllInstancesOfSongFromHistory(action.getValue().getAbsolutePath());
                }
            }
            Platform.runLater(
                    () -> playbackHistory.setContent(UserInterfaceUtils.createUIList(manager.getHistory(),
                            createHistoryAction(model)))
            );

        });

        model.addMinimodeObserver(() -> {
            TitledPane expandedPane = this.getExpandedPane();
            if (expandedPane != null) {
                expandedPane.setExpanded(false);
            }
        });
    }

    /**
     * Helper Function to create a styled label for the oldest element.
     *
     * @param message The message you want the label to have
     * @return A styled label that contains the message given.
     */
    private Label createCurrentPlayingLabelStyle(String message) {
        Label styledLabel = new Label(message);
        styledLabel.setStyle("-fx-font-style: italic; -fx-font-weight: bold");

        return styledLabel;
    }

    /**
     * Function to create a implementation of the interface that will contain the logic for displaying songs that are
     * in the history.
     *
     * @param songManager The model
     *
     * @return The implementation of the history action.
     */
    private ILabelAction createHistoryAction(SongManager songManager){
        return (songForRow, songNumber) -> {
            HBox row = new HBox();

            // Since we are starting the list from 1 the index needs to be compensated for.
            if (m_manager.isPlayingSongOnFromHistoryList() && songNumber == m_manager.getM_historyIndex() + 1) {
                row.getChildren().add(createCurrentPlayingLabelStyle(Integer.toString(songNumber)));

                Label fileName = createCurrentPlayingLabelStyle(songForRow.getFileName());
                row.getChildren().add(fileName);
                HBox.setHgrow(fileName, Priority.ALWAYS);
            } else {
                row.getChildren().add(new Label(Integer.toString(songNumber)));

                Label fileName = new Label(songForRow.getFileName());
                row.getChildren().add(fileName);
                HBox.setHgrow(fileName, Priority.ALWAYS);
            }

            ContextMenu playbackMenu = ContextMenuBuilder.buildPlaybackContextMenu(m_manager, songManager, songForRow);
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
