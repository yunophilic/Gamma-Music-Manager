package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.model.*;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import com.teamgamma.musicmanagementsystem.util.ContextMenuBuilder;
import com.teamgamma.musicmanagementsystem.util.FileTreeUtils;
import com.teamgamma.musicmanagementsystem.util.UserInterfaceUtils;
import javafx.event.Event;
import javafx.event.EventDispatcher;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;

/**
 * Class for a custom cell in the tree view for search results.
 */
public class SearchTreeCell extends TextFieldTreeCell<Item> {
    private SongManager m_songManager;
    private MusicPlayerManager m_musicPlayerManager;
    private DatabaseManager m_databaseManager;
    private TreeView<Item> m_tree;
    private Item m_selectedItem;

    /**
     * Constructor
     *
     * @param songManager               The song manager to interact with.
     * @param musicPlayerManager        The music player manager interact with.
     * @param dbManager                 The database manager to interact with.
     * @param tree                      The tree view that the cell is working on.
     */
    public SearchTreeCell(SongManager songManager, MusicPlayerManager musicPlayerManager, DatabaseManager dbManager, TreeView<Item> tree) {
        m_songManager = songManager;
        m_musicPlayerManager = musicPlayerManager;
        m_databaseManager = dbManager;
        m_tree = tree;
    }

    @Override
    public void updateItem(Item item, boolean empty) {
        super.updateItem(item, empty);
        m_selectedItem = item;

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
                    m_songManager, m_musicPlayerManager, m_databaseManager, item, false, m_tree);
            contextMenu.getItems().add(ContextMenuBuilder.createShowInLibraryMenuItem(m_songManager, item));
            this.setContextMenu(contextMenu);
        }
    }

}
