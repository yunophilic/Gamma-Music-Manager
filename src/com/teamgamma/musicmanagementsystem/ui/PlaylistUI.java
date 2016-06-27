package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.model.*;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.control.*;

import java.io.File;

/**
 * UI class for list of songs in center of application
 */
public class PlaylistUI extends StackPane {
    private SongManager m_model;
    private MusicPlayerManager m_musicPlayerManager;
    private DatabaseManager m_databaseManager;
    private TableView<Song> m_table;
    private ComboBox<Playlist> m_dropDownMenu;

    //constants
    private static final int DROP_DOWN_MENU_MIN_WIDTH = 100;
    private static final int DROP_DOWN_MENU_MAX_WIDTH = 700;
    private static final int DROP_DOWN_MENU_PREF_WIDTH = 200;
    private static final int DROP_DOWN_MENU_PREF_HEIGHT = 30;
    private static final int SELECT_PLAYLIST_LABEL_PREF_WIDTH = 80;
    private static final int SELECT_PLAYLIST_LABEL_PREF_HEIGHT = 30;
    private static final Insets TABLE_VIEW_MARGIN = new Insets(30, 0, 0, 0);
    private static final String ADD_PLAYLIST_BUTTON_ICON_PATH = "res" + File.separator + "add-playlist-button.png";
    private static final String REMOVE_PLAYLIST_BUTTON_ICON_PATH = "res" + File.separator + "remove-playlist-button.png";
    private static final String EDIT_PLAYLIST_BUTTON_ICON_PATH = "res" + File.separator + "edit-playlist-button.png";


    public PlaylistUI(SongManager model, MusicPlayerManager musicPlayerManager, DatabaseManager databaseManager) {
        super();
        m_model = model;
        m_musicPlayerManager = musicPlayerManager;
        m_databaseManager = databaseManager;
        m_dropDownMenu = new ComboBox<>();
        initTopMenu(createSelectPlaylistLabel(),
                    createDropDownMenu(),
                    createCreateNewPlaylistButton(),
                    createRemovePlaylistButton(),
                    createEditPlaylistButton());
        initTableView();
        setCssStyle();
        registerAsPlaylistObserver();
    }

    /**
     * Register as a observer to changes for the folder selected to be displayed here
     */
    private void registerAsPlaylistObserver() {
        m_model.addPlaylistObserver(new PlaylistObserver() {
            @Override
            public void songsChanged() {
                clearTable();
                updateTable();
            }

            @Override
            public void playlistsChanged() {
                m_dropDownMenu.getItems().clear();
                m_dropDownMenu.getItems().addAll(m_model.getM_playlists());
            }
        });
    }

    private Label createSelectPlaylistLabel() {
        Label selectPlaylistLabel = new Label(" Select Playlist:");
        selectPlaylistLabel.setPrefSize(SELECT_PLAYLIST_LABEL_PREF_WIDTH, SELECT_PLAYLIST_LABEL_PREF_HEIGHT);
        return selectPlaylistLabel;
    }

    private ComboBox<Playlist> createDropDownMenu() {
        ObservableList<Playlist> options = FXCollections.observableList(m_model.getM_playlists());
        ComboBox<Playlist> dropDownMenu = new ComboBox<>();
        dropDownMenu.getItems().addAll(options);
        dropDownMenu.setMinWidth(DROP_DOWN_MENU_MIN_WIDTH);
        dropDownMenu.setMaxWidth(DROP_DOWN_MENU_MAX_WIDTH);
        dropDownMenu.setPrefSize(DROP_DOWN_MENU_PREF_WIDTH, DROP_DOWN_MENU_PREF_HEIGHT);
        if(!options.isEmpty()) {
            dropDownMenu.setValue(options.get(0));
        }
        return dropDownMenu;
    }

