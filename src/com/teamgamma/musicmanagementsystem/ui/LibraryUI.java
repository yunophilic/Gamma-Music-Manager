package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.FileManager;
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
    private TreeView<String> createTrees() {
        TreeItem<String> root = new TreeItem<>("Dummy root");

        for (Library library: libraries) {
            TreeItem<String> rootItem = FileManager.generateTreeItems(
                    library.getM_rootDir(), library.getM_rootDirPath()
            );
            rootItem.setExpanded(true);
            System.out.println("Added new root path:" + rootItem.toString());
            root.getChildren().add(rootItem);
        }
        TreeView<String> tree = new TreeView<>(root);
        tree.setShowRoot(false);
        return tree;
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
