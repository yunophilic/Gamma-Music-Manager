package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.*;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import java.io.File;
import java.io.FileFilter;
import java.util.List;

/**
 * UI for the user's or external library
 */
public class LibraryUI extends StackPane {
    //private static List<Library> libraries;
    private static SongManager model;
    TreeView<TreeViewFolderItem> tree;

    public LibraryUI(SongManager model){
        super();

        this.model = model;

        updateTreeView();

        setPaneStyle();
    }

    private void updateTreeView(){
        List<Library> libraries = model.getM_libraries();

        //this.getChildren().add(new Label(text));

        if (libraries.isEmpty()){
            this.getChildren().add(new Label("Add a library"));
        } else {
            tree = createTrees(libraries);
            this.getChildren().add(tree);
            setMouseEvent(tree);
        }
    }

    private void setMouseEvent(TreeView<TreeViewFolderItem> treeView) {
        treeView.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent mouseEvent)
            {
                if(mouseEvent.getClickCount() == 2)
                {
                    TreeItem<TreeViewFolderItem> item = treeView.getSelectionModel().getSelectedItem();
                    System.out.println("Selected Text : " + item.getValue());

                    model.setCenterFolder(item.getValue().getPath());
                }
            }
        });
    }

    private void registerAsLibraryObserver() {
        model.addObserver(new SongManagerObserver() {
            @Override
            public void librariesChanged() {
                clearTreeView();
                updateTreeView();
            }

            @Override
            public void centerFolderChanged() {

            }

            @Override
            public void rightFolderChanged() {

            }

            @Override
            public void songChanged() {

            }
        });
    }

    private void clearTreeView() {
        //tree.setRoot(null);
        this.getChildren().clear();
    }

    /**
     * Construct the tree view
     * @return TreeView<String>
     */
    private TreeView<TreeViewFolderItem> createTrees(List<Library> libraries) {
        File dummyRootFile = new File(libraries.get(0).getM_rootDirPath());
        TreeItem<TreeViewFolderItem> root = new TreeItem<>(new TreeViewFolderItem(dummyRootFile, true));

        for (Library library: libraries) {
            TreeItem<TreeViewFolderItem> rootItem = FileManager.generateTreeItems(
                    library.getM_rootDir(), library.getM_rootDirPath()
            );
            rootItem.setExpanded(true);
            System.out.println("Added new root path:" + rootItem.toString());
            root.getChildren().add(rootItem);
        }
        TreeView<TreeViewFolderItem> tree = new TreeView<>(root);
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
