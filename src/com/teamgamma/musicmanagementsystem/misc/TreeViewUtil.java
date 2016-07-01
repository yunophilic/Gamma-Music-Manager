package com.teamgamma.musicmanagementsystem.misc;

import com.teamgamma.musicmanagementsystem.model.Library;
import com.teamgamma.musicmanagementsystem.model.Song;
import com.teamgamma.musicmanagementsystem.model.SongManager;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that provides functionality for the FileTree
 */
public class TreeViewUtil {
    private static final Image openFolderImage = new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator + "Status-folder-open-icon.png"));
    private static final Image folderImage = new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator + "folder-icon.png"));
    private static final Image songImage = new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator + "music-file-icon.png"));

    /**
     * Recursively create tree items from the files in a directory and return a reference to the root item,
     * Set nodes in expandedPaths to expanded state
     *
     * @return TreeItem<String> to the root item
     */
    public static TreeItem<TreeViewItem> generateTreeItems(File file, String dirPath, boolean showFolderOnly, List<String> expandedPaths) {
        System.out.println(file + ", " + dirPath);
        TreeItem<TreeViewItem> item = new TreeItem<>(
                (file.getAbsolutePath().equals(dirPath)) ? new TreeViewItem(file, true) : new TreeViewItem(file, false)
        );

        File treeItemFile = item.getValue().getM_file();
        System.out.println("$$$" + treeItemFile + ", " + treeItemFile.isDirectory());
        if (treeItemFile.isDirectory()) {
            item.setGraphic(new ImageView(folderImage));

            if (expandedPaths != null) {
                if (expandedPaths.contains(item.getValue().getM_file().getAbsolutePath())) {
                    item.setExpanded(true);
                }
            }
        } else {
            item.setGraphic(new ImageView(songImage));
        }

        File[] children = getFiles(file, showFolderOnly);

        if (children != null) {
            for (File child : children) {
                item.getChildren().add(generateTreeItems(child, dirPath, showFolderOnly, expandedPaths)); //recursion here
            }
        }

        return item;
    }

    private static File[] getFiles(File file, final boolean showFolderOnly) {
        return file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if (showFolderOnly) {
                        return f.isDirectory();
                    } else {
                        return f.isDirectory() || f.getAbsolutePath().endsWith(".mp3");
                    }
                }
            });
    }

    /**
     * Search for the TreeItem<TreeViewItem> from the specified TreeView<TreeViewItem> based on the given path
     *
     * @param path the specified path
     * @return TreeItem<TreeViewItem> or null if not found
     */
    public static TreeItem<TreeViewItem> searchTreeItem(TreeView<TreeViewItem> tree, String path) {
        return searchTreeItem(tree.getRoot(), path);
    }

    /**
     * Search for the TreeItem<TreeViewItem> from the sub-tree rooted at the specified node based on the given path
     *
     * @param node the specified node
     * @param path the specified path
     * @return TreeItem<TreeViewItem> or null if not found
     */
    private static TreeItem<TreeViewItem> searchTreeItem(TreeItem<TreeViewItem> node, String path) {
        //base case
        if (node.getValue().getM_file().getAbsolutePath().equals(path)) {
            //System.out.println("Returning node: " + node);
            return node;
        }

        //recursive case
        for (TreeItem<TreeViewItem> child : node.getChildren()) {
            TreeItem<TreeViewItem> target = searchTreeItem(child, path);
            if (target != null) {
                return target;
            }
        }

        return null;
    }

    /**
     * Set all tree item icons to closed folder icon or a song icon
     *
     * @param treeItem
     */
    public static void closeAllFoldersIcons(TreeItem<TreeViewItem> treeItem) {
        //System.out.println("#### closing file: " + treeItem.getValue());
        if (treeItem.getValue().getM_file().isDirectory()) {
            treeItem.setGraphic(new ImageView(folderImage));
        } else {
            treeItem.setGraphic(new ImageView(songImage));
        }
        if (!treeItem.getChildren().isEmpty()) {
            List<TreeItem<TreeViewItem>> childTreeItems = treeItem.getChildren();
            for (TreeItem<TreeViewItem> child : childTreeItems) {
                closeAllFoldersIcons(child);
            }
        }
    }

    /**
     * Set selected tree item's icon to open folder icon
     *
     * @param m_tree
     * @param filePath
     */
    public static void setOpenFolder(TreeView<TreeViewItem> m_tree, String filePath) {
        System.out.println("^^^^^ Tree root: " + m_tree.getRoot());
        TreeItem<TreeViewItem> selectedTreeItem = TreeViewUtil.searchTreeItem(m_tree, filePath);
        //System.out.println("@@@ Found treeitem: " + selectedTreeItem.getValue());
        System.out.println("@@@ Found treeitem: " + selectedTreeItem);
        selectedTreeItem.setGraphic(new ImageView(openFolderImage));
    }

    public static List<TreeViewItem> getTreeViewItems(List<TreeItem<TreeViewItem>> treeItems) {
        List<TreeViewItem> treeViewItems = new ArrayList<>();

        for (TreeItem<TreeViewItem> treeItem : treeItems) {
            treeViewItems.add(treeItem.getValue());
        }

        return treeViewItems;
    }

    public static boolean isLibraryInList(List<TreeViewItem> libraryNodes, Library library) {
        for (TreeViewItem libraryNode : libraryNodes) {
            String libraryNodePath = libraryNode.getM_file().getAbsolutePath();
            if (libraryNodePath.equals(library.getM_rootDirPath())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Update items of the tree depending on the action
     *
     * @param fileAction
     * @param changedFile Until a better implementation is found, this file is only used by the ADD, DELETE and RENAME action for now (because of watcher)
     * @param tree
     * @param model
     * @throws IOException
     */
    public static void updateTreeItems(Actions fileAction, File changedFile, TreeView<TreeViewItem> tree, SongManager model) throws IOException {
        switch (fileAction) {
            case ADD: {
                // Add new if it does not already exist (For watcher)
                TreeItem<TreeViewItem> searchedItem = searchTreeItem(tree, changedFile.getAbsolutePath());

                if (searchedItem == null) {
                    addNewNode(tree, model, changedFile.getName(), changedFile.getParent());
                }
                break;
            }
            case DRAG: {
                // Nothing to do for now...
                break;
            }
            case DROP: {
                // Add new node to destination file node
                addNewNode(tree, model, model.getM_fileToMove().getName(), model.getM_moveDest().getAbsolutePath());

                // Remove node from old folder it was in
                String deletedFilePath = model.getM_fileToMove().getAbsolutePath();
                TreeItem<TreeViewItem> removedFile = searchTreeItem(tree, deletedFilePath);

                removedFile.getParent().getChildren().remove(removedFile);
                break;
            }
            case DELETE: {
                String deletedFilePath = changedFile.getAbsolutePath();
                TreeItem<TreeViewItem> removedFile = searchTreeItem(tree, deletedFilePath);

                if (removedFile != null) {
                    removedFile.getParent().getChildren().remove(removedFile);
                }
                break;
            }
            case PASTE: {
                addNewNode(tree, model, model.getM_fileToCopy().getName(), model.getM_copyDest().getAbsolutePath());
                break;
            }
            case RENAME: {
                renameNode(changedFile, tree, model);
                break;
            }
            default: {
                throw new IOException("Invalid file action!");
            }
        }
    }

    private static void renameNode(File changedFile, TreeView<TreeViewItem> tree, SongManager model) {
        TreeItem<TreeViewItem> nodeToRename = searchTreeItem(tree, changedFile.getAbsolutePath());
        File renamedFile = model.getM_renamedFile();

        System.out.println("NEW FILE NAME: " + renamedFile);

        //TreeViewItem renamedItem = new TreeViewItem(renamedFile, model.isLibrary(renamedFile));

        //TreeItem<TreeViewItem> renamedNode = generateTreeItems(renamedFile, renamedFile.getParent(), model.getM_menuOptions().getM_leftPanelShowFolder());
        TreeItem<TreeViewItem> parentNode = nodeToRename.getParent();

        System.out.println("^^^^ RENAMING NODE: " + nodeToRename);
        System.out.println("^^^^ PARENT NODE: " + parentNode);
        //System.out.println("^^^^ NEW NODE: " + renamedNode);

        /*parentNode.getChildren().remove(nodeToRename);
        parentNode.getChildren().add(renamedNode);*/

        recursivelyRenameNodes(nodeToRename, renamedFile.getAbsolutePath(), model);
    }

    private static void recursivelyRenameNodes(TreeItem<TreeViewItem> node, String path, SongManager model) {
        File file = new File(path);

        TreeViewItem newItem = new TreeViewItem(file, model.isLibrary(file));
        System.out.println("^^^^ NEW ITEM: " + newItem);
        node.setValue(newItem);

        List<TreeItem<TreeViewItem>> children = node.getChildren();

        if (children != null) {
            for (TreeItem<TreeViewItem> child : children) {
                String newPath = path + File.separator + child.getValue().getM_file().getName();
                recursivelyRenameNodes(child, newPath, model);
            }
        }
    }

    private static void addNewNode(TreeView<TreeViewItem> tree, SongManager model, String fileName, String newParentPath) {
        String newFilePath = newParentPath + File.separator + fileName;
        File copiedFile = new File(newFilePath);

        TreeItem<TreeViewItem> newFileNode = generateTreeItems(copiedFile, newParentPath, model.getM_menuOptions().getM_leftPanelShowFolder(), null);
        TreeItem<TreeViewItem> parentFileNode = searchTreeItem(tree, newParentPath);

        if (newFileNode != null && parentFileNode != null) {
            parentFileNode.getChildren().add(newFileNode);
            parentFileNode.setExpanded(true);
        }
    }

    public static boolean isLibraryNodeInList(List<Library> libraries, TreeViewItem libraryNode) {
        for (Library library : libraries) {
            String libraryNodePath = libraryNode.getM_file().getAbsolutePath();
            if (libraryNodePath.equals(library.getM_rootDirPath())) {
                return true;
            }
        }

        return false;
    }

    public static void deleteLibrary(TreeView<TreeViewItem> tree, TreeItem<TreeViewItem> libraryNode) {
        TreeItem<TreeViewItem> root = tree.getRoot();

        root.getChildren().remove(libraryNode);
    }

    /**
     * Function to get a song that was selected if possible.
     *
     * @param tree  The tree view to that is being used.
     * @param selectedItem  The selected item in the tree.
     * @param model The model to in the song manager.
     *
     * @return The song that was selected or null if something was not selected yet.
     */
    public static Song getSongSelected(TreeView<TreeViewItem> tree, TreeViewItem selectedItem, SongManager model) {
        //get library song is in
        if (selectedItem != null && tree.getSelectionModel().getSelectedItem() != null) {
            TreeItem<TreeViewItem> selectedTreeItem = tree.getSelectionModel().getSelectedItem();
            while (!selectedTreeItem.getValue().isM_isRootPath()) {
                selectedTreeItem = selectedTreeItem.getParent();
            }
            //play song
            return model.getSongInLibrary(
                    selectedItem.getM_file(), selectedTreeItem.getValue().getM_file()
            );
        }
        return null;
    }

    /**
     * Get list of paths in tree that are expanded
     * @param m_tree
     * @return Arraylist of paths as String
     */
    public static List<String> getExpandedPaths(TreeView<TreeViewItem> m_tree) {
        List<String> expandedPaths = getExpandedPathsRecursively(m_tree.getRoot());

        return expandedPaths;
    }

    /**
     * Recursively get list of paths that are expanded
     * @param currentItem
     * @return Arraylist of paths as String
     */
    private static List<String> getExpandedPathsRecursively(TreeItem<TreeViewItem> currentItem) {
        List<String> expandedPaths = new ArrayList<>();
        List<TreeItem<TreeViewItem>> children = currentItem.getChildren();

        // Base case
        if (children.isEmpty()) {
            return expandedPaths;
        }

        // Add to list if this path is expanded
        if (currentItem.isExpanded()) {
            File file = currentItem.getValue().getM_file();
            expandedPaths.add(file.getAbsolutePath());
        }

        // Recursive case
        for (TreeItem<TreeViewItem> child : children) {
            List<String> childExpandedPaths = getExpandedPathsRecursively(child);

            expandedPaths.addAll(childExpandedPaths);
        }

        return expandedPaths;
    }
}
