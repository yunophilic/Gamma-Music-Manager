package com.teamgamma.musicmanagementsystem.util;

import com.teamgamma.musicmanagementsystem.model.*;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import com.teamgamma.musicmanagementsystem.ui.PromptUI;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder class to create context menu
 */
public class ContextMenuBuilder {

    // Constants
    private static final String COPY = "Copy";
    private static final String PASTE = "Paste";
    private static final String DELETE = "Delete";
    private static final String RENAME = "Rename";

    private static final String REMOVE_THIS_LIBRARY = "Remove This Library";
    private static final String SHOW_IN_RIGHT_PANE = "Show in Right Pane";

    private static final String EDIT_PROPERTIES = "Edit Properties";

    private static final String ADD_TO_PLAYLIST = "Add to Playlist";
    private static final String ADD_TO_CURRENT_PLAYLIST = "Add to Current Playlist";

    private static final String REMOVE_FROM_PLAYLIST = "Remove From Playlist";

    private static final String PLAY_SONG = "Play Song";
    private static final String PLAY_SONG_NEXT = "Play Song Next";
    private static final String PLACE_SONG_ON_QUEUE = "Place Song On Queue";
    private static final String SHOW_IN_EXPLORER = "Show in Explorer";

    /**
     * Construct file tree context menu
     *
     * @param model              The model
     * @param musicPlayerManager The music player manager
     * @param databaseManager    The db manager
     * @param selectedItem       The selected item in the file tree
     * @return ContextMenu for file tree
     */
    public static ContextMenu buildFileTreeContextMenu(SongManager model,
                                                       MusicPlayerManager musicPlayerManager,
                                                       DatabaseManager databaseManager,
                                                       Item selectedItem,
                                                       boolean isLeftPane) {
        MenuItem playSong = createPlaySongMenuItem(musicPlayerManager, selectedItem);
        MenuItem playSongNext = createPlaySongNextMenuItem(musicPlayerManager, selectedItem);
        MenuItem placeSongOnQueue = createPlaceSongOnQueueMenuItem(musicPlayerManager, selectedItem);

        MenuItem addToPlaylist = createAddToPlaylistMenuItem(model, musicPlayerManager, selectedItem);
        MenuItem addToCurrentPlaylist = createAddToCurrentPlaylistMenuItem(model, musicPlayerManager, selectedItem);

        MenuItem copy = createCopyMenuItem(model, selectedItem);
        MenuItem paste = createFileTreePasteMenuItem(model, selectedItem);
        MenuItem rename = createRenameMenuItem(model, selectedItem);
        MenuItem delete = createDeleteMenuItem(model, musicPlayerManager, databaseManager, selectedItem);

        MenuItem removeLibrary = createRemoveLibraryMenuItem(model, databaseManager, selectedItem);
        MenuItem showInRightPane = createShowInRightPaneMenuItem(model, selectedItem);
        MenuItem openFileLocation = createShowInExplorerMenuItem(selectedItem);

        //separators (non functional menu items, just for display)
        MenuItem songOptionsSeparator = new SeparatorMenuItem();
        MenuItem playlistOptionsSeparator = new SeparatorMenuItem();
        MenuItem fileOptionsSeparator = new SeparatorMenuItem();

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(playSong, playSongNext, placeSongOnQueue,
                songOptionsSeparator,
                addToPlaylist, addToCurrentPlaylist,
                playlistOptionsSeparator,
                copy, paste, rename, delete,
                fileOptionsSeparator,
                removeLibrary, showInRightPane, openFileLocation);

        contextMenu.setOnShown(event -> {
            // Hide all if selected item is null
            if (selectedItem == null) {
                for (MenuItem menuItem : contextMenu.getItems()) {
                    hideMenuItem(menuItem);
                }
                return;
            }

            // Disable paste if nothing is chosen to be copied
            if (model.getM_itemToCopy() == null) {
                paste.setDisable(true);
            } else {
                paste.setDisable(false);
            }

            // Only show the show in right pane option if it is in left pane
            if (!isLeftPane) {
                hideMenuItem(fileOptionsSeparator);
                hideMenuItem(removeLibrary);
                hideMenuItem(showInRightPane);
            }

            // Do not show remove library option if selected item is not a library
            if (!selectedItem.isRootItem()) {
                hideMenuItem(removeLibrary);
            }

            // Do not show song options if this is not a song
            if (!(selectedItem instanceof Song)) {
                hideMenuItem(playSong);
                hideMenuItem(playSongNext);
                hideMenuItem(placeSongOnQueue);

                hideMenuItem(addToPlaylist);
                hideMenuItem(addToCurrentPlaylist);

                hideMenuItem(songOptionsSeparator);
                hideMenuItem(playlistOptionsSeparator);
            }
        });

        return contextMenu;
    }

