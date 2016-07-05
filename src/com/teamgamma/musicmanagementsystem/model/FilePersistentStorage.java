package com.teamgamma.musicmanagementsystem.model;

import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerConstants;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class to save state for the application.
 */
public class FilePersistentStorage {
    private static final String DB_DIR = System.getProperty("user.dir") + File.separator + "db";
    private static final String CONFIG_PATH = DB_DIR + File.separator + "config.json";
    private static final String VOLUME = "volume";
    private static final String LEFT_PANEL_OPTION = "left_panel_option";
    private static final String RIGHT_PANEL_FOLDER = "right_panel_folder";
    private static final String CENTER_PANEL_FOLDER = "center_panel_folder";
    private static final String CENTER_PANEL_OPTION = "center_panel_option";
    private JSONObject m_jsonObject;

    /**
     * Constructor.
     */
    public FilePersistentStorage() {
        this.m_jsonObject = new JSONObject();
        setupConfig();
    }

    /**
     * Create the config file if it does not exist.
     * Initialize the config file if it exists.
     */
    private void setupConfig() {
        if(!isConfigExists()) {
            createConfigFile();
        } else {
            initializeConfigFile();
        }
    }

    /**
     * Initialize the config file
     */
    private void initializeConfigFile() {
        JSONParser parser = new JSONParser();
        try {
            m_jsonObject = (JSONObject) parser.parse(new FileReader(CONFIG_PATH));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the config file
     */
    private void createConfigFile() {
        Path configDir = Paths.get(CONFIG_PATH);
        try {
            Files.createDirectories(configDir.getParent());
            Files.createFile(configDir);
            setupConfigDefaults();
            writeConfigFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the default configuration in the config file
     */
    private void setupConfigDefaults() {
        m_jsonObject.put(VOLUME, MusicPlayerConstants.MAX_VOLUME);
        m_jsonObject.put(LEFT_PANEL_OPTION, false);
        m_jsonObject.put(RIGHT_PANEL_FOLDER, "");
        m_jsonObject.put(CENTER_PANEL_FOLDER, "");
        m_jsonObject.put(CENTER_PANEL_OPTION, false);
    }

    /**
     * Save the config file to the system
     */
    public void saveConfigFile(File rightPanelFile, File centerPanelFile, MenuOptions menuOptions) {
        saveRightPanelFolder(rightPanelFile.getAbsolutePath());
        saveCenterPanelFolder(centerPanelFile.getAbsolutePath());
        saveCenterPanelOption(menuOptions.getM_centerPanelShowSubfolderFiles());
        saveLeftPanelOption(menuOptions.getM_leftPanelShowFolder());

        writeConfigFile();
    }

    private void writeConfigFile() {
        try (FileWriter writer = new FileWriter(CONFIG_PATH)){
            writer.write(m_jsonObject.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if config file exists
     * @return true if exists. false if does not exist.
     */
    private boolean isConfigExists() {
        return new File(CONFIG_PATH).exists();
    }

    /**
     * Save the volume to the config file
     * @param volumeLevel an integer indicating the volume.
     */
    public void saveVolumeState(double volumeLevel) {
        m_jsonObject.replace(VOLUME, volumeLevel);
    }

    /**
     * Returns the volume state from config file
     * @return volume as a double
     */
    public double getVolumeConfig() {
        return (double) m_jsonObject.get(VOLUME);
    }

    private void saveRightPanelFolder(String rightFolderPath) {
        m_jsonObject.replace(RIGHT_PANEL_FOLDER, rightFolderPath);
    }

    public String getRightPanelFolder() {
        return (String) m_jsonObject.get(RIGHT_PANEL_FOLDER);
    }

    private void saveCenterPanelFolder(String centerFolderPath) {
        m_jsonObject.replace(CENTER_PANEL_FOLDER, centerFolderPath);
    }

    public String getCenterPanelFolder() {
        return (String) m_jsonObject.get(CENTER_PANEL_FOLDER);
    }

    private void saveCenterPanelOption(boolean option) {
        m_jsonObject.replace(CENTER_PANEL_OPTION, option);
    }

    public boolean getCenterPanelOption() {
        return (boolean) m_jsonObject.get(CENTER_PANEL_OPTION);
    }

    private void saveLeftPanelOption(boolean option) {
        m_jsonObject.replace(LEFT_PANEL_OPTION, option);
    }

    public boolean getLeftPanelOption() {
        return (boolean) m_jsonObject.get(LEFT_PANEL_OPTION);
    }
}