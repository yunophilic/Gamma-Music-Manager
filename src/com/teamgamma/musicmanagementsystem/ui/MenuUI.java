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
import java.nio.file.Files;

/**
 * Class for the Menu Bar
 */
public class MenuUI extends MenuBar{
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
        final Menu menuFile = new Menu("File");
        MenuItem addLibraryMenu = new MenuItem("Add Library");
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
        final Menu menuOptions = new Menu("Options");
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
        Menu leftPanelSubMenu = new Menu("Left Panel");
        CheckMenuItem showFoldersOnly = new CheckMenuItem("Show folders only");
        showFoldersOnly.setSelected(config.getLeftPanelOption());

        showFoldersOnly.setOnAction(event -> {
            if (showFoldersOnly.isSelected()){
                System.out.println("Display folders only");
                MenuOptions menuManager = m_model.getM_menuOptions();
                menuManager.setM_leftPanelShowFoldersOnly(true);
            } else {
                System.out.println("Don't display folders only");
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
        Menu centerPanelSubMenu = new Menu("Center Panel");
        CheckMenuItem showFoldersOnly = new CheckMenuItem("Show files in subfolders");
        showFoldersOnly.setSelected(config.getCenterPanelOption());

        showFoldersOnly.setOnAction(event -> {
            if (showFoldersOnly.isSelected()){
                System.out.println("Display subfolder files");
                MenuOptions menuManager = m_model.getM_menuOptions();
                menuManager.setM_centerPanelShowSubfolderFiles(true);
            } else {
                System.out.println("Don't subfolder files");
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
        Menu playlistSubMenu = new Menu("Playlist");

        MenuItem createNewPlaylistMenu = new MenuItem("Create New Playlist");
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

        MenuItem removePlaylistMenu = new MenuItem("Remove Existing Playlist");
        removePlaylistMenu.setOnAction(event -> {
            Playlist playlistToRemove = PromptUI.removePlaylistSelection(m_model.getM_playlists());
            if (playlistToRemove != null) {
                m_model.removePlaylist(playlistToRemove);
                m_databaseManager.removePlaylist(playlistToRemove.getM_playlistName());
                m_model.notifyPlaylistObservers();
            }
        });

        MenuItem exportPlaylistMenu = new MenuItem("Export Playlist");
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
