package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.util.Action;
import com.teamgamma.musicmanagementsystem.model.*;
import com.teamgamma.musicmanagementsystem.*;


import javafx.scene.control.*;

/**
 * Class for the Menu Bar
 */
public class MenuUI extends MenuBar{
    private SongManager m_model;
    private DatabaseManager m_databaseManager;
    private ApplicationController m_applicationController;
    private MainUI m_main;

    public MenuUI(SongManager model, DatabaseManager databaseManager, FilePersistentStorage filePersistentStorage, MainUI mainUI, ApplicationController applicationController){
        super();
        m_model = model;
        m_databaseManager = databaseManager;

        setMenu(filePersistentStorage);

        m_main = mainUI;
        m_applicationController = applicationController;
    }

    private void setMenu(FilePersistentStorage filePersistentStorage) {
        super.getMenus().addAll(getMenuFile(), getMenuOptions(filePersistentStorage), getPlaylistSubMenu(), miniMode());
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

            m_model.setM_libraryAction(Action.ADD);
            m_model.notifyLibraryObservers();
        });
        menuFile.getItems().addAll(addLibraryMenu);
        return menuFile;
    }

    private Menu getMenuOptions(FilePersistentStorage filePersistentStorage) {
        final Menu menuOptions = new Menu("Options");
        Menu leftPanelSubMenu = getLeftPanelSubMenu(filePersistentStorage);
        Menu centerPanelSubMenu = getCenterPanelSubMenu(filePersistentStorage);

        //hide left panel option for now
        leftPanelSubMenu.setVisible(false);
        leftPanelSubMenu.setDisable(true);

        menuOptions.getItems().addAll(leftPanelSubMenu, centerPanelSubMenu);
        return menuOptions;
    }

    private Menu getLeftPanelSubMenu(FilePersistentStorage config) {
        Menu leftPanelSubMenu = new Menu("Left Panel");
        CheckMenuItem showFoldersOnly = new CheckMenuItem("Show folders only");
        showFoldersOnly.setSelected(config.getLeftPanelOption());

        showFoldersOnly.setOnAction(event -> {
            if (showFoldersOnly.isSelected()){
                System.out.println("Display folders only");
                MenuOptions menuManager = m_model.getM_menuOptions();
                menuManager.setM_leftPanelShowFolder(true);
            } else {
                System.out.println("Don't display folders only");
                MenuOptions menuManager = m_model.getM_menuOptions();
                menuManager.setM_leftPanelShowFolder(false);
            }

            m_model.notifyLeftPanelOptionsObservers();
        });

        leftPanelSubMenu.getItems().addAll(showFoldersOnly);
        return leftPanelSubMenu;
    }

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

        playlistSubMenu.getItems().addAll(createNewPlaylistMenu, removePlaylistMenu);
        return playlistSubMenu;
    }

    // Toggles minimode on or off
    private Menu miniMode() {
        Menu minimodeButton = new Menu("Minimode");
        CheckMenuItem mini = new CheckMenuItem("Minimode!");
        mini.setOnAction(event -> {
            System.out.println("Clicked minimode");
            if (Boolean.miniCheck == false) {
                Boolean.miniCheck = true;
                m_applicationController.miniOn();
                m_main.miniOn();
            }


            else if (Boolean.miniCheck == true){
                System.out.println("Clicked minimode");
                Boolean.miniCheck = false;
                m_applicationController.miniOff();
                m_main.miniOff();
            }

        });
        minimodeButton.getItems().addAll(mini);
        return minimodeButton;
    }
}
