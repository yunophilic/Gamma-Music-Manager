package com.teamgamma.musicmanagementsystem.util;

import javafx.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * FileActions abstract class
 * Contains a list of
 */
public abstract class FileActions extends ArrayList<Pair<Action, File>> {
    /**
     * Add new Pair into the ArrayList
     * @param action
     * @param file
     */
    public abstract void add(Action action, File file);
}
