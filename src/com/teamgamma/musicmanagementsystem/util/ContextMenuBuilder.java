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
import java.util.List;

/**
 * Builder class to create context menu
 */
public class ContextMenuBuilder {
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

    /**
     * Construct file tree context menu
     *
     * @param model The model
     * @param musicPlayerManager The music player manager
     * @param databaseManager The db manager
     * @param selectedItem The selected item in the file tree
     *
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
                                      removeLibrary, showInRightPane);

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
                hideMenuItem(removeLibrary);
                hideMenuItem(showInRightPane);
            }

            // Do not show remove library option if selected item is not a library
            if (!selectedItem.isRootPath()) {
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
     * @param model The model
     * @param musicPlayerManager The music player manager
     * @param databaseManager The db manager
     * @param selectedItem The selected song in the center panel (in Item interface form)
     *
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

        //separators (non functional menu items, just for display)
        MenuItem songOptionsSeparator = new SeparatorMenuItem();
        MenuItem playlistOptionsSeparator = new SeparatorMenuItem();
        MenuItem editPropertiesOptionSeparator = new SeparatorMenuItem();

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setAutoHide(true);
        contextMenu.getItems().addAll(playSong, playSongNext, placeSongOnQueue, songOptionsSeparator,
                                      addToPlaylist, addToCurrentPlaylist, playlistOptionsSeparator,
                                      editProperties, editPropertiesOptionSeparator,
                                      copy, paste, rename, delete);

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
     * @param model The model
     * @param musicPlayerManager The music player manager
     * @param databaseManager The db manager
     * @param selectedSongIndex The selected song index in the playlist
     *
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

        //separators (non functional menu items, just for display)
        MenuItem songOptionsSeparator = new SeparatorMenuItem();
        MenuItem playlistOptionsSeparator = new SeparatorMenuItem();
        MenuItem editPropertiesOptionSeparator = new SeparatorMenuItem();

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(playSong, playSongNext, placeSongOnQueue, songOptionsSeparator,
                                      removeFromPlaylist, playlistOptionsSeparator,
                                      editProperties, editPropertiesOptionSeparator,
                                      rename, delete);

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
     * @param selectedItem The selected song (in Item interface form)
     *
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

    private static void hideMenuItem(MenuItem item) {
        item.setVisible(false);
        item.setDisable(true);
    }

    private static MenuItem createCopyMenuItem(SongManager model, Item selectedItem) {
        MenuItem copy = new MenuItem(COPY);

        copy.setOnAction(event -> {
            if (selectedItem != null) {
                model.setM_itemToCopy(selectedItem);
            }
        });

        return copy;
    }

    private static MenuItem createFileTreePasteMenuItem(SongManager model, Item selectedItem) {
        MenuItem paste = new MenuItem(PASTE);

        paste.setOnAction(event -> {
            if (selectedItem != null) {
                File dest = selectedItem.getFile();
                if (!dest.isDirectory()) {
                    PromptUI.customPromptError("Not a directory!", "", "Please select a directory as the paste target.");
                    return;
                }
                try {
                    model.copyToDestination(dest);
                    model.notifyFileObservers(Actions.PASTE, null);
                } catch (FileAlreadyExistsException ex) {
                    PromptUI.customPromptError("Error", "", "The following file or folder already exist!\n" + ex.getMessage());
                } catch (IOException ex) {
                    PromptUI.customPromptError("Error", "", "IOException: " + ex.getMessage());
                } catch (Exception ex) {
                    PromptUI.customPromptError("Error", "", "Exception: " + ex.getMessage());
                }
            }
        });

        return paste;
    }

    private static MenuItem createCenterPanelPasteMenuItem(SongManager model) {
        MenuItem paste = new MenuItem(PASTE);

        paste.setOnAction(event -> {
            File dest = model.getM_selectedCenterFolder();
            if (!dest.isDirectory()) {
                PromptUI.customPromptError("Not a directory!", "", "Please select a directory as the paste target.");
                return;
            }
            try {
                model.copyToDestination(dest);
                model.notifyFileObservers(Actions.PASTE, null);
            } catch (FileAlreadyExistsException ex) {
                PromptUI.customPromptError("Error", "", "The following file or folder already exist!\n" + ex.getMessage());
            } catch (IOException ex) {
                PromptUI.customPromptError("Error", "", "IOException: " + ex.getMessage());
            } catch (Exception ex) {
                PromptUI.customPromptError("Error", "", "Exception: " + ex.getMessage());
            }
        });

        return paste;
    }

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

    private static MenuItem createDeleteMenuItem(SongManager model,
                                                 MusicPlayerManager musicPlayerManager,
                                                 DatabaseManager databaseManager,
                                                 Item selectedItem) {
        MenuItem delete = new MenuItem(DELETE);

        delete.setOnAction(event -> {
            if (selectedItem != null) {
                File fileToDelete = selectedItem.getFile();

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
                        model.deleteFile(fileToDelete);
                        break;
                    } catch (IOException ex) {
                        musicPlayerManager.stopSong();
                        musicPlayerManager.removeSongFromHistory(musicPlayerManager.getCurrentSongPlaying());

                        if (musicPlayerManager.isThereANextSong()) {
                            musicPlayerManager.playNextSong();
                        } else if (!musicPlayerManager.getHistory().isEmpty()) {
                            musicPlayerManager.playPreviousSong();
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

                databaseManager.removeLibrary(fileToDelete.getAbsolutePath());
            }
        });

        return delete;
    }

    private static MenuItem createRemoveLibraryMenuItem(SongManager model,
                                                        DatabaseManager databaseManager,
                                                        Item selectedItem) {
        MenuItem removeLibrary = new MenuItem(REMOVE_THIS_LIBRARY);

        removeLibrary.setOnAction(event -> {
            if (selectedItem != null) {
                System.out.println("Remove library");
                model.removeLibrary(selectedItem.getFile());

                if (model.getM_rightFolderSelected() != null) {
                    boolean isLibraryInRight = model.getM_rightFolderSelected().getAbsolutePath().contains(
                            selectedItem.getFile().getAbsolutePath()
                    );
                    if (isLibraryInRight) {
                        model.setM_rightFolderSelected(null);
                    }
                }

                if (model.getM_selectedCenterFolder() != null) {
                    boolean isLibraryInCenter = model.getM_selectedCenterFolder().getAbsolutePath().contains(
                            selectedItem.getFile().getAbsolutePath()
                    );
                    if (isLibraryInCenter) {
                        System.out.println("SELECTED CENTER FOLDER REMOVED!!!");
                        model.setM_selectedCenterFolder(null);
                    }
                }

                databaseManager.removeLibrary(
                        selectedItem.getFile().getAbsolutePath()
                );
                model.setM_libraryAction(Actions.REMOVE_FROM_VIEW);
                model.notifyLibraryObservers();
            }
        });

        return removeLibrary;
    }

    private static MenuItem createShowInRightPaneMenuItem(SongManager model, Item selectedItem) {
        MenuItem showInRightPane = new MenuItem(SHOW_IN_RIGHT_PANE);

        showInRightPane.setOnAction(event -> {
            if (selectedItem != null) {
                File folderSelected = selectedItem.getFile();
                if (!folderSelected.isDirectory()) {
                    PromptUI.customPromptError("Not a directory!", "", "Please select a directory.");
                } else {
                    model.setM_rightFolderSelected(folderSelected);
                    model.notifyRightFolderObservers();
                }
            }
        });

        return showInRightPane;
    }

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
