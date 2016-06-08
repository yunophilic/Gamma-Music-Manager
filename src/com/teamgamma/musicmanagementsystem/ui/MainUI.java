package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.model.PersistentStorage;
import com.teamgamma.musicmanagementsystem.model.SongManager;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;

/**
 * MainUI Class.
 */
public class MainUI extends BorderPane {
    private SongManager model;

    private MusicPlayerManager m_musicPlayerManager;

    public MainUI(SongManager model, MusicPlayerManager musicPlayerManager) {
        super();

        this.model = model;
        m_musicPlayerManager = musicPlayerManager;

        this.setLeft(leftPane());
        this.setRight(rightPane());
        this.setCenter(centerPane());
        this.setTop(topPane());
        this.setBottom(bottomePane());
    }

    private Node leftPane() {
        BorderPane leftPane = new BorderPane();
        //List<Library> libraries = model.getM_libraries();

        LibraryUI libraryUI = new LibraryUI(model);

        leftPane.setCenter(libraryUI);
        leftPane.setPrefWidth(250);

        return leftPane;
    }

    private Node rightPane() {
        BorderPane rightPane = new BorderPane();
        DynamicTreeViewUI dynamicTreeViewUI = new DynamicTreeViewUI(model);

        rightPane.setCenter(dynamicTreeViewUI);
        rightPane.setPrefWidth(250);

        return rightPane;
    }

    /*private Node sidePane(Library library){
        BorderPane sidePane = new BorderPane();

        LibraryUI libraryUI = new LibraryUI(library);
        libraryUI.setMaxWidth(Double.MAX_VALUE);
        libraryUI.setMaxHeight(Double.MAX_VALUE);

        sidePane.setCenter(libraryUI);
        return sidePane;
    }*/

    private Node topPane() {
        return getMenu();
    }

    private Node bottomePane() {
        //return new Label("Music Player");

        return new MusicPlayerUI(m_musicPlayerManager);
    }

    private Node centerPane() {
        BorderPane centerPane = new BorderPane();

        centerPane.setCenter(new ContentListUI(model, m_musicPlayerManager));
        return centerPane;
    }

    private Node getMenu() {
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

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(menuFile);
        return menuBar;
    }
}
