package com.teamgamma.musicmanagementsystem.model;

import com.teamgamma.musicmanagementsystem.model.ISearchMethod;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to implement search in the application.
 */
public class Searcher {
    private TreeItem<Item> m_searchTreeRoot;
    private String m_searchString;

    public Searcher(TreeItem<Item> rootToStartSearch, String searchString) {
        m_searchString = searchString;
        m_searchTreeRoot = findAllInstancesInTree(rootToStartSearch, caseInsensitiveStringSearch());
    }

    /**
     * Function to search through the tree passed in to find all elements that match the search criteria.
     *
     * @param parentNode        The parent node in the tree.
     * @param searchMethod      The search criteria method to use.
     * @return                  A copy of item that matches the search criteria as well as any of its children that match
     *                          or a node of DummyItem that represents there is no hits for the node passed in or any of
     *                          children.
     */
    private TreeItem<Item> findAllInstancesInTree(TreeItem<Item> parentNode, ISearchMethod searchMethod){
        List<TreeItem<Item>> listOfNodesHit = new ArrayList<>();

        ObservableList<TreeItem<Item>> allChildren = parentNode.getChildren();
        for (TreeItem<Item> currentChildren : allChildren) {
            boolean isDirectory = !currentChildren.isLeaf();
            if (searchMethod.isSearchHit(currentChildren.getValue())) {
                if (isDirectory) {
                    TreeItem<Item> childrenSearchResult = findAllInstancesInTree(currentChildren, searchMethod);

                    if (childrenSearchResult.getValue() instanceof DummyItem) {
                        // Just add the current node to results
                        listOfNodesHit.add(new TreeItem<>(currentChildren.getValue()));
                    } else {
                        listOfNodesHit.add(childrenSearchResult);
                    }
                    continue;

                } else {
                    listOfNodesHit.add(new TreeItem<>(currentChildren.getValue()));
                }
            }

            // Must be a directory if there are children so we have to search it to make sure we do not miss anything.
            if (isDirectory) {
                TreeItem<Item> results = findAllInstancesInTree(currentChildren, searchMethod);
                if (!(results.getValue() instanceof DummyItem)) {
                    // Results where found in this case so add it to the current node
                    listOfNodesHit.add(results);
                }
            }
        }

        if (listOfNodesHit.isEmpty()){
            return new TreeItem<>(new DummyItem());
        } else {
            TreeItem<Item> copyOfParent = new TreeItem<>(parentNode.getValue());
            copyOfParent.getChildren().addAll(listOfNodesHit);
            return copyOfParent;
        }
    }

    /**
     * Function to get the search results of the searcher.
     *
     * @return  The results of the search.
     */
    public TreeItem<Item> getTree() {
        return m_searchTreeRoot;
    }

    /**
     * Function to create a implementation of the ISearchMethod interface to check if the item contains the
     * search string in its name, case insensitive.
     *
     * @return  A implementation of a case insensitive check on the search string and the file name.
     */
    private ISearchMethod caseInsensitiveStringSearch(){
        return item -> {
            String stringToCheck = item.getFile().getName().toLowerCase();
            return stringToCheck.contains(m_searchString.toLowerCase());
        };
    }
}