    /**
     * Construct center panel context menu
     *
     * @param model              The model
     * @param musicPlayerManager The music player manager
     * @param databaseManager    The db manager
     * @param selectedItem       The selected song in the center panel (in Item interface form)
     * @return ContextMenu for center panel
     */
    public static ContextMenu buildCenterPanelContextMenu(SongManager model,
                                                          MusicPlayerManager musicPlayerManager,
                                                          DatabaseManager databaseManager,
                                                          Item selectedItem) {
        MenuItem playSong = createPlaySongMenuItem(musicPlayerManager, selectedItem);
        MenuItem playSongNext = createPlaySongNextMenuItem(musicPlayerManager, selectedItem);
        MenuItem placeSongOnQueue = createPlaceSongOnQueueMenuItem(musicPlayerManager, selectedItem);

        MenuItem addToPlaylist = createAddToPlaylistMenuItem(model, musicPlayerManager, selectedItem);
        MenuItem addToCurrentPlaylist = createAddToCurrentPlaylistMenuItem(model, musicPlayerManager, selectedItem);

        MenuItem editProperties = createEditPropertiesMenuItem(model, selectedItem);

        MenuItem copy = createCopyMenuItem(model, selectedItem);
        MenuItem paste = createCenterPanelPasteMenuItem(model);
        MenuItem rename = createRenameMenuItem(model, selectedItem);
        MenuItem delete = createDeleteMenuItem(model, musicPlayerManager, databaseManager, selectedItem);

        MenuItem openFileLocation = createShowInExplorerMenuItem(selectedItem);

        //separators (non functional menu items, just for display)
        MenuItem songOptionsSeparator = new SeparatorMenuItem();
        MenuItem playlistOptionsSeparator = new SeparatorMenuItem();
        MenuItem editPropertiesOptionSeparator = new SeparatorMenuItem();
        MenuItem explorerOptionsSeparator = new SeparatorMenuItem();

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setAutoHide(true);
        contextMenu.getItems().addAll(playSong, playSongNext, placeSongOnQueue, songOptionsSeparator,
                addToPlaylist, addToCurrentPlaylist, playlistOptionsSeparator,
                editProperties, editPropertiesOptionSeparator,
                copy, paste, rename, delete, explorerOptionsSeparator, openFileLocation);

        contextMenu.setOnShown(event -> {
            // Hide all except paste if selected item is null
            if (selectedItem == null) {
                for (MenuItem menuItem : contextMenu.getItems()) {
                    if (!menuItem.equals(paste)) {
                        hideMenuItem(menuItem);
                    }
                }
            }

            // Disable paste if nothing is chosen to be copied
            if (model.getM_itemToCopy() == null) {
                paste.setDisable(true);
            } else {
                paste.setDisable(false);
            }
        });

        return contextMenu;
    }

