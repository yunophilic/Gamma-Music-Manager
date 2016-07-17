package com.teamgamma.musicmanagementsystem.util;

import javafx.util.Pair;

import java.io.File;

/**
 * Concrete implementation of the file action class
 */
public class ConcreteFileActions extends FileActions{
    /**
     * Default constructor
     */
    public ConcreteFileActions() {
        super();
    }

    /**
     * Constructor accepting initial values for the first Pair in the list
     * @param action
     * @param file
     */
    public ConcreteFileActions(Action action, File file) {
        super();
        add(action, file);
    }

    /**
     * Add a new Action, Fie pair
     * @param action
     * @param file
     */
    @Override
    public void add(Action action, File file) {
        this.add(new Pair<>(action, file));
    }
}
