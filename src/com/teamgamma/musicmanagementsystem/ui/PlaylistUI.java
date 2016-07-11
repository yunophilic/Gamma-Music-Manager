package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.misc.ContextMenuConstants;
import com.teamgamma.musicmanagementsystem.model.*;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerConstants;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;

import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerObserver;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.File;
import java.util.List;

/**
 * UI class for list of songs in center of application
 */
public class PlaylistUI extends VBox {

    private SongManager m_model;
    private MusicPlayerManager m_musicPlayerManager;
    private DatabaseManager m_databaseManager;
    private ContextMenu m_contextMenu;
    private ContextMenu m_playbackContextMenu;
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
    private static final double PLAYLIST_PLAYBACK_BUTTON_SCALE = 0.75;

    private static final Insets TABLE_VIEW_MARGIN = new Insets(30, 0, 0, 0);

    private static final String ADD_PLAYLIST_BUTTON_ICON_PATH = "res" + File.separator + "add-playlist-button.png";
    private static final String ADD_PLAYLIST_BUTTON_HIGHLIGHT_ICON_PATH = "res" + File.separator + "add-playlist-button-highlight.png";
    private static final String REMOVE_PLAYLIST_BUTTON_ICON_PATH = "res" + File.separator + "remove-playlist-button.png";
    private static final String REMOVE_PLAYLIST_BUTTON__HIGHLIGHT_ICON_PATH = "res" + File.separator + "remove-playlist-button-highlight.png";
    private static final String EDIT_PLAYLIST_BUTTON_ICON_PATH = "res" + File.separator + "edit-playlist-button.png";
    private static final String EDIT_PLAYLIST_BUTTON_HIGHLIGHT_ICON_PATH = "res" + File.separator + "edit-playlist-button-highlight.png";
    private static final String SHUFFLE_PLAYLIST_BUTTON_ICON_PATH = "res" + File.separator + "shuffle-playlist-button.png";
    private static final String SHUFFLE_PLAYLIST_BUTTON_HIGHLIGHT_ICON_PATH = "res" + File.separator + "shuffle-playlist-button-highlight.png";
    private static final String PLAY_PLAYLIST_ICON = "res" + File.separator + "ic_play_circle_filled_black_48dp_1x.png";
    private static final String REPEAT_PLAYLIST_ICON = "res" + File.separator + "ic_repeat_black_48dp_1x.png";

    private static final String SELECT_PLAYLIST_HEADER = " Select Playlist:";
    private static final String ADD_PLAYLIST_TOOL_TIP_MESSAGE = "Add Playlist";
    private static final String REMOVE_PLAYLIST_TOOLTIP_MESSAGE = "Remove Playlist";
    private static final String RENAME_PLAYLIST_TOOL_TIP_MESSAGE = "Rename Playlist";
    private static final String SHUFFLE_PLAYLIST_TOOL_TIP_MESSAGE = "Shuffle Playlist";
    private static final String PLAY_PLAYLIST_TOOL_TIP_MESSAGE = "Play Playlist";
    private static final String REPEAT_PLAYLIST_TOOL_TIP_BUTTON = "Repeat Playlist Mode";
    private static final String EMPTY_PLAYLIST_MESSAGE = "Playlist empty";

