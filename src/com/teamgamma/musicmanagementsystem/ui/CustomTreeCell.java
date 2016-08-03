package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.model.DatabaseManager;
import com.teamgamma.musicmanagementsystem.model.Item;
import com.teamgamma.musicmanagementsystem.model.Song;
import com.teamgamma.musicmanagementsystem.model.SongManager;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import com.teamgamma.musicmanagementsystem.util.ContextMenuBuilder;
import com.teamgamma.musicmanagementsystem.util.UserInterfaceUtils;

import javafx.event.EventDispatcher;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Event handling class used in LibraryUI and DynamicTreeViewUI
 */
public class CustomTreeCell extends TextFieldTreeCell<Item> {
    private static final String OPEN_FOLDER_ICON_URL = "Status-folder-open-icon.png";
    private static final String FOLDER_ICON_URL = "folder-icon.png";
    private static final String SONG_ICON_URL = "music-file-icon.png";

    //attributes
    private SongManager m_model;
    private MusicPlayerManager m_musicPlayerManager;
    private DatabaseManager m_databaseManager;
    private ContextMenu m_contextMenu;
    private TreeView<Item> m_tree;
    private Item m_selectedItem;
    private boolean m_isLeftPane;
    private CellType m_cellType;

    public CustomTreeCell(SongManager model,
                          MusicPlayerManager musicPlayerManager,
                          DatabaseManager databaseManager,
                          TreeView<Item> tree,
                          CellType cellType) {
        m_model = model;
        m_musicPlayerManager = musicPlayerManager;
        m_databaseManager = databaseManager;
        m_tree = tree;
        m_cellType = cellType;
        createContextMenu();
        m_isLeftPane = (cellType == CellType.LEFT_FILE_PANE);
        setDragEvents();

        m_tree.getCellFactory();
    }

    /**
     * Build the m_contextMenu
     */
    private void createContextMenu() {
        m_contextMenu = ContextMenuBuilder.buildFileTreeContextMenu(m_model,
                m_musicPlayerManager,
                m_databaseManager,
                m_selectedItem,
                m_cellType,
                m_tree);
        }

    /**
     * Set mouse events on this CustomTreeCell
     */
    private void setDragEvents() {
        m_tree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setOnDragDetected((mouseEvent) -> {
            if (m_selectedItem != null) {
                System.out.println("Drag detected on " + m_selectedItem);

                //update model
                List<Item> selectedItems = new ArrayList<>();
                for (TreeItem<Item> selectedTreeItem : m_tree.getSelectionModel().getSelectedItems()) {
                    selectedItems.add(selectedTreeItem.getValue());
                }
                m_model.setM_itemsToMove(selectedItems);

                //update drag board
                Dragboard dragBoard = startDragAndDrop(TransferMode.MOVE);
                dragBoard.setDragView(snapshot(null, null));
                ClipboardContent content = new ClipboardContent();
                content.put(DataFormat.PLAIN_TEXT, m_selectedItem.getFile().getAbsolutePath());
                dragBoard.setContent(content);

                mouseEvent.consume();
            }
        });

        setOnDragOver((dragEvent) -> {
            System.out.println("Drag over on " + m_selectedItem);
            if (dragEvent.getDragboard().hasString()) {
                List<Item> itemsToMove = m_model.getM_itemsToMove();

                boolean itemsToMoveContainsRoot = false;
                for (Item item : itemsToMove) {
                    if (item.isRootItem()) {
                        itemsToMoveContainsRoot = true;
                    }
                }

                if (!itemsToMove.contains(m_selectedItem) && !itemsToMoveContainsRoot) {
                    dragEvent.acceptTransferModes(TransferMode.MOVE);
                }
            }
            dragEvent.consume();
        });

        setOnDragDropped((dragEvent) -> {
            System.out.println("Drag dropped on " + m_selectedItem);

            File destination;
            if (!m_selectedItem.getFile().isDirectory()) {
                destination = m_selectedItem.getFile().getParentFile();
            } else {
                destination = m_selectedItem.getFile();
            }

            UserInterfaceUtils.moveFileAction(m_model, destination);

            dragEvent.consume();
        });

        setOnDragDone((dragEvent) -> {
            System.out.println("Drag done");
            m_model.setM_itemsToMove(null);
            dragEvent.consume();
        });
    }

    @Override
    public void updateItem(Item item, boolean empty) {
        super.updateItem(item, empty);
        m_selectedItem = item;
        EventDispatcher originalDispatcher = getEventDispatcher();
        setEventDispatcher(new CustomEventDispatcher(originalDispatcher,
                                                        m_model,
                                                        m_musicPlayerManager,
                                                        m_tree,
                                                        m_selectedItem,
                                                        m_isLeftPane,
                                                        m_databaseManager));
        createContextMenu();
        setContextMenu(m_contextMenu);

        if (m_selectedItem != null) {
            boolean isLeftPaneRoot = ((m_cellType == CellType.LEFT_FILE_PANE) && m_selectedItem.isRootItem());
            boolean isRightPaneRoot = ((m_cellType == CellType.RIGHT_FILE_PANE) && m_selectedItem.isRightRootItem());
            if (isLeftPaneRoot || isRightPaneRoot) {
                setText(m_selectedItem.getFile().getAbsolutePath());
            } else {
                setText(m_selectedItem.getFile().getName());
            }

            if (m_cellType == CellType.LEFT_FILE_PANE
                    && m_model.getM_selectedCenterFolder() != null
                    && m_model.getM_selectedCenterFolder().equals(item.getFile())) {
                setGraphic(new ImageView(OPEN_FOLDER_ICON_URL));
            } else {
                String iconPath = item.getFile().isDirectory() ? FOLDER_ICON_URL : SONG_ICON_URL;
                setGraphic(new ImageView(iconPath));
            }
        }
    }
}
