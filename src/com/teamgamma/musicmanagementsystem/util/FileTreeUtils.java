package com.teamgamma.musicmanagementsystem.util;

import com.teamgamma.musicmanagementsystem.model.*;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that provides functionality for the FileTree
 */
public class FileTreeUtils {
    public static final String OPEN_FOLDER_ICON_URL = "res" + File.separator + "Status-folder-open-icon.png";
    public static final String FOLDER_ICON_URL = "res" + File.separator + "folder-icon.png";
    public static final String SONG_ICON_URL = "res" + File.separator + "music-file-icon.png";

    private static String loadingFilePath;

    private static List<LoadingObserver> filePathObservers = new ArrayList<>();

    /**
     * Recursively create tree items from the files in a directory and return a reference to the root item,
     * Set nodes in expandedPaths to expanded state
     *
     * @param file current file
     * @param dirPath file path of root node
     * @param expandedPaths list of all expanded paths
     * @return TreeItem<Item> to the root item
     */
    public static TreeItem<Item> generateTreeItems(File file, String dirPath, List<String> expandedPaths) {
        setLoadingPathString(file);

        notifyObservers();
        System.out.println(file + ", " + dirPath);
        System.out.println("$$$" + file + ", " + file.isDirectory());

        TreeItem<Item> item;
        if(file.isFile()) {
            item = new TreeItem<>(new Song(file));
        } else {
            item = new TreeItem<>(
                    (file.getAbsolutePath().equals(dirPath)) ? new Folder(file, true) : new Folder(file, false)
            );

            if (expandedPaths != null && !expandedPaths.isEmpty()) {
                if (expandedPaths.contains(item.getValue().getFile().getAbsolutePath())) {
                    item.setExpanded(true);
                }
            }
        }

        File[] children = getSubFiles(file);

        if (children != null) {
            for (File child : children) {
                item.getChildren().add(generateTreeItems(child, dirPath, expandedPaths)); //recursion here
            }
        }

        return item;
    }

    /**
     * Sets loadingFilePath variable the file path that is currently being loaded, to display on loading screen.
     * Has a 60 character limit. If file path is too long, break it down to two separate halves and separate
     * with ellipses.
     *
     * @param file that is being loaded
     */
    private static void setLoadingPathString(File file) {
        String filePath = file.getAbsolutePath();
        final int MAX_FILE_ROW_LENGTH = 60;
        if (filePath.length() < MAX_FILE_ROW_LENGTH) {
            loadingFilePath = filePath;
        } else {
            final int HALF_WAY_POINT = MAX_FILE_ROW_LENGTH / 2;
            final String ELLIPSES_BREAK = "...";
            String firstHalf = filePath.substring(0, HALF_WAY_POINT);
            String secondHalf = filePath.substring(filePath.length() - HALF_WAY_POINT);

            int firstHalfEndTrimIndex = firstHalf.length() - 1;
            while (0 < firstHalfEndTrimIndex) {
                if (firstHalf.charAt(firstHalfEndTrimIndex) != '\\') {
                    firstHalfEndTrimIndex--;
                } else {
                    firstHalfEndTrimIndex++;
                    firstHalf = firstHalf.substring(0, firstHalfEndTrimIndex);
                    break;
                }
            }

            int secondHalfEndTrimIndex = 0;
            final int secondHalfLength = secondHalf.length();
            while (secondHalfEndTrimIndex < secondHalfLength) {
                if (secondHalf.charAt(secondHalfEndTrimIndex) != '\\') {
                    secondHalfEndTrimIndex++;
                } else {
                    secondHalf = secondHalf.substring(secondHalfEndTrimIndex);
                    break;
                }
            }
            loadingFilePath = firstHalf + ELLIPSES_BREAK + secondHalf;
        }
    }

