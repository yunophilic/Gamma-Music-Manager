package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.model.DatabaseManager;
import com.teamgamma.musicmanagementsystem.model.Item;

import com.teamgamma.musicmanagementsystem.model.SongManager;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;

import javafx.application.Platform;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;

/**
 * Created by Eric on 2016-07-22.
 */
public class SearchResultUI extends BorderPane{

    public SearchResultUI(SongManager model, MusicPlayerManager musicPlayerManager, DatabaseManager dbManager) {
        model.registerSearchObserver(() ->
                Platform.runLater(
                        () -> {
                            TreeView<Item> searchResults = new TreeView(model.getSearchResults().getTree());
                            searchResults.setShowRoot(false);
                            // TODO: setup the cell builder for the tree view to show what we actually want.
                            searchResults.setCellFactory(param -> new SearchTreeCell(model, musicPlayerManager, dbManager));
                            this.setCenter(searchResults);
                        }
                )
        );
    }

}
