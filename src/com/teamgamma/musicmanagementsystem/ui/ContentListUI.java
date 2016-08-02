package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerConstants;
import com.teamgamma.musicmanagementsystem.util.ContextMenuBuilder;
import com.teamgamma.musicmanagementsystem.model.*;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;

import com.teamgamma.musicmanagementsystem.util.UserInterfaceUtils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.*;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UI class for list of songs in center of application
 */
public class ContentListUI extends StackPane {
    private SongManager m_model;
    private MusicPlayerManager m_musicPlayerManager;
    private DatabaseManager m_databaseManager;
    private ContextMenu m_contextMenu;
    private TableView<Song> m_table;

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
    private static final int FILE_COLUMN_MIN_WIDTH = 80;
    private static final int COLUMN_MIN_WIDTH = 60;
    private static final int RATING_COLUMN_MIN_WIDTH = 20;
    private static final int LENGTH_COLUMN_MIN_WIDTH = 50;

    /**
     * Constructor
     *
     * @param model
     * @param musicPlayerManager
     * @param databaseManager
     */
    public ContentListUI(SongManager model,
                         MusicPlayerManager musicPlayerManager,
                         DatabaseManager databaseManager,
                         Map<String, Boolean> tableColumnVisibilityMap) {
        super();
        m_model = model;
        m_musicPlayerManager = musicPlayerManager;
        m_databaseManager = databaseManager;
        m_contextMenu = new ContextMenu();
        initTableView(tableColumnVisibilityMap);
        UserInterfaceUtils.applyBlackBoarder(this);
        registerAsCenterFolderObserver();
    }

    /**
     * Register as a observer to changes for the folder selected to be displayed here
     */
    private void registerAsCenterFolderObserver() {
        m_model.addLibraryObserver((fileActions) -> {
            clearTable();
            updateTable();
        });

        m_model.addCenterFolderObserver((fileActions) -> {
            clearTable();
            updateTable();
        });

        m_model.addFileObserver((fileActions) -> {
            clearTable();
            updateTable();
        });
    }

    /**
     * Initialize table view
     */
    private void initTableView(Map<String, Boolean> tableColumnVisibilityMap) {
        m_table = new TableView<>();
        m_table.setTableMenuButtonVisible(true);
        setTableColumns(tableColumnVisibilityMap);
        setTableRowMouseEvents();
        setDragEvents();
        super.getChildren().add(m_table);
        updateTable();
    }

    /**
     * Set the various table column labels
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

        setTableColumnAttributes(filePathCol, fileNameCol, titleCol, artistCol, albumCol, genreCol, lengthCol, ratingCol);
        if (tableColumnVisibilityMap != null && !tableColumnVisibilityMap.isEmpty()) {
            setColumnsVisibility(tableColumnVisibilityMap);
        } else {
            setDefaultVisibleColumns(filePathCol, fileNameCol, titleCol, artistCol, albumCol, genreCol, lengthCol, ratingCol);
        }

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

    /**
     * Clear the song explorer table
     */
    private void clearTable() {
        System.out.println("Clearing song explorer table...");
        m_table.getItems().clear();
    }

    /**
     * Update the song explorer table
     */
    private void updateTable() {
        System.out.println("Updating song explorer table...");
        if (m_model.getM_selectedCenterFolder() != null) {
            List<Song> songs = m_model.getCenterPanelSongs();
            if (songs.isEmpty()) {
                m_table.setPlaceholder(new Label("No songs in folder"));
            } else {
                m_table.setItems(FXCollections.observableArrayList(songs));
            }
        } else {
            m_table.setPlaceholder(new Label("Choose a folder to view its contents"));
        }
    }

    private void setColumnsVisibility(Map<String, Boolean> map) {
        for (TableColumn column : m_table.getColumns()) {
            column.setVisible( map.get(column.getId()) );
        }
    }

    /**
     * Allows user to show or hide different columns
     *
     * @param filePathCol full path of the song file
     * @param fileNameCol name of the song file
     * @param titleCol title of the song
     * @param artistCol artist of the song
     * @param albumCol album of the song
     * @param genreCol genre of the song
     * @param lengthCol length of the song
     * @param ratingCol rating of the song
     */
    private void setDefaultVisibleColumns(TableColumn<Song, String> filePathCol,
                                          TableColumn<Song, String> fileNameCol,
                                          TableColumn<Song, String> titleCol,
                                          TableColumn<Song, String> artistCol,
                                          TableColumn<Song, String> albumCol,
                                          TableColumn<Song, String> genreCol,
                                          TableColumn<Song, String> lengthCol,
                                          TableColumn<Song, Integer> ratingCol) {
        //default columns
        fileNameCol.setVisible(true);
        artistCol.setVisible(true);

        filePathCol.setVisible(false);
        titleCol.setVisible(false);
        albumCol.setVisible(false);
        genreCol.setVisible(false);
        lengthCol.setVisible(false);
        ratingCol.setVisible(false);
    }

