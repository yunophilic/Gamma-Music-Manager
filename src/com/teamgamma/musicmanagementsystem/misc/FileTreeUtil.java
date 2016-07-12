package com.teamgamma.musicmanagementsystem.misc;

import com.teamgamma.musicmanagementsystem.model.*;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that provides functionality for the FileTree
 */
public class FileTreeUtil {
    private static final Image openFolderImage = new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator + "Status-folder-open-icon.png"));
    private static final Image folderImage = new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator + "folder-icon.png"));
    private static final Image songImage = new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator + "music-file-icon.png"));

    /*public static TreeItem<Item> generateTreeItems(File file, String dirPath, boolean showFolderOnly, List<String> expandedPaths) {
        System.out.println(file + ", " + dirPath);
        TreeItem<Item> item = new TreeItem<>(
                (file.getAbsolutePath().equals(dirPath)) ? new Item(file, true) : new Item(file, false)
        );

        File treeItemFile = item.getValue().getFile();
        System.out.println("$$$" + treeItemFile + ", " + treeItemFile.isDirectory());
        if (treeItemFile.isDirectory()) {
            item.setGraphic(new ImageView(folderImage));

            if (expandedPaths != null && !expandedPaths.isEmpty()) {
                if (expandedPaths.contains(item.getValue().getFile().getAbsolutePath())) {
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
                        return f.isDirectory() || FileManager.isAccept(f);
                    }
                }
            });
    }*/

    /**
     * Recursively create tree items from the files in a directory and return a reference to the root item,
     * Set nodes in expandedPaths to expanded state
     *
     * @return TreeItem<Item> to the root item
     */
    public static TreeItem<Item> generateTreeItems(File file, String dirPath, List<String> expandedPaths) {
        System.out.println(file + ", " + dirPath);
        System.out.println("$$$" + file + ", " + file.isDirectory());

        TreeItem<Item> item;
        if(!file.isDirectory()) {
            item = new TreeItem<>(new Song(file));
            item.setGraphic(new ImageView(songImage));
        } else {
            item = new TreeItem<>(
                    (file.getAbsolutePath().equals(dirPath)) ? new Folder(file, true) : new Folder(file, false)
            );

            item.setGraphic(new ImageView(folderImage));

            if (expandedPaths != null && !expandedPaths.isEmpty()) {
                if (expandedPaths.contains(item.getValue().getFile().getAbsolutePath())) {
                    item.setExpanded(true);
                }
            }
        }

        File[] children = getFiles(file);

        if (children != null) {
            for (File child : children) {
                item.getChildren().add(generateTreeItems(child, dirPath, expandedPaths)); //recursion here
            }
        }

        return item;
    }

    private static File[] getFiles(File file) {
        return file.listFiles(f -> f.isDirectory() || FileManager.isAccept(f));
    }

    /**
     * Search for the TreeItem<Item> from the specified TreeView<Item> based on the given path
     *
     * @param path the specified path
     * @return TreeItem<Item> or null if not found
     */
    public static TreeItem<Item> searchTreeItem(TreeView<Item> tree, String path) {
        return searchTreeItem(tree.getRoot(), path);
    }

    /**
     * Search for the TreeItem<Item> from the sub-tree rooted at the specified node based on the given path
     *
     * @param node the specified node
     * @param path the specified path
     * @return TreeItem<Item> or null if not found
     */
    public static TreeItem<Item> searchTreeItem(TreeItem<Item> node, String path) {
        //base case
        if (node.getValue().getFile().getAbsolutePath().equals(path)) {
            //System.out.println("Returning node: " + node);
            return node;
        }

        //recursive case
        for (TreeItem<Item> child : node.getChildren()) {
            TreeItem<Item> target = searchTreeItem(child, path);
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
    public static void closeAllFoldersIcons(TreeItem<Item> treeItem) {
        //System.out.println("#### closing file: " + treeItem.getValue());
        if (treeItem.getValue().getFile().isDirectory()) {
            treeItem.setGraphic(new ImageView(folderImage));
        } else {
            treeItem.setGraphic(new ImageView(songImage));
        }
        if (!treeItem.getChildren().isEmpty()) {
            List<TreeItem<Item>> childTreeItems = treeItem.getChildren();
            for (TreeItem<Item> child : childTreeItems) {
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
    public static void setOpenFolder(TreeView<Item> m_tree, String filePath) {
        System.out.println("^^^^^ Tree root: " + m_tree.getRoot());
        TreeItem<Item> selectedTreeItem = FileTreeUtil.searchTreeItem(m_tree, filePath);
        //System.out.println("@@@ Found treeitem: " + selectedTreeItem.getValue());
        System.out.println("@@@ Found treeitem: " + selectedTreeItem);
        selectedTreeItem.setGraphic(new ImageView(openFolderImage));
    }

    public static List<Item> getTreeViewItems(List<TreeItem<Item>> treeItems) {
        List<Item> items = new ArrayList<>();

        for (TreeItem<Item> treeItem : treeItems) {
            items.add(treeItem.getValue());
        }

        return items;
    }

    public static boolean isLibraryInList(List<Item> libraryNodes, Library library) {
        for (Item libraryNode : libraryNodes) {
            String libraryNodePath = libraryNode.getFile().getAbsolutePath();
            if (libraryNodePath.equals(library.getRootDirPath())) {
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
    public static void updateTreeItems(Actions fileAction, File changedFile, TreeView<Item> tree, SongManager model) throws IOException {
        switch (fileAction) {
            case ADD: {
                // Add new if it does not already exist (For watcher)
                TreeItem<Item> searchedItem = searchTreeItem(tree, changedFile.getAbsolutePath());
                if (searchedItem == null) {
                    addNewNode(tree, changedFile.getName(), changedFile.getParent());
                }
                break;
            }

            case DRAG: {
                // Nothing to do for now...
                break;
            }

            case DROP: {
                // Add new node to destination file node
                /*addNewNode(tree, model.getFileToMove().getName(), model.getM_moveDest().getAbsolutePath());

                // Remove node from old folder it was in
                String deletedFilePath = model.getFileToMove().getAbsolutePath();
                TreeItem<Item> removedFile = searchTreeItem(tree, deletedFilePath);

                removedFile.getParent().getChildren().remove(removedFile);*/

                TreeItem<Item> nodeToMove = searchTreeItem(tree, model.getFileToMove().getAbsolutePath());

                System.out.println("...NODE TO MOVE: " + nodeToMove.getValue());

                TreeItem<Item> destParentNode = searchTreeItem(tree, model.getM_moveDest().getAbsolutePath());

                System.out.println("...DESTINATION PARENT: " + destParentNode.getValue());

                moveNode(nodeToMove, destParentNode);

                break;
            }

            case DELETE: {
                String deletedFilePath = changedFile.getAbsolutePath();
                TreeItem<Item> removedNode = searchTreeItem(tree, deletedFilePath);
                if (removedNode != null) {
                    deleteNode(removedNode);
                }
                break;
            }

            case PASTE: {
                addNewNode(tree, model.getFileToCopy().getName(), model.getM_copyDest().getAbsolutePath());
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

    private static void renameNode(File changedFile, TreeView<Item> tree, SongManager model) {
        TreeItem<Item> nodeToRename = searchTreeItem(tree, changedFile.getAbsolutePath());
        File renamedFile = model.getM_renamedFile();

        System.out.println("NEW FILE NAME: " + renamedFile);

        TreeItem<Item> parentNode = nodeToRename.getParent();

        System.out.println("^^^^ RENAMING NODE: " + nodeToRename);
        System.out.println("^^^^ PARENT NODE: " + parentNode);

        recursivelyRenameNodes(nodeToRename, renamedFile.getAbsolutePath());
    }

    private static void recursivelyRenameNodes(TreeItem<Item> node, String path) {
        Item item = node.getValue();
        item.changeFile(path);

        System.out.println("^^^^ CHANGED ITEM: " + item);

        node.setValue(null); //this line ensures the renamed node display string gets refreshed
        node.setValue(item);

        List<TreeItem<Item>> children = node.getChildren();
        if (children != null) {
            for (TreeItem<Item> child : children) {
                String newPath = path + File.separator + child.getValue().getFile().getName();
                recursivelyRenameNodes(child, newPath);
            }
        }
    }

    private static void addNewNode(TreeView<Item> tree, String fileName, String newParentPath) {
        String newFilePath = newParentPath + File.separator + fileName;
        File copiedFile = new File(newFilePath);

        TreeItem<Item> newFileNode = generateTreeItems(copiedFile, newParentPath, null);
        TreeItem<Item> parentFileNode = searchTreeItem(tree, newParentPath);

        if (newFileNode != null && parentFileNode != null) {
            parentFileNode.getChildren().add(newFileNode);
            parentFileNode.setExpanded(true);
        }
    }

    private static void moveNode(TreeItem<Item> nodeToMove, TreeItem<Item> destParentNode) {
        String fileName = nodeToMove.getValue().getFile().getName();
        String destPath = destParentNode.getValue().getFile().getAbsolutePath();
        String newPath = destPath + File.separator + fileName;

        destParentNode.getChildren().add(moveNodesRecursively(nodeToMove, newPath));
        deleteNode(nodeToMove);
    }

    private static TreeItem<Item> moveNodesRecursively(TreeItem<Item> nodeToMove, String path) {
        Item item = nodeToMove.getValue();
        item.changeFile(path);

        TreeItem<Item> newNode = new TreeItem<>();
        newNode.setValue(item);
        newNode.setGraphic(nodeToMove.getGraphic());

        List<TreeItem<Item>> children = nodeToMove.getChildren();
        if (children != null) {
            for (TreeItem<Item> child : children) {
                String newPath = path + File.separator + child.getValue().getFile().getName();
                newNode.getChildren().add(moveNodesRecursively(child, newPath));
            }
        }

        return newNode;
    }

    private static void deleteNode(TreeItem<Item> nodeToDelete) {
        nodeToDelete.getParent().getChildren().remove(nodeToDelete);
    }

    /*public static boolean isLibraryNodeInList(List<Library> libraries, Item libraryNode) {
        for (Library library : libraries) {
            String libraryNodePath = libraryNode.getFile().getAbsolutePath();
            if (libraryNodePath.equals(library.getRootDirPath())) {
                return true;
            }
        }

        return false;
    }

    public static void deleteLibrary(TreeView<Item> tree, TreeItem<Item> libraryNode) {
        TreeItem<Item> root = tree.getRoot();

        root.getChildren().remove(libraryNode);
    }*/

    /**
     * Function to get a song that was selected if possible.
     *
     * @param tree  The tree view to that is being used.
     * @param selectedItem  The selected item in the tree.
     * @param model The model to in the song manager.
     *
     * @return The song that was selected or null if something was not selected yet.
     */
    /*public static Song getSongSelected(TreeView<Item> tree, Item selectedItem, SongManager model) {
        //get library song is in
        if (selectedItem != null && tree.getSelectionModel().getSelectedItem() != null) {
            TreeItem<Item> selectedTreeItem = tree.getSelectionModel().getSelectedItem();
            while (!selectedTreeItem.getValue().isM_isRootPath()) {
                selectedTreeItem = selectedTreeItem.getParent();
            }
            //play song
            return model.getSongInLibrary(
                    selectedItem.getFile(), selectedTreeItem.getValue().getFile()
            );
        }
        return null;
    }*/

    /**
     * Get list of paths in tree that are expanded
     * @param m_tree
     * @return Arraylist of paths as String
     */
    public static List<String> getExpandedPaths(TreeView<Item> m_tree) {
        return getExpandedPathsRecursively(m_tree.getRoot());
    }

    /**
     * Recursively get list of paths that are expanded
     * @param currentItem
     * @return Arraylist of paths as String
     */
    private static List<String> getExpandedPathsRecursively(TreeItem<Item> currentItem) {
        List<String> expandedPaths = new ArrayList<>();
        List<TreeItem<Item>> children = currentItem.getChildren();

        // Base case
        if (children.isEmpty()) {
            return expandedPaths;
        }

        // Add to list if this path is expanded
        if (currentItem.isExpanded()) {
            File file = currentItem.getValue().getFile();
            expandedPaths.add(file.getAbsolutePath());
        }

        // Recursive case
        for (TreeItem<Item> child : children) {
            List<String> childExpandedPaths = getExpandedPathsRecursively(child);

            expandedPaths.addAll(childExpandedPaths);
        }

        return expandedPaths;
    }

    /**
     * Copy tree rooted at rootNode, newly created tree hold the same reference for the values
     *
     * @param node the root tree item
     * @param expandedPaths list of expanded paths
     */
    public static void setTreeExpandedState(TreeItem<Item> node, List<String> expandedPaths) {
        File file = node.getValue().getFile();
        if (file.isDirectory())
            if (expandedPaths != null && !expandedPaths.isEmpty()) {
                if (expandedPaths.contains(file.getAbsolutePath())) {
                    node.setExpanded(true);
                }
            }

        List<TreeItem<Item>> children = node.getChildren();
        for(TreeItem<Item> child : children) {
            setTreeExpandedState(child, expandedPaths);
        }
    }
}
