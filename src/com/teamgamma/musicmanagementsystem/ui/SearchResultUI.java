package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.model.DatabaseManager;
import com.teamgamma.musicmanagementsystem.model.Item;

import com.teamgamma.musicmanagementsystem.model.SongManager;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;

import com.teamgamma.musicmanagementsystem.util.FileTreeUtils;
import com.teamgamma.musicmanagementsystem.util.UserInterfaceUtils;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;

import java.util.ArrayList;
import java.util.List;

/**
 * The UI component to show the search results.
 */
public class SearchResultUI extends BorderPane{

    private static final String DEFAULT_SEARCH_MESSAGE = "Search Something";

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
                    if (model.getSearchResults() != null) {
                        List<String> allExpanedPaths = new ArrayList<>();
                        if (this.getCenter() instanceof TreeView) {
                            allExpanedPaths.addAll(FileTreeUtils.getExpandedPaths((TreeView<Item>) this.getCenter()));
                        }

                        TreeView<Item> searchResults = new TreeView(model.getSearchResults().getTree());
                        searchResults.setShowRoot(false);

                        // Wants to only have the context menu for the Show in Library for the search results.
                        FileTreeUtils.setTreeExpandedState(model.getSearchResults().getTree(), allExpanedPaths);

                        searchResults.setCellFactory(
                                param -> new CustomTreeCell(model, musicPlayerManager, dbManager, searchResults,
                                        CellType.SEARCH_RESULTS));

                        this.setCenter(searchResults);
                    }
                }
            )
        );

        this.setCenter(new Label(DEFAULT_SEARCH_MESSAGE));

        model.registerInitalSearchObserver(() -> this.setCenter(new Label(DEFAULT_SEARCH_MESSAGE)));

        UserInterfaceUtils.applyBlackBoarder(this);
    }

}
