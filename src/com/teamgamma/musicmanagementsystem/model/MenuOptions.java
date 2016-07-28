package com.teamgamma.musicmanagementsystem.model;

/**
 * Manages choices made in the Menu Bar
 */
public class MenuOptions {
    private boolean m_centerPanelShowSubfolderFiles;
    private boolean m_leftPanelShowFoldersOnly;
    private boolean m_showFilesInFolderSearchHit;

    public MenuOptions(boolean centerPanelOption, boolean leftPanelOption, boolean showFilesInFolderSearchHit) {
        this.m_centerPanelShowSubfolderFiles = centerPanelOption;
        this.m_leftPanelShowFoldersOnly = leftPanelOption;
        this.m_showFilesInFolderSearchHit = showFilesInFolderSearchHit;
    }

    /**
     * Set whether to show only folders in left panel
     *
     * @param leftPanelShowFolder
     */
    public void setM_leftPanelShowFoldersOnly(boolean leftPanelShowFolder){
        m_leftPanelShowFoldersOnly = leftPanelShowFolder;
    }

    /**
     *  Get whether to show only folders in left panel
     *
     * @return boolean
     */
    public boolean getM_leftPanelShowFoldersOnly(){
        return m_leftPanelShowFoldersOnly;
    }

    /**
     * Set whether to show files in subfolder in center panel
     *
     * @param centerPanelShowSubfolderFiles
     */
    public void setM_centerPanelShowSubfolderFiles(boolean centerPanelShowSubfolderFiles){
        m_centerPanelShowSubfolderFiles = centerPanelShowSubfolderFiles;
    }

    /**
     * Get whether to show files in subfolder in center panel
     *
     * @return boolean
     */
    public boolean getM_centerPanelShowSubfolderFiles(){
        return m_centerPanelShowSubfolderFiles;
    }

    /**
     * Function to configure the application to show files that are in a folder that is a search hit.
     *
     * @param showFilesInFolderSerachHit
     */
    public void setShowFilesInFolderSearchHit(boolean showFilesInFolderSerachHit) {
        m_showFilesInFolderSearchHit = showFilesInFolderSerachHit;
    }

    /**
     * Function to get if search should show files in a folder hit.
     *
     * @return  True if it should show files in a folder, false otherwise.
     */
    public boolean getShowFilesInFolderSerachHit() {
        return m_showFilesInFolderSearchHit;
    }
}
