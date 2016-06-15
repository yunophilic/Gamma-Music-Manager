package com.teamgamma.musicmanagementsystem.model;

/**
 * Manages choices made in the Menu Bar
 */
public class MenuOptions {
    private boolean m_leftPanelShowFolder;
    private boolean m_centerPanelShowSubfolderFiles;

    public MenuOptions(){
        this.m_leftPanelShowFolder = false;
        this.m_centerPanelShowSubfolderFiles = false;
    }

    /**
     * Set whether to show only folders in left panel
     * @param leftPanelShowFolder
     */
    public void setM_leftPanelShowFolder(boolean leftPanelShowFolder){
        m_leftPanelShowFolder = leftPanelShowFolder;
    }

    /**
     *  Get whether to show only folders in left panel
     * @return boolean
     */
    public boolean getM_leftPanelShowFolder(){
        return m_leftPanelShowFolder;
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