    /**
     * Helper function for generateTreeItems()
     *
     * @param file The file specified to get the sub files
     */
    private static File[] getSubFiles(File file) {
        return file.listFiles(f -> f.isDirectory() || FileManager.isAccept(f));
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
     * Copy tree rooted at node
     *
     * @param node the specified node
     * @return root node of the copied tree
     */
    public static TreeItem<Item> copyTree(TreeItem<Item> node) {
        TreeItem<Item> nodeCopy = new TreeItem<>();
        Item item = node.getValue();
        nodeCopy.setValue(item);

        for (TreeItem<Item> child : node.getChildren()) {
            nodeCopy.getChildren().add(copyTree(child));
        }

        return nodeCopy;
    }

    /**
     * Convert list of <TreeItem<Item>> to list of Item
     *
     * @param treeItems the list to be converted
     */
    public static List<Item> getItems(List<TreeItem<Item>> treeItems) {
        List<Item> items = new ArrayList<>();

        for (TreeItem<Item> treeItem : treeItems) {
            items.add(treeItem.getValue());
        }

        return items;
    }

    /**
     * Check if library is in the list of nodes
     *
     * @param libraryNodes the list of library nodes
     * @param library the library to check
     */
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
     * @param model the model
     * @param tree the tree to be updated
     * @param fileAction the file action
     * @param changedFile the update file
     *
     * @throws IOException
     */
    public static void updateTreeItems(SongManager model, TreeView<Item> tree, Action fileAction, File changedFile) throws IOException {
        switch (fileAction) {
            case ADD: {
                // Add new if it does not already exist (For watcher)
                TreeItem<Item> searchedItem = searchTreeItem(tree.getRoot(), changedFile.getAbsolutePath());
                if (searchedItem == null) {
                    createNewNodes(tree, changedFile.getName(), changedFile.getParent());
                }
                break;
            }

            case DRAG: {
                // Nothing to do for now...
                break;
            }

            case DROP: {
                File fileToMove = model.getFileToMove();
                TreeItem<Item> nodeToMove = searchTreeItem(tree.getRoot(), fileToMove.getAbsolutePath());

                // Search in model if it does not exists in current tree
                if (nodeToMove == null) {
                    nodeToMove = model.search(fileToMove);
                }

                TreeItem<Item> destParentNode = searchTreeItem(tree.getRoot(), model.getM_moveDest().getAbsolutePath());

                if (destParentNode == null) {
                    deleteNode(nodeToMove);
                } else if(!(destParentNode.getValue() instanceof DummyItem)) {
                    moveNode(nodeToMove, destParentNode);
                }
                break;
            }

            case DELETE: {
                String deletedFilePath = changedFile.getAbsolutePath();
                TreeItem<Item> removedNode = searchTreeItem(tree.getRoot(), deletedFilePath);
                if (removedNode != null) {
                    deleteNode(removedNode);
                }
                break;
            }

            case PASTE: {
                createNewNodes(tree, model.getFileToCopy().getName(), model.getM_copyDest().getAbsolutePath());
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

    /**
     * Update items of the tree depending on the action
     *
     * @param model the model
     * @param rootNode the root node of tree to be updated
     * @param fileAction the file action
     * @param changedFile the update file
     *
     * @throws IOException
     */
    public static void updateTreeItems(SongManager model, TreeItem<Item> rootNode, Action fileAction, File changedFile) throws IOException {
        TreeView<Item> tree = new TreeView<>(rootNode);
        updateTreeItems(model, tree, fileAction, changedFile);
    }

    /**
     * Rename node based on the update file
     *
     * @param changedFile the update file
     * @param tree the tree view
     * @param model the model
     */
    private static void renameNode(File changedFile, TreeView<Item> tree, SongManager model) {
        File renamedFile = model.getM_renamedFile();

        System.out.println("NEW FILE NAME: " + renamedFile);

        TreeItem<Item> nodeToRename = searchTreeItem(tree.getRoot(), changedFile.getAbsolutePath());

        if (nodeToRename == null) {
            return;
        }

        TreeItem<Item> parentNode = nodeToRename.getParent();

        System.out.println("^^^^ RENAMING NODE: " + nodeToRename);
        System.out.println("^^^^ PARENT NODE: " + parentNode);

        recursivelyRenameNodes(nodeToRename, renamedFile.getAbsolutePath());
    }

    /**
     * Helper function for renameNode(), rename nodes recursively starting from the root
     *
     * @param node the root node to be renamed
     * @param path the new path
     */
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

    /**
     * Create fresh new nodes recursively with new Item references based on given fileName and newParentPath
     *
     * @param tree The tree where a new node is to be created
     * @param fileName The file name that the new node will contain
     * @param newParentPath The path of the new parent
     */
    private static void createNewNodes(TreeView<Item> tree, String fileName, String newParentPath) {
        String newFilePath = newParentPath + File.separator + fileName;
        File copiedFile = new File(newFilePath);

        TreeItem<Item> newFileNode = generateTreeItems(copiedFile, newParentPath, null);
        TreeItem<Item> parentFileNode = searchTreeItem(tree.getRoot(), newParentPath);

        if (newFileNode != null && parentFileNode != null) {
            parentFileNode.getChildren().add(newFileNode);
            parentFileNode.setExpanded(true);
        }
    }

    /**
     * Recursively move nodeToMove to destParentNode while keeping the same Item object reference on all nodes
     *
     * @param nodeToMove the node intended to be moved
     * @param destParentNode the destination parent node
     */
    private static void moveNode(TreeItem<Item> nodeToMove, TreeItem<Item> destParentNode) {
        String fileName = nodeToMove.getValue().getFile().getName();
        String destPath = destParentNode.getValue().getFile().getAbsolutePath();
        String newPath = destPath + File.separator + fileName;

        destParentNode.getChildren().add(moveNodesRecursively(nodeToMove, newPath));
        deleteNode(nodeToMove);
    }

    /**
     * Helper function for moveNode(), doesn't actually move the nodes, create nodes with the update file while
     * keeping the same Item reference for all nodes
     *
     * @param nodeToMove node intended to be moved
     * @param path the new path after moving the files
     */
    private static TreeItem<Item> moveNodesRecursively(TreeItem<Item> nodeToMove, String path) {
        Item item = nodeToMove.getValue();
        item.changeFile(path);

        TreeItem<Item> newNode = new TreeItem<>();
        newNode.setValue(item);

        List<TreeItem<Item>> children = nodeToMove.getChildren();
        if (children != null) {
            for (TreeItem<Item> child : children) {
                String newPath = path + File.separator + child.getValue().getFile().getName();
                newNode.getChildren().add(moveNodesRecursively(child, newPath));
            }
        }

        return newNode;
    }

    /**
     * Delete node recursively
     *
     * @param nodeToDelete node to be deleted
     */
    private static void deleteNode(TreeItem<Item> nodeToDelete) {
        nodeToDelete.getParent().getChildren().remove(nodeToDelete);
    }

    /**
     * Get list of paths in tree that are expanded
     *
     * @param tree the tree
     * @return Arraylist of paths as String
     */
    public static List<String> getExpandedPaths(TreeView<Item> tree) {
        return getExpandedPathsRecursively(tree.getRoot());
    }

    /**
     * Recursively get list of paths that are expanded in the sub-tree rooted at node
     *
     * @param node the root node
     * @return list of paths as String
     */
    private static List<String> getExpandedPathsRecursively(TreeItem<Item> node) {
        List<String> expandedPaths = new ArrayList<>();
        List<TreeItem<Item>> children = node.getChildren();

        // Base case
        if (children.isEmpty()) {
            return expandedPaths;
        }

        // Add to list if this path is expanded
        if (node.isExpanded()) {
            File file = node.getValue().getFile();
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
        if (file.isDirectory()) {
            if (expandedPaths != null && !expandedPaths.isEmpty()) {
                if (expandedPaths.contains(file.getAbsolutePath())) {
                    node.setExpanded(true);
                }
            }
        }

        List<TreeItem<Item>> children = node.getChildren();
        for (TreeItem<Item> child : children) {
            setTreeExpandedState(child, expandedPaths);
        }
    }

    /**
     * Hide files in the tree by removing the file nodes
     *
     * @param tree
     */
    public static void hideFiles(TreeView<Item> tree) {
        List<TreeItem<Item>> nodesToRemove = new ArrayList<>();
        for (TreeItem<Item> child : tree.getRoot().getChildren()) {
            nodesToRemove.addAll(getFileNodes(child));
        }
        System.out.println("FILE NODES FOUND: " + nodesToRemove);

        for (int i = 0; i < nodesToRemove.size(); i++) {
            TreeItem<Item> node = nodesToRemove.get(i);
            deleteNode(node);
        }
    }

    /**
     * Find all song nodes (leaves) in the tree
     *
     * @param node
     * @return a list of TreeItem nodes that contain a Song
     */
    private static List<TreeItem<Item>> getFileNodes(TreeItem<Item> node) {
        List<TreeItem<Item>> nodesToRemove = new ArrayList<>();
        for (TreeItem<Item> child: node.getChildren()) {
            if (child.getValue().getFile().isDirectory()){
                nodesToRemove.addAll(getFileNodes(child));
            } else {
                nodesToRemove.add(child);
            }
        }

        return nodesToRemove;
    }

    /**
     * Show files in the tree by adding the file nodes
     *
     * @param uiRootNode
     * @param modelNode
     */
    public static void showFiles(TreeItem<Item> uiRootNode, TreeItem<Item> modelNode) {
        for (TreeItem<Item> modelChild: modelNode.getChildren()) {
            if (modelChild.getValue().getFile().isDirectory()) {
                showFiles(uiRootNode, modelChild);
            } else {
                if (searchTreeItem(uiRootNode, modelChild.getValue().getFile().getAbsolutePath()) == null) {
                    TreeItem<Item> newUINode = new TreeItem<>(modelChild.getValue());
                    TreeItem<Item> uiNode = searchTreeItem(uiRootNode, modelNode.getValue().getFile().getAbsolutePath());
                    uiNode.getChildren().add(newUINode);
                }
            }
        }
    }

    /**
     * Get what is currently being loaded into the application
     *
     * @return the String of the file path being loaded
     */
    public static String getLoadingFilePath() {
        return loadingFilePath;
    }

    /**
     * Add observer to filePathObservers
     *
     * @param  observer to add
     */
    public static void addObserver(LoadingObserver observer) {
        filePathObservers.add(observer);
    }

    /**
     * Notify all observers in filePathObservers when a new component is being loaded
     */
    private static void notifyObservers() {
        for (LoadingObserver observer : filePathObservers) {
            observer.loadNextElement();
        }
    }
}
