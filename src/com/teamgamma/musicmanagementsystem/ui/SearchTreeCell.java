package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.model.DatabaseManager;
import com.teamgamma.musicmanagementsystem.model.Item;
import com.teamgamma.musicmanagementsystem.model.SongManager;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import com.teamgamma.musicmanagementsystem.util.ContextMenuBuilder;
import com.teamgamma.musicmanagementsystem.util.FileTreeUtils;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.ImageView;

/**
 * Class for a custom cell in the tree view for search results.
 */
public class SearchTreeCell extends TextFieldTreeCell<Item> {

    private SongManager m_songManager;
    private MusicPlayerManager m_musicPlayerManager;
    private DatabaseManager m_databaseManager;

    public SearchTreeCell(SongManager songManager, MusicPlayerManager musicPlayerManager, DatabaseManager dbManager) {
        m_songManager = songManager;
        m_musicPlayerManager = musicPlayerManager;
        m_databaseManager = dbManager;
    }

    @Override
    public void updateItem(Item item, boolean empty) {
        super.updateItem(item, empty);

        if (item != null) {
            if (item.isRootItem()) {
                setText(item.getFile().getAbsolutePath());
            } else {
                setText(item.getFile().getName());
            }
            String iconPath = item.getFile().isDirectory() ? FileTreeUtils.FOLDER_ICON_URL : FileTreeUtils.SONG_ICON_URL;
            setGraphic(new ImageView(iconPath));

            this.setContextMenu(ContextMenuBuilder.buildFileTreeContextMenu(
                    m_songManager, m_musicPlayerManager, m_databaseManager, item, false));
        }
    }
}
