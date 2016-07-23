package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.model.DatabaseManager;
import com.teamgamma.musicmanagementsystem.model.FilePersistentStorage;
import com.teamgamma.musicmanagementsystem.model.SongManager;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;

import com.teamgamma.musicmanagementsystem.util.UserInterfaceUtils;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * MainUI Class.
 */
public class MainUI extends BorderPane {
    public static final String SEARCH_BUTTON_HEADER = "Search";
    private SongManager m_model;
    private MusicPlayerManager m_musicPlayerManager;
    private DatabaseManager m_databaseManager;
    private FilePersistentStorage m_filePersistentStorage;
    private LibraryUI m_libraryUI;
    private DynamicTreeViewUI m_rightFilePane;

    public MainUI(SongManager model,
                  MusicPlayerManager musicPlayerManager,
                  DatabaseManager databaseManager,
                  FilePersistentStorage filePersistentStorage,
                  List<String> libraryExpandedPaths,
                  List<String> dynamicTreeViewExpandedPaths) {
        super();

        m_model = model;
        m_musicPlayerManager = musicPlayerManager;
        m_databaseManager = databaseManager;
        m_filePersistentStorage = filePersistentStorage;

        this.setLeft(leftPane(libraryExpandedPaths));
        this.setRight(rightPane());
        this.setCenter(centerPane(dynamicTreeViewExpandedPaths));
        this.setTop(topPane());
    }

    /**
     * Function to create the UI components that will be shown on the left side of the application.
     *
     * @param libraryExpandedPaths      A list of expanded folders that are on the left side.
     * @return                          All the UI components that are to be shown on the left side of the application.
     */
    private Node leftPane(List<String> libraryExpandedPaths) {
        m_libraryUI = new LibraryUI(m_model, m_musicPlayerManager, m_databaseManager, libraryExpandedPaths);

        BorderPane leftPane = new BorderPane();
        leftPane.setCenter(m_libraryUI);
        leftPane.setPrefWidth(250);
        return leftPane;
    }

    /**
     * Function to create the UI element that will be shown on the right side of the application
     *
     * @return  The UI elements that are to be shown in the right side of the application.
     */
    private Node rightPane() {
        PlaylistUI playlistUI = new PlaylistUI(m_model, m_musicPlayerManager, m_databaseManager);

        VBox musicPlayerWrapper = new VBox();
        musicPlayerWrapper.getChildren().add(new MusicPlayerHistoryUI(m_musicPlayerManager));
        musicPlayerWrapper.getChildren().add(new MusicPlayerUI(m_model, m_musicPlayerManager, m_databaseManager,
                m_filePersistentStorage));
        musicPlayerWrapper.getChildren().add(new MusicPlayerPlaybackQueueUI(m_musicPlayerManager, m_model));

        BorderPane rightPane = new BorderPane();
        rightPane.setCenter(playlistUI);
        rightPane.setBottom(musicPlayerWrapper);
        rightPane.setPrefWidth(350);

        return rightPane;
    }

    /**
     * Function to create the top UI component for the application.
     *
     * @return  All the UI components that will be shown in the top area of the application.
     */
    private Node topPane() {
        BorderPane wrapper = new BorderPane();
        wrapper.setCenter(new MenuUI(m_model, m_databaseManager, m_filePersistentStorage));

        HBox searchWrapper = new HBox();
        TextField searchText = new TextField();

        searchText.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER){
                m_model.searchForFilesAndFolders(searchText.getText());
            }
        });

        this.setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.F){
                searchText.requestFocus();
            }
        });

        Button search = new Button(SEARCH_BUTTON_HEADER);
        UserInterfaceUtils.createMouseOverUIChange(search, search.getStyle());

        search.setMaxHeight(wrapper.getHeight());
        search.setPrefHeight(wrapper.getHeight());
        search.setScaleShape(false);

        search.setOnMouseClicked(event -> {
            System.out.println("Searching for " + searchText.getText());
            m_model.searchForFilesAndFolders(searchText.getText());
        });

        searchWrapper.getChildren().addAll(searchText, search);

        wrapper.setRight(searchWrapper);
        return wrapper;
    }

    /**
     * Function to create the center pane for the application.
     *
     * @param dynamicTreeViewExpandedPaths      The list of expanded folders for the alternative (right) file pane.
     * @return                                  The UI components that will be shown in the center.
     */
    private Node centerPane(List<String> dynamicTreeViewExpandedPaths) {
        BorderPane centerPane = new BorderPane();

        centerPane.setCenter(createFileExplorer(dynamicTreeViewExpandedPaths));
        return centerPane;
    }

    /**
     * Function to create the file explorer UI component.
     *
     * @param dynamicTreeViewExpandedPaths      The list of expanded folders for the right file pane.
     * @return                                  The UI component for the file explorer.
     */
    private Node createFileExplorer(List<String> dynamicTreeViewExpandedPaths) {

        HBox pane = new HBox();

        ContentListUI contentListUI = new ContentListUI(m_model, m_musicPlayerManager, m_databaseManager);

        m_rightFilePane = new DynamicTreeViewUI(m_model, m_musicPlayerManager, m_databaseManager, dynamicTreeViewExpandedPaths);

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        Tab fileTree = new Tab("File Tree", m_rightFilePane);

        Tab searchResults = new Tab("Search Results", new SearchResultUI(m_model, m_musicPlayerManager, m_databaseManager));
        tabPane.getTabs().addAll(fileTree, searchResults);

        pane.getChildren().addAll(contentListUI, tabPane);
        HBox.setHgrow(contentListUI, Priority.ALWAYS);
        HBox.setHgrow(tabPane, Priority.ALWAYS);
        
        m_model.registerSearchObserver(() -> tabPane.getSelectionModel().select(searchResults));

        return pane;
    }

    /**
     * Function to get a list of libraries that are expanded on the left pane.
     *
     * @return  A list of expanded folders
     */
    public List<String> getLibraryUIExpandedPaths() {
        return m_libraryUI.getExpandedPaths();
    }

    /**
     * Function to get a list of folders that are expanded on the right file pane.
     *
     * @return  A list of expanded folders
     */
    public List<String> getDynamicTreeViewUIExpandedPaths() {
        return m_rightFilePane.getExpandedPaths();
    }
}
