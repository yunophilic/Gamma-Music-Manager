package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.Library;
import com.teamgamma.musicmanagementsystem.Song;
import com.teamgamma.musicmanagementsystem.SongManager;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * UI for the user's or external library
 */
public class LibraryUI extends StackPane{
    private static Library library;
    private TreeView<File> filesTree;

    public LibraryUI(Library library){
        super();

        this.library = library;

        //this.getChildren().add(new Label(text));

        TreeView<String> tree = createTree();

        //this.getChildren().add(tree);
        //findFiles(library.getM_file(), null);
        this.getChildren().add(tree);

        setCssStyle();
    }

    private void registerAsObserver() {

    }

    private TreeView<String> createTree(){
        List<File> folders = library.getFolders();
        TreeItem<String> rootItem = new TreeItem<> (folders.get(0).getAbsolutePath());
        //TreeItem<String> rootItem = null;
        List<Song> songList = library.getM_songList();
        rootItem.setExpanded(true);
        for (int i = 0; i < folders.size(); i++) {
            TreeItem<String> folderItem = new TreeItem<>(folders.get(i).getName());
            for (Song song : songList) {
                String filePath = song.getM_file().getParent();
                if (filePath.equals(folders.get(i).getAbsolutePath())) {
                    TreeItem<String> item = new TreeItem<>(song.getM_songName());
                    folderItem.getChildren().add(item);
                }
            }
            rootItem.getChildren().add(folderItem);
        }
        TreeView<String> tree = new TreeView<> (rootItem);

        return tree;
    }

    private void setCssStyle(){
        final String cssDefault = "-fx-border-color: black;\n";
        this.setStyle(cssDefault);
    }
}
