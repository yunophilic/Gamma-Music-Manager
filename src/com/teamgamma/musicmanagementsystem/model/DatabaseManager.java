package com.teamgamma.musicmanagementsystem.model;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;


/**
 * Class to manage database
 */
public class DatabaseManager {

    private Connection connection = null;
    private Statement stmt;
    private ResultSet rs;

    public DatabaseManager() {
        connectToDatabase();
    }

    /**
     * Connect to a database, a database file will be created if there is no database object yet
     */
    private void connectToDatabase(){
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("Connecting to database...");
            // establish connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + System.getProperty("user.dir") +
                    File.separator + "db" + File.separator + "dbFile.db");

        }
        catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
