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


    public PlaylistUI(SongManager model, MusicPlayerManager musicPlayerManager, DatabaseManager databaseManager) {
        super();
        m_model = model;
        m_musicPlayerManager = musicPlayerManager;
        m_databaseManager = databaseManager;
        m_dropDownMenu = new ComboBox<>();
        initTopMenu(createSelectPlaylistLabel(), createDropDownMenu(), createAddNewPlaylistButton());
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
                //refresh drop down menu
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

    private Button createAddNewPlaylistButton() {
        Button addNewPlaylistButton = new Button();
        addNewPlaylistButton.setStyle("-fx-background-color: transparent");
        addNewPlaylistButton.setGraphic( new ImageView("res" + File.separator + "plus-button.png") );
        addNewPlaylistButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String playlistName = PromptUI.addNewPlaylist();
                if (playlistName != null) {
                    Playlist newPlaylist = m_model.addPlaylist(playlistName);
                    m_model.notifyPlaylistsObservers();
                    m_databaseManager.addPlaylist(playlistName);
                    m_dropDownMenu.setValue(newPlaylist);
                }
            }
        });
        return addNewPlaylistButton;
    }

    private void initTopMenu(Label selectPlaylistLabel, ComboBox<Playlist> dropDownMenu, Button addPlaylistButton) {
        m_dropDownMenu = dropDownMenu;

        HBox topMenu = new HBox();
        topMenu.getChildren().addAll(selectPlaylistLabel, m_dropDownMenu, addPlaylistButton);
        HBox.setHgrow(selectPlaylistLabel, Priority.NEVER);
        HBox.setHgrow(m_dropDownMenu, Priority.ALWAYS);
        HBox.setHgrow(addPlaylistButton, Priority.NEVER);

        super.getChildren().add(topMenu);
    }

    private void initTableView() {
        m_table = new TableView<>();
        m_table.setPlaceholder(new Label("Empty"));
        super.getChildren().add(m_table);
        setMargin(m_table, TABLE_VIEW_MARGIN);
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
