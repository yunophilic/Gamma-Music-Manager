package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.model.FileManager;
import com.teamgamma.musicmanagementsystem.model.Library;
import com.teamgamma.musicmanagementsystem.model.SongManager;
import com.teamgamma.musicmanagementsystem.model.SongManagerObserver;
import com.teamgamma.musicmanagementsystem.misc.CustomTreeCell;
import com.teamgamma.musicmanagementsystem.misc.TreeViewItem;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

import java.io.File;
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
            public TreeCell<TreeViewItem> call(TreeView<TreeViewItem> arg) {
                // custom tree cell that defines a context menu for the root tree item
                return new CustomTreeCell(model, tree, false);
            }
        });
    }


    private void registerAsObserver() {
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

            @Override
            public void leftPanelOptionsChanged() {
                /* Do nothing */
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
                    model.getRightFolderSelected(), model.getRightFolderSelected().getAbsolutePath(), false
            );

            rootItem.setExpanded(true);
            if (rootItem.getValue() != null) {
                System.out.println("Added new root path:" + rootItem.toString());
            }
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
}
