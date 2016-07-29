package com.teamgamma.musicmanagementsystem.model;

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
    private boolean m_showFilesInFolderHits;

    /**
     * Constructor
     *
     * @param rootToStartSearch         Root element to start the search.
     * @param searchString              String to search on.
     * @param showFilesInFolderHits     A flag to determine if the searcher should show all files in a folder
     *                                  that is a hit.
     */
    public Searcher(TreeItem<Item> rootToStartSearch, String searchString, boolean showFilesInFolderHits) {
        m_searchString = searchString;
        m_showFilesInFolderHits = showFilesInFolderHits;
        m_searchTreeRoot = findAllInstancesInTree(rootToStartSearch, caseInsensitiveStringSearch());
    }

    /**
     * Function to search through the tree passed in to find all elements that match the search criteria.
     *
     * @param parentNode        The parent node in the tree.
     * @param searchMethod      The search criteria method to use.
     * @return                  A TreeItem containing a copy of item that matches the search criteria as well as any
     *                          of its children that match or a TreeItem containing a DummyItem that represents
     *                          there is no hits for the node passed in or any of children.
     */
    private TreeItem<Item> findAllInstancesInTree(TreeItem<Item> parentNode, ISearchMethod searchMethod){
        List<TreeItem<Item>> listOfNodesHit = new ArrayList<>();

        ObservableList<TreeItem<Item>> allChildren = parentNode.getChildren();
        for (TreeItem<Item> currentChildren : allChildren) {
            boolean hasChildren = !currentChildren.isLeaf();
            if (searchMethod.isSearchHit(currentChildren.getValue())) {
                if (hasChildren) {
                    TreeItem<Item> childrenSearchResult = findAllInstancesInTree(currentChildren, searchMethod);

                    if (childrenSearchResult.getValue() instanceof DummyItem) {
                        // Just add the current node to results
                        listOfNodesHit.add(createExpandedNode(currentChildren.getValue()));
                    } else {
                        listOfNodesHit.add(childrenSearchResult);
                    }
                    // Skip to next child the parent node has.
                    continue;

                } else {
                    listOfNodesHit.add(createExpandedNode(currentChildren.getValue()));
                }
            }

            // Must be a directory if there are children so we have to search it to make sure we do not miss anything.
            if (hasChildren) {
                TreeItem<Item> results = findAllInstancesInTree(currentChildren, searchMethod);
                results.setExpanded(true);
                if (!(results.getValue() instanceof DummyItem)) {
                    // Results where found in this case so add it to the current node
                    listOfNodesHit.add(results);
                }
            } else if (m_showFilesInFolderHits && searchMethod.isSearchHit(parentNode.getValue())){
                listOfNodesHit.add(createExpandedNode(currentChildren.getValue()));
            }
        }

        if (listOfNodesHit.isEmpty()){
            return new TreeItem<>(new DummyItem());
        } else {
            TreeItem<Item> copyOfParent = createExpandedNode(parentNode.getValue());
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
     * Function to update the search results based on the new tree that is passed in.
     *
     * @param root      The tree to search on.
     */
    public void updateSearchResults(TreeItem<Item> root) {
        m_searchTreeRoot = findAllInstancesInTree(root, caseInsensitiveStringSearch());
    }

    /**
     * Function to configure search to show files in folders that are hits in the search results. This will only show
     * files that are direct children of a folder that is a hit.
     *
     * @param showFilesInFolderHits     The flag to to set if you want search to show all the files in a folder that is a hit.
     */
    public void setShowFilesInFolderHits(boolean showFilesInFolderHits) {
        m_showFilesInFolderHits = showFilesInFolderHits;
    }

    /**
     * Function to create a implementation of the ISearchMethod interface to check if the item contains the
     * search string in its name, case insensitive.
     *
     * @return  A implementation of a case insensitive check on the search string and the file name.
     */
    private ISearchMethod caseInsensitiveStringSearch(){
        return (item) -> {
            String stringToCheck = item.getFile().getName().toLowerCase();
            return stringToCheck.contains(m_searchString.toLowerCase());
        };
    }

    /**
     * Function to create a node and copy it.
     *
     * @param value     The value to put the item in it.
     * @return          A new node containing the item passed in.
     */
    private TreeItem<Item> createExpandedNode(Item value){
        TreeItem<Item> node = new TreeItem<>(value);
        node.setExpanded(true);

        return node;
    }
}