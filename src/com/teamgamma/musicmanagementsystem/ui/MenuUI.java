package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.model.*;
import com.teamgamma.musicmanagementsystem.util.Action;
import com.teamgamma.musicmanagementsystem.util.ConcreteFileActions;
import com.teamgamma.musicmanagementsystem.util.FileActions;
import com.teamgamma.musicmanagementsystem.ApplicationController;

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
    // Constants
    private static final String MINI_MODE = "Minimode";
   	private static final String FILE_TITLE = "File";
    private static final String ADD_LIBRARY_OPTION = "Add Library";
    private static final String OPTIONS_TITLE = "Options";
    private static final String LEFT_PANEL_OPTION = "Left Panel";
    private static final String SHOW_FOLDERS_ONLY = "Show folders only";
    private static final String RIGHT_PANEL_OPTION = "Right Panel";
    private static final String HIDE_RIGHT_PANEL = "Hide";
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
    private static final String SEARCH_HEADER = "Search";
    private static final String SHOW_FILES_IN_FOLDER_HITS_HEADER = "Show Files In Folder Hits";

    private SongManager m_model;
    private DatabaseManager m_databaseManager;
    private ApplicationController m_applicationController;
    private MainUI m_main;
    private boolean m_miniCheck = false;
    private CheckMenuItem m_menuItem;

    /**
     * Constructor
     *
     * @param model                 The song manager
     * @param databaseManager       The database manager
     * @param filePersistentStorage The configuration file
	 * @param mainUI                The Main UI
     * @param applicationController The application controller
     */
    public MenuUI(SongManager model, DatabaseManager databaseManager, FilePersistentStorage filePersistentStorage,
                  MainUI mainUI, ApplicationController applicationController){
        super();
        m_model = model;
        m_databaseManager = databaseManager;
        m_main = mainUI;
        m_applicationController = applicationController;
        setMenu(filePersistentStorage);
    }

    /**
     * Set the menu options
     *
     * @param filePersistentStorage The configuration file
     */
    private void setMenu(FilePersistentStorage filePersistentStorage) {
        super.getMenus().addAll(getMenuFile(), getMenuOptions(filePersistentStorage), getPlaylistSubMenu(), getMinimodeMenu());
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
     * @return                      The menu options
     */
    private Menu getMenuOptions(FilePersistentStorage filePersistentStorage) {
        final Menu menuOptions = new Menu(OPTIONS_TITLE);
        Menu leftPanelSubMenu = getLeftPanelSubMenu(filePersistentStorage);
        Menu centerPanelSubMenu = getCenterPanelSubMenu(filePersistentStorage);
        Menu rightPanelSubMenu = getRightPanelSubMenu(filePersistentStorage);
        Menu searchSubMenu = createSearchSubmenu(filePersistentStorage);

        menuOptions.getItems().addAll(leftPanelSubMenu, centerPanelSubMenu, rightPanelSubMenu, searchSubMenu);
        return menuOptions;
    }

    /**
     * Function to create the right panel submenu for the top.
     *
     * @param filePersistentStorage The file storage to retrieve the state.
     * @return                      A Menu object that is the right panel submenu
     */
    private Menu getRightPanelSubMenu(FilePersistentStorage filePersistentStorage) {
        Menu rightPanelSubMenu = new Menu(RIGHT_PANEL_OPTION);
        CheckMenuItem hideRightPanel = new CheckMenuItem(HIDE_RIGHT_PANEL);
        hideRightPanel.setSelected(filePersistentStorage.getHideRightFilePane());

        hideRightPanel.setOnAction(event -> {
            if (hideRightPanel.isSelected()){
                MenuOptions menuManager = m_model.getM_menuOptions();
                menuManager.setHideRightPanel(true);
            } else {
                MenuOptions menuManager = m_model.getM_menuOptions();
                menuManager.setHideRightPanel(false);
            }

            m_model.notifyRightPanelOptionsObservers();
        });

        m_model.registerRightPanelOptionsObserver(() -> {
            hideRightPanel.setSelected( m_model.getM_menuOptions().getHideRightPanel());
        });

        m_model.notifyRightPanelOptionsObservers();

        rightPanelSubMenu.getItems().addAll(hideRightPanel);
        return rightPanelSubMenu;
    }

    /**
     * Function to create the search submenu for the top.
     *
     * @param filePersistentStorage The file storage to retrieve the state.
     * @return                      The search submenu options.
     */
    private Menu createSearchSubmenu(FilePersistentStorage filePersistentStorage) {
        Menu searchSubMenu = new Menu(SEARCH_HEADER);
        CheckMenuItem showFilesInFolderHits = new CheckMenuItem(SHOW_FILES_IN_FOLDER_HITS_HEADER);
        showFilesInFolderHits.setSelected(filePersistentStorage.getShowFilesInFolderHit());

        showFilesInFolderHits.setOnAction(event -> {
            if (showFilesInFolderHits.isSelected()) {
                m_model.getM_menuOptions().setShowFilesInFolderSearchHit(true);
            } else {
                m_model.getM_menuOptions().setShowFilesInFolderSearchHit(false);
            }
            m_model.notifySearchObservers();
        });

        searchSubMenu.getItems().add(showFilesInFolderHits);

        return searchSubMenu;
    }

    /**
     * Get the left panel sub menu
     *
     * @param config The previous configuration of the sub menu
     * @return       The left panel sub menu
     */
    private Menu getLeftPanelSubMenu(FilePersistentStorage config) {
        Menu leftPanelSubMenu = new Menu(LEFT_PANEL_OPTION);
        CheckMenuItem showFoldersOnly = new CheckMenuItem(SHOW_FOLDERS_ONLY);
        showFoldersOnly.setSelected(config.getLeftPanelShowOnlyFoldersOption());

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
     * @return       The center panel sub menu
     */
    private Menu getCenterPanelSubMenu(FilePersistentStorage config) {
        Menu centerPanelSubMenu = new Menu(CENTER_PANEL_OPTION);
        CheckMenuItem showFoldersOnly = new CheckMenuItem(SHOW_FILES_IN_SUBFOLDERS);
        showFoldersOnly.setSelected(config.getShowAllFilesInCenterPanelOption());

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
                m_model.copyPlaylistToDestination(playlistFilePair.getKey(), playlistFilePair.getValue());
            }
        });

        playlistSubMenu.getItems().addAll(createNewPlaylistMenu, removePlaylistMenu, exportPlaylistMenu);
        return playlistSubMenu;
    }

    /**
     * Get the minimode Menu
	 *
     * @return The minimode menu
     */
    private Menu getMinimodeMenu() {
        Menu minimodeMenu = new Menu(MINI_MODE);
        m_menuItem = new CheckMenuItem(MINI_MODE + "!");
        m_menuItem.setOnAction(event -> {
            System.out.println("Clicked minimode");
            if (!m_miniCheck) {
                m_miniCheck = true;
                m_applicationController.minimodeTurnOn();
                m_main.minimodeTurnOn();
            }
            else {
                System.out.println("Clicked minimode");
                m_miniCheck = false;
                m_applicationController.minimodeTurnOff();
                m_main.minimodeTurnOff();
            }
        });
        minimodeMenu.getItems().addAll(m_menuItem);
        return minimodeMenu;
    }

    /**
     * Enable minimode from external source
     */
    public void fireMiniMode() {
        m_menuItem.fire();
    }

    /**
     * Get the minimode menu item
     *
     * @return minimode CheckMenuItem
     */
    public CheckMenuItem getMenuItem() {
        return m_menuItem;
    }

    /**
     * Set the minimode menu item
     *
     * @param miniModeMenuItem CheckMenuItem
     */
    public void setMenuItem(CheckMenuItem miniModeMenuItem) {
        m_menuItem = miniModeMenuItem;
    }

    /**
     * Set status of minimode to on or off
     *
     * @param enable minimode
     */
    public void setMiniModeOn(boolean enable) {
        m_miniCheck = enable;
    }

    /**
     * Get the current status of minimode
     *
     * @return minimode status
     */
    public boolean miniModeStatus() {
        return m_miniCheck;
    }
}
