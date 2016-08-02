package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.util.ContextMenuBuilder;
import com.teamgamma.musicmanagementsystem.model.*;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerConstants;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;

import com.teamgamma.musicmanagementsystem.util.UserInterfaceUtils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UI class for list of songs in center of application
 */
public class PlaylistUI extends VBox {
    private SongManager m_model;
    private MusicPlayerManager m_musicPlayerManager;
    private DatabaseManager m_databaseManager;
    private ContextMenu m_contextMenu;
    private TableView<Song> m_table;
    private ComboBox<Playlist> m_dropDownMenu;

    //constants
    private static final String FILE_PATH_COLUMN_ID = "file_path";
    private static final String FILE_NAME_COLUMN_ID = "file_name";
    private static final String TITLE_COLUMN_ID = "title";
    private static final String ARTIST_COLUMN_ID = "artist";
    private static final String ALBUM_COLUMN_ID = "album";
    private static final String GENRE_COLUMN_ID = "genre";
    private static final String RATING_COLUMN_ID = "rating";
    private static final String LENGTH_COLUMN_ID = "length";

    private static final String FILE_PATH_COLUMN_HEADING = "File Path";
    private static final String FILE_NAME_COLUMN_HEADING = "File Name";
    private static final String TITLE_COLUMN_HEADING = "Title";
    private static final String ARTIST_COLUMN_HEADING = "Artist";
    private static final String ALBUM_COLUMN_HEADING = "Album";
    private static final String GENRE_COLUMN_HEADING = "Genre";
    private static final String RATING_COLUMN_HEADING = "Rating";
    private static final String LENGTH_COLUMN_HEADING = "Length";

    private static final int DROP_DOWN_MENU_MIN_WIDTH = 100;
    private static final int DROP_DOWN_MENU_MAX_WIDTH = 700;
    private static final int DROP_DOWN_MENU_PREF_WIDTH = 200;
    private static final int DROP_DOWN_MENU_PREF_HEIGHT = 30;
    private static final int SELECT_PLAYLIST_LABEL_PREF_WIDTH = 90;
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

    private static final String SELECT_PLAYLIST_HEADER = "   Select Playlist:";
    private static final String ADD_PLAYLIST_TOOL_TIP_MESSAGE = "Add Playlist";
    private static final String REMOVE_PLAYLIST_TOOLTIP_MESSAGE = "Remove Playlist";
    private static final String RENAME_PLAYLIST_TOOL_TIP_MESSAGE = "Rename Playlist";
    private static final String SHUFFLE_PLAYLIST_TOOL_TIP_MESSAGE = "Shuffle Playlist";
    private static final String PLAY_PLAYLIST_TOOL_TIP_MESSAGE = "Play Playlist";
    private static final String REPEAT_PLAYLIST_TOOL_TIP_BUTTON = "Repeat Playlist Mode";
    private static final String EMPTY_PLAYLIST_MESSAGE = "Playlist empty";

    public PlaylistUI(SongManager model,
                      MusicPlayerManager musicPlayerManager,
                      DatabaseManager databaseManager,
                      Map<String, Boolean> tableColumnVisibilityMap) {
        super();
        m_model = model;
        m_musicPlayerManager = musicPlayerManager;
        m_databaseManager = databaseManager;
        m_contextMenu = new ContextMenu();
        m_dropDownMenu = new ComboBox<>();
        initTopMenu(createSelectPlaylistLabel(),
                    createDropDownMenu(),
                    createCreateNewPlaylistButton(),
                    createRemovePlaylistButton(),
                    createRenamePlaylistButton(),
                    createShufflePlaylistButton());
        initTableView(tableColumnVisibilityMap);
        UserInterfaceUtils.applyBlackBoarder(this);
        registerAsPlaylistObserver();
        this.setSpacing(0);
        HBox playbackOptions = createPlaylistPlaybackOptions();
        VBox.setVgrow(playbackOptions, Priority.NEVER);
        this.getChildren().add(playbackOptions);
    }

