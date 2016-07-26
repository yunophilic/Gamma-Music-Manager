package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.model.DatabaseManager;
import com.teamgamma.musicmanagementsystem.model.Item;

import com.teamgamma.musicmanagementsystem.model.SongManager;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;

import com.teamgamma.musicmanagementsystem.util.UserInterfaceUtils;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;

/**
 * The UI component to show the search results.
 */
public class SearchResultUI extends BorderPane{

    public static final String DEFAULT_SEARCH_MESSAGE = "Search Something";

    /**
     * Constructor
     *
     * @param model                 The model to interact with
     * @param musicPlayerManager    The music player manager to interact with.
     * @param dbManager             The database manager to interact with.
     */
    public SearchResultUI(SongManager model, MusicPlayerManager musicPlayerManager, DatabaseManager dbManager) {
        model.registerSearchObserver(() ->
                Platform.runLater(
                        () -> {
                            TreeView<Item> searchResults = new TreeView(model.getSearchResults().getTree());
                            searchResults.setShowRoot(false);
                            searchResults.setCellFactory(param -> new SearchTreeCell(model, musicPlayerManager, dbManager));
                            this.setCenter(searchResults);
                        }
                )
        );

        this.setCenter(new Label(DEFAULT_SEARCH_MESSAGE));

        model.registerInitalSearchObserver(() -> this.setCenter(new Label(DEFAULT_SEARCH_MESSAGE)));

        UserInterfaceUtils.applyBlackBoarder(this);
    }

}
