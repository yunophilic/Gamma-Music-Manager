package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.util.Action;
import com.teamgamma.musicmanagementsystem.model.*;
import com.teamgamma.musicmanagementsystem.util.ConcreteFileActions;
import com.teamgamma.musicmanagementsystem.util.FileActions;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.util.Pair;

import java.io.File;

/**
 * Class for the Menu Bar
 */
public class MenuUI extends MenuBar{
    private static final String FILE_TITLE = "File";
    private static final String ADD_LIBRARY_OPTION = "Add Library";
    private static final String OPTIONS_TITLE = "Options";
    private static final String LEFT_PANEL_OPTION = "Left Panel";
    private static final String SHOW_FOLDERS_ONLY = "Show folders only";
    private static final String DISPLAY_FOLDERS_ONLY = "Display folders only";
    private static final String DONT_DISPLAY_FOLDERS_ONLY = "Don't display folders only";
    private static final String CENTER_PANEL_OPTION = "Center Panel";
    private static final String SHOW_FILES_IN_SUBFOLDERS = "Show files in subfolders";
    private static final String DISPLAY_SUBFOLDER_FILES = "Display subfolder files";
    private static final String DONT_DISPLAY_SUBFOLDER_FILES = "Don't display subfolder files";
    private static final String PLAYLIST_TITLE = "Playlist";
    private static final String CREATE_NEW_PLAYLIST = "Create New Playlist";
    private static final String REMOVE_EXISTING_PLAYLIST = "Remove Existing Playlist";
    private static final String EXPORT_PLAYLIST = "Export Playlist";

    private SongManager m_model;
    private DatabaseManager m_databaseManager;

    /**
     * Constructor
     *
     * @param model The song manager
     * @param databaseManager The database manager
     * @param filePersistentStorage The configuration file
     */
    public MenuUI(SongManager model, DatabaseManager databaseManager, FilePersistentStorage filePersistentStorage){
        super();
        m_model = model;
        m_databaseManager = databaseManager;

        setMenu(filePersistentStorage);
    }

    /**
     * Set the menu options
     *
     * @param filePersistentStorage The configuration file
     */
    private void setMenu(FilePersistentStorage filePersistentStorage) {
        super.getMenus().addAll(getMenuFile(), getMenuOptions(filePersistentStorage), getPlaylistSubMenu());
    }

    private Menu getMenuFile() {
        final Menu menuFile = new Menu(FILE_TITLE);
        MenuItem addLibraryMenu = new MenuItem(ADD_LIBRARY_OPTION);
        addLibraryMenu.setOnAction(event -> {
            String pathInput = PromptUI.addNewLibrary();
            if (pathInput == null) {
                return;
            }
            if (!m_model.addLibrary(pathInput)) {
                PromptUI.customPromptError("Error", null, "Path doesn't exist or duplicate library added");
                return;
            }
            m_databaseManager.addLibrary(pathInput);

            FileActions libraryFileActions = new ConcreteFileActions(Action.ADD, new File(pathInput));
            m_model.notifyLibraryObservers(libraryFileActions);
        });
        menuFile.getItems().addAll(addLibraryMenu);
        return menuFile;
    }

    /**
     * Get the menu options
     *
     * @param filePersistentStorage The configuration file
     * @return The menu options
     */
    private Menu getMenuOptions(FilePersistentStorage filePersistentStorage) {
        final Menu menuOptions = new Menu(OPTIONS_TITLE);
        Menu leftPanelSubMenu = getLeftPanelSubMenu(filePersistentStorage);
        Menu centerPanelSubMenu = getCenterPanelSubMenu(filePersistentStorage);

        menuOptions.getItems().addAll(leftPanelSubMenu, centerPanelSubMenu);
        return menuOptions;
    }

