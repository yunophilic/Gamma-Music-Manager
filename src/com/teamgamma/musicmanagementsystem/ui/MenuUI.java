package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.model.MenuOptions;
import com.teamgamma.musicmanagementsystem.model.PersistentStorage;
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
    private SongManager model;

    public MenuUI(SongManager model){
        super();
        this.model = model;

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
                if (!model.addLibrary(pathInput)) {
                    PromptUI.customPromptError("Error", null, "Path doesn't exist or duplicate library added");
                    return;
                }
                PersistentStorage persistentStorage = new PersistentStorage();
                persistentStorage.updatePersistentStorageLibrary(pathInput);
                model.notifyLibraryObservers();
            }
        });
        menuFile.getItems().addAll(addLibraryMenu);
        return menuFile;
    }

    private Menu getMenuOptions() {
        final Menu menuOptions = new Menu("Options");
        Menu leftPanelSubMenu = new Menu("Left Panel");
        CheckMenuItem showFoldersOnly = new CheckMenuItem("Show folders only");

        showFoldersOnly.setOnAction(event -> {
            if (showFoldersOnly.isSelected()){
                System.out.println("Display folders only");
                MenuOptions menuManager = model.getM_menuOptions();
                menuManager.setShowFolder(true);
            } else {
                System.out.println("Don't display folders only");
                MenuOptions menuManager = model.getM_menuOptions();
                menuManager.setShowFolder(false);
            }

            model.notifyLeftPanelObservers();
        });

        leftPanelSubMenu.getItems().addAll(showFoldersOnly);

        menuOptions.getItems().addAll(leftPanelSubMenu);
        return menuOptions;
    }
}
