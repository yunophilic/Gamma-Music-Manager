package com.teamgamma.musicmanagementsystem.model;

import com.teamgamma.musicmanagementsystem.util.FileTreeUtils;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to implement search in the appliclation.
 */
public class Searcher {
    private TreeItem<Item> m_searchTreeRoot;
    private String m_searchString;

    public Searcher(TreeItem<Item> rootToStartSearch, String searchString) {
        // Need to get all libraries and search through it.
        // Take a root and begin to search through it to see if it is there
        m_searchString = searchString;

        m_searchTreeRoot = new TreeItem<>(new DummyItem());
        m_searchTreeRoot.getChildren().add(findAllinstancesInTreeWrapper(rootToStartSearch));
    }


    private TreeItem<Item> findAllinstancesInTreeWrapper(TreeItem<Item> node){
        TreeItem<Item> rootNode = new TreeItem<Item>(new DummyItem());
        rootNode.getChildren().add(findAllInstancesInTree(node));
        return rootNode;

    }

    private TreeItem<Item> findAllInstancesInTree(TreeItem<Item> parentNode){
        List<TreeItem<Item>> listOfNodesHit = new ArrayList<>();

        ObservableList<TreeItem<Item>> allChildren = parentNode.getChildren();
        for (TreeItem<Item> currentChildren : allChildren) {
            System.out.println("Comparing " + currentChildren.getValue().getFile().getName());
            if (currentChildren.getValue().getFile().getName().contains(m_searchString)) {
                if (!currentChildren.isLeaf()) {
                    listOfNodesHit.add(findAllInstancesInTree(currentChildren));
                    continue;
                } else {
                    System.out.println("Search hit");
                    Item nodeItem = currentChildren.getValue();
                    TreeItem<Item> searchHit = new TreeItem<>(nodeItem);
                    searchHit.setGraphic(new ImageView(nodeItem.getFile().isDirectory() ? FileTreeUtils.FOLDER_ICON_URL : FileTreeUtils.SONG_ICON_URL));

                    listOfNodesHit.add(searchHit);
                }
            }
            // Must be a directory if there are children so we have to search it to make sure we do not miss anything.
            if (!currentChildren.isLeaf()) {
                TreeItem<Item> results = findAllInstancesInTree(currentChildren);
                if (!(results.getValue() instanceof DummyItem)) {
                    // Results where found in this case so add it to the current node
                    listOfNodesHit.add(results);
                }
            }
        }

        if (listOfNodesHit.isEmpty()){
            return new TreeItem<>(new DummyItem());
        } else {
            TreeItem<Item> currentNodeCopy = new TreeItem<>(parentNode.getValue());
            currentNodeCopy.getChildren().addAll(listOfNodesHit);
            return  currentNodeCopy;
        }
    }

    public TreeItem<Item> getTree() {
        return m_searchTreeRoot;
    }
}