    public PlaylistUI(SongManager model, MusicPlayerManager musicPlayerManager, DatabaseManager databaseManager) {
        super();
        m_model = model;
        m_musicPlayerManager = musicPlayerManager;
        m_databaseManager = databaseManager;
        m_contextMenu = new ContextMenu();
        m_playbackContextMenu = new ContextMenu();
        m_dropDownMenu = new ComboBox<>();
        initTopMenu(createSelectPlaylistLabel(),
                    createDropDownMenu(),
                    createCreateNewPlaylistButton(),
                    createRemovePlaylistButton(),
                    createRenamePlaylistButton(),
                    createShufflePlaylistButton());
        initTableView();
        setCssStyle();
        registerAsPlaylistObserver();
        this.setSpacing(0);
        HBox playbackOptions = createPlaylistPlaybackOptions();
        VBox.setVgrow(playbackOptions, Priority.NEVER);
        this.getChildren().add(playbackOptions);
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

    /**
     * Function to return the playlist header label.
     *
     * @return The playlist header label
     */
    private Label createSelectPlaylistLabel() {
        Label selectPlaylistLabel = new Label(SELECT_PLAYLIST_HEADER);
        selectPlaylistLabel.setPrefSize(SELECT_PLAYLIST_LABEL_PREF_WIDTH, SELECT_PLAYLIST_LABEL_PREF_HEIGHT);
        return selectPlaylistLabel;
    }

    /**
     * Function to create the drop down menu to select a playlist.
     *
     * @return The drop down menu containing the playlist you can select.
     */
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

    /**
     * Function to create the playlist button.
     *
     * @return The create playlist button.
     */
    private Button createCreateNewPlaylistButton() {
        Button createNewPlaylistButton = new Button();
        createNewPlaylistButton.setTooltip(new Tooltip(ADD_PLAYLIST_TOOL_TIP_MESSAGE));
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
                    Playlist newPlaylist = m_model.addAndCreatePlaylist(newPlaylistName);
                    m_databaseManager.addPlaylist(newPlaylistName);
                    m_model.notifyPlaylistsObservers();
                    m_dropDownMenu.getSelectionModel().select(newPlaylist);
                }
            }
        });
        return createNewPlaylistButton;
    }

    /**
     * Function to create the remove playlist button.
     *
     * @return  The remove playlist button
     */
    private Button createRemovePlaylistButton() {
        Button removePlaylistButton = new Button();
        removePlaylistButton.setTooltip(new Tooltip(REMOVE_PLAYLIST_TOOLTIP_MESSAGE));
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

    /**
     * Function to create the edit playlist button.
     *
     * @return The edit playlist button
     */
    private Button createRenamePlaylistButton() {
        Button editPlaylistButton = new Button();
        editPlaylistButton.setTooltip(new Tooltip(RENAME_PLAYLIST_TOOL_TIP_MESSAGE));
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
        shufflePlaylistButton.setTooltip(new Tooltip(SHUFFLE_PLAYLIST_TOOL_TIP_MESSAGE));
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

    /**
     * Function to create the play playlist button
     *
     * @return The play palylist button.
     */
    private Button createPlayPlaylistButton() {
        Button playlistButton = UserInterfaceUtils.createIconButton(PLAY_PLAYLIST_ICON);
        playlistButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (m_model.getM_selectedPlaylist() != null) {
                    m_musicPlayerManager.playPlaylist(m_model.getM_selectedPlaylist());
                }
            }
        });
        UserInterfaceUtils.createMouseOverUIChange(playlistButton, playlistButton.getStyle());
        playlistButton.setTooltip(new Tooltip(PLAY_PLAYLIST_TOOL_TIP_MESSAGE));

        playlistButton.setScaleY(PLAYLIST_PLAYBACK_BUTTON_SCALE);
        playlistButton.setScaleX(PLAYLIST_PLAYBACK_BUTTON_SCALE);
        playlistButton.setPadding(new Insets(0));
        return playlistButton;
    }

    /**
     * Function to create the repeat playlist button.
     *
     * @return The repeat playlist button.
     */
    private ToggleButton createPlaylistRepeatButton() {
        ToggleButton playlistRepeat = new ToggleButton();
        playlistRepeat.setStyle("-fx-background-color: transparent");
        playlistRepeat.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (playlistRepeat.isSelected()){
                    m_musicPlayerManager.setRepeat(true);
                    playlistRepeat.setStyle("-fx-background-color: lightgray");
                } else {
                    m_musicPlayerManager.setRepeat(false);
                    playlistRepeat.setStyle("-fx-background-color: transparent");
                }
                UserInterfaceUtils.createMouseOverUIChange(playlistRepeat, playlistRepeat.getStyle());
            }
        });

        playlistRepeat.setGraphic(new ImageView(REPEAT_PLAYLIST_ICON));
        playlistRepeat.setTooltip(new Tooltip(REPEAT_PLAYLIST_TOOL_TIP_BUTTON));
        UserInterfaceUtils.createMouseOverUIChange(playlistRepeat, playlistRepeat.getStyle());

        playlistRepeat.setScaleY(PLAYLIST_PLAYBACK_BUTTON_SCALE);
        playlistRepeat.setScaleX(PLAYLIST_PLAYBACK_BUTTON_SCALE);
        playlistRepeat.setPadding(new Insets(0));
        return playlistRepeat;
    }

    /**
     * Function to create the playlist playback options UI elements.
     *
     * @return A Hbox containing the playlist playback options.
     */
    private HBox createPlaylistPlaybackOptions() {
        HBox wrapper = new HBox();
        wrapper.getChildren().add(createPlayPlaylistButton());
        wrapper.getChildren().add(createPlaylistRepeatButton());
        wrapper.setPadding(new Insets(0));
        wrapper.setAlignment(Pos.CENTER);
        return wrapper;
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
        setTableRowMouseEvents();
        super.getChildren().add(m_table);
        StackPane.setMargin(m_table, TABLE_VIEW_MARGIN);
        updateTable();
        VBox.setVgrow(m_table, Priority.ALWAYS);
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
        TableColumn<Song, String> lengthCol = new TableColumn<>("Length");
        lengthCol.setMinWidth(LENGTH_COLUMN_MIN_WIDTH);
        setTableColumnAttributes(filePathCol, fileNameCol, titleCol, artistCol, albumCol, genreCol, ratingCol, lengthCol);
        showOrHideTableColumns(filePathCol, fileNameCol, titleCol, artistCol, albumCol, genreCol, ratingCol, lengthCol);
    }

    private void clearTable() {
        //System.out.println("Clearing playlist panel...");
        m_table.getItems().clear();
    }

    private void updateTable() {
        //System.out.println("Updating playlist panel...");
        Playlist selectedPlaylist = m_model.getM_selectedPlaylist();
        if (selectedPlaylist != null) {
            List<Song> songs = selectedPlaylist.getM_songList();

            if (songs.isEmpty()) {
                m_table.setPlaceholder(new Label(EMPTY_PLAYLIST_MESSAGE));
            } else {
                m_table.setItems(FXCollections.observableArrayList(songs));
            }
        } else {
            m_table.setPlaceholder(new Label(SELECT_PLAYLIST_HEADER));
        }
    }

    private void showOrHideTableColumns(TableColumn<Song, File> filePathCol,
                                        TableColumn<Song, String> fileNameCol,
                                        TableColumn<Song, String> titleCol,
                                        TableColumn<Song, String> artistCol,
                                        TableColumn<Song, String> albumCol,
                                        TableColumn<Song, String> genreCol,
                                        TableColumn<Song, Integer> ratingCol,
                                        TableColumn<Song, String> lengthCol) {
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
                                          TableColumn<Song, String> lengthCol) {
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

        lengthCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Song, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Song, String> param) {
                Duration lengthOfSong = new Duration(
                        param.getValue().getM_length() * MusicPlayerConstants.NUMBER_OF_MILISECONDS_IN_SECOND);
                return new ReadOnlyObjectWrapper<>(UserInterfaceUtils.convertDurationToTimeString(lengthOfSong));
            }
        });
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
                // For Debugging
                //System.out.println("Drag over on playlist");
                // TODO: is this check still correct?
                if (m_model.getSongToMove() != null && m_model.getM_selectedPlaylist() != null) {
                    dragEvent.acceptTransferModes(TransferMode.MOVE);
                }
                dragEvent.consume();
            }
        });

        m_table.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                //System.out.println("Drag dropped on playlist");
                m_model.addSongToPlaylist( m_model.getSongToMove(), m_model.getM_selectedPlaylist() );
                m_musicPlayerManager.notifyQueingObserver();
                dragEvent.consume();
            }
        });

        m_table.setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                //System.out.println("Drag done on playlist");
                //m_model.setM_songToAddToPlaylist(null);
                dragEvent.consume();
            }
        });
    }

    private void setTableRowMouseEvents() {
        m_table.setRowFactory(new Callback<TableView<Song>, TableRow<Song>>() {
            @Override
            public TableRow<Song> call(TableView<Song> param) {
                TableRow<Song> row = new TableRow<>();
                row.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        int selectedSongIndex = row.getIndex();
                        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                            Playlist selectedPlaylist = m_model.getM_selectedPlaylist();
                            selectedPlaylist.setM_currentSongIndex(selectedSongIndex);
                            m_musicPlayerManager.playPlaylist(selectedPlaylist);
                        } else if (event.getButton() == MouseButton.PRIMARY) {
                            m_contextMenu.hide();
                            if (m_playbackContextMenu != null) {
                                m_playbackContextMenu.hide();
                            }
                        } else if (event.getButton() == MouseButton.SECONDARY) {
                            m_contextMenu.hide();
                            m_contextMenu = generateContextMenu(selectedSongIndex);
                            m_contextMenu.show(m_table, event.getScreenX(), event.getScreenY());
                        }
                    }
                });

                UserInterfaceUtils.createMouseOverUIChange(row, null);

                m_musicPlayerManager.registerNewSongObserver(new MusicPlayerObserver() {
                    @Override
                    public void updateUI() {
                        if (m_musicPlayerManager.getCurrentIndexOfPlaylistSong() == row.getIndex()){
                            row.setStyle(UserInterfaceUtils.SELECTED_BACKGROUND_COLOUR);

                            // Make it persist
                            UserInterfaceUtils.createMouseOverUIChange(row, row.getStyle());
                            return;
                        }

                        row.setStyle(null);
                        UserInterfaceUtils.createMouseOverUIChange(row, null);
                    }
                });


                return row;
            }
        });
    }

    private ContextMenu generateContextMenu(int selectedSongIndex) {
        ContextMenu contextMenu = new ContextMenu();

        //remove from playlist option
        MenuItem removeFromPlaylist = new MenuItem(ContextMenuConstants.REMOVE_FROM_PLAYLIST);
        removeFromPlaylist.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Playlist selectedPlaylist = m_model.getM_selectedPlaylist();
                if (PromptUI.removeSongFromPlaylist(selectedPlaylist,
                                                    selectedPlaylist.getSongByIndex(selectedSongIndex))) {
                    boolean songToRemoveIsPlaying = (selectedSongIndex == selectedPlaylist.getM_currentSongIndex());

                    selectedPlaylist.removeSong(selectedSongIndex);
                    m_model.notifyPlaylistSongsObservers();

                    if (!selectedPlaylist.isEmpty() && songToRemoveIsPlaying) {
                        m_musicPlayerManager.playPlaylist(selectedPlaylist);
                    }

                    if (selectedPlaylist.isEmpty()) {
                        m_musicPlayerManager.stopSong();
                        m_musicPlayerManager.resetCurrentPlaylist();
                    }
                }
            }
        });

        contextMenu.getItems().add(removeFromPlaylist);

        return contextMenu;
    }

    private void setCssStyle() {
        final String cssDefault = "-fx-border-color: black;\n";
        this.setStyle(cssDefault);
    }
}
