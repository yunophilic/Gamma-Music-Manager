package com.teamgamma.musicmanagementsystem.misc;

import com.teamgamma.musicmanagementsystem.model.PersistentStorage;
import com.teamgamma.musicmanagementsystem.model.SongManager;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import com.teamgamma.musicmanagementsystem.ui.PromptUI;

import javafx.event.ActionEvent;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.*;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.util.ArrayList;
import java.util.List;

/**
 * Event handling class used in LibraryUI and DynamicTreeViewUI
 */
public class CustomTreeCell extends TextFieldTreeCell<TreeViewItem> {
    //attributes
    private ContextMenu m_contextMenu;
    private SongManager m_model;
    private MusicPlayerManager m_musicPlayerManager;
    private TreeView<TreeViewItem> m_tree;
    private TreeViewItem m_selectedTreeViewItem;

    //constants
    private static final String COPY = "Copy";
    private static final String PASTE = "Paste";
    private static final String DELETE = "Delete";
    private static final String REMOVE_THIS_LIBRARY = "Remove This Library";
    private static final String SHOW_IN_RIGHT_PANE = "Show in Right Pane";


    public CustomTreeCell(SongManager model, MusicPlayerManager musicPlayerManager, TreeView<TreeViewItem> tree, boolean isLeftPane) {
        m_model = model;
        m_musicPlayerManager = musicPlayerManager;
        m_tree = tree;
        m_contextMenu = new ContextMenu();
        m_contextMenu.getItems().addAll(generateMenuItems(isLeftPane));
        setDragEvents();
    }

