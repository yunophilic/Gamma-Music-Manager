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
 * Note: @SuppressWarnings("unchecked") is used because of JSONObject implementation
 * from the json-simple api
 */
public class FilePersistentStorage {
    private static final String DB_DIR = System.getProperty("user.dir") + File.separator + "db";
    private static final String CONFIG_PATH = DB_DIR + File.separator + "config.json";
    private static final String VOLUME = "volume";
    private static final String LEFT_PANEL_SHOW_FOLDERS_ONLY_OPTION = "left_panel_option";
    private static final String RIGHT_PANEL_FOLDER = "right_panel_folder";
    private static final String CENTER_PANEL_FOLDER = "center_panel_folder";
    private static final String CENTER_PANEL_SHOW_ALL_FILES_IN_FOLDER = "center_panel_option";
    private static final String SEARCH_SHOW_FILES_IN_FOLDER = "show_files_in_folder_hit";
    private static final String HIDE_RIGHT_FILE_PANE_OPTION = "hide_right_panel";
    private static final String SELECTED_PLAYLIST = "selected_playlist";
    private JSONObject m_jsonObject;

    /**
     * Constructor.
     */
    public FilePersistentStorage() {
        m_jsonObject = new JSONObject();
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
     * Initialize the config file.
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
     * Create the config file.
     */
    private void createConfigFile() {
        Path configDir = Paths.get(CONFIG_PATH);
        try {
            if(!isDbDirExists()) {
                Files.createDirectories(configDir.getParent());
            }
            Files.createFile(configDir);
            setupConfigDefaults();
            writeConfigFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the default configuration in the config file.
     */
    @SuppressWarnings("unchecked")
    private void setupConfigDefaults() {
        m_jsonObject.put(VOLUME, MusicPlayerConstants.MAX_VOLUME);
        m_jsonObject.put(LEFT_PANEL_SHOW_FOLDERS_ONLY_OPTION, false);
        m_jsonObject.put(RIGHT_PANEL_FOLDER, "");
        m_jsonObject.put(CENTER_PANEL_FOLDER, "");
        m_jsonObject.put(CENTER_PANEL_SHOW_ALL_FILES_IN_FOLDER, false);
        m_jsonObject.put(SEARCH_SHOW_FILES_IN_FOLDER, false);
        m_jsonObject.put(HIDE_RIGHT_FILE_PANE_OPTION, false);
        m_jsonObject.put(SELECTED_PLAYLIST, "");
    }

    /**
     * Save the config file settings.
     */
    public void saveConfigFile(File rightPanelFile,
                               File centerPanelFile,
                               Playlist selectedPlaylist,
                               MenuOptions menuOptions) {
        System.out.println("RIGHT FOLDER: " + rightPanelFile);
        System.out.println("CENTER FOLDER: " + centerPanelFile);
        System.out.println("SELECTED PLAYLIST: " + selectedPlaylist);

        if(rightPanelFile != null) {
            saveRightPanelFolder(rightPanelFile.getAbsolutePath());
        } else {
            saveRightPanelFolder("");
        }
        if(centerPanelFile != null) {
            saveCenterPanelFolder(centerPanelFile.getAbsolutePath());
        } else {
            saveCenterPanelFolder("");
        }
        if(selectedPlaylist != null) {
            saveSelectedPlaylist(selectedPlaylist.getM_playlistName());
        } else {
            saveSelectedPlaylist("");
        }

        saveCenterPanelOption(menuOptions.getM_centerPanelShowSubfolderFiles());
        saveLeftPanelShowOnlyFoldersOption(menuOptions.getM_leftPanelShowFoldersOnly());
        saveShowFilesInFolderHit(menuOptions.getShowFilesInFolderSerachHit());
        saveHideRightFilePane(menuOptions.getHideRightPanel());

        writeConfigFile();
    }

    /**
     * Write saved config settings to the file system.
     */
    private void writeConfigFile() {
        try {
            FileWriter writer = new FileWriter(CONFIG_PATH);
            writer.write(m_jsonObject.toJSONString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if config file exists.
     *
     * @return true if exists. false if does not exist.
     */
    private boolean isConfigExists() {
        return new File(CONFIG_PATH).exists();
    }

    /**
     * Check if db folder exists.
     *
     * @return true if exists. false if does not exist.
     */
    private boolean isDbDirExists() {
        return new File(DB_DIR).exists();
    }

    /**
     * Save the volume to the config file.
     *
     * @param volumeLevel an integer indicating the volume.
     */
    @SuppressWarnings("unchecked")
    public void saveVolumeState(double volumeLevel) {
        m_jsonObject.replace(VOLUME, volumeLevel);
    }

    /**
     * Returns the volume state from config file.
     *
     * @return volume as a double.
     */
    public double getVolumeConfig() {
        return (double) m_jsonObject.get(VOLUME);
    }

    /**
     * Save the right panel folder to the config file.
     *
     * @param rightFolderPath path to save.
     */
    @SuppressWarnings("unchecked")
    private void saveRightPanelFolder(String rightFolderPath) {
        m_jsonObject.replace(RIGHT_PANEL_FOLDER, rightFolderPath);
    }

    /**
     * Returns the right panel folder from config file.
     *
     * @return right folder path as a string.
     */
    public String getRightPanelFolder() {
        return (String) m_jsonObject.get(RIGHT_PANEL_FOLDER);
    }

    /**
     * Save the center panel folder to the config file.
     *
     * @param centerFolderPath path to save.
     */
    @SuppressWarnings("unchecked")
    private void saveCenterPanelFolder(String centerFolderPath) {
        m_jsonObject.replace(CENTER_PANEL_FOLDER, centerFolderPath);
    }

    /**
     * Returns the center panel folder from config file.
     *
     * @return center folder path as a string.
     */
    public String getCenterPanelFolder() {
        return (String) m_jsonObject.get(CENTER_PANEL_FOLDER);
    }

    /**
     * Save the center panel folder to the config file.
     *
     * @param selectedPlaylistName path to save.
     */
    @SuppressWarnings("unchecked")
    private void saveSelectedPlaylist(String selectedPlaylistName) {
        m_jsonObject.put(SELECTED_PLAYLIST, selectedPlaylistName);
    }

    /**
     * Returns the selected playlist from config file.
     *
     * @return selected playlist name as a string.
     */
    public String getSelectedPlaylist() {
        return (String) m_jsonObject.get(SELECTED_PLAYLIST);
    }

    /**
     * Save the center panel option to the config file.
     *
     * @param option boolean value to save.
     */
    @SuppressWarnings("unchecked")
    private void saveCenterPanelOption(boolean option) {
        m_jsonObject.replace(CENTER_PANEL_SHOW_ALL_FILES_IN_FOLDER, option);
    }

    /**
     * Returns the center panel option from config file.
     *
     * @return center folder option as a boolean.
     */
    public boolean getShowAllFilesInCenterPanelOption() {
        return getValueFromJson(CENTER_PANEL_SHOW_ALL_FILES_IN_FOLDER, false);
    }

    /**
     * Save the left panel option to the config file.
     *
     * @param option boolean value to save.
     */
    @SuppressWarnings("unchecked")
    private void saveLeftPanelShowOnlyFoldersOption(boolean option) {
        m_jsonObject.replace(LEFT_PANEL_SHOW_FOLDERS_ONLY_OPTION, option);
    }

    /**
     * Returns the left panel option from config file.
     *
     * @return left folder option as a boolean.
     */
    public boolean getLeftPanelShowOnlyFoldersOption() {
        return getValueFromJson(LEFT_PANEL_SHOW_FOLDERS_ONLY_OPTION, false);
    }

    /**
     * Returns if we want to show files in folder hits in the search results.
     *
     * @return True if we want to show the files in folder hits for the search results. False otherwise.
     */
    public boolean getShowFilesInFolderHit() {
        return getValueFromJson(SEARCH_SHOW_FILES_IN_FOLDER, false);
    }

    /**
     * Returns if we need to hide the right file pane option from config file.
     *
     * @return True if we want to hide the right file pane. False otherwise.
     */
    public boolean getHideRightFilePane() {
        return getValueFromJson(HIDE_RIGHT_FILE_PANE_OPTION, false);
    }

    /**
     * Save if we want to show all the files in folder for the search results to the config file.
     *
     * @param option boolean value to save.
     */
    @SuppressWarnings("unchecked")
    private void saveShowFilesInFolderHit(boolean option) {
        m_jsonObject.replace(SEARCH_SHOW_FILES_IN_FOLDER, option);
    }

    /**
     * Save if we want to show or hide the right file pane to the config file.
     *
     * @param option boolean value to save.
     */
    @SuppressWarnings("unchecked")
    private void saveHideRightFilePane(boolean option) {
        m_jsonObject.replace(HIDE_RIGHT_FILE_PANE_OPTION, option);
    }

    /**
     * Function to access the JSON file to get a boolean configuration flag if it exists.
     *
     * @param key               The JSON key for the configuration option.
     * @param defaultValue      The default value to use if the item is not in the JSON file.
     * @return                  The configuration flag.
     */
    private boolean getValueFromJson(String key, boolean defaultValue) {
        if (m_jsonObject.containsKey(key)) {
            return (boolean) m_jsonObject.get(key);
        } else {
            m_jsonObject.put(key, defaultValue);
            return defaultValue;
        }
    }
}
