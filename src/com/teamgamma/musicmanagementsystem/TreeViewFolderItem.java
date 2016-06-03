package com.teamgamma.musicmanagementsystem;

import java.io.File;

/**
 * Created by Karen on 2016-06-02.
 */
public class TreeViewFolderItem {
    private File path;
    private boolean isRootPath;

    public TreeViewFolderItem(File path, boolean isRootPath){
        this.path = path;
        this.isRootPath = isRootPath;
    }

    /**
     * For tree view, show absolute path if this is an root path, otherwise, show just the file name
     * @return
     */
    @Override
    public String toString() {
        if (isRootPath){
            return path.getAbsolutePath();
        } else {
            return path.getName();
        }
    }

    public File getPath(){
        return path;
    }
}
