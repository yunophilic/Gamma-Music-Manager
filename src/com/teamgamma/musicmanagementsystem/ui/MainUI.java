package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.ApplicationController;
import com.teamgamma.musicmanagementsystem.model.DatabaseManager;
import com.teamgamma.musicmanagementsystem.model.FilePersistentStorage;
import com.teamgamma.musicmanagementsystem.model.SongManager;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;

import com.teamgamma.musicmanagementsystem.util.UserInterfaceUtils;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.List;

/**
 * MainUI Class.
 */
public class MainUI extends BorderPane {
    private static final String SEARCH_ICON_PATH = "res"  + File.separator + "search.png";
    private static final String SEARCH_PROMPT_TEXT = "Search";
    private static final String SEARCH_TOOL_TIP = "Find songs or libraries";
    private static final String SEARCH_TAB_HEADER = "Search Results";
    private static final String FILE_TREE_TAB_HEADER = "File Tree";

    private final double LEFT_PANEL_PREF_WIDTH = 250;
    private final double RIGHT_PANEL_PREF_WIDTH = 350;

    private SongManager m_model;
    private MusicPlayerManager m_musicPlayerManager;
    private DatabaseManager m_databaseManager;
    private FilePersistentStorage m_filePersistentStorage;
    private LibraryUI m_libraryUI;
    private DynamicTreeViewUI m_rightFilePane;
    private ApplicationController m_applicationController;
    private BorderPane m_leftPane;
    private BorderPane m_centerPane;
    private BorderPane m_rightPane;
    private MenuUI m_menuUI;

    public MainUI(SongManager model,
                  MusicPlayerManager musicPlayerManager,
                  DatabaseManager databaseManager,
                  FilePersistentStorage filePersistentStorage,
                  List<String> libraryExpandedPaths,
                  List<String> dynamicTreeViewExpandedPaths,
                  ApplicationController applicationController) {
        super();

        m_model = model;
        m_musicPlayerManager = musicPlayerManager;
        m_databaseManager = databaseManager;
        m_filePersistentStorage = filePersistentStorage;
        m_applicationController = applicationController;
        m_menuUI = new MenuUI(m_model, m_databaseManager, m_filePersistentStorage, this, m_applicationController);

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

        m_leftPane = new BorderPane();
        m_leftPane.setCenter(m_libraryUI);
        m_leftPane.setPrefWidth(LEFT_PANEL_PREF_WIDTH);
        return m_leftPane;
    }

    /**
     * Function to create the UI element that will be shown on the right side of the application
     *
     * @return  The UI elements that are to be shown in the right side of the application.
     */
    private Node rightPane() {
        PlaylistUI playlistUI = new PlaylistUI(m_model, m_musicPlayerManager, m_databaseManager);

        VBox musicPlayerWrapper = new VBox();
        musicPlayerWrapper.getChildren().add(new MusicPlayerHistoryUI(m_model, m_musicPlayerManager));
        musicPlayerWrapper.getChildren().add(new MusicPlayerUI(m_model, m_musicPlayerManager, m_databaseManager,
                m_filePersistentStorage, m_menuUI));
        musicPlayerWrapper.getChildren().add(new MusicPlayerPlaybackQueueUI(m_musicPlayerManager, m_model));

        m_rightPane = new BorderPane();
        m_rightPane.setCenter(playlistUI);
        m_rightPane.setBottom(musicPlayerWrapper);
        m_rightPane.setPrefWidth(RIGHT_PANEL_PREF_WIDTH);

        return m_rightPane;
    }

    /**
     * Function to create the top UI component for the application.
     *
     * @return  All the UI components that will be shown in the top area of the application.
     */
    private Node topPane() {
        BorderPane wrapper = new BorderPane();
        wrapper.setCenter(m_menuUI);

        HBox searchWrapper = new HBox();
        TextField searchText = new TextField();
        searchText.setTooltip(new Tooltip(SEARCH_TOOL_TIP));
        searchText.setPromptText(SEARCH_PROMPT_TEXT);
        searchText.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER){
                searchForFiles(searchText);
            }
        });

        this.setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.F){
                searchText.requestFocus();
            }
        });

        Button search = new Button();
        search.setStyle("-fx-background-color: transparent");
        search.setGraphic(UserInterfaceUtils.createImageViewForImage(SEARCH_ICON_PATH));
        UserInterfaceUtils.createMouseOverUIChange(search, search.getStyle());

        search.setOnMouseClicked(event -> {
            System.out.println("Searching for " + searchText.getText());
            searchForFiles(searchText);
        });

        searchWrapper.getChildren().addAll(searchText, search);

        wrapper.setRight(searchWrapper);
        return wrapper;
    }

    /**
     * Function to call the search function for model depending on the input.
     *
     * @param searchText        The text to search for.
     */
    private void searchForFiles(TextField searchText) {
        if (searchText.getText().isEmpty()) {
            m_model.notifyInitalSearchObserver();
        } else {
            m_model.searchForFilesAndFolders(searchText.getText());
            m_model.getM_menuOptions().setHideRightPanel(false);
            m_model.notifyRightPanelOptionsObservers();
        }
    }

    /**
     * Function to create the center pane for the application.
     *
     * @param dynamicTreeViewExpandedPaths      The list of expanded folders for the alternative (right) file pane.
     * @return                                  The UI components that will be shown in the center.
     */
    private Node centerPane(List<String> dynamicTreeViewExpandedPaths) {
        m_centerPane = new BorderPane();
        m_centerPane.setCenter(createFileExplorer(dynamicTreeViewExpandedPaths));
        return m_centerPane;
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
        Tab fileTree = new Tab(FILE_TREE_TAB_HEADER, m_rightFilePane);

        Tab searchResults = new Tab(SEARCH_TAB_HEADER, new SearchResultUI(m_model, m_musicPlayerManager, m_databaseManager));
        tabPane.getTabs().addAll(fileTree, searchResults);

        pane.getChildren().addAll(contentListUI, tabPane);
        HBox.setHgrow(contentListUI, Priority.ALWAYS);
        HBox.setHgrow(tabPane, Priority.ALWAYS);
        
        m_model.registerSearchObserver(() -> tabPane.getSelectionModel().select(searchResults));

        m_model.addRightFolderObserver((fileActions) -> {
            tabPane.getSelectionModel().select(fileTree);
        });

        m_model.registerRightPanelOptionsObserver(() -> {
            if (m_model.getM_menuOptions().getHideRightPanel()) {
                pane.getChildren().remove(tabPane);
            } else {
                if (!pane.getChildren().contains(tabPane)) {
                    pane.getChildren().add(tabPane);
                }
            }
        });

        m_model.notifyRightPanelOptionsObservers();

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

    /**
     * Turns on minimode. Sets leftPane to be
     *  the rightPane (MusicPlayer)
     */
    public void minimodeTurnOn() {
        this.setCenter(null);
        this.setLeft(rightPane());
    }

    /**
     * Turns off minimode, resetting the panes to their
     *  original places
     */
    public void minimodeTurnOff() {
        this.setLeft(m_leftPane);
        this.setRight(m_rightPane);
        this.setCenter(m_centerPane);
    }
}