    /**
     * Construct playlist context menu
     *
     * @param model              The model
     * @param musicPlayerManager The music player manager
     * @param databaseManager    The db manager
     * @param selectedSongIndex  The selected song index in the playlist
     * @return ContextMenu for playlist
     */
    public static ContextMenu buildPlaylistContextMenu(SongManager model,
                                                       MusicPlayerManager musicPlayerManager,
                                                       DatabaseManager databaseManager,
                                                       int selectedSongIndex) {
        Playlist selectedPlaylist = model.getM_selectedPlaylist();
        Song selectedSong = selectedPlaylist.isValid(selectedSongIndex) ? selectedPlaylist.getSongByIndex(selectedSongIndex) : null;

        MenuItem playSong = createPlaySongMenuItem(musicPlayerManager, selectedSong);
        MenuItem playSongNext = createPlaySongNextMenuItem(musicPlayerManager, selectedSong);
        MenuItem placeSongOnQueue = createPlaceSongOnQueueMenuItem(musicPlayerManager, selectedSong);

        MenuItem removeFromPlaylist = createRemoveFromPlaylistMenuItem(model, musicPlayerManager, selectedSongIndex);

        MenuItem editProperties = createEditPropertiesMenuItem(model, selectedSong);

        MenuItem rename = createRenameMenuItem(model, selectedSong);
        MenuItem delete = createDeleteMenuItem(model, musicPlayerManager, databaseManager, selectedSong);

        MenuItem openFileLocation = createShowInExplorerMenuItem(selectedSong);

        //separators (non functional menu items, just for display)
        MenuItem songOptionsSeparator = new SeparatorMenuItem();
        MenuItem playlistOptionsSeparator = new SeparatorMenuItem();
        MenuItem editPropertiesOptionSeparator = new SeparatorMenuItem();
        MenuItem explorerOptionsSeparator = new SeparatorMenuItem();

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(playSong, playSongNext, placeSongOnQueue, songOptionsSeparator,
                removeFromPlaylist, playlistOptionsSeparator,
                editProperties, editPropertiesOptionSeparator,
                rename, delete, explorerOptionsSeparator, openFileLocation);

        contextMenu.setOnShown(event -> {
            // Hide all if selectedSongIndex out of bounds
            if (selectedSong == null) {
                for (MenuItem menuItem : contextMenu.getItems()) {
                    hideMenuItem(menuItem);
                }
            }
        });

        return contextMenu;
    }

    /**
     * Construct playback context menu
     *
     * @param musicPlayerManager The music player manager
     * @param selectedItem       The selected song (in Item interface form)
     * @return Playback context menu for music player history UI
     */
    public static ContextMenu buildPlaybackContextMenu(MusicPlayerManager musicPlayerManager, Item selectedItem) {
        ContextMenu playbackMenu = new ContextMenu();
        playbackMenu.setAutoHide(true);

        MenuItem playSong = createPlaySongMenuItem(musicPlayerManager, selectedItem);
        MenuItem playSongNext = createPlaySongNextMenuItem(musicPlayerManager, selectedItem);
        MenuItem placeSongOnQueue = createPlaceSongOnQueueMenuItem(musicPlayerManager, selectedItem);

        playbackMenu.getItems().addAll(playSong, playSongNext, placeSongOnQueue);

        return playbackMenu;
    }

    /**
     * Helper functions
     */

    /**
     * Function to hide a menu item.
     *
     * @param item The item to hide
     */
    private static void hideMenuItem(MenuItem item) {
        item.setVisible(false);
        item.setDisable(true);
    }

    /**
     * Function to create the menu item for copying a file.
     *
     * @param model        The model set the item that is to be copied
     * @param selectedItem The item to copy
     * @return A menu item containing the logic copy a song.
     */
    private static MenuItem createCopyMenuItem(SongManager model, Item selectedItem) {
        MenuItem copy = new MenuItem(COPY);

        copy.setOnAction(event -> {
            if (selectedItem != null) {
                model.setM_itemToCopy(selectedItem);
            }
        });

        return copy;
    }

    /**
     * Function to create the paste menu item option.
     *
     * @param model        The model to get the file that is to be copied.
     * @param selectedItem The location to paste the file to.
     * @return A menu item containing the logic for this operation.
     */
    private static MenuItem createFileTreePasteMenuItem(SongManager model, Item selectedItem) {
        MenuItem paste = new MenuItem(PASTE);

        paste.setOnAction(event -> {
            if (selectedItem != null) {
                File dest = selectedItem.getFile();
                if (!dest.isDirectory()) {
                    PromptUI.customPromptError("Not a directory!", null, "Please select a directory as the paste target.");
                    return;
                }
                try {
                    model.copyToDestination(dest);
                    model.notifyFileObservers(Action.PASTE, null);
                } catch (FileAlreadyExistsException ex) {
                    PromptUI.customPromptError("Error", null, "The following file or folder already exist!\n" + ex.getMessage());
                } catch (IOException ex) {
                    PromptUI.customPromptError("Error", null, "IOException: " + ex.getMessage());
                } catch (Exception ex) {
                    PromptUI.customPromptError("Error", null, "Exception: " + ex.getMessage());
                }
            }
        });

        return paste;
    }

