package com.teamgamma.musicmanagementsystem.misc;

import com.teamgamma.musicmanagementsystem.model.SongManager;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * Custom EventDispatcher class to override the default double-click behaviour of TreeView
 */
public class TreeMouseEventDispatcher implements EventDispatcher {
    private final EventDispatcher m_originalDispatcher;
    private SongManager m_model;
    private TreeViewItem m_selectedTreeViewItem;

    public TreeMouseEventDispatcher(EventDispatcher originalDispatcher, SongManager model, TreeViewItem selectedTreeViewItem) {
        m_originalDispatcher = originalDispatcher;
        m_model = model;
        m_selectedTreeViewItem = selectedTreeViewItem;
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
            boolean isPrimaryMouseButton = ((MouseEvent) event).getButton() == MouseButton.PRIMARY;
            boolean isDoubleClick = ((MouseEvent) event).getClickCount() == 2;

            if (isPrimaryMouseButton && isDoubleClick) {
                if (!event.isConsumed()) {
                    System.out.println("Selected Item: " + m_selectedTreeViewItem);
                    m_model.setCenterFolder(m_selectedTreeViewItem.getM_file());
                    m_model.notifyCenterFolderObservers();
                }

                event.consume();
            }
        }
        return m_originalDispatcher.dispatchEvent(event, tail);
    }
}
