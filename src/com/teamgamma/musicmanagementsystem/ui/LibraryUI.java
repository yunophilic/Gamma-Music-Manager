package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.misc.TreeViewUtil;
import com.teamgamma.musicmanagementsystem.model.Library;
import com.teamgamma.musicmanagementsystem.model.SongManager;
import com.teamgamma.musicmanagementsystem.model.SongManagerObserver;
import com.teamgamma.musicmanagementsystem.misc.CustomTreeCell;
import com.teamgamma.musicmanagementsystem.misc.TreeViewItem;

import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

import java.io.File;
import java.util.List;

/**
 * UI for the user's or external library
 */
public class LibraryUI extends StackPane {
    private SongManager m_model;
    private MusicPlayerManager m_musicPlayerManager;
    private TreeView<TreeViewItem> m_tree;

    public LibraryUI(SongManager model, MusicPlayerManager musicPlayerManager) {
        super();
        m_model = model;
        m_musicPlayerManager = musicPlayerManager;
        updateTreeView();
        setPaneStyle();
        registerAsLibraryObserver();
    }

    private void updateTreeView() {
        System.out.println("updating treeview...");
        List<Library> libraries = m_model.getM_libraries();

        if (libraries.isEmpty()) {
            this.getChildren().add(new Label("Add a library"));
        } else {
            m_tree = createTrees(libraries);
            this.getChildren().add(m_tree);
            setTreeCellFactory();
        }
    }

    private void setTreeCellFactory() {
        System.out.println("setting cell factory...");
        m_tree.setCellFactory(new Callback<TreeView<TreeViewItem>, TreeCell<TreeViewItem>>() {
            @Override
            public TreeCell<TreeViewItem> call(TreeView<TreeViewItem> arg) {
                // custom m_tree cell that defines a context menu for the root m_tree item
                return new CustomTreeCell(m_model, m_musicPlayerManager, m_tree, true);
            }
        });
    }

    private void registerAsLibraryObserver() {
        m_model.addSongManagerObserver(new SongManagerObserver() {
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

            @Override
            public void leftPanelOptionsChanged() {
                System.out.println("Left panel options in treeview");
                clearTreeView();
                updateTreeView();
            }
        });
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
        TreeItem<TreeViewItem> root = new TreeItem<>(new TreeViewItem(dummyRootFile, true));

        for (Library library : libraries) {
            TreeItem<TreeViewItem> rootItem = TreeViewUtil.generateTreeItems(
                    library.getM_rootDir(), library.getM_rootDirPath(), m_model.getM_menuOptions().getM_leftPanelShowFolder()
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
