package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.model.Song;
import com.teamgamma.musicmanagementsystem.model.SongManager;
import com.teamgamma.musicmanagementsystem.model.SongManagerObserver;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;

import javafx.collections.FXCollections;
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
import java.util.List;

import javafx.event.EventHandler;
import javafx.util.Callback;

/**
 * UI class for list of songs in center of application
 */
public class ContentListUI extends StackPane {
    private SongManager m_model;
    private MusicPlayerManager m_musicPlayerManager;
    private TableView<Song> m_table;

    public ContentListUI(SongManager model, MusicPlayerManager musicPlayerManager) {
        super();

        m_model = model;
        m_musicPlayerManager = musicPlayerManager;

        updateList();

        m_table = new TableView<>();

        setCssStyle();

        registerAsCenterFolderObserver();
    }

    /**
     * Register as a observer to changes for the folder selected to be displayed here
     */
    private void registerAsCenterFolderObserver() {
        m_model.addObserver(new SongManagerObserver() {
            @Override
            public void librariesChanged() {
                clearList();
                updateList();
            }

            @Override
            public void centerFolderChanged() {
                clearList();
                updateList();
            }

            @Override
            public void rightFolderChanged() {
                /* Do nothing */
            }

            @Override
            public void songChanged() {
                /* Do nothing */
            }

            @Override
            public void fileChanged() {
                clearList();
                //setEmptyText();
                updateList();
            }

            @Override
            public void leftPanelOptionsChanged() {
                /* Do nothing */
            }
        });
    }

    private void setEmptyText(TableColumn fileNameCol, TableColumn titleCol, TableColumn artistCol, TableColumn albumCol, TableColumn genreCol, TableColumn ratingCol) {
        m_table.getColumns().addAll(fileNameCol, titleCol, artistCol, albumCol, genreCol, ratingCol);
        m_table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        m_table.setPlaceholder(new Label("Choose a folder to view its contents"));
        this.getChildren().add(m_table);

    }

    private void clearList() {
        System.out.println("Clearing list...");
        m_table.getItems().clear();
        this.getChildren().clear();
    }

