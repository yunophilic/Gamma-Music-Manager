package com.teamgamma.musicmanagementsystem.model;

import com.teamgamma.musicmanagementsystem.misc.TreeViewItem;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Class to manage database
 */
public class DatabaseManager {
    private static final String DB_DIR = System.getProperty("user.dir") + File.separator + "db";
    private static final String DB_FILE_PATH = DB_DIR + File.separator + "persistence.db";

    private Connection m_connection;
    private PreparedStatement m_addLibraryStatement;
    private PreparedStatement m_addLeftTreeItemStatement;
    private PreparedStatement m_deleteLibraryStatement;
    private PreparedStatement m_clearLeftTreeViewStatement;
    private PreparedStatement m_setSelectedLeftTreeItemStatement;
    private PreparedStatement m_getLibrariesStatement;
    private PreparedStatement m_getSelectedLeftTreeItemStatement;
    private PreparedStatement m_getExpandedLeftTreeItemsStatement;

    public DatabaseManager() {
    }

    /**
     * prepare all prepared statements
     */
    private void prepareStatements() {
        try {
            m_addLibraryStatement = m_connection.prepareStatement("INSERT INTO Library VALUES (?)");

            m_addLeftTreeItemStatement = m_connection.prepareStatement("INSERT INTO LeftTreeView (path, isExpanded) " +
                                                                        "VALUES (?, ?)");

            m_deleteLibraryStatement = m_connection.prepareStatement("DELETE FROM Library WHERE libraryPath=?");

            m_clearLeftTreeViewStatement = m_connection.prepareStatement("DELETE FROM LeftTreeView");

            m_setSelectedLeftTreeItemStatement = m_connection.prepareStatement("UPDATE LeftTreeView " +
                                                                            "SET isSelected=1 " +
                                                                            "WHERE path=?");

            m_getLibrariesStatement = m_connection.prepareStatement("SELECT * FROM Library");

            m_getSelectedLeftTreeItemStatement = m_connection.prepareStatement("SELECT * " +
                                                                            "FROM LeftTreeView " +
                                                                            "WHERE isSelected=1");

            m_getExpandedLeftTreeItemsStatement = m_connection.prepareStatement("SELECT * " +
                                                                            "FROM LeftTreeView " +
                                                                            "WHERE isExpanded=1");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize database
     */
    public void setupDatabase() {
        setupConnection();
        createTables();
        prepareStatements();
    }

    /**
     * Function to see if there is something that is saved.
     *
     * @return true if something, false otherwise
     */
    public boolean isDatabaseFileExist() {
        return new File(DB_FILE_PATH).exists();
    }

    /**
     * Function to create .txt file to save library names
     */
    public void createDatabaseFile() {
        Path dbFilePath = Paths.get(DB_FILE_PATH);
        try {
            Files.createDirectories(dbFilePath.getParent());
            Files.createFile(dbFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize database connection
     */
    private void setupConnection(){
        try {
            System.out.println("Connecting to database...");
            m_connection = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE_PATH);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Close connection
     */
    public void closeConnection() {
        try {
            System.out.println("Closing connection...");
            m_connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create tables if not exist
     */
    private void createTables() {
        try {
            Statement statement = m_connection.createStatement();

            //library table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Library (" +
                                        "libraryPath TEXT PRIMARY KEY NOT NULL" +
                                    ")");

            //left tree view table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS LeftTreeView (" +
                                        "path       TEXT    PRIMARY KEY NOT NULL," +
                                        "isExpanded BOOLEAN             NOT NULL," +
                                        "isSelected BOOLEAN             NOT NULL DEFAULT 0" +
                                    ")");

            //right tree view table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS RightTreeView (" +
                                        "path       TEXT    PRIMARY KEY NOT NULL," +
                                        "isExpanded BOOLEAN             NOT NULL" +
                                    ")");

            //playlist table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Playlist (" +
                                        "playlistName TEXT PRIMARY KEY NOT NULL" +
                                    ")");

            //song playlist table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS SongPlaylist (" +
                                        "songPath       TEXT      NOT NULL," +
                                        "playlistName   TEXT      NOT NULL," +
                                        "isLastPlayed   BOOLEAN   NOT NULL," +
                                        "orderNumber    INTEGER   NOT NULL," +
                                        "PRIMARY KEY(songPath, playlistName)" +
                                        "FOREIGN KEY(playlistName) REFERENCES Playlist(playlistName)" +
                                    ")");

            //history table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS History (" +
                                        "songPath TEXT      PRIMARY KEY               NOT NULL," +
                                        "time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL" +
                                    ")");

            //playback queue table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS PlaybackQueue (" +
                                        "songPath TEXT      PRIMARY KEY               NOT NULL," +
                                        "time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL" +
                                    ")");

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates file by adding a new library
     *
     * @param libraryPath String
     */
    public void addLibrary(String libraryPath) {
        try {
            m_addLibraryStatement.setString(1, libraryPath);
            m_addLibraryStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates file by adding multiple libraries
     *
     * @param librariesPath List<String>
     */
    public void addLibraries(List<String> librariesPath) {
        try {
            for(String libraryPath : librariesPath) {
                m_addLibraryStatement.setString(1, libraryPath);
                m_addLibraryStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates file by removing an existing library
     *
     * @param libraryPath String
     * @return success or fail boolean
     */
    public boolean removeLibrary(String libraryPath) {
        try {
            m_deleteLibraryStatement.setString(1, libraryPath);
            m_deleteLibraryStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates file by removing multiple existing libraries
     *
     * @param librariesPath List<String>
     * @return success or fail boolean
     */
    public boolean removeLibraries(List<String> librariesPath) {
        try {
            for (String libraryPath : librariesPath) {
                m_deleteLibraryStatement.setString(1, libraryPath);
                m_deleteLibraryStatement.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Fetch library paths
     *
     * @return List of libraries path
     */
    public List<String> getLibraries() {
        try {
            List<String> librariesPath = new ArrayList<>();
            ResultSet resultSet = m_getLibrariesStatement.executeQuery();
            while(resultSet.next()) {
                librariesPath.add(resultSet.getString("libraryPath"));
            }
            resultSet.close();
            return librariesPath;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Save state of the left tree view
     */
    public void saveLeftTreeViewState(Map<String, Boolean> pathExpandedMap) {
        try {
            m_clearLeftTreeViewStatement.execute();
            for (Map.Entry<String, Boolean> entry : pathExpandedMap.entrySet()) {
                m_addLeftTreeItemStatement.setString(1, entry.getKey());
                m_addLeftTreeItemStatement.setBoolean(2, entry.getValue());
                m_addLeftTreeItemStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save selected left tree item
     */
    public void saveSelectedLeftTreeItem(String centerFolderPath) {
        try {
            m_setSelectedLeftTreeItemStatement.setString(1, centerFolderPath);
            m_setSelectedLeftTreeItemStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the path of the tree view item in the center table
     *
     * @return List of paths of expanded tree view items
     */
    public String getSelectedLeftTreeItem() {
        try {
            ResultSet resultSet = m_getSelectedLeftTreeItemStatement.executeQuery();
            return resultSet.getString("path");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the path of the tree view items that are expanded
     *
     * @return List of paths of expanded tree view items
     */
    public List<String> getExpandedLeftTreeViewItems() {
        try {
            List<String> items = new ArrayList<>();
            ResultSet resultSet = m_getExpandedLeftTreeItemsStatement.executeQuery();
            while (resultSet.next()) {
                items.add(resultSet.getString("path"));
            }
            return items;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
