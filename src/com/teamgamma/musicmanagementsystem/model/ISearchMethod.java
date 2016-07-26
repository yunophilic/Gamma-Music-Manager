package com.teamgamma.musicmanagementsystem.model;

/**
 * Interface to implement for the search criteria for the seracher class.
 */
public interface ISearchMethod {
    /**
     * Function to determine if the item passed in is a search hit or not.
     *
     * @param item      The item to check to see if it matches the search criteria.
     * @return          True if the item passed in matches search criteria. False otherwise.
     */
    boolean isSearchHit(Item item);
}
