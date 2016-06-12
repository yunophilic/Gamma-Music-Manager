package com.teamgamma.musicmanagementsystem.model;

/**
 * Manages choices made in the Menu Bar
 */
public class MenuOptions {
    private boolean showFolder;

    public MenuOptions(){
        this.showFolder = false;
    }

    public void setShowFolder(boolean showFolder){
        this.showFolder = showFolder;
    }

    public boolean getShowFolder(){
        return showFolder;
    }
}
