package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.model.DatabaseManager;
import com.teamgamma.musicmanagementsystem.model.Song;
import com.teamgamma.musicmanagementsystem.model.SongManager;
import com.teamgamma.musicmanagementsystem.model.Item;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import com.teamgamma.musicmanagementsystem.util.FileTreeUtils;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * Custom EventDispatcher class to override the default double-click behaviour of TreeView
 * This class is a modification of http://stackoverflow.com/questions/15509203/disable-treeitems-default-expand-collapse-on-double-click-javafx-2-2
 */
public class CustomEventDispatcher implements EventDispatcher {
    private final EventDispatcher m_originalDispatcher;
    private SongManager m_model;
    private MusicPlayerManager m_musicPlayerManager;
    private Item m_selectedItem;
    private TreeView<Item> m_tree;
    private boolean m_isLeftPane;
    private DatabaseManager m_databaseManager;

    // Static private member for making the control click menu be just a single instance.
    //private static ContextMenu m_playbackContextMenuInstance;

    public CustomEventDispatcher(EventDispatcher originalDispatcher,
                                 SongManager model,
                                 MusicPlayerManager musicPlayerManager,
                                 TreeView<Item> tree,
                                 Item selectedItem,
                                 boolean isLeftPane,
                                 DatabaseManager databaseManager) {
        m_originalDispatcher = originalDispatcher;
        m_model = model;
        m_musicPlayerManager = musicPlayerManager;
        m_tree = tree;
        m_selectedItem = selectedItem;
        m_isLeftPane = isLeftPane;
        m_databaseManager = databaseManager;
    }

    /**
     * Override the default collapse/expand behaviour of TreeViewItems, only show content in center panel
     * @param event
     * @param tail
     * @return dispatch event
     */
    @Override
    public Event dispatchEvent(Event event, EventDispatchChain tail) {
        if (event instanceof MouseEvent) {
            MouseEvent mouseEvent = (MouseEvent) event;
            boolean isPrimaryMouseButton = mouseEvent.getButton() == MouseButton.PRIMARY;
            boolean isDoubleClick = mouseEvent.getClickCount() == 2;

            if (isPrimaryMouseButton && isDoubleClick) {
                if (!event.isConsumed()) {
                    if (m_selectedItem != null) {
                        boolean isFolder = m_selectedItem.getFile().isDirectory();

                        //Only notify center panel if this is a left panel and if this is a directory
                        if (m_isLeftPane && isFolder) {
                            System.out.println("Selected Item: " + m_selectedItem);
                            m_model.setM_selectedCenterFolder(m_selectedItem.getFile());
                            m_model.notifyCenterFolderObservers();

                            FileTreeUtils.closeAllFoldersIcons(m_tree.getRoot());
                            FileTreeUtils.setOpenFolder(m_tree, m_selectedItem.getFile().getAbsolutePath());

                        } else if (!isFolder) {
                            Song songToPlay = (Song) m_selectedItem;
                            if (!songToPlay.equals(m_musicPlayerManager.getCurrentSongPlaying())) {
                                m_musicPlayerManager.playSongRightNow(songToPlay);
                            }
                        }

                        //FileTreeUtils.setTreeCellText(m_tree);
                        m_tree.setCellFactory(arg -> new CustomTreeCell(m_model, m_musicPlayerManager, m_databaseManager, m_tree, m_isLeftPane));
                    }
                }

                event.consume();
            } /*else if (mouseEvent.isControlDown() && mouseEvent.isPrimaryButtonDown()) {
                if (!event.isConsumed()) {
                    if (m_selectedItem instanceof Song) {
                        Song songSelected = (Song) m_selectedItem;
                        if (m_playbackContextMenuInstance != null) {
                            m_playbackContextMenuInstance.hide();
                        }
                        m_playbackContextMenuInstance = MusicPlayerHistoryUI.createSubmenu(m_musicPlayerManager, songSelected);
                        m_playbackContextMenuInstance.show(m_tree, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                    }
                }
                event.consume();
            }*/
        }
        return m_originalDispatcher.dispatchEvent(event, tail);
    }

}
