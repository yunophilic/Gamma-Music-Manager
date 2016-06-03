package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * UI for the user's or external library
 */
public class LibraryUI extends StackPane {
    private SongManager model;
    private TreeView<TreeViewFolderItem> tree;
    private File selectedItem;

    public LibraryUI(SongManager model){
        super();
        this.model = model;
        this.selectedItem = null;
        updateTreeView();
        setTreeCellFactory();
        setPaneStyle();
    }

    private void updateTreeView(){
        List<Library> libraries = model.getM_libraries();

        if (libraries.isEmpty()){
            this.getChildren().add(new Label("Add a library"));
        } else {
            tree = createTrees(libraries);
            this.getChildren().add(tree);
            setMouseEvent(tree);
        }
    }

    private void setTreeCellFactory() {
        tree.setCellFactory(new Callback<TreeView<TreeViewFolderItem>, TreeCell<TreeViewFolderItem>>() {
            @Override
            public TreeCell<TreeViewFolderItem> call(TreeView<TreeViewFolderItem> arg0) {
                // custom tree cell that defines a context menu for the root tree item
                return new CustomTreeCell();
            }
        });
    }

    /**
     * Notify observers on mouse event
     * @param treeView
     */
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

    private class CustomTreeCell extends TextFieldTreeCell<TreeViewFolderItem> {
        private ContextMenu contextMenu;

        public CustomTreeCell() {
            contextMenu = new ContextMenu();

            //context menu items
            MenuItem delete = new MenuItem("Delete");
            delete.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e) {
                    System.out.println("Deleting " + tree.getSelectionModel().getSelectedItem()); //for now
                }
            });
            MenuItem paste = new MenuItem("Paste");
            //paste.setDisable(true);
            paste.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e) {
                    File dest = tree.getSelectionModel().getSelectedItem().getValue().getPath();
                    if (!dest.isDirectory()) {
                        System.out.println("Not a directory!"); //for now
                    }
                    try {
                        FileManager.copyFilesRecursively(selectedItem, dest);
                        selectedItem = null;
                    } catch (IOException ex) {
                        ex.printStackTrace(); //for now
                    }
                }
            });
            MenuItem copy = new MenuItem("Copy");
            copy.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e) {
                    selectedItem = tree.getSelectionModel().getSelectedItem().getValue().getPath();
                    //paste.setDisable(false);
                }
            });

            contextMenu.getItems().addAll(delete, copy, paste);
        }

        @Override
        public void updateItem(TreeViewFolderItem item, boolean empty) {
            super.updateItem(item, empty);
            setContextMenu(contextMenu);
        }
    }
}
