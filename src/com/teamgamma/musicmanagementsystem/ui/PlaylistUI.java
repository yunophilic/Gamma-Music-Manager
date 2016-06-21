package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.model.*;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.scene.control.*;

/**
 * UI class for list of songs in center of application
 */
public class PlaylistUI extends StackPane {
    private SongManager m_model;
    private MusicPlayerManager m_musicPlayerManager;
    private TableView<Song> m_table;
    private ComboBox<Playlist> m_dropDownMenu;

    /*private ContextMenu m_contextMenu;*/

    public PlaylistUI(SongManager model, MusicPlayerManager musicPlayerManager) {
        super();
        m_model = model;
        m_musicPlayerManager = musicPlayerManager;
        /*m_contextMenu = new ContextMenu();*/
        m_dropDownMenu = new ComboBox<>();
        initDropDownMenu();
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
                // TODO: Add/remove tab for playlist or add/remove playlist from dropdown list
            }
        });
    }

    private void initDropDownMenu() {
        ObservableList<Playlist> options = FXCollections.observableList(m_model.getM_playlists());
        m_dropDownMenu.getItems().addAll(options);
        m_dropDownMenu.setMinSize(200, 10);
        getChildren().add(m_dropDownMenu);
        setAlignment(m_dropDownMenu, Pos.TOP_CENTER);
    }

    private void initTableView() {
        m_table = new TableView<>();
        m_table.setPlaceholder(new Label("Empty"));
        getChildren().add(m_table);
        setMargin(m_table, new Insets(25, 0, 0, 0));
    }

    private void clearTable() {
        System.out.println("Clearing playlist panel...");
        m_table.getItems().clear();
        getChildren().clear();
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
        getChildren().add(m_table);
    }



    private void setCssStyle() {
        final String cssDefault = "-fx-border-color: black;\n";
        this.setStyle(cssDefault);
    }
}
