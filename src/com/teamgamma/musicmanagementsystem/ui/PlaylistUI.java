package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.model.*;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.List;

/**
 * UI class for list of songs in center of application
 */
public class PlaylistUI extends VBox {
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
    private static final int FILE_COLUMN_MIN_WIDTH = 80;
    private static final int COLUMN_MIN_WIDTH = 60;
    private static final int RATING_COLUMN_MIN_WIDTH = 20;
    private static final int LENGTH_COLUMN_MIN_WIDTH = 50;
    private static final Insets TABLE_VIEW_MARGIN = new Insets(30, 0, 0, 0);
    private static final String ADD_PLAYLIST_BUTTON_ICON_PATH = "res" + File.separator + "add-playlist-button.png";
    private static final String ADD_PLAYLIST_BUTTON_HIGHLIGHT_ICON_PATH = "res" + File.separator + "add-playlist-button-highlight.png";
    private static final String REMOVE_PLAYLIST_BUTTON_ICON_PATH = "res" + File.separator + "remove-playlist-button.png";
    private static final String REMOVE_PLAYLIST_BUTTON__HIGHLIGHT_ICON_PATH = "res" + File.separator + "remove-playlist-button-highlight.png";
    private static final String EDIT_PLAYLIST_BUTTON_ICON_PATH = "res" + File.separator + "edit-playlist-button.png";
    private static final String EDIT_PLAYLIST_BUTTON_HIGHLIGHT_ICON_PATH = "res" + File.separator + "edit-playlist-button-highlight.png";
    private static final String SHUFFLE_PLAYLIST_BUTTON_ICON_PATH = "res" + File.separator + "shuffle-playlist-button.png";
    private static final String SHUFFLE_PLAYLIST_BUTTON_HIGHLIGHT_ICON_PATH = "res" + File.separator + "shuffle-playlist-button-highlight.png";

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
                    createEditPlaylistButton(),
                    createShufflePlaylistButton());
        initTableView();
        setCssStyle();
        registerAsPlaylistObserver();

        this.getChildren().add(createPlayPlaylistButton());
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
                updateTable();
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

        dropDownMenu.valueProperty().addListener(new ChangeListener<Playlist>() {
            @Override
            public void changed(ObservableValue<? extends Playlist> observable, Playlist oldValue, Playlist newValue) {
                m_model.setM_selectedPlaylist(newValue);
                m_model.notifyPlaylistSongsObservers();
            }
        });

        dropDownMenu.setMinWidth(DROP_DOWN_MENU_MIN_WIDTH);
        dropDownMenu.setMaxWidth(DROP_DOWN_MENU_MAX_WIDTH);
        dropDownMenu.setPrefSize(DROP_DOWN_MENU_PREF_WIDTH, DROP_DOWN_MENU_PREF_HEIGHT);
        if (!options.isEmpty()) {
            dropDownMenu.setValue(options.get(0));
        }
        return dropDownMenu;
    }

    private Button createCreateNewPlaylistButton() {
        Button createNewPlaylistButton = new Button();
        createNewPlaylistButton.setTooltip(new Tooltip("Add Playlist"));
        createNewPlaylistButton.setStyle("-fx-background-color: transparent");
        createNewPlaylistButton.setGraphic(new ImageView(ADD_PLAYLIST_BUTTON_ICON_PATH));
        createNewPlaylistButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                createNewPlaylistButton.setGraphic(new ImageView(ADD_PLAYLIST_BUTTON_HIGHLIGHT_ICON_PATH));
            }
        });
        createNewPlaylistButton.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                createNewPlaylistButton.setGraphic(new ImageView(ADD_PLAYLIST_BUTTON_ICON_PATH));
            }
        });
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
        removePlaylistButton.setTooltip(new Tooltip("Remove Playlist"));
        removePlaylistButton.setStyle("-fx-background-color: transparent");
        removePlaylistButton.setGraphic(new ImageView(REMOVE_PLAYLIST_BUTTON_ICON_PATH));
        removePlaylistButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                removePlaylistButton.setGraphic(new ImageView(REMOVE_PLAYLIST_BUTTON__HIGHLIGHT_ICON_PATH));
            }
        });
        removePlaylistButton.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                removePlaylistButton.setGraphic(new ImageView(REMOVE_PLAYLIST_BUTTON_ICON_PATH));
            }
        });
        removePlaylistButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (m_dropDownMenu.getItems().isEmpty()) {
                    PromptUI.customPromptError("Error", null, "No playlist exist!");
                    return;
                }
                int selectedDropDownIndex = m_dropDownMenu.getSelectionModel().getSelectedIndex();
                Playlist selectedPlaylist = m_model.getM_selectedPlaylist();
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
        editPlaylistButton.setTooltip(new Tooltip("Rename Playlist"));
        editPlaylistButton.setStyle("-fx-background-color: transparent");
        editPlaylistButton.setGraphic(new ImageView(EDIT_PLAYLIST_BUTTON_ICON_PATH));
        editPlaylistButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                editPlaylistButton.setGraphic(new ImageView(EDIT_PLAYLIST_BUTTON_HIGHLIGHT_ICON_PATH));
            }
        });
        editPlaylistButton.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                editPlaylistButton.setGraphic(new ImageView(EDIT_PLAYLIST_BUTTON_ICON_PATH));
            }
        });
        editPlaylistButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (m_dropDownMenu.getItems().isEmpty()) {
                    PromptUI.customPromptError("Error", null, "No playlist exist!");
                    return;
                }
                int selectedDropDownIndex = m_dropDownMenu.getSelectionModel().getSelectedIndex();
                Playlist selectedPlaylist = m_model.getM_selectedPlaylist();
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

    private Button createShufflePlaylistButton() {
        Button shufflePlaylistButton = new Button();
        shufflePlaylistButton.setTooltip(new Tooltip("Shuffle Playlist"));
        shufflePlaylistButton.setStyle("-fx-background-color: transparent");
        shufflePlaylistButton.setGraphic(new ImageView(SHUFFLE_PLAYLIST_BUTTON_ICON_PATH));
        shufflePlaylistButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                shufflePlaylistButton.setGraphic(new ImageView(SHUFFLE_PLAYLIST_BUTTON_HIGHLIGHT_ICON_PATH));
            }
        });
        shufflePlaylistButton.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                shufflePlaylistButton.setGraphic(new ImageView(SHUFFLE_PLAYLIST_BUTTON_ICON_PATH));
            }
        });
        shufflePlaylistButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Playlist selectedPlaylist = m_model.getM_selectedPlaylist();
                if (selectedPlaylist == null) {
                    PromptUI.customPromptError("Error", null, "Please select a playlist from the drop down menu!");
                    return;
                }
                selectedPlaylist.shuffleUnplayedSongs();
                m_model.notifyPlaylistSongsObservers();
            }
        });
        return shufflePlaylistButton;
    }

    private Button createPlayPlaylistButton() {
        Button playlistButton = new Button("Play Playlist");
        playlistButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (m_model.getM_selectedPlaylist() != null) {
                    m_musicPlayerManager.playPlaylist(m_model.getM_selectedPlaylist());
                }
            }
        });

        return playlistButton;
    }

    private void initTopMenu(Label selectPlaylistLabel,
                             ComboBox<Playlist> dropDownMenu,
                             Button addPlaylistButton,
                             Button removePlaylistButton,
                             Button shufflePlaylistButton,
                             Button editPlaylistButton) {
        m_dropDownMenu = dropDownMenu;

        HBox topMenu = new HBox();
        topMenu.getChildren().addAll(m_dropDownMenu, addPlaylistButton, removePlaylistButton, editPlaylistButton, shufflePlaylistButton);
        HBox.setHgrow(m_dropDownMenu, Priority.ALWAYS);
        HBox.setHgrow(addPlaylistButton, Priority.NEVER);
        HBox.setHgrow(removePlaylistButton, Priority.NEVER);
        HBox.setHgrow(shufflePlaylistButton, Priority.NEVER);

        VBox menuWrapper = new VBox();
        menuWrapper.getChildren().addAll(selectPlaylistLabel, topMenu);
        VBox.setVgrow(topMenu, Priority.ALWAYS);
        super.getChildren().add(menuWrapper);
    }

    private void initTableView() {
        m_table = new TableView<>();
        setTableColumns();
        setTableDragEvents();
        super.getChildren().add(m_table);
        StackPane.setMargin(m_table, TABLE_VIEW_MARGIN);
        updateTable();
    }

    private void setTableColumns() {
        TableColumn<Song, File> filePathCol = new TableColumn<>("File Path");
        filePathCol.setMinWidth(FILE_COLUMN_MIN_WIDTH);
        TableColumn<Song, String> fileNameCol = new TableColumn<>("File Name");
        fileNameCol.setMinWidth(FILE_COLUMN_MIN_WIDTH);
        TableColumn<Song, String> titleCol = new TableColumn<>("Title");
        titleCol.setMinWidth(COLUMN_MIN_WIDTH);
        TableColumn<Song, String> artistCol = new TableColumn<>("Artist");
        artistCol.setMinWidth(COLUMN_MIN_WIDTH);
        TableColumn<Song, String> albumCol = new TableColumn<>("Album");
        albumCol.setMinWidth(COLUMN_MIN_WIDTH);
        TableColumn<Song, String> genreCol = new TableColumn<>("Genre");
        genreCol.setMinWidth(COLUMN_MIN_WIDTH);
        TableColumn<Song, Integer> ratingCol = new TableColumn<>("Rating");
        ratingCol.setMinWidth(RATING_COLUMN_MIN_WIDTH);
        TableColumn<Song, Double> lengthCol = new TableColumn<>("Length");
        lengthCol.setMinWidth(LENGTH_COLUMN_MIN_WIDTH);
        setTableColumnAttributes(filePathCol, fileNameCol, titleCol, artistCol, albumCol, genreCol, ratingCol, lengthCol);
        showOrHideTableColumns(filePathCol, fileNameCol, titleCol, artistCol, albumCol, genreCol, ratingCol, lengthCol);
    }

    private void clearTable() {
        System.out.println("Clearing playlist panel...");
        m_table.getItems().clear();
    }

    private void updateTable() {
        System.out.println("Updating playlist panel...");
        Playlist selectedPlaylist = m_model.getM_selectedPlaylist();
        if (selectedPlaylist != null) {
            List<Song> songs = selectedPlaylist.getM_songList();

            if (songs.isEmpty()) {
                m_table.setPlaceholder(new Label("Playlist empty"));
            } else {
                m_table.setItems(FXCollections.observableArrayList(songs));
            }
        } else {
            m_table.setPlaceholder(new Label("Select a playlist"));
        }
    }

    private void showOrHideTableColumns(TableColumn<Song, File> filePathCol,
                                        TableColumn<Song, String> fileNameCol,
                                        TableColumn<Song, String> titleCol,
                                        TableColumn<Song, String> artistCol,
                                        TableColumn<Song, String> albumCol,
                                        TableColumn<Song, String> genreCol,
                                        TableColumn<Song, Integer> ratingCol,
                                        TableColumn<Song, Double> lengthCol) {
        m_table.setTableMenuButtonVisible(true);

        //default columns
        fileNameCol.setVisible(true);
        artistCol.setVisible(true);
        lengthCol.setVisible(true);

        filePathCol.setVisible(false);
        titleCol.setVisible(false);
        albumCol.setVisible(false);
        genreCol.setVisible(false);
        ratingCol.setVisible(false);
    }


    private void setTableColumnAttributes(TableColumn<Song, File> filePathCol,
                                          TableColumn<Song, String> fileNameCol,
                                          TableColumn<Song, String> titleCol,
                                          TableColumn<Song, String> artistCol,
                                          TableColumn<Song, String> albumCol,
                                          TableColumn<Song, String> genreCol,
                                          TableColumn<Song, Integer> ratingCol,
                                          TableColumn<Song, Double> lengthCol) {
        filePathCol.setCellValueFactory(new PropertyValueFactory<>("m_file"));
        filePathCol.setSortable(false);

        fileNameCol.setCellValueFactory(new PropertyValueFactory<>("m_fileName"));
        fileNameCol.setSortable(false);

        titleCol.setCellValueFactory(new PropertyValueFactory<>("m_title"));
        titleCol.setSortable(false);

        artistCol.setCellValueFactory(new PropertyValueFactory<>("m_artist"));
        artistCol.setSortable(false);

        albumCol.setCellValueFactory(new PropertyValueFactory<>("m_album"));
        albumCol.setSortable(false);

        genreCol.setCellValueFactory(new PropertyValueFactory<>("m_genre"));
        genreCol.setSortable(false);

        ratingCol.setCellValueFactory(new PropertyValueFactory<>("m_rating"));
        ratingCol.setSortable(false);

        lengthCol.setCellValueFactory(new PropertyValueFactory<>("m_length"));
        lengthCol.setSortable(false);

        m_table.getColumns().add(filePathCol);
        m_table.getColumns().add(fileNameCol);
        m_table.getColumns().add(titleCol);
        m_table.getColumns().add(artistCol);
        m_table.getColumns().add(albumCol);
        m_table.getColumns().add(genreCol);
        m_table.getColumns().add(ratingCol);
        m_table.getColumns().add(lengthCol);
        m_table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setTableDragEvents() {
        m_table.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                System.out.println("Drag over on playlist");
                if(m_model.getM_songToAddToPlaylist() != null && m_model.getM_selectedPlaylist() != null) {
                    dragEvent.acceptTransferModes(TransferMode.MOVE);
                }
                dragEvent.consume();
            }
        });

        m_table.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                System.out.println("Drag dropped on playlist");
                m_model.addSongToPlaylist(m_model.getM_songToAddToPlaylist(), m_model.getM_selectedPlaylist());
                dragEvent.consume();
            }
        });

        m_table.setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                System.out.println("Drag done on playlist");
                m_model.setM_songToAddToPlaylist(null);
                dragEvent.consume();
            }
        });
    }

    private void setCssStyle() {
        final String cssDefault = "-fx-border-color: black;\n";
        this.setStyle(cssDefault);
    }
}
