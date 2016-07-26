package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.model.*;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import com.teamgamma.musicmanagementsystem.util.ContextMenuBuilder;
import com.teamgamma.musicmanagementsystem.util.FileTreeUtils;
import javafx.event.Event;
import javafx.event.EventDispatcher;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * Class for a custom cell in the tree view for search results.
 */
public class SearchTreeCell extends TextFieldTreeCell<Item> {
    private SongManager m_songManager;
    private MusicPlayerManager m_musicPlayerManager;
    private DatabaseManager m_databaseManager;

    /**
     * Constructor
     *
     * @param songManager               The song manager to interact with.
     * @param musicPlayerManager        The music player manager interact with.
     * @param dbManager
     */
    public SearchTreeCell(SongManager songManager, MusicPlayerManager musicPlayerManager, DatabaseManager dbManager) {
        m_songManager = songManager;
        m_musicPlayerManager = musicPlayerManager;
        m_databaseManager = dbManager;
    }

    @Override
    public void updateItem(Item item, boolean empty) {
        super.updateItem(item, empty);

        EventDispatcher originalEventDispatcher = getEventDispatcher();
        setEventDispatcher((event, tail) -> {
            if (event instanceof MouseEvent) {
                MouseEvent mouseEvent = (MouseEvent) event;
                if (mouseEvent.isPrimaryButtonDown() && mouseEvent.getClickCount() == 2){
                    if (item instanceof Song){
                        Song songToPlay = (Song) item;
                        if (!songToPlay.equals(m_musicPlayerManager.getCurrentSongPlaying())) {
                            m_musicPlayerManager.playSongRightNow(songToPlay);
                        }
                    }
                }
            }
            return originalEventDispatcher.dispatchEvent(event, tail);
        });

        if (item != null) {
            if (item.isRootItem()) {
                setText(item.getFile().getAbsolutePath());
            } else {
                setText(item.getFile().getName());
            }
            String iconPath = item.getFile().isDirectory() ? FileTreeUtils.FOLDER_ICON_URL : FileTreeUtils.SONG_ICON_URL;
            setGraphic(new ImageView(iconPath));

            ContextMenu contextMenu = ContextMenuBuilder.buildFileTreeContextMenu(
                    m_songManager, m_musicPlayerManager, m_databaseManager, item, false);
            contextMenu.getItems().add(ContextMenuBuilder.createShowInLibraryMenuItem(m_songManager, item));
            this.setContextMenu(contextMenu);
        }
    }
}
