package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.model.DatabaseManager;
import com.teamgamma.musicmanagementsystem.model.Item;
import com.teamgamma.musicmanagementsystem.model.Song;
import com.teamgamma.musicmanagementsystem.model.SongManager;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import com.teamgamma.musicmanagementsystem.ui.CustomEventDispatcher;
import com.teamgamma.musicmanagementsystem.ui.PromptUI;
import com.teamgamma.musicmanagementsystem.util.ContextMenuBuilder;
import javafx.collections.ObservableList;
import javafx.event.EventDispatcher;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;

/**
 * Event handling class used in LibraryUI and DynamicTreeViewUI
 */
public class CustomTreeCell extends TextFieldTreeCell<Item> {
    //attributes
    private SongManager m_model;
    private MusicPlayerManager m_musicPlayerManager;
    private DatabaseManager m_databaseManager;
    private ContextMenu m_contextMenu;
    private TreeView<Item> m_tree;
    private Item m_selectedItem;
    private boolean m_isLeftPane;
    private List<Song> m_selectedSongs;

    public CustomTreeCell(SongManager model,
                          MusicPlayerManager musicPlayerManager,
                          DatabaseManager databaseManager,
                          TreeView<Item> tree,
                          boolean isLeftPane) {
        m_model = model;
        m_musicPlayerManager = musicPlayerManager;
        m_databaseManager = databaseManager;
        m_tree = tree;
        createContextMenu();
        m_isLeftPane = isLeftPane;
        setDragEvents();
    }

    /**
     * Build the m_contextMenu
     */
    private void createContextMenu() {
        m_contextMenu = ContextMenuBuilder.buildFileTreeContextMenu(m_model,
                                                                    m_musicPlayerManager,
                                                                    m_databaseManager,
                                                                    m_selectedItem,
                                                                    m_isLeftPane,
                                                                    m_selectedSongs);
    }

    /**
     * Set mouse events on this CustomTreeCell
     */
    private void setDragEvents() {
        m_tree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setOnDragDetected(mouseEvent -> {
            if (m_selectedItem != null) {
                System.out.println("Drag detected on " + m_selectedItem);

                //update model
                m_model.setM_itemToMove(m_selectedItem);

                //update drag board
                Dragboard dragBoard = startDragAndDrop(TransferMode.MOVE);
                dragBoard.setDragView(snapshot(null, null));
                ClipboardContent content = new ClipboardContent();
                content.put(DataFormat.PLAIN_TEXT, m_selectedItem.getFile().getAbsolutePath());
                dragBoard.setContent(content);

                mouseEvent.consume();
            }
        });

        setOnDragOver(dragEvent -> {
            System.out.println("Drag over on " + m_selectedItem);
            if (dragEvent.getDragboard().hasString()) {
                String draggedItemPath = dragEvent.getDragboard().getString();
                String destination = m_selectedItem.getFile().getAbsolutePath();
                if (!draggedItemPath.equals(destination)) {
                    dragEvent.acceptTransferModes(TransferMode.MOVE);
                }
            }
            dragEvent.consume();
        });

        setOnDragDropped(dragEvent -> {
            System.out.println("Drag dropped on " + m_selectedItem);

            try {
                //move in the file system
                File fileToMove = m_model.getFileToMove();
                File destination;
                if (!m_selectedItem.getFile().isDirectory()) {
                    destination = m_selectedItem.getFile().getParentFile();
                } else {
                    destination = m_selectedItem.getFile();
                }

                m_model.moveFile(fileToMove, destination);
            } catch (FileAlreadyExistsException ex) {
                PromptUI.customPromptError("Error", null, "The following file or folder already exist!\n" + ex.getMessage());
            } catch (AccessDeniedException ex) {
                PromptUI.customPromptError("Error", null, "AccessDeniedException: \n" + ex.getMessage());
                ex.printStackTrace();
            } catch (IOException ex) {
                PromptUI.customPromptError("Error", null, "IOException: \n" + ex.getMessage());
                ex.printStackTrace();
            }

            dragEvent.consume();
        });

        setOnDragDone(dragEvent -> {
            System.out.println("Drag done");
            m_model.setM_itemToMove(null);
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
                                                        m_isLeftPane));
        createContextMenu();
        setContextMenu(m_contextMenu);
    }
}