    /**
     * Function to create the menu item to set what folder is in the center panel.
     *
     * @param model The model to set the center panel folder to show.
     * @return A menu item containing the logic to set the center panel.
     */
    private static MenuItem createCenterPanelPasteMenuItem(SongManager model) {
        MenuItem paste = new MenuItem(PASTE);

        paste.setOnAction(event -> {
            File dest = model.getM_selectedCenterFolder();
            if (!dest.isDirectory()) {
                PromptUI.customPromptError("Not a directory!", null, "Please select a directory as the paste target.");
                return;
            }
            try {
                model.copyToDestination(dest);
                model.notifyFileObservers(Action.PASTE, null);
            } catch (FileAlreadyExistsException ex) {
                PromptUI.customPromptError("Error", null, "The following file or folder already exist!\n" + ex.getMessage());
            } catch (IOException ex) {
                PromptUI.customPromptError("Error", null, "IOException: " + ex.getMessage());
            } catch (Exception ex) {
                PromptUI.customPromptError("Error", null, "Exception: " + ex.getMessage());
            }
        });

        return paste;
    }

    /**
     * Function to create a menu item for renaming a item.
     *
     * @param model        The model to carry out the renaming
     * @param selectedItem The item to rename.
     * @return A menu item to rename a file or folder
     */
    private static MenuItem createRenameMenuItem(SongManager model, Item selectedItem) {
        MenuItem rename = new MenuItem(RENAME);

        rename.setOnAction(event -> {
            if (selectedItem != null) {
                File fileToRename = selectedItem.getFile();
                Path newPath = PromptUI.fileRename(fileToRename);

                if (newPath != null) {
                    model.renameFile(fileToRename, newPath);
                }
            }
        });

        return rename;
    }

    /**
     * Function to create the delete menu option based on the selected item
     *
     * @param model              The model to do the operation.
     * @param musicPlayerManager The music player manager to update if the song is currently playing
     * @param databaseManager    The database to update
     * @param selectedItem       The item to delete.
     * @return A menu item containing the logic to delete a item.
     */
    private static MenuItem createDeleteMenuItem(SongManager model,
                                                 MusicPlayerManager musicPlayerManager,
                                                 DatabaseManager databaseManager,
                                                 Item selectedItem) {
        MenuItem delete = new MenuItem(DELETE);

        delete.setOnAction(event -> {
            if (selectedItem != null) {
                File fileToDelete = selectedItem.getFile();
                UserInterfaceUtils.deleteFileAction(model, musicPlayerManager, databaseManager, fileToDelete);
            }
        });

        return delete;
    }

    /**
     * Function to create the remove from library menu option.
     *
     * @param model           The model that will update the backend
     * @param databaseManager The database to update
     * @param selectedItem    The item to remove from the library
     * @return A menu item containing the logic to remove something from the library
     */
    private static MenuItem createRemoveLibraryMenuItem(SongManager model,
                                                        DatabaseManager databaseManager,
                                                        Item selectedItem) {
        MenuItem removeLibrary = new MenuItem(REMOVE_THIS_LIBRARY);

        removeLibrary.setOnAction(event -> {
            if (selectedItem != null) {
                System.out.println("Remove library");

                model.removeLibrary(selectedItem.getFile());
                String selectedItemPath = selectedItem.getFile().getAbsolutePath();

                if (model.getM_rightFolderSelected() != null) {
                    String rightFolderPath = model.getM_rightFolderSelected().getAbsolutePath();
                    boolean isLibraryInRight = rightFolderPath.equals(selectedItemPath) ||
                            rightFolderPath.contains(selectedItemPath + File.separator);
                    if (isLibraryInRight) {
                        model.setM_rightFolderSelected(null);
                    }
                }

                if (model.getM_selectedCenterFolder() != null) {
                    String centerFolderPath = model.getM_selectedCenterFolder().getAbsolutePath();
                    boolean isLibraryInCenter = centerFolderPath.equals(selectedItemPath) ||
                            centerFolderPath.contains(selectedItemPath + File.separator);
                    if (isLibraryInCenter) {
                        System.out.println("SELECTED CENTER FOLDER REMOVED!!!");
                        model.setM_selectedCenterFolder(null);
                    }
                }

                databaseManager.removeLibrary(
                        selectedItemPath
                );
                model.setM_libraryAction(Action.REMOVE_FROM_VIEW);
                model.notifyLibraryObservers();
            }
        });

        return removeLibrary;
    }

