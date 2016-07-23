package com.teamgamma.musicmanagementsystem.ui;


import com.teamgamma.musicmanagementsystem.model.Item;
import com.teamgamma.musicmanagementsystem.model.Searcher;
import com.teamgamma.musicmanagementsystem.model.SongManager;
import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;

/**
 * Created by Eric on 2016-07-22.
 */
public class SearchResultUI extends BorderPane{

    public SearchResultUI(SongManager model) {
        model.registerSearchObserver(() ->
                Platform.runLater(
                        () -> {
                            TreeView<TreeItem<Item>> searchResults = new TreeView(model.getSearchResults().getTree());
                            // TODO: setup the cell builder for the tree view to show what we actually want.
                            this.setCenter(new TreeView<>(model.getSearchResults().getTree()));
                        }
                )
        );
    }

}