    private Button createCreateNewPlaylistButton() {
        Button createNewPlaylistButton = new Button();
        createNewPlaylistButton.setStyle("-fx-background-color: transparent");
        createNewPlaylistButton.setGraphic( new ImageView(ADD_PLAYLIST_BUTTON_ICON_PATH) );
        createNewPlaylistButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String newPlaylistName = PromptUI.createNewPlaylist();
                if (m_model.playlistNameExist(newPlaylistName)) {
                    PromptUI.customPromptError("Error", null, "Playlist with name \"" + newPlaylistName + "\" already exist!");
                    return;
                }
                if (newPlaylistName != null) {
                    Playlist newPlaylist = m_model.addPlaylist(newPlaylistName);
                    m_databaseManager.addPlaylist(newPlaylistName);
                    m_model.notifyPlaylistsObservers();
                    m_dropDownMenu.getSelectionModel().select(newPlaylist);
                }
            }
        });
        return createNewPlaylistButton;
    }

    private Button createRemovePlaylistButton() {
        Button removePlaylistButton = new Button();
        removePlaylistButton.setStyle("-fx-background-color: transparent");
        removePlaylistButton.setGraphic( new ImageView(REMOVE_PLAYLIST_BUTTON_ICON_PATH) );
        removePlaylistButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (m_dropDownMenu.getItems().isEmpty()) {
                    PromptUI.customPromptError("Error", null, "No playlist exist!");
                    return;
                }
                int selectedDropDownIndex = m_dropDownMenu.getSelectionModel().getSelectedIndex();
                Playlist selectedPlaylist = m_dropDownMenu.getValue();
                if (selectedPlaylist == null) {
                    PromptUI.customPromptError("Error", null, "Please select a playlist from the drop down menu!");
                    return;
                }
                if (PromptUI.removePlaylist(selectedPlaylist)) {
                    m_model.removePlaylist(selectedPlaylist);
                    m_databaseManager.removePlaylist(selectedPlaylist.getM_playlistName());
                    m_model.notifyPlaylistsObservers();
                    m_dropDownMenu.getSelectionModel().select(selectedDropDownIndex);
                }
            }
        });
        return removePlaylistButton;
    }

    private Button createEditPlaylistButton() {
        Button editPlaylistButton = new Button();
        editPlaylistButton.setStyle("-fx-background-color: transparent");
        editPlaylistButton.setGraphic( new ImageView(EDIT_PLAYLIST_BUTTON_ICON_PATH) );
        editPlaylistButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (m_dropDownMenu.getItems().isEmpty()) {
                    PromptUI.customPromptError("Error", null, "No playlist exist!");
                    return;
                }
                int selectedDropDownIndex = m_dropDownMenu.getSelectionModel().getSelectedIndex();
                Playlist selectedPlaylist = m_dropDownMenu.getValue();
                if (selectedPlaylist == null) {
                    PromptUI.customPromptError("Error", null, "Please select a playlist from the drop down menu!");
                    return;
                }
                String oldPlaylistName = selectedPlaylist.getM_playlistName();
                String newPlaylistName = PromptUI.editPlaylist(selectedPlaylist);
                if (newPlaylistName != null) {
                    selectedPlaylist.setM_playlistName(newPlaylistName);
                    m_databaseManager.renamePlaylist(oldPlaylistName, newPlaylistName);
                    m_model.notifyPlaylistsObservers();
                    m_dropDownMenu.getSelectionModel().select(selectedDropDownIndex);
                }
            }
        });
        return editPlaylistButton;
    }

    private void initTopMenu(Label selectPlaylistLabel,
                             ComboBox<Playlist> dropDownMenu,
                             Button addPlaylistButton,
                             Button removePlaylistButton,
                             Button editPlaylistButton) {
        m_dropDownMenu = dropDownMenu;

        HBox topMenu = new HBox();
        topMenu.getChildren().addAll(selectPlaylistLabel, m_dropDownMenu, addPlaylistButton, removePlaylistButton, editPlaylistButton);
        HBox.setHgrow(selectPlaylistLabel, Priority.NEVER);
        HBox.setHgrow(m_dropDownMenu, Priority.ALWAYS);
        HBox.setHgrow(addPlaylistButton, Priority.NEVER);
        HBox.setHgrow(removePlaylistButton, Priority.NEVER);

        super.getChildren().add(topMenu);
    }

    private void initTableView() {
        m_table = new TableView<>();
        m_table.setPlaceholder(new Label("Empty"));
        super.getChildren().add(m_table);
        StackPane.setMargin(m_table, TABLE_VIEW_MARGIN);
    }

    private void clearTable() {
        System.out.println("Clearing playlist panel...");
        m_table.getItems().clear();
        super.getChildren().clear();
    }

    private void updateTable() {
        System.out.println("Updating playlist panel...");
        m_table = new TableView<>();
        //m_table.setEditable(true);

        /*MenuItem createPlaylist = new MenuItem(ContextMenuConstants.CREATE_NEW_PLAYLIST);
        createPlaylist.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Playlist playlist = new Playlist("Playlist");
                List<Playlist> playlistStorage = new ArrayList<>();
                playlistStorage.add(playlist);
                System.out.println("Created New Playlist");

            }
        });*/

        m_table.setPlaceholder(new Label("This pane was notified of changes to playlist"));
        super.getChildren().add(m_table);
    }



    private void setCssStyle() {
        final String cssDefault = "-fx-border-color: black;\n";
        this.setStyle(cssDefault);
    }
}
