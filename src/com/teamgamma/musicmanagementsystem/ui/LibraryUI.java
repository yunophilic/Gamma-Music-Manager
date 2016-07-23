package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.util.*;
import com.teamgamma.musicmanagementsystem.util.FileTreeUtils;
import com.teamgamma.musicmanagementsystem.model.*;

import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * UI for the user's or external library
 */
public class LibraryUI extends StackPane {
    private SongManager m_model;
    private MusicPlayerManager m_musicPlayerManager;
    private DatabaseManager m_databaseManager;
    private TreeView<Item> m_tree;

    public LibraryUI(SongManager model,
                     MusicPlayerManager musicPlayerManager,
                     DatabaseManager databaseManager,
                     List<String> expandedPaths) {
        super();
        m_model = model;
        m_musicPlayerManager = musicPlayerManager;
        m_databaseManager = databaseManager;
        initTreeView();
        if (m_tree != null) {
            setTreeExpandedState(expandedPaths);
        }
        setPaneStyle();
        registerAsLibraryObserver();
    }

    /**
     * Initialize the tree view
     */
    private void initTreeView() {
        System.out.println("initializing treeview...");
        List<Library> libraries = m_model.getM_libraries();

        if (libraries.isEmpty()) {
            setEmptyLibraryUI();
        } else {
            m_tree = buildTreeView(libraries);
            this.getChildren().add(m_tree);
            setTreeCellFactory();
            setFileVisibility();
        }
    }

    /**
     * Set nodes of the tree that are expanded based on the list of expanded paths given
     *
     * @param expandedPaths list of expanded paths
     */
    private void setTreeExpandedState(List<String> expandedPaths) {
        FileTreeUtils.setTreeExpandedState(m_tree.getRoot(), expandedPaths);
    }

    /**
     * Construct the tree view
     *
     * @return TreeView<String>
     */
    private TreeView<Item> buildTreeView(List<Library> libraries) {
        TreeItem<Item> root = new TreeItem<>(new DummyItem());

        for (Library library : libraries) {
            File file = library.getM_treeRoot().getValue().getFile();
            TreeItem<Item> rootItem = FileTreeUtils.copyTree(m_model.search(file));
            rootItem.setExpanded(true);
            System.out.println("Added new root path:" + rootItem.toString());
            root.getChildren().add(rootItem);
        }
        TreeView<Item> tree = new TreeView<>(root);
        tree.setShowRoot(false);

        if (m_model.getM_selectedCenterFolder() != null) {
            FileTreeUtils.setOpenFolder(tree, m_model.getM_selectedCenterFolder().getAbsolutePath());
        }

        return tree;
    }

    /**
     * Clear the tree view
     */
    private void clearTreeView() {
        System.out.println("clearing treeview...");
        this.getChildren().clear();
    }

    /**
     * Set placeholder text if no library exist
     */
    private void setEmptyLibraryUI() {
        this.getChildren().add(new Label("Add a library"));
    }

    /**
     * Set custom tree cell factory for the tree view
     */
    private void setTreeCellFactory() {
        System.out.println("setting cell factory...");
        m_tree.setCellFactory(arg -> new CustomTreeCell(m_model, m_musicPlayerManager, m_databaseManager, m_tree, true));
    }

    /**
     * Register as observer to update any changes made
     */
    private void registerAsLibraryObserver() {
        m_model.addLibraryObserver((FileActions fileActions) -> {
            System.out.println("Library changed in treeview");
            updateLibraryTrees(fileActions);
            setFileVisibility();
        });
        m_model.addFileObserver((FileActions fileActions) -> {
            System.out.println("File changed in treeview");
            updateFiles(fileActions);
            setFileVisibility();
        });
        m_model.addLeftPanelOptionsObserver((FileActions fileActions) -> {
            System.out.println("Left panel options in treeview");
            setFileVisibility();
        });
        m_model.addCenterFolderObserver((FileActions fileActions) -> {
            FileTreeUtils.closeAllFoldersIcons(m_tree.getRoot());
            FileTreeUtils.setOpenFolder(m_tree, m_model.getM_selectedCenterFolder().getAbsolutePath());
            setTreeCellFactory();
        });
    }

    /**
     * Hide or show files depending the on left panel options
     */
    private void setFileVisibility() {
        if (m_model.getM_menuOptions().getM_leftPanelShowFoldersOnly()) {
            FileTreeUtils.hideFiles(m_tree);
        } else {
            FileTreeUtils.showFiles(m_tree.getRoot(), m_model.getM_fileTreeRoot());
        }
    }

    /**
     * Update the files in the tree
     *
     * @param fileActions the file action
     */
    private void updateFiles(FileActions fileActions) {
        try {
            for (Pair<Action, File> fileAction: fileActions) {
                Action action = fileAction.getKey();
                if (fileAction != null && action != Action.NONE) {
                    FileTreeUtils.updateTreeItems(m_model, m_tree, action, fileAction.getValue());
                }
            }
        } catch (IOException ex) {
            PromptUI.customPromptError("Error", null, "IOException: \n" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Update libraries depending on the action
     *
     * @param fileActions the library action
     */
    private void updateLibraryTrees(FileActions fileActions) {
        for (Pair<Action, File> fileAction: fileActions) {
            if (fileAction.getKey().equals(Action.ADD)) {
                // If this is not the first library added, add it without resetting the state of the other libraries
                // Else, simply reset the tree to show the library
                if (m_model.getM_libraries().size() > 1) {
                    List<TreeItem<Item>> libraryNodes = m_tree.getRoot().getChildren();
                    List<Item> libraryItems = FileTreeUtils.getItems(libraryNodes);
                    List<Library> libraries = m_model.getM_libraries();

                    for (Library library : libraries) {
                        // If library is not in libraryItems, add new node
                        if (!FileTreeUtils.isLibraryInList(libraryItems, library)) {
                            TreeItem<Item> newLibrary = FileTreeUtils.copyTree(library.getM_treeRoot());
                            newLibrary.setExpanded(true);
                            libraryNodes.add(newLibrary);
                        }
                    }
                } else {
                    initTreeView();
                }
            } else if (fileAction.getKey().equals(Action.REMOVE_FROM_VIEW) || fileAction.getKey().equals(Action.DELETE)) {
                TreeItem<Item> removedLibrary = m_tree.getSelectionModel().getSelectedItem();
                removedLibrary.getParent().getChildren().remove(removedLibrary);

                // If there are no more libraries, show a text label
                if (m_model.getM_libraries().size() < 1) {
                    setEmptyLibraryUI();
                }
            }
        }

    }

    /**
     * Set pane style
     */
    private void setPaneStyle() {
        this.setMaxWidth(Double.MAX_VALUE);
        this.setMaxHeight(Double.MAX_VALUE);

        UserInterfaceUtils.applyBlackBoarder(this);
    }

    /**
     * Get list of file paths that are expanded in this tree
     *
     * @return list of expanded paths
     */
    public List<String> getExpandedPaths() {
        if (m_tree != null) {
            return FileTreeUtils.getExpandedPaths(m_tree);
        } else {
            return null;
        }
    }
}