    /**
     * Register as observer to update any changes made
     */
    private void registerAsPlaylistObserver() {
        m_model.addPlaylistObserver(() -> {
            m_dropDownMenu.getItems().clear();
            m_dropDownMenu.getItems().addAll(m_model.getM_playlists());
            clearTable();
            updateTable();
        });

        m_model.addPlaylistSongObserver(() -> {
            clearTable();
            updateTable();
        });

        m_model.addLibraryObserver((fileActions) -> {
            clearTable();
            updateTable();
        });

        m_model.addFileObserver((fileActions) -> {
            m_model.refreshPlaylists();
            clearTable();
            updateTable();
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

        dropDownMenu.valueProperty().addListener((observable, oldValue, newValue) -> {
            m_model.setM_selectedPlaylist(newValue);
            m_model.notifyPlaylistSongsObservers();
        });

        dropDownMenu.setMinWidth(DROP_DOWN_MENU_MIN_WIDTH);
        dropDownMenu.setMaxWidth(DROP_DOWN_MENU_MAX_WIDTH);
        dropDownMenu.setPrefSize(DROP_DOWN_MENU_PREF_WIDTH, DROP_DOWN_MENU_PREF_HEIGHT);
        if (!options.isEmpty()) {
            Playlist selectedPlaylist = m_model.getM_selectedPlaylist();
            dropDownMenu.setValue((selectedPlaylist != null) ? selectedPlaylist : options.get(0));
        }

        return dropDownMenu;
    }

    /**
     * Function to create the playlist button.
     *
     * @return The create playlist button.
     */
    private Button createCreateNewPlaylistButton() {
        Button createNewPlaylistButton = buildButton(ADD_PLAYLIST_TOOL_TIP_MESSAGE, ADD_PLAYLIST_BUTTON_ICON_PATH);
        UserInterfaceUtils.setMouseOverImageChange(
                createNewPlaylistButton,
                ADD_PLAYLIST_BUTTON_HIGHLIGHT_ICON_PATH,
                ADD_PLAYLIST_BUTTON_ICON_PATH);


        createNewPlaylistButton.setOnMouseClicked((event) -> {
            String newPlaylistName = PromptUI.createNewPlaylist();
            if (m_model.playlistNameExist(newPlaylistName)) {
                PromptUI.customPromptError("Error", null, "Playlist with name \"" + newPlaylistName + "\" already exist!");
                return;
            }
            if (newPlaylistName != null) {
                Playlist newPlaylist = m_model.addAndCreatePlaylist(newPlaylistName);
                m_databaseManager.addPlaylist(newPlaylistName);
                m_model.notifyPlaylistObservers();
                m_dropDownMenu.getSelectionModel().select(newPlaylist);
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
        Button removePlaylistButton = buildButton(REMOVE_PLAYLIST_TOOLTIP_MESSAGE, REMOVE_PLAYLIST_BUTTON_ICON_PATH);
        UserInterfaceUtils.setMouseOverImageChange(
                removePlaylistButton,
                REMOVE_PLAYLIST_BUTTON__HIGHLIGHT_ICON_PATH,
                REMOVE_PLAYLIST_BUTTON_ICON_PATH);

        removePlaylistButton.setOnMouseClicked((event) -> {
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
                m_model.notifyPlaylistObservers();
                m_dropDownMenu.getSelectionModel().select(selectedDropDownIndex);
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
        Button editPlaylistButton = buildButton(RENAME_PLAYLIST_TOOL_TIP_MESSAGE, EDIT_PLAYLIST_BUTTON_ICON_PATH);
        UserInterfaceUtils.setMouseOverImageChange(
                editPlaylistButton,
                EDIT_PLAYLIST_BUTTON_HIGHLIGHT_ICON_PATH,
                EDIT_PLAYLIST_BUTTON_ICON_PATH);

        editPlaylistButton.setOnMouseClicked((event) -> {
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
                m_model.notifyPlaylistObservers();
                m_dropDownMenu.getSelectionModel().select(selectedDropDownIndex);
            }
        });

        return editPlaylistButton;
    }

    /**
     * Function to create the shuffle playlist button.
     *
     * @return The button that will control the shuffle playlist.
     */
    private Button createShufflePlaylistButton() {
        Button shufflePlaylistButton = buildButton(SHUFFLE_PLAYLIST_TOOL_TIP_MESSAGE, SHUFFLE_PLAYLIST_BUTTON_ICON_PATH);
        UserInterfaceUtils.setMouseOverImageChange(
                shufflePlaylistButton,
                SHUFFLE_PLAYLIST_BUTTON_HIGHLIGHT_ICON_PATH,
                SHUFFLE_PLAYLIST_BUTTON_ICON_PATH);

        shufflePlaylistButton.setOnMouseClicked((event) -> {
            Playlist selectedPlaylist = m_model.getM_selectedPlaylist();
            if (selectedPlaylist == null) {
                PromptUI.customPromptError("Error", null, "Please select a playlist from the drop down menu!");
                return;
            }
            selectedPlaylist.shuffleUnplayedSongs();
            m_model.notifyPlaylistSongsObservers();
            m_musicPlayerManager.notifyQueingObserver();
        });

        return shufflePlaylistButton;
    }

    /**
     * Function to build a button with a static tooltip message that is an image.
     *
     * @param toolTipMessage The tooltip message to display/
     * @param iconPath The path of the icon button
     * @return The button built
     */
    private Button buildButton(String toolTipMessage, String iconPath) {
        Button button = new Button();

        button.setTooltip(new Tooltip(toolTipMessage));
        button.setStyle("-fx-background-color: transparent");
        button.setGraphic(new ImageView(iconPath));

        return button;
    }

    /**
     * Function to create the play playlist button
     *
     * @return The play playlist button.
     */
    private Button createPlayPlaylistButton() {
        Button playPlaylistButton = UserInterfaceUtils.createIconButton(PLAY_PLAYLIST_ICON);
        playPlaylistButton.setOnMouseClicked((event) -> {
            if (m_model.getM_selectedPlaylist() != null) {
                m_musicPlayerManager.playPlaylist(m_model.getM_selectedPlaylist());
            }
        });

        UserInterfaceUtils.createMouseOverUIChange(playPlaylistButton, playPlaylistButton.getStyle());
        playPlaylistButton.setTooltip(new Tooltip(PLAY_PLAYLIST_TOOL_TIP_MESSAGE));

        playPlaylistButton.setScaleY(PLAYLIST_PLAYBACK_BUTTON_SCALE);
        playPlaylistButton.setScaleX(PLAYLIST_PLAYBACK_BUTTON_SCALE);
        playPlaylistButton.setPadding(new Insets(0));

        return playPlaylistButton;
    }

    /**
     * Function to create the repeat playlist button.
     *
     * @return The repeat playlist button.
     */
    private ToggleButton createPlaylistRepeatButton() {
        ToggleButton playlistRepeat = new ToggleButton();
        playlistRepeat.setStyle("-fx-background-color: transparent");
        playlistRepeat.setOnMouseClicked((event) -> {
            if (playlistRepeat.isSelected()){
                m_musicPlayerManager.setRepeat(true);
                playlistRepeat.setStyle("-fx-background-color: lightgray");
            } else {
                m_musicPlayerManager.setRepeat(false);
                playlistRepeat.setStyle("-fx-background-color: transparent");
            }
            UserInterfaceUtils.createMouseOverUIChange(playlistRepeat, playlistRepeat.getStyle());
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
        wrapper.setStyle("-fx-background-color: #F4F4F4;");
        return wrapper;
    }

    /**
     * Function to initialize the menu above the table for playlist.
     *
     * @param selectPlaylistLabel
     * @param dropDownMenu
     * @param addPlaylistButton
     * @param removePlaylistButton
     * @param shufflePlaylistButton
     * @param editPlaylistButton
     */
    private void initTopMenu(Label selectPlaylistLabel,
                             ComboBox<Playlist> dropDownMenu,
                             Button addPlaylistButton,
                             Button removePlaylistButton,
                             Button shufflePlaylistButton,
                             Button editPlaylistButton) {
        m_dropDownMenu = dropDownMenu;

        Label spacing = new Label("  ");
        HBox topMenu = new HBox();
        topMenu.getChildren().addAll(spacing, m_dropDownMenu, addPlaylistButton, removePlaylistButton, editPlaylistButton, shufflePlaylistButton);
        HBox.setHgrow(m_dropDownMenu, Priority.ALWAYS);
        HBox.setHgrow(addPlaylistButton, Priority.NEVER);
        HBox.setHgrow(removePlaylistButton, Priority.NEVER);
        HBox.setHgrow(shufflePlaylistButton, Priority.NEVER);

        VBox menuWrapper = new VBox();
        menuWrapper.getChildren().addAll(selectPlaylistLabel, topMenu);
        VBox.setVgrow(topMenu, Priority.ALWAYS);
        super.getChildren().add(menuWrapper);
    }

    /**
     * Function to initialize and build the table for the playlist.
     */
    private void initTableView(Map<String, Boolean> tableColumnVisibilityMap) {
        m_table = new TableView<>();
        setTableColumns(tableColumnVisibilityMap);
        setTableDragEvents();
        setupTableRowFactory();
        super.getChildren().add(m_table);
        StackPane.setMargin(m_table, TABLE_VIEW_MARGIN);
        updateTable();
        VBox.setVgrow(m_table, Priority.ALWAYS);
    }

    /**
     * Function to set the columns for the playlist table.
     */
    private void setTableColumns(Map<String, Boolean> tableColumnVisibilityMap) {
        TableColumn<Song, String> filePathCol = new TableColumn<>(FILE_PATH_COLUMN_HEADING);
        filePathCol.setId(FILE_PATH_COLUMN_ID);
        filePathCol.setMinWidth(FILE_COLUMN_MIN_WIDTH);

        TableColumn<Song, String> fileNameCol = new TableColumn<>(FILE_NAME_COLUMN_HEADING);
        fileNameCol.setId(FILE_NAME_COLUMN_ID);
        fileNameCol.setMinWidth(FILE_COLUMN_MIN_WIDTH);

        TableColumn<Song, String> titleCol = new TableColumn<>(TITLE_COLUMN_HEADING);
        titleCol.setId(TITLE_COLUMN_ID);
        titleCol.setMinWidth(COLUMN_MIN_WIDTH);

        TableColumn<Song, String> artistCol = new TableColumn<>(ARTIST_COLUMN_HEADING);
        artistCol.setId(ARTIST_COLUMN_ID);
        artistCol.setMinWidth(COLUMN_MIN_WIDTH);

        TableColumn<Song, String> albumCol = new TableColumn<>(ALBUM_COLUMN_HEADING);
        albumCol.setId(ALBUM_COLUMN_ID);
        albumCol.setMinWidth(COLUMN_MIN_WIDTH);

        TableColumn<Song, String> genreCol = new TableColumn<>(GENRE_COLUMN_HEADING);
        genreCol.setId(GENRE_COLUMN_ID);
        genreCol.setMinWidth(COLUMN_MIN_WIDTH);

        TableColumn<Song, Integer> ratingCol = new TableColumn<>(RATING_COLUMN_HEADING);
        ratingCol.setId(RATING_COLUMN_ID);
        ratingCol.setMinWidth(RATING_COLUMN_MIN_WIDTH);

        TableColumn<Song, String> lengthCol = new TableColumn<>(LENGTH_COLUMN_HEADING);
        lengthCol.setId(LENGTH_COLUMN_ID);
        lengthCol.setMinWidth(LENGTH_COLUMN_MIN_WIDTH);

        m_table.getColumns().add(filePathCol);
        m_table.getColumns().add(fileNameCol);
        m_table.getColumns().add(titleCol);
        m_table.getColumns().add(artistCol);
        m_table.getColumns().add(albumCol);
        m_table.getColumns().add(genreCol);
        m_table.getColumns().add(ratingCol);
        m_table.getColumns().add(lengthCol);
        m_table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        setTableColumnAttributes(filePathCol, fileNameCol, titleCol, artistCol, albumCol, genreCol, ratingCol, lengthCol);
        if (tableColumnVisibilityMap != null && !tableColumnVisibilityMap.isEmpty()) {
            setVisibleColumnsInTable(tableColumnVisibilityMap);
        } else {
            setDefaultVisibleColumnsInTable(filePathCol, fileNameCol, titleCol, artistCol, albumCol, genreCol, ratingCol, lengthCol);
        }
    }

    /**
     * Function to clear the playlist table.
     */
    private void clearTable() {
        m_table.getItems().clear();
    }

    /**
     * Function to update the table based on new values from the model.
     */
    private void updateTable() {
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

        m_table.refresh();
    }

    private void setVisibleColumnsInTable(Map<String, Boolean> map) {
        for (TableColumn column : m_table.getColumns()) {
            column.setVisible( map.get(column.getId()) );
        }
    }

    /**
     * Function to set which columns will be shown in the playtlist UI table by default.
     *
     * @param filePathCol
     * @param fileNameCol
     * @param titleCol
     * @param artistCol
     * @param albumCol
     * @param genreCol
     * @param ratingCol
     * @param lengthCol
     */
    private void setDefaultVisibleColumnsInTable(TableColumn<Song, String> filePathCol,
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

    /**
     * Function to set the column attribute for the playlist table.
     *
     * @param filePathCol
     * @param fileNameCol
     * @param titleCol
     * @param artistCol
     * @param albumCol
     * @param genreCol
     * @param ratingCol
     * @param lengthCol
     *
     */
    private void setTableColumnAttributes(TableColumn<Song, String> filePathCol,
                                          TableColumn<Song, String> fileNameCol,
                                          TableColumn<Song, String> titleCol,
                                          TableColumn<Song, String> artistCol,
                                          TableColumn<Song, String> albumCol,
                                          TableColumn<Song, String> genreCol,
                                          TableColumn<Song, Integer> ratingCol,
                                          TableColumn<Song, String> lengthCol) {
        filePathCol.setCellValueFactory((param) -> new ReadOnlyObjectWrapper<>(param.getValue().getFile().getParentFile().getName()));
        filePathCol.setSortable(false);

        fileNameCol.setCellValueFactory((param) -> new ReadOnlyObjectWrapper<>(param.getValue().getFileName()));
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

        lengthCol.setCellValueFactory((param) -> {
            Duration lengthOfSong = new Duration(
                    param.getValue().getM_length() * MusicPlayerConstants.NUMBER_OF_MILISECONDS_IN_SECOND);
            return new ReadOnlyObjectWrapper<>(UserInterfaceUtils.convertDurationToTimeString(lengthOfSong));
        });
        lengthCol.setSortable(false);
    }

    /**
     * Function to set the drag events for the table.
     */
    private void setTableDragEvents() {
        m_table.setOnDragOver((dragEvent) -> {
            // For Debugging
            System.out.println("Drag over on playlist");
            if (m_model.getM_selectedPlaylist() != null) {
                dragEvent.acceptTransferModes(TransferMode.MOVE);
            }
            dragEvent.consume();
        });

        m_table.setOnDragDropped((dragEvent) -> {
            for (Item itemToMove : m_model.getM_itemsToMove()) {
                m_model.addItemToPlaylist(itemToMove, m_model.getM_selectedPlaylist());
                m_musicPlayerManager.notifyQueingObserver();
            }
            dragEvent.consume();
        });

        m_table.setOnDragDone((dragEvent) -> {
            m_model.setM_itemsToMove(null);
            dragEvent.consume();
        });
    }

    /**
     * Function to set up the row factory for the table
     */
    private void setupTableRowFactory() {
        m_table.setRowFactory(new Callback<TableView<Song>, TableRow<Song>>() {
            @Override
            public TableRow<Song> call(TableView<Song> param) {
                // Idea taken from
                // http://stackoverflow.com/questions/20350099/programmatically-change-the-tableview-row-appearance
                TableRow<Song> row = new TableRow<Song>() {
                    @Override
                    protected void updateItem(Song song, boolean empty){
                        super.updateItem(song, empty);
                        if (!empty){
                            setTableRowStyle(this);
                        }
                    }
                };

                row.setOnMouseClicked((event) -> {
                    int selectedSongIndex = row.getIndex();
                    if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                        Playlist selectedPlaylist = m_model.getM_selectedPlaylist();
                        if (selectedPlaylist.isValid(selectedSongIndex)) {
                            selectedPlaylist.setSongToPlay(selectedSongIndex);
                            m_musicPlayerManager.playPlaylist(selectedPlaylist);
                        }
                    } else if (event.getButton() == MouseButton.PRIMARY) {
                        m_contextMenu.hide();
                    } else if (event.getButton() == MouseButton.SECONDARY) {
                        m_contextMenu.hide();
                        m_contextMenu = generateContextMenu(selectedSongIndex);
                        m_contextMenu.show(m_table, event.getScreenX(), event.getScreenY());
                    }
                });

                setTableRowStyle(row);

                m_musicPlayerManager.registerNewSongObserver(() -> setTableRowStyle(row));

                return row;
            }
        });
    }

    /**
     * Helper function to style a row in the playlist. If the song is playing on the playlist then it should
     * be highlighted.
     *
     * @param row The row to style
     */
    private void setTableRowStyle(TableRow<Song> row) {
        boolean sameSong = m_musicPlayerManager.getCurrentPlaylistSong() == row.getItem();
        boolean sameIndexLocation = m_musicPlayerManager.getCurrentIndexOfPlaylistSong() == row.getIndex();
        if (row.getItem() != null && sameSong && sameIndexLocation){
            row.setStyle(UserInterfaceUtils.SELECTED_BACKGROUND_COLOUR);

            // Make it persist
            UserInterfaceUtils.createMouseOverUIChange(row, row.getStyle());
            return;
        }

        row.setStyle(null);
        UserInterfaceUtils.createMouseOverUIChange(row, null);
    }

    /**
     * Function to generate a context menu for the song specifed by the index of where it is in the playlist.
     *
     * @param selectedSongIndex The index of the song in the playlist.
     * @return A context menu for that will work on the song at the index
     */
    private ContextMenu generateContextMenu(int selectedSongIndex) {
        List<Song> selectedSongs = m_table.getSelectionModel().getSelectedItems();
        return ContextMenuBuilder.buildPlaylistContextMenu(m_model,
                                                           m_musicPlayerManager,
                                                           m_databaseManager,
                                                           selectedSongIndex,
                                                           selectedSongs);
    }

    public Map<String, Boolean> getColumnsVisibility() {
        Map<String, Boolean> map = new HashMap<>();
        for (TableColumn<Song, ?> column : m_table.getColumns()) {
            map.put(column.getId(), column.isVisible());
        }
        return map;
    }
}
