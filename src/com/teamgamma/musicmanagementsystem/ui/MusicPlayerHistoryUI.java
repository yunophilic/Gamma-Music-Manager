package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.model.Song;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerObserver;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.util.Collection;

/**
 * Class to show the MusicPlayer History.
 */
public class MusicPlayerHistoryUI extends HBox{

    // Constants
    public static final int VERTICAL_SPACING = 5;
    public static final int HORIZONTAIL_ELEMENT_SPACING = 5;
    public static final int MAX_HIGHT = 400;

    public static final String MENU_ITEM_PLAY_SONG = "Play Song";
    public static final String MENU_ITEM_PLAY_SONG_NEXT = "Play Song Next";
    public static final String MENU_ITEM_PLACE_SONG_ON_QUEUE = "Place Song On Queue";
    public static final String PLAYBACK_HISTORY_HEADER = "History";
    public static final String QUEUING_HEADER = "Playing Next";

    private MusicPlayerManager m_manager;

    public MusicPlayerHistoryUI(MusicPlayerManager manager) {
        m_manager = manager;

        TitledPane playbackHistory = createTitlePane(PLAYBACK_HISTORY_HEADER, m_manager.getHistory());
        TitledPane queuingList = createTitlePane(QUEUING_HEADER, m_manager.getPlayingQueue());

        Accordion accordion = new Accordion();
        accordion.getPanes().addAll(playbackHistory, queuingList);

        HBox.setHgrow(accordion, Priority.ALWAYS);
        this.getChildren().add(accordion);

        manager.registerNewSongObserver(new MusicPlayerObserver() {
            @Override
            public void updateUI() {
                playbackHistory.setContent(createUIList(manager.getHistory()));

            }
        });

        manager.registerQueingObserver(new MusicPlayerObserver() {
            @Override
            public void updateUI() {
                queuingList.setContent(createUIList(manager.getPlayingQueue()));
            }
        });
    }

    /**
     * Helper function to create a titled pane for the accordian
     *
     * @param title The title of the accordian
     *
     * @param songs The collection of songs that is wanted to be displayed.
     *
     * @return  A TitlePane with the title and collection of songs displayed.
     */
    private TitledPane createTitlePane(String title, Collection<Song> songs) {
        TitledPane titlePane = new TitledPane(title, createUIList(songs));
        titlePane.setAnimated(true);
        titlePane.setCollapsible(true);
        titlePane.setExpanded(false);
        return titlePane;
    }

    /**
     * Function to create UI element that will hold a list of songs in it based on the collection passed in.
     *
     * @param listOfSongs   The list of songs to display.s
     *
     * @return A scrollable UI element that contains all the songs in the collection.
     */
    private ScrollPane createUIList(Collection<Song> listOfSongs) {
        ScrollPane wrapper = new ScrollPane();

        VBox allSongs = new VBox();
        allSongs.setSpacing(VERTICAL_SPACING);

        int songNumber = 0;
        for (Song song : listOfSongs) {
            HBox row = new HBox();

            if (songNumber == 0) {
                row.getChildren().add(createOldestStyleLabel(Integer.toString(songNumber)));
                row.getChildren().add(createOldestStyleLabel(song.getM_fileName()));
            } else {
                row.getChildren().add(new Label(Integer.toString(songNumber)));
                row.getChildren().add(new Label(song.getM_fileName()));
            }

            row.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (event.getButton() == MouseButton.SECONDARY) {
                        createSubmenu(song).show(row, event.getScreenX(), event.getScreenY());
                    }
                }
            });

            row.setSpacing(HORIZONTAIL_ELEMENT_SPACING);
            allSongs.getChildren().add(row);

            songNumber++;
        }
        allSongs.setMaxHeight(MAX_HIGHT);

        wrapper.setContent(allSongs);
        return wrapper;
    }

    /**
     * Helper Function to create a styled label for the oldest element.
     *
     * @param message The message you want the label to have
     *
     * @return A styled label that contains the message given.
     */
    private Label createOldestStyleLabel(String message) {
        Label styledLabel = new Label(message);
        styledLabel.setStyle("-fx-font-style: italic; -fx-font-weight: bold");

        return styledLabel;
    }


    /**
     * Function to create a submenu for that will be shown when the user right clicks on a element.
     *
     * @param song The song that is selected
     *
     * @return  A context menu containing actions to do.
     */
    private ContextMenu createSubmenu(Song song) {
        ContextMenu playbackMenu = new ContextMenu();

        MenuItem playSong = new MenuItem(MENU_ITEM_PLAY_SONG);
        playSong.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                m_manager.playSongRightNow(song);
            }
        });

        MenuItem placeSongAtStartOfQueue = new MenuItem(MENU_ITEM_PLAY_SONG_NEXT);
        placeSongAtStartOfQueue.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                m_manager.placeSongAtStartOfQueue(song);
            }
        });

        MenuItem placeSongOnBackOfQueue = new MenuItem(MENU_ITEM_PLACE_SONG_ON_QUEUE);
        placeSongOnBackOfQueue.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                m_manager.placeSongOnBackOfPlaybackQueue(song);
            }
        });

        playbackMenu.getItems().addAll(playSong, placeSongAtStartOfQueue, placeSongOnBackOfQueue);
        return playbackMenu;
    }
}
