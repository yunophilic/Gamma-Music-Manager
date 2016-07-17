package com.teamgamma.musicmanagementsystem.util;

import javafx.util.Pair;

import java.io.File;

/**
 * Class representing the empty FileAction
 */
public class EmptyFileAction extends FileActions {
    /**
     * Default constructor
     */
    public EmptyFileAction() {
        super();
        this.add(new Pair<>(Action.NONE, null));
    }

    /**
     * Do not add anything to the list (since this is a class representing an empty file action)
     * @param action
     * @param file
     */
    @Override
    public void add(Action action, File file) {
        // Do nothing (since this is empty file action class)
    }
}
