package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.misc.Actions;
import com.teamgamma.musicmanagementsystem.misc.TreeViewUtil;
import com.teamgamma.musicmanagementsystem.model.*;
import com.teamgamma.musicmanagementsystem.misc.CustomTreeCell;

import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

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
    private TreeView<TreeViewItem> m_tree;

    public LibraryUI(SongManager model,
                     MusicPlayerManager musicPlayerManager,
                     DatabaseManager databaseManager,
                     List<String> expandedPaths) {
        super();
        m_model = model;
        m_musicPlayerManager = musicPlayerManager;
        m_databaseManager = databaseManager;
        updateTreeView();
        if (m_tree != null) {
            setTreeExpandedState(expandedPaths);
        }
        setPaneStyle();
        registerAsLibraryObserver();
    }

    private void setTreeExpandedState(List<String> expandedPaths) {
        TreeViewUtil.setTreeExpandedState(m_tree.getRoot(), expandedPaths);
    }

    private void updateTreeView() {
        System.out.println("updating treeview...");
        List<Library> libraries = m_model.getM_libraries();

        if (libraries.isEmpty()) {
            setEmptyLibraryUI();
        } else {
            m_tree = createTrees(libraries);
            this.getChildren().add(m_tree);
            setTreeCellFactory();
        }
    }

    private void setEmptyLibraryUI() {
        this.getChildren().add(new Label("Add a library"));
    }

    private void setTreeCellFactory() {
        System.out.println("setting cell factory...");
        m_tree.setCellFactory(new Callback<TreeView<TreeViewItem>, TreeCell<TreeViewItem>>() {
            @Override
            public TreeCell<TreeViewItem> call(TreeView<TreeViewItem> arg) {
                // custom m_tree cell that defines a context menu for the root m_tree item
                return new CustomTreeCell(m_model, m_musicPlayerManager, m_databaseManager, m_tree, true);
            }
        });
    }

    private void registerAsLibraryObserver() {
        m_model.addSongManagerObserver(new SongManagerObserver() {
            @Override
            public void librariesChanged() {
                System.out.println("Library changed in treeview");
                //clearTreeView();
                //updateTreeView();

                updateLibraryTrees(m_model.getM_libraryAction());
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
            public void fileChanged(Actions action, File file) {
                System.out.println("File changed in treeview");
                //clearTreeView();
                //updateTreeView();

                updateFiles(action, file);
            }

            @Override
            public void leftPanelOptionsChanged() {
                System.out.println("Left panel options in treeview");
                clearTreeView();
                updateTreeView();
            }
        });
    }

    /**
     * Update the files in the tree
     * @param fileAction
     * @param file
     */
    private void updateFiles(Actions fileAction, File file) {
        try {
            if (fileAction != null && fileAction != Actions.NONE) {
                TreeViewUtil.updateTreeItems(fileAction, file, m_tree, m_model);
                m_model.setM_libraryFileAction(Actions.NONE);
            }
        } catch (IOException ex) {
            PromptUI.customPromptError("Error", null, "IOException: \n" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Update libraries depending on the action
     * @param libraryAction
     */
    private void updateLibraryTrees(Actions libraryAction) {
        if (libraryAction.equals(Actions.ADD)) {
            // If this is not the first library added, add it without resetting the state of the other libraries
            // Else, simply reset the tree to show the library
            if (m_model.getM_libraries().size() > 1){
                List<TreeItem<TreeViewItem>> libraryNodes = m_tree.getRoot().getChildren();
                List<TreeViewItem> libraryItems = TreeViewUtil.getTreeViewItems(libraryNodes);
                List<Library> libraries = m_model.getM_libraries();

                for (Library library : libraries) {
                    // If library is not in libraryItems, add new node
                    if (!TreeViewUtil.isLibraryInList(libraryItems, library)) {
                        TreeItem<TreeViewItem> newLibrary = library.getM_treeRoot();
                        newLibrary.setExpanded(true);
                        libraryNodes.add(newLibrary);
                    }
                }
            } else {
                updateTreeView();
            }
        } else if (libraryAction.equals(Actions.REMOVE_FROM_VIEW) || libraryAction.equals(Actions.DELETE)) {
            TreeItem<TreeViewItem> removedLibrary = m_tree.getSelectionModel().getSelectedItem();
            removedLibrary.getParent().getChildren().remove(removedLibrary);

            // If there are no more libraries, show a text label
            if (m_model.getM_libraries().size() < 1) {
                setEmptyLibraryUI();
            }
        }

    }

    private void clearTreeView() {
        //m_tree.setRoot(null);
        System.out.println("clearing treeview...");
        this.getChildren().clear();
    }

    /**
     * Construct the m_tree view
     *
     * @return TreeView<String>
     */
    private TreeView<TreeViewItem> createTrees(List<Library> libraries) {
        File dummyRootFile = new File(System.getProperty("user.dir"));
        TreeItem<TreeViewItem> root = new TreeItem<>(new Folder(dummyRootFile, true));

        for (Library library : libraries) {
            /*TreeItem<TreeViewItem> rootItem = TreeViewUtil.generateTreeItems(
                    library.getRootDir(), library.getRootDirPath(), m_model.getM_menuOptions().getM_leftPanelShowFolder(),
                    expandedPaths
            );*/
            TreeItem<TreeViewItem> rootItem = library.getM_treeRoot();
            rootItem.setExpanded(true);
            System.out.println("Added new root path:" + rootItem.toString());
            root.getChildren().add(rootItem);
        }
        TreeView<TreeViewItem> tree = new TreeView<>(root);
        tree.setShowRoot(false);

        if (m_model.getM_selectedCenterFolder() != null) {
            TreeViewUtil.setOpenFolder(tree, m_model.getM_selectedCenterFolder().getAbsolutePath());
        }

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

    public List<String> getExpandedPaths() {
        if (m_tree != null) {
            return TreeViewUtil.getExpandedPaths(m_tree);
        } else {
            return null;
        }
    }
}
