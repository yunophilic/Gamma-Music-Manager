package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.misc.*;
import com.teamgamma.musicmanagementsystem.misc.FileTreeUtil;
import com.teamgamma.musicmanagementsystem.model.*;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DynamicTreeViewUI extends StackPane {
    private SongManager m_model;
    private MusicPlayerManager m_musicPlayerManager;
    private DatabaseManager m_databaseManager;
    private TreeView<Item> m_tree;

    public DynamicTreeViewUI(SongManager model, MusicPlayerManager musicPlayerManager, DatabaseManager databaseManager,
                             List<String> dynamicTreeViewExpandedPaths) {
        super();
        m_model = model;
        m_musicPlayerManager = musicPlayerManager;
        m_databaseManager = databaseManager;
        setPaneStyle();
        registerAsObserver();

        updateTreeView(dynamicTreeViewExpandedPaths);
    }

    /**
     * Update the tree view and load previously expanded paths if applicable
     * @param dynamicTreeViewExpandedPaths
     */
    private void updateTreeView(List<String> dynamicTreeViewExpandedPaths) {
        System.out.println("updating treeview...");

        if (m_model.getM_rightFolderSelected() == null) {
            this.getChildren().add(new Label("Choose a folder to view"));
        } else {
            m_tree = createTrees(m_model.getM_libraries(), dynamicTreeViewExpandedPaths);
            this.getChildren().add(m_tree);
            setTreeCellFactory();
        }
    }

    /**
     *  Set a tree cell factory for the file tree
     */
    private void setTreeCellFactory() {
        System.out.println("setting cell factory...");
        m_tree.setCellFactory(new Callback<TreeView<Item>, TreeCell<Item>>() {
            @Override
            public TreeCell<Item> call(TreeView<Item> arg) {
                // custom m_tree cell that defines a context menu for the root m_tree item
                return new CustomTreeCell(m_model, m_musicPlayerManager, m_databaseManager, m_tree, false);
            }
        });
    }

    /**
     * Register as a Song Manager Observer and define actions upon receiving notifications
     */
    private void registerAsObserver() {
        m_model.addLibraryObserver((action, file) -> {
            clearTreeView();
            updateTreeView(null);
        });
        m_model.addRightFolderObserver((action, file) -> {
            System.out.println("Right folder changed in treeview");
            clearTreeView();
            updateTreeView(null);
        });
        m_model.addFileObserver((action, file) -> {
            System.out.println("File changed in treeview");
            updateFiles(action, file);
        });
    }

    /**
     * Update the files to show in this tree
     * @param fileAction
     * @param file
     */
    private void updateFiles(Actions fileAction, File file) {
        try {
            if (fileAction != null && fileAction != Actions.NONE) {
                if (m_model.getM_rightFolderSelected() == null) {
                    this.getChildren().add(new Label("Choose a folder to view"));
                } else {
                    FileTreeUtil.updateTreeItems(fileAction, file, m_tree, m_model);
                    m_model.setM_rightPanelFileAction(Actions.NONE);
                }
            }
        } catch (IOException ex) {
            PromptUI.customPromptError("Error", null, "IOException: \n" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Clear the tree
     */
    private void clearTreeView() {
        //m_tree.setRoot(null);
        System.out.println("clearing treeview...");
        this.getChildren().clear();
    }

    /**
     * Construct the m_tree view and show previously expanded folders if applicable
     *
     * @return TreeView<String>
     */
    private TreeView<Item> createTrees(List<Library> libraries, List<String> dynamicTreeViewExpandedPaths) {
        if (!libraries.isEmpty()) {
            File dummyRootFile = new File(libraries.get(0).getRootDirPath());
            TreeItem<Item> root = new TreeItem<>(new Folder(dummyRootFile, true));

            TreeItem<Item> rootItem = FileTreeUtil.generateTreeItems(
                    m_model.getM_rightFolderSelected(), m_model.getM_rightFolderSelected().getAbsolutePath(), dynamicTreeViewExpandedPaths
            );

        rootItem.setExpanded(true);
        if (rootItem.getValue() != null) {
            System.out.println("Added new root path:" + rootItem.toString());
        }
        root.getChildren().add(rootItem);

            TreeView<Item> tree = new TreeView<>(root);
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

    /**
     * Get list of file paths that are expanded in this tree
     * @return
     */
    public List<String> getExpandedPaths() {
        if (m_tree != null) {
            return FileTreeUtil.getExpandedPaths(m_tree);
        } else {
            return null;
        }
    }
}
