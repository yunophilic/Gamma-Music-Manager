package com.teamgamma.musicmanagementsystem.misc;

import com.teamgamma.musicmanagementsystem.model.Song;
import com.teamgamma.musicmanagementsystem.model.SongManager;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import com.teamgamma.musicmanagementsystem.ui.MusicPlayerHistoryUI;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.util.List;

/**
 * Custom EventDispatcher class to override the default double-click behaviour of TreeView
 * This class is a modification of http://stackoverflow.com/questions/15509203/disable-treeitems-default-expand-collapse-on-double-click-javafx-2-2
 */
public class TreeMouseEventDispatcher implements EventDispatcher {
    private final EventDispatcher m_originalDispatcher;
    private SongManager m_model;
    private MusicPlayerManager m_musicPlayerManager;
    private TreeViewItem m_selectedTreeViewItem;
    private TreeView<TreeViewItem> m_tree;
    private boolean m_isLeftPane;

    public TreeMouseEventDispatcher(EventDispatcher originalDispatcher,
                                    SongManager model,
                                    MusicPlayerManager musicPlayerManager,
                                    TreeView<TreeViewItem> tree,
                                    TreeViewItem selectedTreeViewItem,
                                    boolean isLeftPane) {
        m_originalDispatcher = originalDispatcher;
        m_model = model;
        m_musicPlayerManager = musicPlayerManager;
        m_tree = tree;
        m_selectedTreeViewItem = selectedTreeViewItem;
        m_isLeftPane = isLeftPane;
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
                    if (m_selectedTreeViewItem != null) {
                        boolean isFolder = m_selectedTreeViewItem.getM_file().isDirectory();

                        //Only notify center panel if this is a left panel and if this is a directory
                        if (m_isLeftPane && isFolder) {
                            System.out.println("Selected Item: " + m_selectedTreeViewItem);
                            m_model.setM_selectedCenterFolder(m_selectedTreeViewItem.getM_file());
                            m_model.notifyCenterFolderObservers();

                            TreeViewUtil.closeAllFoldersIcons(m_tree.getRoot());

                            TreeViewUtil.setOpenFolder(m_tree, m_selectedTreeViewItem.getM_file().getAbsolutePath());
                        } else if (!isFolder) {
                            Song songToPlay = TreeViewUtil.getSongSelected(m_tree, m_selectedTreeViewItem, m_model);

                            if (songToPlay != null) {
                                m_musicPlayerManager.playSongRightNow(songToPlay);
                            } else {
                                System.out.println("SOMETHING WRONG!!!");
                            }
                        }
                    }
                }

                event.consume();
            } else if (mouseEvent.isControlDown() && (mouseEvent.isPrimaryButtonDown())){
                if (!event.isConsumed()) {
                    Song songSelected = TreeViewUtil.getSongSelected(m_tree, m_selectedTreeViewItem, m_model);
                    if (songSelected != null) {
                        MusicPlayerHistoryUI.createSubmenu(m_musicPlayerManager, songSelected).show(
                                m_tree, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                    }
                }
                event.consume();
            }
        }
        return m_originalDispatcher.dispatchEvent(event, tail);
    }

}
