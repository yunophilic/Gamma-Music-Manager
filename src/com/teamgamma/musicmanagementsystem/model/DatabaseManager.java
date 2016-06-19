package com.teamgamma.musicmanagementsystem.model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


/**
 * Class to manage database
 */
public class DatabaseManager {
    private static final String DB_DIR = System.getProperty("user.dir") + File.separator + "db";
    private static final String DB_FILE_PATH = DB_DIR + File.separator + "persistence.db";

    private Connection m_connection;
    private Statement m_statement;
    private ResultSet m_resultSet;

    public DatabaseManager() {
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
     * Initialize database
     */
    public void setupDatabase() {
        setupConnection();
        createTables();
    }

    /**
     * Init database connection
     */
    private void setupConnection(){
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("Connecting to database...");
            m_connection = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE_PATH);

        } catch(Exception e) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create tables if not exist
     */
    private void createTables() {
        try {
            m_statement = m_connection.createStatement();

            //library table
            m_statement.executeUpdate("CREATE TABLE IF NOT EXISTS Library (" +
                                            "libraryPath TEXT PRIMARY KEY NOT NULL" +
                                      ")");

            //left tree view table
            m_statement.executeUpdate("CREATE TABLE IF NOT EXISTS LeftTreeView (" +
                                            "path TEXT PRIMARY KEY NOT NULL," +
                                            "expanded BOOLEAN NOT NULL," +
                                            "selected BOOLEAN NOT NULL" +
                                      ")");

            //right tree view table
            m_statement.executeUpdate("CREATE TABLE IF NOT EXISTS LeftTreeView (" +
                                            "path TEXT PRIMARY KEY NOT NULL," +
                                            "expanded BOOLEAN NOT NULL" +
                                      ")");

            m_statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates file by adding a new library
     *
     * @param libraryPath String
     */
    public void addLibrary(String libraryPath) {
        /*File dbFile = new File(System.getProperty("user.dir") + File.separator + "db" +
                File.separator + "libraries.txt");
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(dbFile, true));
            writer.println(libraryPath);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        try {
            m_statement = m_connection.createStatement();
            m_statement.executeUpdate("INSERT INTO Library VALUES ('" + libraryPath + "')");
            m_statement.close();
        } catch (Exception e) {
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
            m_statement = m_connection.createStatement();
            for(String libraryPath : librariesPath) {
                m_statement.executeUpdate("INSERT INTO Library VALUES ('" + libraryPath + "')");
            }
            m_statement.close();
        } catch (Exception e) {
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
            m_statement = m_connection.createStatement();
            m_statement.executeUpdate("DELETE FROM Library WHERE libraryPath='" + libraryPath + "'");
            m_statement.close();
            return true;
        } catch (Exception e) {
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
            m_statement = m_connection.createStatement();
            for (String libraryPath : librariesPath) {
                m_statement.executeUpdate("DELETE FROM Library WHERE libraryPath='" + libraryPath + "'");
            }
            m_statement.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Reads file and finds specified library name
     *
     * @return an ArrayList of libraries path
     */
    public List<String> getLibraries() {
        List<String> librariesPath = new ArrayList<>();
        try {
            m_statement = m_connection.createStatement();
            m_resultSet = m_statement.executeQuery("SELECT * FROM Library");
            while(m_resultSet.next()) {
                librariesPath.add(m_resultSet.getString("libraryPath"));
            }
            m_resultSet.close();
            m_statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return librariesPath;
    }
}