    /**
     * Generate menu items
     *
     * @param isLeftPane determines the menu items
     * @return List<MenuItem>
     */
    private List<MenuItem> generateMenuItems(boolean isLeftPane) {
        //copy option
        MenuItem copy = new MenuItem(COPY);
        copy.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (m_selectedTreeViewItem != null) {
                    m_model.setM_fileToCopy(m_selectedTreeViewItem.getM_file());
                }
            }
        });

        //paste option
        MenuItem paste = new MenuItem(PASTE);
        paste.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (m_selectedTreeViewItem != null) {
                    File dest = m_selectedTreeViewItem.getM_file();
                    if (!dest.isDirectory()) {
                        PromptUI.customPromptError("Not a directory!", "", "Please select a directory as the paste target.");
                        return;
                    }
                    try {
                        m_model.copyToDestination(dest);
                        m_model.notifyFileObservers();
                    } catch (FileAlreadyExistsException ex) {
                        PromptUI.customPromptError("Error", "", "The following file or folder already exist!\n" + ex.getMessage());
                    } catch (IOException ex) {
                        PromptUI.customPromptError("Error", "", "IOException: " + ex.getMessage());
                    }
                }
            }
        });

        //delete option
        MenuItem delete = new MenuItem(DELETE);
        delete.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (m_selectedTreeViewItem != null) {
                    File fileToDelete = m_selectedTreeViewItem.getM_file();
                    //confirmation dialog
                    if (!PromptUI.customPromptConfirmation(
                            "Deleting " + (fileToDelete.isDirectory() ? "folder" : "file"),
                            null,
                            "Are you sure you want to permanently delete \"" + fileToDelete.getName() + "\"?")) {
                        return;
                    }
                    //try to actually delete (retry if FileSystemException happens)
                    for(int i=0; i<2; i++) {
                        try {
                            m_model.deleteFile(fileToDelete);
                            break;
                        } catch (FileSystemException ex) {
                            m_musicPlayerManager.stopSong();
                            if (i==1) { //if this exception still thrown after retry (for debugging)
                                ex.printStackTrace();
                            }
                        } catch (Exception ex) {
                            PromptUI.customPromptError("Error", null, "Exception: \n" + ex.getMessage());
                            ex.printStackTrace();
                            break;
                        }
                    }
                    PersistentStorage persistentStorage = new PersistentStorage();
                    persistentStorage.removePersistentStorageLibrary(fileToDelete.getAbsolutePath());
                    m_model.notifyFileObservers();
                }
            }
        });

        //remove library
        MenuItem removeLibrary = new MenuItem(REMOVE_THIS_LIBRARY);
        removeLibrary.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (m_selectedTreeViewItem != null) {
                    System.out.println("Remove library");
                    m_model.removeLibrary(m_selectedTreeViewItem.getM_file());

                    if (m_model.getRightFolderSelected() != null) {
                        boolean isLibraryInRight = m_model.getRightFolderSelected().getAbsolutePath().contains(
                                m_selectedTreeViewItem.getM_file().getAbsolutePath()
                        );
                        if (isLibraryInRight) {
                            m_model.setRightFolderSelected(null);
                        }
                    }

                    if (m_model.getM_selectedCenterFolder() != null) {
                        boolean isLibraryInCenter = m_model.getM_selectedCenterFolder().getAbsolutePath().contains(
                                m_selectedTreeViewItem.getM_file().getAbsolutePath()
                        );
                        if (isLibraryInCenter) {
                            System.out.println("SELECTED CENTER FOLDER REMOVED!!!");
                            m_model.setCenterFolder(null);
                        }
                    }
                    PersistentStorage persistentStorage = new PersistentStorage();
                    persistentStorage.removePersistentStorageLibrary(
                            m_selectedTreeViewItem.getM_file().getAbsolutePath()
                    );
                    m_model.notifyLibraryObservers();
                }
            }
        });

        //open in right pane option
        MenuItem openInRightPane = new MenuItem(SHOW_IN_RIGHT_PANE);
        openInRightPane.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (m_selectedTreeViewItem != null) {
                    File folderSelected = m_selectedTreeViewItem.getM_file();
                    if (!folderSelected.isDirectory()) {
                        PromptUI.customPromptError("Not a directory!", "", "Please select a directory.");
                    } else {
                        m_model.setRightFolderSelected(folderSelected);
                        m_model.notifyRightFolderObservers();
                    }
                }
            }
        });

        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(copy);
        menuItems.add(paste);
        menuItems.add(delete);
        if (isLeftPane) {
            menuItems.add(removeLibrary);
            menuItems.add(openInRightPane);
        }

        m_contextMenu.setOnShown(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                // Disable paste if nothing is chosen to be copied
                if (m_model.getM_fileToCopy() == null) {
                    paste.setDisable(true);
                    paste.setStyle("-fx-text-fill: gray;");
                } else {
                    paste.setDisable(false);
                    paste.setStyle("-fx-text-fill: black;");
                }

                // Do not show remove library option if selected item is not a library
                if (m_selectedTreeViewItem == null || !m_selectedTreeViewItem.isM_isRootPath()) {
                    removeLibrary.setDisable(true);
                    removeLibrary.setVisible(false);
                }
            }
        });

        return menuItems;
    }

    /**
     * Set mouse events on this CustomTreeCell
     */
    private void setDragEvents() {
        setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(m_selectedTreeViewItem != null) {
                    System.out.println("Drag detected on " + m_selectedTreeViewItem);

                    //update model
                    m_model.setM_fileToMove(m_selectedTreeViewItem.getM_file());

                    //update drag board
                    Dragboard dragBoard = startDragAndDrop(TransferMode.MOVE);
                    dragBoard.setDragView(snapshot(null, null));
                    ClipboardContent content = new ClipboardContent();
                    content.put(DataFormat.PLAIN_TEXT, m_selectedTreeViewItem.getM_file().getAbsolutePath());
                    dragBoard.setContent(content);

                    mouseEvent.consume();
                }
            }
        });

        setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                System.out.println("Drag over on " + m_selectedTreeViewItem);
                if (dragEvent.getDragboard().hasString()) {
                    String draggedItemPath = dragEvent.getDragboard().getString();
                    String destination = m_selectedTreeViewItem.getM_file().getAbsolutePath();
                    if (!draggedItemPath.equals(destination)) {
                        dragEvent.acceptTransferModes(TransferMode.MOVE);
                    }
                }
                dragEvent.consume();
            }
        });

        setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                System.out.println("Drag dropped on " + m_selectedTreeViewItem);

                if (!m_selectedTreeViewItem.getM_file().isDirectory()) {
                    PromptUI.customPromptError("Error", null, "Cannot move to a file! Please drag to a directory!");
                    return;
                }

                //fetch item to be moved and destination
                /*String draggedItemPath = dragEvent.getDragboard().getString();
                TreeItem<TreeViewItem> nodeToMove = searchTreeItem(draggedItemPath);
                TreeItem<TreeViewItem> targetNode = searchTreeItem(m_selectedTreeViewItem.getM_file().getAbsolutePath());*/

                //move the item in UI (this have no effect because the file tree will be refreshed)
                /*nodeToMove.getParent().getChildren().remove(nodeToMove);
                targetNode.getChildren().add(nodeToMove);
                targetNode.setExpanded(true);*/

                //move in the file system
                File fileToMove = m_model.getM_fileToMove();
                File destination = m_selectedTreeViewItem.getM_file();
                try {
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

                m_model.notifyLibraryObservers();
                dragEvent.consume();
            }
        });

        setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                System.out.println("Drag done");
                m_model.setM_fileToMove(null);
                dragEvent.consume();
            }
        });

        /*setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                System.out.println("Drag entered on " + m_selectedTreeViewItem);
                dragEvent.consume();
            }
        });

        setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                System.out.println("Drag exited on " + m_selectedTreeViewItem);
                dragEvent.consume();
            }
        });*/
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

    @Override
    public void updateItem(TreeViewItem item, boolean empty) {
        super.updateItem(item, empty);
        m_selectedTreeViewItem = item;
        EventDispatcher originalDispatcher = getEventDispatcher();
        setEventDispatcher(new TreeMouseEventDispatcher(originalDispatcher, m_model, m_selectedTreeViewItem));
        setContextMenu(m_contextMenu);
    }
}
