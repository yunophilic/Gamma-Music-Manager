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
import javafx.scene.layout.StackPane;
import javafx.scene.control.*;
import java.io.File;
import java.util.List;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;

/**
 * UI class for list of songs in center of application
 */
public class ContentListUI extends StackPane {
    private SongManager m_model;
    private MusicPlayerManager m_musicPlayerManager;
    //    private GridPane gridPane;
    private TableView m_table;

    public ContentListUI(SongManager model, MusicPlayerManager musicPlayerManager) {
        super();

        m_model = model;
        m_musicPlayerManager = musicPlayerManager;

        updateList();

        m_table = new TableView();

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
        m_table = new TableView();
        TableColumn fileNameCol = new TableColumn("File Name");
        fileNameCol.setMinWidth(80);
        TableColumn titleCol = new TableColumn("Title");
        titleCol.setMinWidth(60);
        TableColumn artistCol = new TableColumn("Artist");
        artistCol.setMinWidth(60);
        TableColumn albumCol = new TableColumn("Album");
        albumCol.setMinWidth(60);
        TableColumn genreCol = new TableColumn("Genre");
        genreCol.setMinWidth(60);
        TableColumn ratingCol = new TableColumn("Rating");
        ratingCol.setMinWidth(20);

        if (m_model.getM_selectedCenterFolder() == null) {
            setEmptyText(fileNameCol, titleCol, artistCol, albumCol, genreCol, ratingCol);
        } else {
            m_table = new TableView();
            System.out.println("Updating m_table...");
            List<Song> songs = m_model.getCenterPanelSongs();

            m_table.setEditable(true);

            setTableColAttributes(fileNameCol, titleCol, artistCol, albumCol, genreCol, ratingCol);

            if (songs.isEmpty()){
                m_table.setPlaceholder(new Label("No songs in folder"));
            } else {
                m_table.setItems(FXCollections.observableArrayList(songs));

                m_table.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getClickCount() == 2) {
                            System.out.println(m_table.getSelectionModel().getSelectedItem());
                            m_musicPlayerManager.playSongRightNow((Song) m_table.getSelectionModel().getSelectedItem());
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

    private void setTableColAttributes(TableColumn fileNameCol, TableColumn titleCol, TableColumn artistCol, TableColumn albumCol, TableColumn genreCol, TableColumn ratingCol) {
        fileNameCol.setCellValueFactory(new PropertyValueFactory<Song, File>("m_fileName"));

        titleCol.setCellValueFactory(new PropertyValueFactory<Song, String>("m_title"));
        titleCol.setCellFactory(TextFieldTableCell.forTableColumn());
        titleCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Song, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Song, String> t) {
                        ((Song) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())).setTitle(t.getNewValue());
                    }
                }
        );

        artistCol.setCellValueFactory(new PropertyValueFactory<Song, String>("m_artist"));
        artistCol.setCellFactory(TextFieldTableCell.forTableColumn());
        artistCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Song, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Song, String> t) {
                        ((Song) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())).setArtist(t.getNewValue());
                    }
                }
        );

        albumCol.setCellValueFactory(new PropertyValueFactory<Song, String>("m_album"));
        albumCol.setCellFactory(TextFieldTableCell.forTableColumn());
        albumCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Song, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Song, String> t) {
                        ((Song) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())).setAlbum(t.getNewValue());
                    }
                }
        );

        genreCol.setCellValueFactory(new PropertyValueFactory<Song, String>("m_genre"));
        genreCol.setCellFactory(TextFieldTableCell.forTableColumn());
        genreCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Song, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Song, String> t) {
                        ((Song) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())).setGenre(t.getNewValue());
                    }
                }
        );

        ratingCol.setCellValueFactory(new PropertyValueFactory<Song, String>("m_rating"));
        ratingCol.setCellFactory(TextFieldTableCell.forTableColumn());
        ratingCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Song, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Song, String> t) {
                        try {
                            ((Song) t.getTableView().getItems().get(
                                    t.getTablePosition().getRow())).setRating(Integer.parseInt(t.getNewValue()));
                        } catch (IllegalArgumentException ex) {
                            PromptUI.customPromptError("Error", "", "Rating should be in range 0 to 5");
                            m_model.notifyCenterFolderObservers();
                        }
                    }
                }
        );

        m_table.getColumns().addAll(fileNameCol, titleCol, artistCol, albumCol, genreCol, ratingCol);
        m_table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setCssStyle() {
        final String cssDefault = "-fx-border-color: black;\n";
        this.setStyle(cssDefault);
    }
}
