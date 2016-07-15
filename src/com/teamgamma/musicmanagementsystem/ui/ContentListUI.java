package com.teamgamma.musicmanagementsystem.ui;

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
import javafx.util.converter.IntegerStringConverter;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;

/**
 * UI class for list of songs in center of application
 */
public class ContentListUI extends StackPane {
    private SongManager m_model;
    private MusicPlayerManager m_musicPlayerManager;
    private DatabaseManager m_databaseManager;
    private ContextMenu m_contextMenu;
    //private ContextMenu m_playbackContextMenu;
    private TableView<Song> m_table;

    //constants
    private static final int FILE_COLUMN_MIN_WIDTH = 80;
    private static final int COLUMN_MIN_WIDTH = 60;
    private static final int RATING_COLUMN_MIN_WIDTH = 20;
    //private String style = "";

    /**
     * Constructor
     *
     * @param model
     * @param musicPlayerManager
     * @param databaseManager
     */
    public ContentListUI(SongManager model, MusicPlayerManager musicPlayerManager, DatabaseManager databaseManager) {
        super();
        m_model = model;
        m_musicPlayerManager = musicPlayerManager;
        m_databaseManager = databaseManager;
        m_contextMenu = new ContextMenu();
        initTableView();
        UserInterfaceUtils.applyBlackBoarder(this);
        registerAsCenterFolderObserver();
    }

    /**
     * Register as a observer to changes for the folder selected to be displayed here
     */
    private void registerAsCenterFolderObserver() {
        m_model.addLibraryObserver((action, file) -> {
            clearTable();
            updateTable();
        });

        m_model.addCenterFolderObserver((action, file) -> {
            clearTable();
            updateTable();
        });

        m_model.addFileObserver((action, file) -> {
            clearTable();
            updateTable();
        });
    }

    /**
     * Intitial table view
     */
    private void initTableView() {
        m_table = new TableView<>();
        setTableColumns();
        setTableRowMouseEvents();
        super.getChildren().add(m_table);
        updateTable();
    }

