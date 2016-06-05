package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.Library;
import com.teamgamma.musicmanagementsystem.SongManager;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;

import java.util.List;

/**
 * MainUI Class.
 */
public class MainUI extends BorderPane {
    private SongManager model;

    public MainUI(SongManager model) {
        super();

        this.model = model;

        this.setLeft(leftPane());
        this.setRight(rightPane());
        this.setCenter(centerPane());
        this.setTop(topPane());
        //this.setBottom(bottomePane());
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

        MusicPlayerManager musicManager = new MusicPlayerManager();
        return new MusicPlayerUI(musicManager);
    }

    private Node centerPane() {
        BorderPane centerPane = new BorderPane();

        MusicPlayerManager musicManager = new MusicPlayerManager();
        MusicPlayerUI musicPlayerUI = new MusicPlayerUI(musicManager);

        centerPane.setBottom(musicPlayerUI);
        centerPane.setCenter(new ContentListUI(model, musicManager));
        return centerPane;
    }

    private Node getMenu() {
        final Menu menuFile = new Menu("File");
        final Menu menuOptions = new Menu("Options");
        final Menu menuHelp = new Menu("Help");

        MenuItem addLibraryMenu = new MenuItem("Add library");
        addLibraryMenu.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                System.out.println("Add new library");

            }
        });

        menuFile.getItems().addAll(addLibraryMenu);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(menuFile, menuOptions, menuHelp);
        return menuBar;
    }
}