    /**
     * Create various columns for the table
     *
     * @param filePathCol full path of the song file
     * @param fileNameCol name of the song file
     * @param titleCol title of the song
     * @param artistCol artist of the song
     * @param albumCol album of the song
     * @param genreCol genre of the song
     * @param lengthCol length of the song
     * @param ratingCol rating of the song
     */
    private void setTableColumnAttributes(TableColumn<Song, String> filePathCol,
                                          TableColumn<Song, String> fileNameCol,
                                          TableColumn<Song, String> titleCol,
                                          TableColumn<Song, String> artistCol,
                                          TableColumn<Song, String> albumCol,
                                          TableColumn<Song, String> genreCol,
                                          TableColumn<Song, String> lengthCol,
                                          TableColumn<Song, Integer> ratingCol) {
        filePathCol.setCellValueFactory((param) -> new ReadOnlyObjectWrapper<>(param.getValue().getFile().getParentFile().getName()));

        fileNameCol.setCellValueFactory((param) -> new ReadOnlyObjectWrapper<>(param.getValue().getFileName()));

        titleCol.setCellValueFactory(new PropertyValueFactory<>("m_title"));
        titleCol.setCellFactory(TextFieldTableCell.forTableColumn());
        titleCol.setOnEditCommit((t) -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setTitle(t.getNewValue()));

        artistCol.setCellValueFactory(new PropertyValueFactory<>("m_artist"));
        artistCol.setCellFactory(TextFieldTableCell.forTableColumn());
        artistCol.setOnEditCommit((t) -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setArtist(t.getNewValue()));

        albumCol.setCellValueFactory(new PropertyValueFactory<>("m_album"));
        albumCol.setCellFactory(TextFieldTableCell.forTableColumn());
        albumCol.setOnEditCommit((t) -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setAlbum(t.getNewValue()));

        genreCol.setCellValueFactory(new PropertyValueFactory<>("m_genre"));
        genreCol.setCellFactory(TextFieldTableCell.forTableColumn());
        genreCol.setOnEditCommit((t) -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setGenre(t.getNewValue()));

        lengthCol.setCellValueFactory((param) -> {
            Duration lengthOfSong = new Duration(
                    param.getValue().getM_length() * MusicPlayerConstants.NUMBER_OF_MILISECONDS_IN_SECOND);
            return new ReadOnlyObjectWrapper<>(UserInterfaceUtils.convertDurationToTimeString(lengthOfSong));
        });

        ratingCol.setCellValueFactory(new PropertyValueFactory<>("m_rating"));
        ratingCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        ratingCol.setOnEditCommit((t) -> {
            try {
                t.getTableView().getItems().get(t.getTablePosition().getRow()).setRating(t.getNewValue());
            } catch (IllegalArgumentException ex) {
                PromptUI.customPromptError("Error", "", "Rating should be in range 0 to 5");
                m_model.notifyCenterFolderObservers();
            }
        });
    }

    /**
     * Apply all of the different mouse actions on a selected song
     */
    private void setTableRowMouseEvents() {
        m_table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        m_table.setRowFactory((param) -> {
            TableRow<Song> row = new TableRow<>();
            row.setOnMouseClicked((event) -> {
                Song selectedSong = row.getItem();
                if (selectedSong != null && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    m_musicPlayerManager.playSongRightNow(selectedSong);
                } else if (event.getButton() == MouseButton.PRIMARY) {
                    m_contextMenu.hide();
                } else if (event.getButton() == MouseButton.SECONDARY) {
                    m_contextMenu.hide();
                    m_contextMenu = generateContextMenu(row.getItem());
                    m_contextMenu.show(m_table, event.getScreenX(), event.getScreenY());
                }
            });

            UserInterfaceUtils.createMouseOverUIChange(row, row.getStyle());

            row.setOnDragDetected((mouseEvent) -> {
                Song selectedItem = row.getItem();

                System.out.println("Drag detected on " + selectedItem);

                if (selectedItem != null) {
                    //update model
                    List<Item> selectedItems = new ArrayList<>();
                    selectedItems.addAll(m_table.getSelectionModel().getSelectedItems());
                    m_model.setM_itemsToMove(selectedItems);

                    //update drag board
                    Dragboard dragBoard = ContentListUI.this.startDragAndDrop(TransferMode.MOVE);
                    dragBoard.setDragView(row.snapshot(null, null));
                    ClipboardContent content = new ClipboardContent();
                    content.put(DataFormat.PLAIN_TEXT, selectedItem.getFile().getAbsolutePath());
                    dragBoard.setContent(content);
                }

                mouseEvent.consume();
            });

            return row;
        });

        // Set context menu on tableview to show PASTE option when no songs exist
        m_table.setOnMouseClicked((event) -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                if (m_model.getM_selectedCenterFolder() != null && m_table.getSelectionModel().isEmpty()) {
                    m_contextMenu.hide();
                    m_contextMenu = generateContextMenu(null);
                    m_contextMenu.show(m_table, event.getScreenX(), event.getScreenY());
                }
            }
        });
    }

    /**
     * Set drag events on the TableView
     */
    private void setDragEvents() {
        m_table.setOnDragOver((dragEvent) -> {
            System.out.println("Drag over on center");
            dragEvent.acceptTransferModes(TransferMode.MOVE);
            dragEvent.consume();
        });

        m_table.setOnDragDropped((dragEvent) -> {
            System.out.println("Drag dropped on center");

            //move to the selected center folder
            if (m_model.getM_selectedCenterFolder() != null) {
                UserInterfaceUtils.moveFileAction(m_model, m_model.getM_selectedCenterFolder());
            }

            dragEvent.consume();
        });

        m_table.setOnDragDone((dragEvent) -> {
            System.out.println("Drag done");
            m_model.setM_itemsToMove(null);
            dragEvent.consume();
        });
    }

    /**
     * Generate all the different options when a song is right clicked
     *
     * @param selectedSong song selected
     * @return context menu generated
     */
    private ContextMenu generateContextMenu(Song selectedSong) {
        List<Song> selectedSongs = m_table.getSelectionModel().getSelectedItems();
        return ContextMenuBuilder.buildCenterPanelContextMenu(m_model, m_musicPlayerManager, m_databaseManager, selectedSong, selectedSongs);
    }

    public Map<String, Boolean> getColumnsVisibility() {
        Map<String, Boolean> map = new HashMap<>();

        List<TableColumn<Song, ?>> columns = m_table.getColumns();
        for (TableColumn<Song, ?> column : columns) {
            map.put(column.getId(), column.isVisible());
        }

        return map;
    }
}