    private void updateList() {
        m_table = new TableView<>();
        TableColumn<Song, File> fileNameCol = new TableColumn<>("File Name");
        fileNameCol.setMinWidth(80);
        TableColumn<Song, String> titleCol = new TableColumn<>("Title");
        titleCol.setMinWidth(60);
        TableColumn<Song, String> artistCol = new TableColumn<>("Artist");
        artistCol.setMinWidth(60);
        TableColumn<Song, String> albumCol = new TableColumn<>("Album");
        albumCol.setMinWidth(60);
        TableColumn<Song, String> genreCol = new TableColumn<>("Genre");
        genreCol.setMinWidth(60);
        TableColumn<Song, String> ratingCol = new TableColumn<>("Rating");
        ratingCol.setMinWidth(20);

        if (m_model.getM_selectedCenterFolder() == null) {
            setEmptyText(fileNameCol, titleCol, artistCol, albumCol, genreCol, ratingCol);
        } else {
            System.out.println("Updating m_table...");
            m_table = new TableView<>();
            m_table.setEditable(true);

            setTableRowDragEvents();
            setTableColumnAttributes(fileNameCol, titleCol, artistCol, albumCol, genreCol, ratingCol);

            List<Song> songs = m_model.getCenterPanelSongs();
            if (songs.isEmpty()){
                m_table.setPlaceholder(new Label("No songs in folder"));
            } else {
                m_table.setItems(FXCollections.observableArrayList(songs));

                m_table.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getClickCount() == 2) {
                            System.out.println(m_table.getSelectionModel().getSelectedItem());
                            m_musicPlayerManager.playSongRightNow(m_table.getSelectionModel().getSelectedItem());
                        }
                        if (event.getButton() == MouseButton.SECONDARY) {
                            Song selectedSong = m_table.getSelectionModel().getSelectedItem();
                            if (selectedSong != null) {
                                m_musicPlayerManager.placeSongOnBackOfPlaybackQueue(selectedSong);
                            }
                        }
                    }
                });
            }
            this.getChildren().add(m_table);

            // Scrolls through list
            ScrollPane scrollpane = new ScrollPane();
            scrollpane.setFitToWidth(true);
            scrollpane.setFitToHeight(true);
            scrollpane.setPrefSize(500, 500);
            scrollpane.setContent(m_table);
        }
    }

    private void setTableColumnAttributes(TableColumn<Song, File> fileNameCol,
                                          TableColumn<Song, String> titleCol,
                                          TableColumn<Song, String> artistCol,
                                          TableColumn<Song, String> albumCol,
                                          TableColumn<Song, String> genreCol,
                                          TableColumn<Song, String> ratingCol) {
        fileNameCol.setCellValueFactory(new PropertyValueFactory<>("m_fileName"));

        titleCol.setCellValueFactory(new PropertyValueFactory<>("m_title"));
        titleCol.setCellFactory(TextFieldTableCell.forTableColumn());
        titleCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Song, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Song, String> t) {
                        t.getTableView().getItems().get(t.getTablePosition().getRow()).setTitle(t.getNewValue());
                    }
                }
        );

        artistCol.setCellValueFactory(new PropertyValueFactory<>("m_artist"));
        artistCol.setCellFactory(TextFieldTableCell.forTableColumn());
        artistCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Song, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Song, String> t) {
                        t.getTableView().getItems().get(t.getTablePosition().getRow()).setArtist(t.getNewValue());
                    }
                }
        );

        albumCol.setCellValueFactory(new PropertyValueFactory<>("m_album"));
        albumCol.setCellFactory(TextFieldTableCell.forTableColumn());
        albumCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Song, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Song, String> t) {
                        t.getTableView().getItems().get(t.getTablePosition().getRow()).setAlbum(t.getNewValue());
                    }
                }
        );

        genreCol.setCellValueFactory(new PropertyValueFactory<>("m_genre"));
        genreCol.setCellFactory(TextFieldTableCell.forTableColumn());
        genreCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Song, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Song, String> t) {
                        t.getTableView().getItems().get(t.getTablePosition().getRow()).setGenre(t.getNewValue());
                    }
                }
        );

        ratingCol.setCellValueFactory(new PropertyValueFactory<>("m_rating"));
        ratingCol.setCellFactory(TextFieldTableCell.forTableColumn());
        ratingCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Song, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Song, String> t) {
                        try {
                            t.getTableView().getItems().get(t.getTablePosition().getRow()).setRating(Integer.parseInt(t.getNewValue()));
                        } catch (IllegalArgumentException ex) {
                            PromptUI.customPromptError("Error", "", "Rating should be in range 0 to 5");
                            m_model.notifyCenterFolderObservers();
                        }
                    }
                }
        );

        m_table.getColumns().add(fileNameCol);
        m_table.getColumns().add(titleCol);
        m_table.getColumns().add(artistCol);
        m_table.getColumns().add(albumCol);
        m_table.getColumns().add(genreCol);
        m_table.getColumns().add(ratingCol);

        m_table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setTableRowDragEvents() {
        m_table.setRowFactory(new Callback<TableView<Song>, TableRow<Song>>() {
            @Override
            public TableRow<Song> call(TableView<Song> param) {
                TableRow<Song> row = new TableRow<>();

                row.setOnDragDetected(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        Song selectedItem = m_table.getSelectionModel().getSelectedItem();

                        System.out.println("Drag detected on " + selectedItem.getM_file());

                        //update model
                        m_model.setM_fileToMove(selectedItem.getM_file());

                        //update drag board
                        Dragboard dragBoard = startDragAndDrop(TransferMode.MOVE);
                        dragBoard.setDragView(row.snapshot(null, null));
                        ClipboardContent content = new ClipboardContent();
                        content.put(DataFormat.PLAIN_TEXT, selectedItem.getM_file().getAbsolutePath());
                        dragBoard.setContent(content);

                        mouseEvent.consume();
                    }
                });

                row.setOnDragOver(new EventHandler<DragEvent>() {
                    @Override
                    public void handle(DragEvent dragEvent) {
                        System.out.println("Drag over on center");
                        dragEvent.acceptTransferModes(TransferMode.MOVE);
                        dragEvent.consume();
                    }
                });

                row.setOnDragDropped(new EventHandler<DragEvent>() {
                    @Override
                    public void handle(DragEvent dragEvent) {
                        System.out.println("Drag dropped on center");

                        //move to the selected center folder
                        File fileToMove = m_model.getM_fileToMove();
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

                        m_model.notifyLibraryObservers();
                        dragEvent.consume();
                    }
                });

                row.setOnDragDone(new EventHandler<DragEvent>() {
                    @Override
                    public void handle(DragEvent dragEvent) {
                        System.out.println("Drag done");
                        m_model.setM_fileToMove(null);
                        dragEvent.consume();
                    }
                });

                /*row.setOnDragEntered(new EventHandler<DragEvent>() {
                    @Override
                    public void handle(DragEvent dragEvent) {
                        Song selectedItem = m_table.getSelectionModel().getSelectedItem();
                        System.out.println("Drag entered on " + selectedItem);
                        dragEvent.consume();
                    }
                });

                row.setOnDragExited(new EventHandler<DragEvent>() {
                    @Override
                    public void handle(DragEvent dragEvent) {
                        Song selectedItem = m_table.getSelectionModel().getSelectedItem();
                        System.out.println("Drag exited on " + selectedItem);
                        dragEvent.consume();
                    }
                });*/

                return row;
            }
        });
    }

    private void setCssStyle() {
        final String cssDefault = "-fx-border-color: black;\n";
        this.setStyle(cssDefault);
    }
}