    /**
     * Get the left panel sub menu
     *
     * @param config The previous configuration of the sub menu
     * @return The left panel sub menu
     */
    private Menu getLeftPanelSubMenu(FilePersistentStorage config) {
        Menu leftPanelSubMenu = new Menu(LEFT_PANEL_OPTION);
        CheckMenuItem showFoldersOnly = new CheckMenuItem(SHOW_FOLDERS_ONLY);
        showFoldersOnly.setSelected(config.getLeftPanelOption());

        showFoldersOnly.setOnAction(event -> {
            if (showFoldersOnly.isSelected()){
                System.out.println(DISPLAY_FOLDERS_ONLY);
                MenuOptions menuManager = m_model.getM_menuOptions();
                menuManager.setM_leftPanelShowFoldersOnly(true);
            } else {
                System.out.println(DONT_DISPLAY_FOLDERS_ONLY);
                MenuOptions menuManager = m_model.getM_menuOptions();
                menuManager.setM_leftPanelShowFoldersOnly(false);
            }

            m_model.notifyLeftPanelOptionsObservers();
        });

        leftPanelSubMenu.getItems().addAll(showFoldersOnly);
        return leftPanelSubMenu;
    }

    /**
     * Get the center panel sub menu
     *
     * @param config The previous configuration of the sub menu
     * @return The center panel sub menu
     */
    private Menu getCenterPanelSubMenu(FilePersistentStorage config) {
        Menu centerPanelSubMenu = new Menu(CENTER_PANEL_OPTION);
        CheckMenuItem showFoldersOnly = new CheckMenuItem(SHOW_FILES_IN_SUBFOLDERS);
        showFoldersOnly.setSelected(config.getCenterPanelOption());

        showFoldersOnly.setOnAction(event -> {
            if (showFoldersOnly.isSelected()){
                System.out.println(DISPLAY_SUBFOLDER_FILES);
                MenuOptions menuManager = m_model.getM_menuOptions();
                menuManager.setM_centerPanelShowSubfolderFiles(true);
            } else {
                System.out.println(DONT_DISPLAY_SUBFOLDER_FILES);
                MenuOptions menuManager = m_model.getM_menuOptions();
                menuManager.setM_centerPanelShowSubfolderFiles(false);
            }

            m_model.notifyCenterFolderObservers();
        });

        centerPanelSubMenu.getItems().addAll(showFoldersOnly);
        return centerPanelSubMenu;
    }

    /**
     * Get the playlist sub menu
     *
     * @return The playlist sub menu
     */
    private Menu getPlaylistSubMenu() {
        Menu playlistSubMenu = new Menu(PLAYLIST_TITLE);

        MenuItem createNewPlaylistMenu = new MenuItem(CREATE_NEW_PLAYLIST);
        createNewPlaylistMenu.setOnAction(event -> {
            String newPlaylistName = PromptUI.createNewPlaylist();
            if (m_model.playlistNameExist(newPlaylistName)) {
                PromptUI.customPromptError("Error", null, "Playlist with name \"" + newPlaylistName + "\" already exist!");
                return;
            }
            if (newPlaylistName != null) {
                m_model.addAndCreatePlaylist(newPlaylistName);
                m_databaseManager.addPlaylist(newPlaylistName);
                m_model.notifyPlaylistObservers();
            }
        });

        MenuItem removePlaylistMenu = new MenuItem(REMOVE_EXISTING_PLAYLIST);
        removePlaylistMenu.setOnAction(event -> {
            Playlist playlistToRemove = PromptUI.removePlaylistSelection(m_model.getM_playlists());
            if (playlistToRemove != null) {
                m_model.removePlaylist(playlistToRemove);
                m_databaseManager.removePlaylist(playlistToRemove.getM_playlistName());
                m_model.notifyPlaylistObservers();
            }
        });

        MenuItem exportPlaylistMenu = new MenuItem(EXPORT_PLAYLIST);
        exportPlaylistMenu.setOnAction(event -> {
            Pair<Playlist, File> playlistFilePair = PromptUI.exportPlaylist(m_model.getM_playlists());
            if (playlistFilePair != null) {
                m_model.copyPlaylistToDestination(playlistFilePair);
            }
        });

        playlistSubMenu.getItems().addAll(createNewPlaylistMenu, removePlaylistMenu, exportPlaylistMenu);
        return playlistSubMenu;
    }
}