    /**
     * Set the various table column labels
     */
    private void setTableColumns() {
        TableColumn<Song, String> filePathCol = new TableColumn<>("File Path");
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
        setTableColumnAttributes(filePathCol, fileNameCol, titleCol, artistCol, albumCol, genreCol, ratingCol);
        showOrHideTableColumns(filePathCol, fileNameCol, titleCol, artistCol, albumCol, genreCol, ratingCol);
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
    /**
     * Allows user to show or hide different columns
     *
     * @param filePathCol full path of the song file
     * @param fileNameCol name of the song file
     * @param titleCol title of the song
     * @param artistCol artist of the song
     * @param albumCol album of the song
     * @param genreCol genre of the song
     * @param ratingCol rating of the song
     */
    private void showOrHideTableColumns(TableColumn<Song, String> filePathCol,
                                        TableColumn<Song, String> fileNameCol,
                                        TableColumn<Song, String> titleCol,
                                        TableColumn<Song, String> artistCol,
                                        TableColumn<Song, String> albumCol,
                                        TableColumn<Song, String> genreCol,
                                        TableColumn<Song, Integer> ratingCol) {
        m_table.setTableMenuButtonVisible(true);

        //default columns
        fileNameCol.setVisible(true);
        artistCol.setVisible(true);

        filePathCol.setVisible(false);
        titleCol.setVisible(false);
        albumCol.setVisible(false);
        genreCol.setVisible(false);
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
     * @param ratingCol rating of the song
     */
    private void setTableColumnAttributes(TableColumn<Song, String> filePathCol,
                                          TableColumn<Song, String> fileNameCol,
                                          TableColumn<Song, String> titleCol,
                                          TableColumn<Song, String> artistCol,
                                          TableColumn<Song, String> albumCol,
                                          TableColumn<Song, String> genreCol,
                                          TableColumn<Song, Integer> ratingCol) {
        filePathCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getFile().getAbsolutePath()));

        fileNameCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getFileName()));

        titleCol.setCellValueFactory(new PropertyValueFactory<>("m_title"));
        titleCol.setCellFactory(TextFieldTableCell.forTableColumn());
        titleCol.setOnEditCommit(t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setTitle(t.getNewValue()));

        artistCol.setCellValueFactory(new PropertyValueFactory<>("m_artist"));
        artistCol.setCellFactory(TextFieldTableCell.forTableColumn());
        artistCol.setOnEditCommit(t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setArtist(t.getNewValue()));

        albumCol.setCellValueFactory(new PropertyValueFactory<>("m_album"));
        albumCol.setCellFactory(TextFieldTableCell.forTableColumn());
        albumCol.setOnEditCommit(t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setAlbum(t.getNewValue()));

        genreCol.setCellValueFactory(new PropertyValueFactory<>("m_genre"));
        genreCol.setCellFactory(TextFieldTableCell.forTableColumn());
        genreCol.setOnEditCommit(t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setGenre(t.getNewValue()));

        ratingCol.setCellValueFactory(new PropertyValueFactory<>("m_rating"));
        ratingCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        ratingCol.setOnEditCommit(t -> {
            try {
                t.getTableView().getItems().get(t.getTablePosition().getRow()).setRating(t.getNewValue());
            } catch (IllegalArgumentException ex) {
                PromptUI.customPromptError("Error", "", "Rating should be in range 0 to 5");
                m_model.notifyCenterFolderObservers();
            }
        });

        m_table.getColumns().add(filePathCol);
        m_table.getColumns().add(fileNameCol);
        m_table.getColumns().add(titleCol);
        m_table.getColumns().add(artistCol);
        m_table.getColumns().add(albumCol);
        m_table.getColumns().add(genreCol);
        m_table.getColumns().add(ratingCol);
        m_table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    /**
     * Apply all of the different mouse actions on a selected song
     */
    private void setTableRowMouseEvents() {
        m_table.setRowFactory(param -> {
            TableRow<Song> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                Song selectedSong = row.getItem();
                if (selectedSong != null && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    m_musicPlayerManager.playSongRightNow(selectedSong);
                } else if (event.getButton() == MouseButton.PRIMARY) {
                    m_contextMenu.hide();
                    /*if (m_playbackContextMenu != null) {
                        m_playbackContextMenu.hide();
                    }*/
                } else if (event.getButton() == MouseButton.SECONDARY) {
                    m_contextMenu.hide();
                    m_contextMenu = generateContextMenu(row.getItem());
                    m_contextMenu.show(m_table, event.getScreenX(), event.getScreenY());
                }
                /*System.out.println("Selected song is " + selectedSong);
                if (selectedSong != null && event.isControlDown() && event.getButton() == MouseButton.PRIMARY) {
                    System.out.println("The condition for the playback Conext menu is true");
                    m_playbackContextMenu = MusicPlayerHistoryUI.createSubmenu(m_musicPlayerManager, selectedSong);
                    m_playbackContextMenu.show(m_table, event.getScreenX(), event.getScreenY());
                }*/
            });

            UserInterfaceUtils.createMouseOverUIChange(row, row.getStyle());

            row.setOnDragDetected(mouseEvent -> {
                Song selectedItem = row.getItem();

                System.out.println("Drag detected on " + selectedItem);

                //update model
                m_model.setM_itemToMove(selectedItem);

                //update drag board
                Dragboard dragBoard = ContentListUI.this.startDragAndDrop(TransferMode.MOVE);
                dragBoard.setDragView(row.snapshot(null, null));
                ClipboardContent content = new ClipboardContent();
                content.put(DataFormat.PLAIN_TEXT, selectedItem.getFile().getAbsolutePath());
                dragBoard.setContent(content);

                mouseEvent.consume();
            });

            row.setOnDragOver(dragEvent -> {
                System.out.println("Drag over on center");
                dragEvent.acceptTransferModes(TransferMode.MOVE);
                dragEvent.consume();
            });

            row.setOnDragDropped(dragEvent -> {
                System.out.println("Drag dropped on center");

                //move to the selected center folder
                File fileToMove = m_model.getFileToMove();
                File destination = m_model.getM_selectedCenterFolder();
                try {
                    m_model.moveFile(fileToMove, destination);
                } catch (FileAlreadyExistsException ex) {
                    PromptUI.customPromptError("Error", null, "The following file or folder already exist!\n" + ex.getMessage());
                } catch (AccessDeniedException ex) {
                    PromptUI.customPromptError("Error", null, "AccessDeniedException: \n" + ex.getMessage());
                    ex.printStackTrace();
                } catch (IOException ex) {
                    PromptUI.customPromptError("Error", null, "IOException: \n" + ex.getMessage());
                    ex.printStackTrace();
                }

                dragEvent.consume();
            });

            row.setOnDragDone(dragEvent -> {
                System.out.println("Drag done");
                m_model.setM_itemToMove(null);
                dragEvent.consume();
            });

            return row;
        });
    }

    /**
     * Generate all the different options when a song is right clicked
     *
     * @param selectedSong song selected
     * @return context menu generated
     */
    private ContextMenu generateContextMenu(Song selectedSong) {
        return ContextMenuBuilder.buildCenterPanelContextMenu(m_model, m_musicPlayerManager, m_databaseManager, selectedSong);
    }

}
