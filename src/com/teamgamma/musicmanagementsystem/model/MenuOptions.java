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

    public void setM_leftPanelShowFolder(boolean leftPanelShowFolder){
        m_leftPanelShowFolder = leftPanelShowFolder;
    }

    public boolean getM_leftPanelShowFolder(){
        return m_leftPanelShowFolder;
    }

    public void setM_centerPanelShowSubfolderFiles(boolean centerPanelShowSubfolderFiles){
        m_centerPanelShowSubfolderFiles = centerPanelShowSubfolderFiles;
    }

    public boolean getM_centerPanelShowSubfolderFiles(){
        return m_centerPanelShowSubfolderFiles;
    }
}
