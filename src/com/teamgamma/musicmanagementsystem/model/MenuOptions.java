package com.teamgamma.musicmanagementsystem.model;

/**
 * Manages choices made in the Menu Bar
 */
public class MenuOptions {
    private boolean m_centerPanelShowSubfolderFiles;
    private boolean m_leftPanelShowFoldersOnly;

    public MenuOptions(boolean centerPanelOption, boolean leftPanelOption) {
        this.m_centerPanelShowSubfolderFiles = centerPanelOption;
        this.m_leftPanelShowFoldersOnly = leftPanelOption;
    }

    /**
     * Set whether to show only folders in left panel
     * @param leftPanelShowFolder
     */
    public void setM_leftPanelShowFoldersOnly(boolean leftPanelShowFolder){
        m_leftPanelShowFoldersOnly = leftPanelShowFolder;
    }

    /**
     *  Get whether to show only folders in left panel
     * @return boolean
     */
    public boolean getM_leftPanelShowFoldersOnly(){
        return m_leftPanelShowFoldersOnly;
    }

    /**
     * Set whether to show files in subfolder in center panel
     * @param centerPanelShowSubfolderFiles
     */
    public void setM_centerPanelShowSubfolderFiles(boolean centerPanelShowSubfolderFiles){
        m_centerPanelShowSubfolderFiles = centerPanelShowSubfolderFiles;
    }

    /**
     * Get whether to show files in subfolder in center panel
     * @return boolean
     */
    public boolean getM_centerPanelShowSubfolderFiles(){
        return m_centerPanelShowSubfolderFiles;
    }
}
