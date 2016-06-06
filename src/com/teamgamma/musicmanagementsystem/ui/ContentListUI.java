package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.Song;
import com.teamgamma.musicmanagementsystem.SongManager;
import com.teamgamma.musicmanagementsystem.SongManagerObserver;
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
    private SongManager model;
    private MusicPlayerManager manager;
//    private GridPane gridPane;
    private TableView table;

    public ContentListUI(SongManager model, MusicPlayerManager manager) {
        super();

        this.model = model;

        this.manager = manager;

        setEmptyText();

//        gridPane = new GridPane();

        table = new TableView();

        //gridPane.add(new Label("Contents in folder"), 10, 20);

        setCssStyle();

        registerAsCenterFolderObserver();

    }

    /**
     * Register as a observer to changes for the folder selected to be displayed here
     */
    private void registerAsCenterFolderObserver() {
        model.addObserver(new SongManagerObserver() {
            @Override
            public void librariesChanged() {
                /* Do nothing */
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
        });
    }

    private void setEmptyText() {
        this.getChildren().add(new Label("Contents in folder"));
    }

    private void clearList() {
        System.out.println("Clearing list...");
        table.getItems().clear();
        this.getChildren().clear();
    }

    private void updateList() {
        if (model.getM_selectedCenterFolder() == null) {
            setEmptyText();
        } else {
            table = new TableView();
            System.out.println("Updating table...");
            List<Song> songs = model.getCenterPanelSongs();

            table.setEditable(true);

            TableColumn fileNameCol = new TableColumn("File Name");
            fileNameCol.setMinWidth(80);
            fileNameCol.setCellValueFactory(new PropertyValueFactory<Song, File>("m_fileName"));

            TableColumn titleCol = new TableColumn("Title");
            titleCol.setMinWidth(60);
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

            TableColumn artistCol = new TableColumn("Artist");
            artistCol.setMinWidth(60);
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

            TableColumn albumCol = new TableColumn("Album");
            albumCol.setMinWidth(60);
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

            TableColumn genreCol = new TableColumn("Genre");
            genreCol.setMinWidth(60);
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

            TableColumn ratingCol = new TableColumn("Rating");
            ratingCol.setMinWidth(20);
            ratingCol.setCellValueFactory(new PropertyValueFactory<Song, String>("m_rating"));
            ratingCol.setCellFactory(TextFieldTableCell.forTableColumn());
            ratingCol.setOnEditCommit(
                    new EventHandler<TableColumn.CellEditEvent<Song, String>>() {
                        @Override
                        public void handle(TableColumn.CellEditEvent<Song, String> t) {
                            ((Song) t.getTableView().getItems().get(
                                    t.getTablePosition().getRow())).setRating( Integer.parseInt(t.getNewValue()) );
                        }
                    }
            );

            table.getColumns().addAll(fileNameCol, titleCol, artistCol, albumCol, genreCol, ratingCol);

            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            table.setItems(FXCollections.observableArrayList(songs));

            table.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(event.getClickCount() == 2) {
                        System.out.println(table.getSelectionModel().getSelectedItem());
                        manager.playSongRightNow((Song) table.getSelectionModel().getSelectedItem());
                    }
                }
            });
        }

        this.getChildren().add(table);

        // Scrolls through list
        ScrollPane scrollpane = new ScrollPane();
        scrollpane.setFitToWidth(true);
        scrollpane.setFitToHeight(true);
        scrollpane.setPrefSize(500,500);
        scrollpane.setContent(table);
    }

//    private void clearList() {
//        System.out.println("Clearing list...");
//        gridPane.getChildren().clear();
//        this.getChildren().clear();
//    }

//    private void updateList() {
//        if (model.getM_selectedCenterFolder() == null) {
//            setEmptyText();
//        } else {
//            gridPane = new GridPane();
//            System.out.println("Updating list...");
//            List<Song> songs = model.getCenterPanelSongs();
//            gridPane.add(new Label("Song Name   "), 0, 0);
//            gridPane.add(new Label("Genre   "), 1, 0);
//            gridPane.add(new Label("Artist   "), 2, 0);
//            gridPane.add(new Label("Rating   "), 3, 0);
//
//            int row = 1;
//            for (Song song : songs) {
//                System.out.println("Found new song: " + song.getM_file().getAbsolutePath());
//                //HBox rowOfSongInfo = new HBox();
//                Label titleLabel = new Label(song.getM_title() + "   ");
//                Label genreLabel = new Label(song.getM_genre() + "   ");
//                Label artistLabel = new Label(song.getM_artist() + "   ");
//                Label ratingLabel = new Label(song.getM_rating() + "   ");
//                gridPane.add(titleLabel, 0, row);
//                gridPane.add(genreLabel, 1, row);
//                gridPane.add(artistLabel, 2, row);
//                gridPane.add(ratingLabel, 3, row);
//
////                rowOfSongInfo.getChildren().addAll(titleLabel, genreLabel, artistLabel, ratingLabel);
////                gridPane.add(rowOfSongInfo, 0, row);
//
//                row++;
//
//                titleLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
//                    @Override public void handle(MouseEvent e) {
//                        System.out.println("CLICKED!");
//
//                        // Code to play song goes here
//                    }
//                });
//
//                genreLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
//                    @Override public void handle(MouseEvent e) {
//                        // Code to play song goes here
//                    }
//                });
//
//                artistLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
//                    @Override public void handle(MouseEvent e) {
//                        // Code to play song goes here
//                    }
//                });
//
//                ratingLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
//                    @Override public void handle(MouseEvent e) {
//                        // Code to play song goes here
//                    }
//                });
//            }
//            this.getChildren().add(gridPane);
//        }
//    }

    private void setCssStyle() {
        final String cssDefault = "-fx-border-color: black;\n";
        this.setStyle(cssDefault);
    }
}
