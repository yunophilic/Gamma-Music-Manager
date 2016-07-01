package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.misc.Actions;
import com.teamgamma.musicmanagementsystem.model.DatabaseManager;
import com.teamgamma.musicmanagementsystem.model.MenuOptions;
import com.teamgamma.musicmanagementsystem.model.Playlist;
import com.teamgamma.musicmanagementsystem.model.SongManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

/**
 * Class for the Menu Bar
 */
public class MenuUI extends MenuBar{
    private SongManager m_model;
    private DatabaseManager m_databaseManager;

    public MenuUI(SongManager model, DatabaseManager databaseManager){
        super();
        m_model = model;
        m_databaseManager = databaseManager;

        setMenu();
    }

    private void setMenu() {
        super.getMenus().addAll(getMenuFile(), getMenuOptions(), getPlaylistSubMenu());
    }

    private Menu getMenuFile() {
        final Menu menuFile = new Menu("File");
        MenuItem addLibraryMenu = new MenuItem("Add Library");
        addLibraryMenu.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                String pathInput = PromptUI.addNewLibrary();
                if (pathInput == null) {
                    return;
                }
                if (!m_model.addLibrary(pathInput)) {
                    PromptUI.customPromptError("Error", null, "Path doesn't exist or duplicate library added");
                    return;
                }
                m_databaseManager.addLibrary(pathInput);

                m_model.setM_libraryAction(Actions.ADD);
                m_model.notifyLibraryObservers();
            }
        });
        menuFile.getItems().addAll(addLibraryMenu);
        return menuFile;
    }

    private Menu getMenuOptions() {
        final Menu menuOptions = new Menu("Options");
        Menu leftPanelSubMenu = getLeftPanelSubMenu();
        Menu centerPanelSubMenu = getCenterPanelSubMenu();

        menuOptions.getItems().addAll(leftPanelSubMenu, centerPanelSubMenu);
        return menuOptions;
    }

    private Menu getLeftPanelSubMenu() {
        Menu leftPanelSubMenu = new Menu("Left Panel");
        CheckMenuItem showFoldersOnly = new CheckMenuItem("Show folders only");

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

            m_model.notifyLeftPanelObservers();
        });

        leftPanelSubMenu.getItems().addAll(showFoldersOnly);
        return leftPanelSubMenu;
    }

    private Menu getCenterPanelSubMenu() {
        Menu centerPanelSubMenu = new Menu("Center Panel");
        CheckMenuItem showFoldersOnly = new CheckMenuItem("Show files in subfolders");

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
        createNewPlaylistMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String newPlaylistName = PromptUI.createNewPlaylist();
                if (m_model.playlistNameExist(newPlaylistName)) {
                    PromptUI.customPromptError("Error", null, "Playlist with name \"" + newPlaylistName + "\" already exist!");
                    return;
                }
                if (newPlaylistName != null) {
                    m_model.addPlaylist(newPlaylistName);
                    m_databaseManager.addPlaylist(newPlaylistName);
                    m_model.notifyPlaylistsObservers();
                }
            }
        });

        MenuItem removePlaylistMenu = new MenuItem("Remove Existing Playlist");
        removePlaylistMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Playlist playlistToRemove = PromptUI.removePlaylistSelection(m_model.getM_playlists());
                if (playlistToRemove != null) {
                    m_model.removePlaylist(playlistToRemove);
                    m_databaseManager.removePlaylist(playlistToRemove.getM_playlistName());
                    m_model.notifyPlaylistsObservers();
                }
            }
        });

        playlistSubMenu.getItems().addAll(createNewPlaylistMenu, removePlaylistMenu);
        return playlistSubMenu;
    }
}
