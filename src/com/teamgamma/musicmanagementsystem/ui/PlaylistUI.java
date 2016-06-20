package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.misc.ContextMenuConstants;
import com.teamgamma.musicmanagementsystem.model.*;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.*;
import javafx.scene.layout.StackPane;
import javafx.scene.control.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.util.ArrayList;
import java.util.List;

import javafx.event.EventHandler;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;

/**
 * UI class for list of songs in center of application
 */
public class PlaylistUI extends StackPane {
    private SongManager m_model;
    private MusicPlayerManager m_musicPlayerManager;
    private TableView<Song> m_table;
    private TableView<Playlist> m_playlistTable;
    private ContextMenu m_contextMenu;

    public PlaylistUI(SongManager model, MusicPlayerManager musicPlayerManager) {
        super();
        m_model = model;
        m_musicPlayerManager = musicPlayerManager;
        m_contextMenu = new ContextMenu();
        setEmptyText();
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

    private void setEmptyText() {
        m_table = new TableView<>();
        m_playlistTable = new TableView<>();

        m_table.setPlaceholder(new Label("Empty"));
        this.getChildren().add(m_table);
    }

    private void clearTable() {
        System.out.println("Clearing playlist panel...");
        m_table.getItems().clear();
        this.getChildren().clear();
    }

    private void updateTable() {
        System.out.println("Updating playlist panel...");
        m_table = new TableView<>();
       // m_table.setEditable(true);

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
        this.getChildren().add(m_table);


    }



    private void setCssStyle() {
        final String cssDefault = "-fx-border-color: black;\n";
        this.setStyle(cssDefault);
    }
}
