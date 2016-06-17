package com.teamgamma.musicmanagementsystem.misc;

import com.teamgamma.musicmanagementsystem.model.SongManager;
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
    private TreeViewItem m_selectedTreeViewItem;
    private TreeView<TreeViewItem> m_tree;
    private boolean m_isLeftPane;

    // Images
    private static final Image openFolerImage = new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator + "Status-folder-open-icon.png"));
    private static final Image folderImage = new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator + "folder-icon.png"));
    private static final Image songImage = new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator + "music-file-icon.png"));

    public TreeMouseEventDispatcher(EventDispatcher originalDispatcher, SongManager model, TreeView<TreeViewItem> tree, TreeViewItem selectedTreeViewItem, boolean isLeftPane) {
        m_originalDispatcher = originalDispatcher;
        m_model = model;
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
            boolean isPrimaryMouseButton = ((MouseEvent) event).getButton() == MouseButton.PRIMARY;
            boolean isDoubleClick = ((MouseEvent) event).getClickCount() == 2;

            if (isPrimaryMouseButton && isDoubleClick) {
                if (!event.isConsumed()) {
                    // Only notify center panel if this is a left panel
                    if (m_isLeftPane) {
                        System.out.println("Selected Item: " + m_selectedTreeViewItem);
                        m_model.setM_selectedCenterFolder(m_selectedTreeViewItem.getM_file());
                        m_model.notifyCenterFolderObservers();

                        closeOpenFolders();

                        setOpenFolderImage();
                    }
                }

                event.consume();
            }
        }
        return m_originalDispatcher.dispatchEvent(event, tail);
    }

    private void closeOpenFolders() {
        closeAllFolders(m_tree.getRoot());
    }

    private void closeAllFolders(TreeItem<TreeViewItem> treeItem) {
        //System.out.println("#### closing file: " + treeItem.getValue());
        if (treeItem.getValue().getM_file().isDirectory()) {
            treeItem.setGraphic(new ImageView(folderImage));
        } else {
            treeItem.setGraphic(new ImageView(songImage));
        }
        if (!treeItem.getChildren().isEmpty()){
            List<TreeItem<TreeViewItem>> childTreeItems = treeItem.getChildren();
            for (TreeItem<TreeViewItem> child: childTreeItems) {
                closeAllFolders(child);
            }
        }
    }

    private void setOpenFolderImage() {
        TreeItem<TreeViewItem> selectedTreeItem = searchTreeItem(m_selectedTreeViewItem.getM_file().getAbsolutePath());
        System.out.println("@@@ Found treeitem: " + selectedTreeItem.getValue());
        System.out.println("@@@ Found treeitem: " + selectedTreeItem);
        selectedTreeItem.setGraphic(new ImageView(openFolerImage));
    }

    /**
     * Search for the TreeItem<TreeViewItem> from the whole tree based on the given path
     *
     * @param path the specified path
     * @return TreeItem<TreeViewItem> or null if not found
     */
    private TreeItem<TreeViewItem> searchTreeItem(String path) {
        return searchTreeItem(m_tree.getRoot(), path);
    }

    /**
     * Search for the TreeItem<TreeViewItem> from the sub-tree rooted at the specified node based on the given path
     *
     * @param node the specified node
     * @param path the specified path
     * @return TreeItem<TreeViewItem> or null if not found
     */
    private TreeItem<TreeViewItem> searchTreeItem(TreeItem<TreeViewItem> node, String path) {
        //base case
        if (node.getValue().getM_file().getAbsolutePath().equals(path)) {
            return node;
        }

        //recursive case
        for (TreeItem<TreeViewItem> child : node.getChildren()) {
            TreeItem<TreeViewItem> target = searchTreeItem(child, path);
            if (target != null) {
                return target;
            }
        }

        return null;
    }
}
