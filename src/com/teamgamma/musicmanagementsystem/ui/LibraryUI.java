package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.Library;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;
import java.io.File;
import java.io.FileFilter;
import java.util.List;

/**
 * UI for the user's or external library
 */
public class LibraryUI extends StackPane {
    private static List<Library> libraries;
    private TreeView<File> fileTreeView;

    public LibraryUI(List<Library> libraries){
        super();

        this.libraries = libraries;

        //this.getChildren().add(new Label(text));

        if (libraries.isEmpty()){
            this.getChildren().add(new Label("Add a library"));
        } else {
            TreeView<String> tree = createTrees();
            this.getChildren().add(tree);
        }

        setPaneStyle();
    }

    private void registerAsObserver() {

    }

    /**
     * Construct the tree view
     * @return TreeView<String>
     */
    private TreeView<String> createTrees(){
        TreeItem<String> root = new TreeItem<>("Dummy root");

        for (Library library: libraries) {
            TreeItem<String> rootItem = generateTreeItems(library.getM_rootDir(), library);
            rootItem.setExpanded(true);
            System.out.println("Added new root path:" + rootItem.toString());
            root.getChildren().add(rootItem);
        }
        TreeView<String> tree = new TreeView<>(root);
        tree.setShowRoot(false);
        return tree;
    }

    /**
     * Helper function to recursively create the tree items and return a reference to the root item
     * @return TreeItem<String>
     */
    private TreeItem<String> generateTreeItems(File file, Library library) {
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
                item.getChildren().add(generateTreeItems(child, library));
            }
        }
        return item;
    }

    private void setPaneStyle() {
        this.setMaxWidth(Double.MAX_VALUE);
        this.setMaxHeight(Double.MAX_VALUE);

        setCssStyle();
    }

    private void setCssStyle(){
        final String cssDefault = "-fx-border-color: black;\n";
        this.setStyle(cssDefault);
    }
}