    /**
     * Function to build the show in right pane menu option.
     *
     * @param model        The model to set what is in the right pane.
     * @param selectedItem The item to show in the right pane.
     * @return A menu item that will contain the logic to show something in the right pane.
     */
    private static MenuItem createShowInRightPaneMenuItem(SongManager model, Item selectedItem) {
        MenuItem showInRightPane = new MenuItem(SHOW_IN_RIGHT_PANE);

        showInRightPane.setOnAction(event -> {
            if (selectedItem != null) {
                File folderSelected = selectedItem.getFile();
                if (!folderSelected.isDirectory()) {
                    PromptUI.customPromptError("Not a directory!", null, "Please select a directory.");
                } else {
                    model.setM_rightFolderSelected(folderSelected);
                    model.notifyRightFolderObservers();
                }
            }
        });

        return showInRightPane;
    }

    /**
     * Function that creates menu option to open the selected file or folder's location in the file explorer.
     *
     * @param selectedItem the file or folder selected in the tree view.
     * @return the menu item which opens the file or folder's location.
     */
    private static MenuItem createShowInExplorerMenuItem(Item selectedItem) {
        MenuItem showInExplorer = new MenuItem(SHOW_IN_EXPLORER);

        showInExplorer.setOnAction(event -> {
            if (selectedItem != null) {
                File folderSelected = selectedItem.getFile();
                try {
                    Runtime.getRuntime().exec("explorer.exe /select," + folderSelected.getAbsolutePath());
                } catch (IOException e) {
                    PromptUI.customPromptError("Failed to Show in Explorer", null, "The file or folder location could not be opened.");
                }
            }
        });

        return showInExplorer;
    }


    /**
     * Function to create a menu item for editing the songs metadata via prompt.
     *
     * @param model The model to update UI .
     * @param selectedItem The song to edit.
     * @return A menu item containing the logic to edit a song metadata.
     */
    private static MenuItem createEditPropertiesMenuItem(SongManager model, Item selectedItem) {
        MenuItem editProperties = new MenuItem(EDIT_PROPERTIES);

        editProperties.setOnAction(event -> {
            if (selectedItem instanceof Song) {
                PromptUI.editMetadata((Song) selectedItem);
                model.notifyCenterFolderObservers();
                model.notifyPlaylistSongsObservers();
            }
        });

        return editProperties;
    }

    /**
     * A function to create a menu item that will add a Song to the playlist via prompt.
     *
     * @param model              The model to get all the playlist from.
     * @param musicPlayerManager The music player manager to notify updates.
     * @param selectedItem       The selected song to add in.
     * @return A menu item setup with the logic to add a song to a playlist via prompt.
     */
    private static MenuItem createAddToPlaylistMenuItem(SongManager model,
                                                        MusicPlayerManager musicPlayerManager,
                                                        Item selectedItem) {
        MenuItem addToPlaylist = new MenuItem(ADD_TO_PLAYLIST);

        addToPlaylist.setOnAction(event -> {
            if (selectedItem != null && selectedItem instanceof Song) {
                Song selectedSong = (Song) selectedItem;
                List<Playlist> playlists = model.getM_playlists();
                if (playlists.isEmpty()) {
                    PromptUI.customPromptError("Error", null, "No playlist exist!");
                    return;
                }
                Playlist selectedPlaylist = PromptUI.addSongToPlaylist(playlists, selectedSong);
                if (selectedPlaylist != null) {
                    model.addSongToPlaylist(selectedSong, selectedPlaylist);
                    musicPlayerManager.notifyQueingObserver();
                }
            }
        });

        return addToPlaylist;
    }

