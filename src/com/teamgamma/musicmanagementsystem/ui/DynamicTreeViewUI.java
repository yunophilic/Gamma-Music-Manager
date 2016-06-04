package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DynamicTreeViewUI extends StackPane {
    private SongManager model;
    private TreeView<TreeViewItem> tree;

    public DynamicTreeViewUI(SongManager model) {
        super();
        this.model = model;
        setPaneStyle();
        registerAsObserver();

        updateTreeView();
    }

    private void updateTreeView() {
        System.out.println("updating treeview...");
        List<Library> libraries = model.getM_libraries();

        if (model.getRightFolderSelected() == null) {
            this.getChildren().add(new Label("Choose a folder to view"));
        } else {
            tree = createTrees(libraries);
            this.getChildren().add(tree);
            setTreeCellFactory();
        }
    }

    private void setTreeCellFactory() {
        System.out.println("setting cell factory...");
        tree.setCellFactory(new Callback<TreeView<TreeViewItem>, TreeCell<TreeViewItem>>() {
            @Override
            public TreeCell<TreeViewItem> call(TreeView<TreeViewItem> arg0) {
                // custom tree cell that defines a context menu for the root tree item
                return new DynamicTreeViewUI.CustomTreeCell();
            }
        });
    }


    private void registerAsObserver() {
        model.addObserver(new SongManagerObserver() {
            @Override
            public void librariesChanged() {

            }

            @Override
            public void centerFolderChanged() {

            }

            @Override
            public void rightFolderChanged() {
                System.out.println("File changed in treeview");
                clearTreeView();
                updateTreeView();
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
     *
     * @return TreeView<String>
     */
    private TreeView<TreeViewItem> createTrees(List<Library> libraries) {
        if (!libraries.isEmpty()) {
            File dummyRootFile = new File(libraries.get(0).getM_rootDirPath());
            TreeItem<TreeViewItem> root = new TreeItem<>(new TreeViewItem(dummyRootFile, true));

            TreeItem<TreeViewItem> rootItem = FileManager.generateTreeItems(
                    model.getRightFolderSelected(), model.getRightFolderSelected().getAbsolutePath()
            );

            rootItem.setExpanded(true);
            System.out.println("Added new root path:" + rootItem.toString());
            root.getChildren().add(rootItem);

            TreeView<TreeViewItem> tree = new TreeView<>(root);
            tree.setShowRoot(false);
            return tree;
        }
        return null;
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

    private class CustomTreeCell extends TextFieldTreeCell<TreeViewItem> {
        private ContextMenu contextMenu;

        public CustomTreeCell() {
            contextMenu = new ContextMenu();

            //delete option
            MenuItem delete = new MenuItem("Delete");
            delete.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e) {
                    System.out.println("Deleting " + tree.getSelectionModel().getSelectedItem()); //for now
                    File fileToDelete = tree.getSelectionModel().getSelectedItem().getValue().getPath();
                    model.deleteFile(fileToDelete);
                    model.notifyFileObservers();
                }
            });

            //copy option
            MenuItem copy = new MenuItem("Copy");
            copy.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e) {
                    model.setM_fileBuffer(tree.getSelectionModel().getSelectedItem().getValue().getPath());
                }
            });

            //paste option
            MenuItem paste = new MenuItem("Paste");
            paste.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e) {
                    File dest = tree.getSelectionModel().getSelectedItem().getValue().getPath();
                    if (!dest.isDirectory()) {
                        System.out.println("Not a directory!"); //for now
                    }
                    try {
                        model.copyToDestination(dest);
                        model.notifyFileObservers();
                    } catch (IOException ex) {
                        ex.printStackTrace(); //for now
                    }
                }
            });

            contextMenu.getItems().addAll(delete, copy, paste);
        }

        @Override
        public void updateItem(TreeViewItem item, boolean empty) {
            super.updateItem(item, empty);
            setContextMenu(contextMenu);
        }
    }
}
