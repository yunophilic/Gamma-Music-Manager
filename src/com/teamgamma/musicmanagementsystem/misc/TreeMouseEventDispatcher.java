package com.teamgamma.musicmanagementsystem.misc;

import com.teamgamma.musicmanagementsystem.model.SongManager;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * Event handling class used in CustomTreeCell
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

    @Override
    public Event dispatchEvent(Event event, EventDispatchChain tail) {
        if (event instanceof MouseEvent) {
            if (((MouseEvent) event).getButton() == MouseButton.PRIMARY
                    && ((MouseEvent) event).getClickCount() == 2) {

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