    /**
     * Function to create a menu item that will allow the user to add a song to the current playlist.
     *
     * @param model              The model to select the current playlist.
     * @param musicPlayerManager The music player manager to updating UI.
     * @param selectedItem       The song to add to the playlist.
     * @return A menu item containing the logic to add a song to the current playlist.
     */
    private static MenuItem createAddToCurrentPlaylistMenuItem(SongManager model,
                                                               MusicPlayerManager musicPlayerManager,
                                                               Item selectedItem) {
        MenuItem addToCurrentPlaylist = new MenuItem(ADD_TO_CURRENT_PLAYLIST);

        addToCurrentPlaylist.setOnAction(event -> {
            if (selectedItem != null && selectedItem instanceof Song) {
                Song selectedSong = (Song) selectedItem;
                Playlist selectedPlaylist = model.getM_selectedPlaylist();
                if (selectedPlaylist == null) {
                    PromptUI.customPromptError("Error", null, "Please select a playlist!");
                    return;
                }
                model.addSongToPlaylist(selectedSong, selectedPlaylist);
                musicPlayerManager.notifyQueingObserver();
            }
        });

        return addToCurrentPlaylist;
    }

    /**
     * Function to create a menu item to remove the song from the playlist based on the index value it is.
     *
     * @param model              The model to get the selected playlist being shown.
     * @param musicPlayerManager The music player manager to update what is playing after removing.
     * @param selectedSongIndex  The index of the song to remove in the playlist.
     * @return A menu item containing the logic to do this.
     */
    private static MenuItem createRemoveFromPlaylistMenuItem(SongManager model,
                                                             MusicPlayerManager musicPlayerManager,
                                                             int selectedSongIndex) {
        MenuItem removeFromPlaylist = new MenuItem(REMOVE_FROM_PLAYLIST);

        removeFromPlaylist.setOnAction(event -> {
            Playlist selectedPlaylist = model.getM_selectedPlaylist();
            if (PromptUI.removeSongFromPlaylist(selectedPlaylist,
                    selectedPlaylist.getSongByIndex(selectedSongIndex))) {
                boolean songToRemoveIsPlaying = (selectedSongIndex == selectedPlaylist.getM_currentSongIndex());

                selectedPlaylist.removeSong(selectedSongIndex);
                model.notifyPlaylistSongsObservers();

                if (!selectedPlaylist.isEmpty() && songToRemoveIsPlaying) {
                    musicPlayerManager.playPlaylist(selectedPlaylist);
                }

                if (selectedPlaylist.isEmpty()) {
                    musicPlayerManager.stopSong();
                    musicPlayerManager.resetCurrentPlaylist();
                }
            }
        });

        return removeFromPlaylist;
    }

    /**
     * Fucnction to create a menu item with logic to play the song passed in to the music player.
     *
     * @param musicPlayerManager The music player manager to use
     * @param selectedItem       The song to add to the queue
     * @return A menu item containing the logic to play a song on the music player
     */
    private static MenuItem createPlaySongMenuItem(MusicPlayerManager musicPlayerManager, Item selectedItem) {
        MenuItem playSong = new MenuItem(PLAY_SONG);

        playSong.setOnAction(event -> {
            if (selectedItem != null && selectedItem instanceof Song) {
                Song song = (Song) selectedItem;
                musicPlayerManager.playSongRightNow(song);
            }
        });

        return playSong;
    }

    /**
     * Function to create a menu item with the logic to add a song to the front of the playback queue
     *
     * @param musicPlayerManager The music player manager to use
     * @param selectedItem       The song to add to the queue
     * @return A menu item containing logic needed to add a song to the front of the queue.
     */
    private static MenuItem createPlaySongNextMenuItem(MusicPlayerManager musicPlayerManager, Item selectedItem) {
        MenuItem playSongNext = new MenuItem(PLAY_SONG_NEXT);

        playSongNext.setOnAction(event -> {
            if (selectedItem != null && selectedItem instanceof Song) {
                Song song = (Song) selectedItem;
                musicPlayerManager.placeSongAtStartOfQueue(song);
            }
        });

        return playSongNext;
    }

    /**
     * Function to create a menu item to place a song on the playback queue
     *
     * @param musicPlayerManager The music player manager to use
     * @param selectedItem       The song to add to the queue
     * @return The menu item containing logic to add a song to the playback queue
     */
    private static MenuItem createPlaceSongOnQueueMenuItem(MusicPlayerManager musicPlayerManager, Item selectedItem) {
        MenuItem placeSongOnQueue = new MenuItem(PLACE_SONG_ON_QUEUE);

        placeSongOnQueue.setOnAction(event -> {
            if (selectedItem != null) {
                Song song = (Song) selectedItem;
                musicPlayerManager.placeSongOnBackOfPlaybackQueue(song);
            }
        });

        return placeSongOnQueue;
    }
}
