package com.teamgamma.musicmanagementsystem.model;

import java.io.File;

/**
 * Dummy Item object that does nothing (kind of like Null Object and used for dummy root on left and right pane)
 */
public class DummyItem implements Item {
    @Override
    public File getFile() {
        return new File("");
    }

    @Override
    public void changeFile(String path) {

    }

    @Override
    public boolean isRootItem() {
        return false;
    }
}