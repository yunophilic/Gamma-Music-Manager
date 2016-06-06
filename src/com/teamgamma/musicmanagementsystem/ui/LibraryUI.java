package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * UI for the user's or external library
 */
public class LibraryUI extends StackPane {
    private SongManager model;
    private TreeView<TreeViewItem> tree;

    public LibraryUI(SongManager model) {
        super();
        this.model = model;
        updateTreeView();
        setPaneStyle();
        registerAsLibraryObserver();
    }

    private void updateTreeView() {
        System.out.println("updating treeview...");
        List<Library> libraries = model.getM_libraries();

        if (libraries.isEmpty()) {
            this.getChildren().add(new Label("Add a library"));
        } else {
            tree = createTrees(libraries);
            this.getChildren().add(tree);
            setMouseEvent(tree);
            setTreeCellFactory();
        }
    }

    private void setTreeCellFactory() {
        System.out.println("setting cell factory...");
        tree.setCellFactory(new Callback<TreeView<TreeViewItem>, TreeCell<TreeViewItem>>() {
            @Override
            public TreeCell<TreeViewItem> call(TreeView<TreeViewItem> arg) {
                // custom tree cell that defines a context menu for the root tree item
                return new CustomTreeCell(model, tree, true);
            }
        });
    }

    /**
     * Notify observers on mouse event
     * @param treeView
     */
    private void setMouseEvent(TreeView<TreeViewItem> treeView) {
        treeView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    TreeItem<TreeViewItem> item = treeView.getSelectionModel().getSelectedItem();
                    if (item.getValue() != null) {
                        System.out.println("Selected Text : " + item.getValue());
                    }

                    model.setCenterFolder(item.getValue().getPath());
                    model.notifyCenterFolderObservers();
                }
            }
        });
    }

    private void registerAsLibraryObserver() {
        model.addObserver(new SongManagerObserver() {
            @Override
            public void librariesChanged() {
                System.out.println("Library changed in treeview");
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

            @Override
            public void fileChanged() {
                System.out.println("File changed in treeview");
                clearTreeView();
                updateTreeView();
            }
        });
    }

    private void clearTreeView() {
        //tree.setRoot(null);
        System.out.println("clearing treeview...");
        this.getChildren().clear();
    }

    /**
     * Construct the tree view
     * @return TreeView<String>
     */
    private TreeView<TreeViewItem> createTrees(List<Library> libraries) {
        File dummyRootFile = new File(libraries.get(0).getM_rootDirPath());
        TreeItem<TreeViewItem> root = new TreeItem<>(new TreeViewItem(dummyRootFile, true));

        for (Library library : libraries) {
            TreeItem<TreeViewItem> rootItem = FileManager.generateTreeItems(
                    library.getM_rootDir(), library.getM_rootDirPath()
            );
            rootItem.setExpanded(true);
            System.out.println("Added new root path:" + rootItem.toString());
            root.getChildren().add(rootItem);
        }
        TreeView<TreeViewItem> tree = new TreeView<>(root);
        tree.setShowRoot(false);
        return tree;
    }

    private void setPaneStyle() {
        this.setMaxWidth(Double.MAX_VALUE);
        this.setMaxHeight(Double.MAX_VALUE);

        setCssStyle();
    }

    private void setCssStyle() {
        final String cssDefault = "-fx-border-color: black;\n";
        this.setStyle(cssDefault);
    }
}
