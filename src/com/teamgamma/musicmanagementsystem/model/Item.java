package com.teamgamma.musicmanagementsystem.model;

import java.io.File;

/**
 * Interface for file tree objects
 */
public interface Item {
    /**
     * Get the File object associated with this Item
     */
    File getFile();

    /**
     * Change the File object depending on the specified path
     */
    void changeFile(String path);

    /**
     * Check if Item is root in the tree it's created in
     */
    boolean isRootItem();

    /**
     * Set this item as the root
     */
    default void setAsRightRootItem() {
        return;
    }

    /**
     * Check if this item is the root of the right file tree
     */
    default boolean isRightRootItem() {
        return false;
    }

    @Override
    boolean equals(Object object);
}
