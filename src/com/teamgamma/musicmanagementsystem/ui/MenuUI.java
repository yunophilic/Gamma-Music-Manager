package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.model.DatabaseManager;
import com.teamgamma.musicmanagementsystem.model.MenuOptions;
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
        final Menu menuFile = getMenuFile();

        final Menu menuOptions = getMenuOptions();

        this.getMenus().addAll(menuFile, menuOptions);
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

    public Menu getCenterPanelSubMenu() {
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

        m_model.notifyCenterFolderObservers();

        centerPanelSubMenu.getItems().addAll(showFoldersOnly);
        return centerPanelSubMenu;
    }
}
