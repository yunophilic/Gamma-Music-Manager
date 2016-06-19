package com.teamgamma.musicmanagementsystem.model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Class to manage database
 */
public class DatabaseManager {
    private static final String DB_DIR = System.getProperty("user.dir") + File.separator + "db";
    private static final String DB_FILE_PATH = DB_DIR + File.separator + "persistence.db";

    private Connection m_connection;
    private PreparedStatement m_addLibraryStatement;
    private PreparedStatement m_deleteLibraryStatement;
    private PreparedStatement m_getLibrariesStatement;
    private PreparedStatement m_getSelectedCenterFolderStatement;
    private PreparedStatement m_getExpandedLeftTreeItemsStatement;

    public DatabaseManager() {
    }

    private void prepareStatements() {
        try {
            m_addLibraryStatement = m_connection.prepareStatement("INSERT INTO Library VALUES (?)");
            m_deleteLibraryStatement = m_connection.prepareStatement("DELETE FROM Library WHERE libraryPath=?");
            m_getLibrariesStatement = m_connection.prepareStatement("SELECT * FROM Library");
            m_getSelectedCenterFolderStatement = m_connection.prepareStatement("SELECT * " +
                                                                            "FROM LeftTreeView " +
                                                                            "WHERE selected=1");
            m_getExpandedLeftTreeItemsStatement = m_connection.prepareStatement("SELECT * " +
                                                                            "FROM LeftTreeView " +
                                                                            "WHERE expanded=1");
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
    public boolean isThereSavedState() {
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
                                            "path TEXT PRIMARY KEY NOT NULL," +
                                            "expanded BOOLEAN NOT NULL," +
                                            "selected BOOLEAN NOT NULL" +
                                      ")");

            //right tree view table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS RightTreeView (" +
                                            "path TEXT PRIMARY KEY NOT NULL," +
                                            "expanded BOOLEAN NOT NULL" +
                                      ")");

            //History table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS History (" +
                                            "songPath TEXT      PRIMARY KEY NOT NULL," +
                                            "time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL" +
                                      ")");

            //Playlist table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Playlist (" +
                                            "songPath TEXT PRIMARY KEY NOT NULL" +
                                      ")");

            //PlaybackQueue table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS PlaybackQueue (" +
                                            "songPath TEXT      PRIMARY KEY NOT NULL," +
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
     * Get the path of the tree view item in the center table
     *
     * @return List of paths of expanded tree view items
     */
    public String getSelectedCenterFolder() {
        try {
            ResultSet resultSet = m_getSelectedCenterFolderStatement.executeQuery();
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
