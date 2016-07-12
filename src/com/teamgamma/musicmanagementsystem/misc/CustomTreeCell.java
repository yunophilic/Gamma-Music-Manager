package com.teamgamma.musicmanagementsystem.misc;

import com.teamgamma.musicmanagementsystem.model.*;
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
import java.nio.file.Path;
import java.util.ArrayList;
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
     * Generate menu items
     *
     * @return List<MenuItem>
     */
    private List<MenuItem> generateMenuItems() {
        //copy option
        MenuItem copy = new MenuItem(ContextMenuConstants.COPY);
        copy.setOnAction(event -> {
            if (m_selectedItem != null) {
                m_model.setM_itemToCopy(m_selectedItem);
            }
        });

        //paste option
        MenuItem paste = new MenuItem(ContextMenuConstants.PASTE);
        paste.setOnAction(event -> {
            if (m_selectedItem != null) {
                File dest = m_selectedItem.getFile();
                if (!dest.isDirectory()) {
                    PromptUI.customPromptError("Not a directory!", "", "Please select a directory as the paste target.");
                    return;
                }
                try {
                    m_model.copyToDestination(dest);
                    m_model.notifyFileObservers(Actions.PASTE, null);
                } catch (FileAlreadyExistsException ex) {
                    PromptUI.customPromptError("Error", "", "The following file or folder already exist!\n" + ex.getMessage());
                } catch (IOException ex) {
                    PromptUI.customPromptError("Error", "", "IOException: " + ex.getMessage());
                } catch (Exception ex) {
                    PromptUI.customPromptError("Error", "", "Exception: " + ex.getMessage());
                }
            }
        });

        //rename option
        MenuItem rename = new MenuItem(ContextMenuConstants.RENAME_THIS_FILE);
        rename.setOnAction(event -> {
            if (m_selectedItem != null) {
                File fileToRename = m_selectedItem.getFile();
                Path newPath = PromptUI.fileRename(fileToRename);

                if (newPath != null) {
                    m_model.renameFile(fileToRename, newPath);
                }
            }
        });

        //delete option
        MenuItem delete = new MenuItem(ContextMenuConstants.DELETE);
        delete.setOnAction(event -> {
            if (m_selectedItem != null) {
                File fileToDelete = m_selectedItem.getFile();
                //confirmation dialog
                if (fileToDelete.isDirectory()) {
                    if (!PromptUI.recycleLibrary(fileToDelete)) {
                        return;
                    }
                } else {
                    if (!PromptUI.recycleSong(fileToDelete)) {
                        return;
                    }
                }
                //try to actually delete (retry if FileSystemException happens)
                for (int i = 0; i < 2; i++) {
                    try {
                        m_model.deleteFile(fileToDelete);
                        break;
                    } catch (IOException ex) {
                        m_musicPlayerManager.stopSong();
                        m_musicPlayerManager.removeSongFromHistory(m_musicPlayerManager.getCurrentSongPlaying());

                        if (m_musicPlayerManager.isThereANextSong()) {
                            m_musicPlayerManager.playNextSong();
                        } else if (!m_musicPlayerManager.getHistory().isEmpty()) {
                            m_musicPlayerManager.playPreviousSong();
                        }

                        if (i == 1) { //if this exception still thrown after retry (for debugging)
                            ex.printStackTrace();
                        }
                    } catch (Exception ex) {
                        PromptUI.customPromptError("Error", null, "Exception: \n" + ex.getMessage());
                        ex.printStackTrace();
                        break;
                    }
                }
                m_databaseManager.removeLibrary(fileToDelete.getAbsolutePath());
            }
        });

        //remove library
        MenuItem removeLibrary = new MenuItem(ContextMenuConstants.REMOVE_THIS_LIBRARY);
        removeLibrary.setOnAction(event -> {
            if (m_selectedItem != null) {
                System.out.println("Remove library");
                m_model.removeLibrary(m_selectedItem.getFile());

                if (m_model.getM_rightFolderSelected() != null) {
                    boolean isLibraryInRight = m_model.getM_rightFolderSelected().getAbsolutePath().contains(
                            m_selectedItem.getFile().getAbsolutePath()
                    );
                    if (isLibraryInRight) {
                        m_model.setM_rightFolderSelected(null);
                    }
                }

                if (m_model.getM_selectedCenterFolder() != null) {
                    boolean isLibraryInCenter = m_model.getM_selectedCenterFolder().getAbsolutePath().contains(
                            m_selectedItem.getFile().getAbsolutePath()
                    );
                    if (isLibraryInCenter) {
                        System.out.println("SELECTED CENTER FOLDER REMOVED!!!");
                        m_model.setM_selectedCenterFolder(null);
                    }
                }

                m_databaseManager.removeLibrary(
                        m_selectedItem.getFile().getAbsolutePath()
                );
                m_model.setM_libraryAction(Actions.REMOVE_FROM_VIEW);
                m_model.notifyLibraryObservers();
            }
        });

        //open in right pane option
        MenuItem openInRightPane = new MenuItem(ContextMenuConstants.SHOW_IN_RIGHT_PANE);
        openInRightPane.setOnAction(event -> {
            if (m_selectedItem != null) {
                File folderSelected = m_selectedItem.getFile();
                if (!folderSelected.isDirectory()) {
                    PromptUI.customPromptError("Not a directory!", "", "Please select a directory.");
                } else {
                    m_model.setM_rightFolderSelected(folderSelected);
                    m_model.notifyRightFolderObservers();
                }
            }
        });

        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(copy);
        menuItems.add(paste);
        menuItems.add(rename);
        menuItems.add(delete);
        if (m_isLeftPane) {
            menuItems.add(removeLibrary);
            menuItems.add(openInRightPane);
        }

        MenuItem addToPlaylist = new MenuItem(ContextMenuConstants.ADD_TO_PLAYLIST);
        addToPlaylist.setOnAction(event -> {
            if (m_selectedItem != null) {
                Song selectedSong = (Song) m_selectedItem;
                List<Playlist> playlists = m_model.getM_playlists();
                if (playlists.isEmpty()) {
                    PromptUI.customPromptError("Error", null, "No playlist exist!");
                    return;
                }
                Playlist selectedPlaylist = PromptUI.addSongToPlaylist(playlists, selectedSong);
                if (selectedPlaylist != null) {
                    m_model.addSongToPlaylist(selectedSong, selectedPlaylist);
                    m_musicPlayerManager.notifyQueingObserver();
                }
            }
        });
        menuItems.add(addToPlaylist);

        MenuItem addToCurrentPlaylist = new MenuItem(ContextMenuConstants.ADD_TO_CURRENT_PLAYLIST);
        addToCurrentPlaylist.setOnAction(event -> {
            Song selectedSong = (Song) m_selectedItem;
            if (selectedSong != null) {
                Playlist selectedPlaylist = m_model.getM_selectedPlaylist();
                if (selectedPlaylist == null) {
                    PromptUI.customPromptError("Error", null, "Please select a playlist!");
                    return;
                }
                m_model.addSongToPlaylist(selectedSong, selectedPlaylist);
                m_musicPlayerManager.notifyQueingObserver();
            }
        });
        menuItems.add(addToCurrentPlaylist);

        MenuItem playSong = new MenuItem(ContextMenuConstants.MENU_ITEM_PLAY_SONG);
        playSong.setOnAction(event -> {
            if (m_selectedItem != null) {
                Song song = (Song) m_selectedItem;
                m_musicPlayerManager.playSongRightNow(song);
            }
        });

        MenuItem placeSongAtStartOfQueue = new MenuItem(ContextMenuConstants.MENU_ITEM_PLAY_SONG_NEXT);
        placeSongAtStartOfQueue.setOnAction(event -> {
            if (m_selectedItem != null) {
                Song song = (Song) m_selectedItem;
                m_musicPlayerManager.placeSongAtStartOfQueue(song);
            }
        });

        MenuItem placeSongOnBackOfQueue = new MenuItem(ContextMenuConstants.MENU_ITEM_PLACE_SONG_ON_QUEUE);
        placeSongOnBackOfQueue.setOnAction(event -> {
            if (m_selectedItem != null) {
                Song song = (Song) m_selectedItem;
                m_musicPlayerManager.placeSongOnBackOfPlaybackQueue(song);
            }
        });

        menuItems.add(playSong);
        menuItems.add(placeSongAtStartOfQueue);
        menuItems.add(placeSongOnBackOfQueue);

        m_contextMenu.setOnShown(event -> {
            // Disable paste if nothing is chosen to be copied
            if (m_model.getM_itemToCopy() == null) {
                paste.setDisable(true);
                paste.setStyle("-fx-text-fill: gray;");
            } else {
                paste.setDisable(false);
                paste.setStyle("-fx-text-fill: black;");
            }

            // Do not show remove library option if selected item is not a library
            if (m_selectedItem == null || !m_selectedItem.isRootPath()) {
                removeLibrary.setDisable(true);
                removeLibrary.setVisible(false);
            }

            // Do not show song options if this is not a song
            if ( !(m_selectedItem instanceof Song) ) {
                disableMenuItem(playSong);
                disableMenuItem(placeSongAtStartOfQueue);
                disableMenuItem(placeSongOnBackOfQueue);
                disableMenuItem(addToPlaylist);
                disableMenuItem(addToCurrentPlaylist);
            }
        });

        return menuItems;
    }

    /**
     * Function to disable the menu item passed in.
     *
     * @param item The menu item to disable.
     */
    private void disableMenuItem(MenuItem item) {
        item.setVisible(false);
        item.setDisable(true);
    }

    private void createContextMenu() {
        m_contextMenu = new ContextMenu();
        m_contextMenu.getItems().addAll(generateMenuItems());
    }

    /**
     * Set mouse events on this CustomTreeCell
     */
    private void setDragEvents() {
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
        setEventDispatcher(new TreeMouseEventDispatcher(originalDispatcher, m_model, m_musicPlayerManager, m_tree, m_selectedItem, m_isLeftPane));
        createContextMenu();
        setContextMenu(m_contextMenu);
    }
}
