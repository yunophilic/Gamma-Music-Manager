package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.Library;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;
import java.io.File;
import java.io.FileFilter;

/**
 * UI for the user's or external library
 */
public class LibraryUI extends StackPane {
    private static Library library;
    private TreeView<File> fileTreeView;

    public LibraryUI(Library library){
        super();

        this.library = library;

        //this.getChildren().add(new Label(text));

        TreeView<String> tree = createTree();

        //this.getChildren().add(tree);
        //findFiles(library.getM_rootDir(), null);
        this.getChildren().add(tree);

        setCssStyle();
    }

    private void registerAsObserver() {

    }

    /**
     * Construct the tree view
     * @return TreeView<String>
     */
    private TreeView<String> createTree(){
        TreeItem<String> rootItem = generateTreeItems(library.getM_rootDir());
        rootItem.setExpanded(true);
        return new TreeView<>(rootItem);
    }

    /**
     * Helper function to recursively create the tree items and return a reference to the root item
     * @return TreeItem<String>
     */
    private TreeItem<String> generateTreeItems(File file) {
        TreeItem<String> item = new TreeItem<>(
                ( file.equals(library.getM_rootDir()) ) ? file.getAbsolutePath() : file.getName()
        );
        File[] children = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory() || pathname.getName().endsWith(".mp3");
            }
        });
        if (children != null) {
            for (File child : children) {
                item.getChildren().add(generateTreeItems(child));
            }
        }
        return item;
    }

    private void setCssStyle(){
        final String cssDefault = "-fx-border-color: black;\n";
        this.setStyle(cssDefault);
    }
}
